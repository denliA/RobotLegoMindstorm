package moteurs;

import lejos.utility.Delay;

public class Pince {
	
	//Attribut
	//les pinces s'ouvriront toujours de la même valeur par défaut
	private static final float OUVERTURE;//TO DO 
	private static boolean ouvert;
	
	//Méthodes
	public static boolean getOuvert() {
		return ouvert;
	}
	
	//Ouverture et fermeture des pinces
	//méthodes modifiant la valeur de ouvert simultanément
	public static void ouvrir() {
		if(ouvert==true) {
			//TO DO	
			//envoyer message d'erreur ?
		}
		else {
			
			ouvert = true;
		}
	}
	public static void fermer() {
		if(ouvert==false) {
			//TO DO
			//envoyer message d'erreur ?
		}
		else {
			
			Moteur.MOTEUR_PINCE.setSpeed(36000);
			Moteur.MOTEUR_PINCE.backward();
			Delay.msDelay(1000);
			Moteur.MOTEUR_PINCE.stop();
			
			ouvert = false;
			
		}
	}
}
