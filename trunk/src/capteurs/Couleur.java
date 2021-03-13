package capteurs;

import lejos.utility.Delay;
import lejos.utility.Timer;
import lejos.utility.TimerListener;
import java.util.ArrayList;

public class Couleur {
	
	//Attributs de la classe Couleur
	private static float rouge;
	private static float vert;
	private static float bleu;
	private static float lumiere;
	private static float IDCouleur;
	private static float intensiteRouge;
	
	private static byte modeFlag;  //4bits 0000e3e2e1e0 avec e3 : light, e2 : RGB, e1 : ID, e0 : RedMode
	private final static byte REDMODE = 0b1;
	private final static byte IDMODE = 0b10;
	private final static byte RGBMODE = 0b100;
	private final static byte LIGHTMODE = 0b1000;
	
	
	private final static Object lock = new Object();
	
	// Pour lancer périodiquement des mesures
	private static Timer lanceur = new Timer(100, 
			new TimerListener() {
		public void timedOut() {
			synchronized(lock) {
				update();
			}
		}
	});
	
	
	// énumération des couleurs, avec les bornes de celles-ci
	enum CouleurLigne { 
		ROUGE ( null, new float[] {2.80f, 3.80f, 0.45f, 0.60f, 0.10f, 0.20f}), 
		VERTE (null, new float[] {0.30f, 0.40f, 0.20f, 0.25f, 0.58f, 0.70f}),
		BLEUE (null, new float[] {0.17f, 0.27f, 0.61f, 0.78f, 2.50f, 4.00f}),
		BLANCHEP (null, new float[] {0.64f, 0.73f, 0.53f, 0.63f, 0.77f, 0.95f}),
		BLANCHEF (null, new float[] {0.65f, 0.76f, 0.52f, 0.65f, 0.75f, 0.92f}), 
		NOIREH(new float[] {0,10,0,10,0,10 },null),
		NOIREV(new float[] {0,10,0,10,0,10 },null),
		NOIRE(new float[] {0,10,0,10,0,10 },null),
		JAUNE (null, new float[] {0.75f, 0.83f, 0.15f, 0.20f, 0.18f, 0.26f}),
		GRIS(null, null), //TODO
		VIDE(new float[] {0,1,0,1,0,1 },null),
		INCONNU(null, null);
		
		Intervalle IRGB;
		Intervalle IRatios;
		
		
		// bc : bonesCouleurs : {rouge_min, rouge_max, vert_min, vert_max, bleu_min, bleu_max} 
		// br : bornesRatios : { R/G_min, R/G_max, B/G_min, B/G_max, B/R_min, B/R_max }
		// pour des valeurs à ne pas prendre en compte, le min et le max de celles-ci doivent tous les deux être à Float.NaN
		private CouleurLigne(float[] bc, float[] br) {
			if (bc != null)
				IRGB = new Intervalle(new float[] {bc[0], bc[2], bc[4]}, new float[] {bc[1], bc[3], bc[5]});
			else
				IRGB = null;
			if (br != null)
				IRatios = new Intervalle(new float[] {br[0], br[2], br[4]}, new float[] {br[1], br[3], br[5]});
			else 
				IRatios=null;
		}
		
	}
	
	//Avoir la valeur de la couleur suivant l'enumeration de leJOS
	public static float getColorID() {
		return(IDCouleur);
	}
	
	//Avoir la valeur de la couleur suivant un encodage RGB
	public static float[] getRGB() {
		return(new float[] {rouge,vert,bleu});
	}
	
	//Ratios R/G, B/G et B/R
	public static float[] getRatios() {
		return (new float[] {rouge/vert, bleu/vert, bleu/rouge});
	}
	
	
	//Recuperer une valeur definissant l'intensite de la lumiere ambiante
	public static float getAmbiantLight() {
		return(lumiere);
	}
	
	
	public static float getRedMode() {
		return(intensiteRouge);
	}
	
	//Definir le mode de scanner de couleur (quelles couleurs capter et quelle couleur ignorer)
	public static void setScanMode(byte flag) {
		modeFlag = flag;
	}
	
	//Retourn le mode de scanner de couleur
	public static byte getScanMode() {
		return(modeFlag);
	}
	
	
	// Démarrer le timer pour lancer des mesures périodiques
	public static void startScanAtRate(int delay) {
		lanceur.setDelay(delay);
		lanceur.start();
	}
	
	// Arrêter le timer
	public static void stopScan() {
		lanceur.stop();
	}
	
	//Fait des choix d'approximation en fonction des valeurs des autres methodes pour retourner la couleur analyse
	public static CouleurLigne getCouleurLigne() {
		setScanMode((byte) (getScanMode() | REDMODE));
		Delay.msDelay(10);
		synchronized(lock) {
			float[] RGB = getRGB();
			float[] ratios = getRatios();
			for (CouleurLigne couleur : CouleurLigne.values()) {
				if (couleur.IRatios!=null) {
					if (couleur.IRatios.contains(ratios))
						return couleur;
				}
				else if (couleur.IRGB!=null) {
					if (couleur.IRGB.contains(RGB))
						return couleur;
				}
			}
			return CouleurLigne.INCONNU;		
		}
		
	}
	
	private static void update() {
		update(0);
	}
	
	private static void update(int delai) {
		synchronized (lock) {
			if((modeFlag & LIGHTMODE)!=0) {
				float[] ambiantLight = new float [Capteur.LUMIERE_AMBIANTE.sampleSize()];
				Capteur.LUMIERE_AMBIANTE.fetchSample(ambiantLight, 0);
				lumiere = ambiantLight[0];
			}
			Delay.msDelay(delai);
			if((modeFlag & RGBMODE) != 0) {
				float[] couleurRGB = new float[Capteur.RGB.sampleSize()];
				Capteur.RGB.fetchSample(couleurRGB, 0);
				rouge = couleurRGB[0];
				vert = couleurRGB[1];
				bleu = couleurRGB[2];
			}
			Delay.msDelay(delai);
			if((modeFlag & IDMODE)!=0) {
				float[] couleur_id = new float[Capteur.ID_COULEUR.sampleSize()];
				Capteur.ID_COULEUR.fetchSample(couleur_id, 0);
				IDCouleur = couleur_id[0];
			}
			Delay.msDelay(delai);
			if((modeFlag & REDMODE)!=0) {
				float[] rouge = new float[1];
				Capteur.ROUGE.fetchSample(rouge, 0);
				intensiteRouge = rouge[0];
			}
		}
	}
}


class Intervalle {
	float[] min;
	float[] max;
	
	public Intervalle(float min, float max) {
		this(new float[] {min}, new float[] {max});
	}
	public Intervalle(float[] min, float[] max) {
		assert(min.length == max.length);
		this.min = min;
		this.max = max;
	}
	
	public boolean contains(float[] values) {
		return contains(values, 0, min.length);
	}
	
	public boolean contains(float[] values, int beg, int end) {
		assert(values.length == end-beg);
		for (int i=0; i<end-beg; i++)
			if (values[i] > max[i] || values[i] < min[i])
				return false;
		return true;
		
	}
	
}
