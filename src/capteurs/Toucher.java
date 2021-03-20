package capteurs;

import lejos.hardware.Button;
import lejos.utility.Delay;
import lejos.utility.Timer;
import lejos.utility.TimerListener;

public class Toucher{
	
	//Atrributs de la classe Toucher
	private static boolean status;
	private static boolean touche;
	
	//Pour lancer des analyses de manière périodique
	private static Timer lanceur = new Timer(100, 
			new TimerListener() {
		public void timedOut() {
				setTouche();
		}
	});
	
	public static boolean getTouche() {
		return(touche);
	}
	
	public static void setTouche() {
		float[] touched = new float[Capteur.TOUCHER.sampleSize()];
		Capteur.TOUCHER.fetchSample(touched, 0);
		touche = (touched[0]==1);
		
	}
	
	//Pour activer la lecture de la valeur envoyée par le scanner
	public static void startScan() {
		lanceur.setDelay(50);
		lanceur.start();
		status = true;
	}
	
	//Pour désactiver la lecture de la valeur envoyée par le scanner
	public static void stopScan() {
		lanceur.stop();
		status = false;
	}
	
	//Retourne le status pour savoir l'état actuel
	public static boolean getStatus() {
		return(status);
	}
}

class testToucher{
	public static void main(String[] args) {
		Toucher.startScan();
		new Capteur();
		while(Button.ENTER.isUp()) {
			System.out.println(Toucher.getTouche());
			Delay.msDelay(500);
		}
		Toucher.stopScan();
	}
}
