package capteurs;

import lejos.hardware.Button;
import lejos.utility.Delay;
import lejos.utility.Timer;
import lejos.utility.TimerListener;

public class Toucher{
	
	//Atrributs de la classe Toucher
	//status indique si la capteur est actif
	private static boolean status;
	//touche contient True si le capteur est activ� par un palet
	private static boolean touche;
	
	//Pour lancer des analyses de maniere periodique et eviter l'activation permanente du capteur
	private static Timer lanceur = 
		new Timer(100, new TimerListener() {
			//au bout du temps imparti (100 ms)
				public void timedOut() {
					//appel � la foction qui scanne effectivement
					setTouche();
				}
		}
	);
	
	public static boolean getTouche() {
		return(touche);
	}
	
	//change la valeur de touche en fonction du scan
	public static void setTouche() {
		//creation du tableau recueillant les donn�es du sampler
		float[] touched = new float[Capteur.TOUCHER.sampleSize()];
		//r�cup�ration des donn�es
		Capteur.TOUCHER.fetchSample(touched, 0);
		touche = (touched[0]==1);
		
	}
	
	//Pour activer la lecture de la valeur envoy�e par le scanner
	public static void startScan() {
		//le temps passe � 50 ms entre chaque scan
		lanceur.setDelay(50);
		lanceur.start();
		status = true;
	}
	
	//Pour d�sactiver la lecture de la valeur envoy�e par le scanner
	public static void stopScan() {
		lanceur.stop();
		status = false;
	}
	
	//Retourne le status pour savoir l'�tat actuel
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
			Delay.msDelay(200);
		}
		Toucher.stopScan();
	}
}
