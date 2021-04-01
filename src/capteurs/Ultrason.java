package capteurs;

import lejos.utility.Timer;
import lejos.utility.TimerListener;

public class Ultrason {
	
	//Attributs 
	//float car utilis� par leJOS
	private static float distance;
	private static boolean bruitDetecte;
	//pour savoir si le capteur est effectivement actif
	private static boolean status;
	
	//Pour lancer des analyses de mani�re p�riodique
		private static Timer lanceur = 
			//cr�ation d'un objet Timer qui lancera les scans toutes les 100ms
			new Timer(100, new TimerListener() {
				public void timedOut() {
					//appel aux deux fonctions lan�ant les scans n�cessaire � la classe
					setDistance();
					setBruitDetecte();
				}
			}
		);
	
	//M�thodes
		
	//Lancent des scans p�riodiques avec lanceur et modifient status de mani�re ad�quate
	public static void startScan() {
		//scan toutes les 50 ms
		lanceur.setDelay(20);
		lanceur.start();
		status = true;
	}
	public static void stopScan() {
		lanceur.stop();
		status = false;
	}
	
	//Donne la valeur de status
	public static boolean getStatus() {
		return status;
	}
	
	//Gestion de la distance mesur�e par le capteur
	public static void setDistance() {
		//cr�ation du tableau qui stockera les valeurs renvoy�es par le sampler
		float[] tabDistance = new float[Capteur.ULTRASON.sampleSize()];
		//remplissage du tableau
		Capteur.ULTRASON.fetchSample(tabDistance, 0);
		distance = tabDistance[0];
		//System.out.println(tabDistance[0]);
	}
	public static float getDistance() {
		return distance;
	}
	
	//D�tection de la pr�sence d'un autre robot
	public static void setBruitDetecte() {
		bruitDetecte=false;
		
		//le sampler contient normalement un seul �l�ment : 1 si il y a un autre robot, 0 sinon
		float[] autreRobot = new float[Capteur.ECOUTE.sampleSize()];
		Capteur.ECOUTE.fetchSample(autreRobot, 0);
		
		if(autreRobot[0]==1) {bruitDetecte=true;}
	}
	public static boolean getBruitDetecte() {
		return bruitDetecte;
	}
	
	
	
}

