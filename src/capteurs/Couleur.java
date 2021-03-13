package capteurs;

import lejos.utility.Timer;
import lejos.utility.TimerListener;

public class Couleur {
	
	//Attributs de la classe Couleur
	private static float rouge;
	private static float vert;
	private static float bleu;
	private static float lumiere;
	private static float IDCouleur;
	private static float intensiteRouge;
	private static byte modeFlag;  //4bits 0000e3e2e1e0 avec e3 : light, e2 : RGB, e1 : ID, e0 : RedMode
	private final static Object lock = new Object();
	private static Timer lanceur = new Timer(100, 
			new TimerListener() {
		public void timedOut() {
			synchronized(lock) {
				update();
			}
		}
	});
	enum CouleurLigne { ROUGE, VERTE, BLEUE, BLANCHE, NOIREH, NOIREV, JAUNE, GRIS };
	
	//Avoir la valeur de la couleur suivant l'enumeration de leJOS
	public static float getColorID() {
		return(IDCouleur);
	}
	
	//Avoir la valeur de la couleur suivant un encoge RGB
	public static float[] getRGB() {
		return(new float[] {rouge,vert,bleu});
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
	
	public static void startScanAtRate(int delay) {
		lanceur.setDelay(delay);
		lanceur.start();
	}
	
	public static void stopScan() {
		lanceur.stop();
	}
	
	//Fait des choix d'approximation en fonction des valeurs des autres methodes pour retourner la couleur analyse
	public static CouleurLigne getCouleurLigne() {
		//TODO
	}
	
	private static void update() {
		update(0);
	}
	
	private static void update(int delai) {
		if((modeFlag & 0b00001000)!=0) {
			float[] ambiantLight = new float [Capteur.LUMIERE_AMBIANTE.sampleSize()];
			Capteur.LUMIERE_AMBIANTE.fetchSample(ambiantLight, 0);
			lumiere = ambiantLight[0];
		}
		if((modeFlag & 0b00000100) != 0) {
			float[] couleurRGB = new float[Capteur.RGB.sampleSize()];
			Capteur.RGB.fetchSample(couleurRGB, 0);
			rouge = couleurRGB[0];
			vert = couleurRGB[1];
			bleu = couleurRGB[2];
		}
		if((modeFlag & 0b00000010)!=0) {
			float[] couleur_id = new float[Capteur.ID_COULEUR.sampleSize()];
			Capteur.ID_COULEUR.fetchSample(couleur_id, 0);
			IDCouleur = couleur_id[0];
		}
		if((modeFlag & 0b1)!=0) {
			float[] rouge = new float[1];
			Capteur.ROUGE.fetchSample(rouge, 0);
			intensiteRouge = rouge[0];
		}
	}
}
