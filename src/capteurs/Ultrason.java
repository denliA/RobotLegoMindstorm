package capteurs;

import lejos.utility.Timer;
import lejos.utility.TimerListener;
/**
 * Gestion du capteur ultrason.
 * <p>Fonctions assurees :</p>
 * <ul>
 * <li> Prise periodique de mesure 
 * <li> Enregistrement des valeurs
 * <li> Transmission des mesures aux autres classes pour traitement
 * </ul>
 * @see Capteur
 * @see PaletUltrason
 */
public class Ultrason {
	
	//Attributs 
	//float car utilise par leJOS
	private static float distance;
	private static boolean bruitDetecte;
	//pour savoir si le capteur est effectivement actif
	private static boolean status;
	
	//Pour lancer des analyses de mani�re p�riodique
		private static Timer lanceur = 
			//cr�ation d'un objet Timer qui lancera les scans toutes les 100ms
			new Timer(0, new TimerListener() {
				public void timedOut() {
					//appel aux deux fonctions lancant les scans necessaire a la classe
					setDistance();
					//setBruitDetecte(); ralentit le scan, inutilise pour le moment 
				}
			}
		);
	
	//Methodes
	/**
	 * Lance des scans periodiques avec lanceur et modifie l'attribut <i>status</i> de maniere adequate
	 */
	public static void startScan() {
		//scan en continu
		lanceur.setDelay(0);
		lanceur.start();
		status = true;
	}
	/**
	 * Arrete le scan
	 */
	public static void stopScan() {
		lanceur.stop();
		status = false;
	}
	
	/**
	 * Permet d'obtenir la valeur de <i>status</i>.
	 * @return status (true si un scan est en cours, false sinon)
	 */
	public static boolean getStatus() {
		return status;
	}
	
	/**
	 * Permet de stocker la distance calculee par le capteur ultrason dans l'attribut <i>distance</i>.
	 */
	public static void setDistance() {
		//creation du tableau qui stockera les valeurs renvoyees par le sampler
		float[] tabDistance = new float[1];
		//remplissage du tableau
		Capteur.ULTRASON.fetchSample(tabDistance, 0);
		distance = tabDistance[0];
		//System.out.println(tabDistance[0]);
	}
	/**
	 * getter de l'attribut <i>distance</i>.
	 * @return distance (float representant une distance en metres)
	 */
	public static float getDistance() {
		return distance;
	}
	
	/**
	 * setter de boolean bruitDetecte (true si presence d'un autre robot, false sinon).
	 */
	public static void setBruitDetecte() {
		bruitDetecte=false;
		
		//le sampler contient normalement un seul element : 1 si il y a un autre robot, 0 sinon
		float[] autreRobot = new float[1];
		Capteur.ECOUTE.fetchSample(autreRobot, 0);
		
		bruitDetecte = autreRobot[0]==1;
	}
	/**
	 * retourne si un autre robot a été détecté en face du notre
	 * @return true si un autre robot est détecté.
	 */
	public static boolean getBruitDetecte() {
		return bruitDetecte;
	}
	
	
	
}

