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
		private static Timer lanceur = new Timer(100, 
				new TimerListener() {
			public void timedOut() {
					setDistance();
					setBruitDetecte();
			}
		});
	
	//Méthodes
		
	//Lancent des scans périodiques avec lanceur et modifient status de manière adéquate
	public static void startScan() {
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
		float[] tabDistance = new float[Capteur.ULTRASON.sampleSize()];
		Capteur.ULTRASON.fetchSample(tabDistance, 0);
		distance = tabDistance[0];
		System.out.println(tabDistance[0]);
	}
	public static float getDistance() {
		return distance;
	}
	
	//Détection de la présence d'un autre robot
	public static void setBruitDetecte() {
		bruitDetecte=false;
		
		float[] autreRobot = new float[Capteur.ECOUTE.sampleSize()];
		Capteur.ECOUTE.fetchSample(autreRobot, 0);
		
		//ECOUTE renvoie 1 ou 0
		if(autreRobot[0]==1) {bruitDetecte=true;}
	}
	public static boolean getBruitDetecte() {
		return bruitDetecte;
	}
	
}


class TestUltrason{
	public static void main(String[] args) {
		new Capteur();
		Ultrason.startScan();
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
