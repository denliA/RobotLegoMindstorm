package capteurs;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.utility.Timer;
import lejos.utility.TimerListener;

public class Ultrason {
	
	//Attributs 
	//float car utilisé par leJOS
	private static float distance;
	private static boolean bruitDetecte;
	//pour savoir si le capteur est effectivement actif
	private static boolean status;
	
	//Pour lancer des analyses de manière périodique
		private static Timer lanceur = 
			//création d'un objet Timer qui lancera les scans toutes les 100ms
			new Timer(100, new TimerListener() {
				public void timedOut() {
					//appel aux deux fonctions lançant les scans nécessaire à la classe
					setDistance();
					setBruitDetecte();
				}
			}
		);
	
	//Méthodes
		
	//Lancent des scans périodiques avec lanceur et modifient status de manière adéquate
	public static void startScan() {
		//scan toutes les 50 ms
		lanceur.setDelay(50);
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
	
	//Gestion de la distance mesurée par le capteur
	public static void setDistance() {
		//création du tableau qui stockera les valeurs renvoyées par le sampler
		float[] tabDistance = new float[Capteur.ULTRASON.sampleSize()];
		//remplissage du tableau
		Capteur.ULTRASON.fetchSample(tabDistance, 0);
		distance = tabDistance[0];
		//System.out.println(tabDistance[0]);
	}
	public static float getDistance() {
		return distance;
	}
	
	//Détection de la présence d'un autre robot
	public static void setBruitDetecte() {
		bruitDetecte=false;
		
		//le sampler contient normalement un seul élément : 1 si il y a un autre robot, 0 sinon
		float[] autreRobot = new float[Capteur.ECOUTE.sampleSize()];
		Capteur.ECOUTE.fetchSample(autreRobot, 0);
		
		if(autreRobot[0]==1) {bruitDetecte=true;}
	}
	public static boolean getBruitDetecte() {
		return bruitDetecte;
	}
	
}

//classe de test
class TestUltrason{
	public static void main(String[] args) {
		//initialisation : création des sampler de la classe Capteur
		new Capteur();
		Ultrason.startScan();
		
		//nettoyage de l'ecran du robot afin d'afficher les messages sans parasite
		LCD.clear();
		
		while(Button.ENTER.isUp()) {
			float d;
			Ultrason.setDistance();
			d = Ultrason.getDistance();
			LCD.drawString("distance : "+d, 0, 0);
			
			Ultrason.setBruitDetecte();
			boolean b = Ultrason.getBruitDetecte();
			if(b) LCD.drawString("Il y a un robot", 0, 2);
			else LCD.drawString("Y a pas de robot", 0, 2);
		}
	}
}
