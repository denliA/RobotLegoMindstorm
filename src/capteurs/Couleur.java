package capteurs;

import lejos.utility.Delay;
import lejos.utility.Timer;
import lejos.utility.TimerListener;
import java.util.concurrent.ConcurrentHashMap;




public class Couleur {
	
	//Attributs de la classe Couleur
	private static float rouge;
	private static float vert;
	private static float bleu;
	private static float lumiere;
	private static float IDCouleur;
	private static float intensiteRouge;
	private static byte modeFlag;  //4bits 0000e3e2e1e0 avec e3 : light, e2 : RGB, e1 : ID, e0 : RedMode
	final static byte REDMODE = 0b1;
	final static byte IDMODE = 0b10;
	final static byte RGBMODE = 0b100;
	final static byte LIGHTMODE = 0b1000;
	
	
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
	
	

	
	//Avoir la valeur de la couleur suivant l'enumeration de leJOS
	public static float getColorID() {
		return(IDCouleur);
	}
	
	//Avoir la valeur de la couleur suivant un encodage RGB
	public static float[] getRGB() {
		synchronized (lock) {
			return(new float[] {rouge,vert,bleu});
		}
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
			ConcurrentHashMap <CouleurLigne, Float> candidats = new ConcurrentHashMap<>();
			for (CouleurLigne couleur : CouleurLigne.values()) {
				Float val = candidats.get(couleur);
				if (couleur.IRGB!=null) {
					if (couleur.IRGB.contains(RGB))
						candidats.put(couleur, (val == null ? 1f : (val=val+1)));
					else 
						candidats.put(couleur, (val==null)? -.5f : (val=val-0.5f));
				}
				else if (couleur.IRatios!=null) {
					if (couleur.IRatios.contains(ratios))
						candidats.put(couleur, (val == null ? 1f : (val=val+1)));
					else 
						candidats.put(couleur, (val==null)? -.5f : (val=val-0.5f));
				}
			}
			System.out.println(candidats.toString());
			CouleurLigne cand=null;
			float max = 0;
			for (CouleurLigne c : CouleurLigne.values()) {
				if (candidats.get(c)!= null &&  candidats.get(c)>max) {
					cand = c;
					max = candidats.get(c);
				}
			}
			return cand==null?  CouleurLigne.INCONNU : cand;
		}
		
	}
	
	/* J'ai mis update en public car pour le suivi de ligne, je dois m'assurer que les valeurs des couleurs soient bien mis à jour dans la boucle
	 * et ne dependent pas d'un autre thread. Lors du redressage du robot, le timer lanceur est arreté. Ceci est un test. On mettra en private si necessaire (Deniza)
	*/
	public static void update() {
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
				rouge = 255*couleurRGB[0];
				vert = 255*couleurRGB[1];
				bleu = 255*couleurRGB[2];
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

