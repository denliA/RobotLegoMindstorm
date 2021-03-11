package moteurs;

import exceptions.OuvertureException;
import lejos.utility.Delay;

public class Pince {
	
	//Attribut
	//les pinces s'ouvriront toujours de la m�me valeur par d�faut
	private static final float OUVERTURE;//TO DO 
	private static boolean ouvert;
	
	//M�thodes
	public static boolean getOuvert() {
		return ouvert;
	}
	
	//Ouverture et fermeture des pinces
	//m�thodes modifiant la valeur de ouvert simultan�ment
	public static void ouvrir() throws OuvertureException {
		if(ouvert==true) {
			//TO DO	
			//envoyer message d'erreur ?
			throw new OuvertureException("Pinces d�j� ouvertes.");
		}
		else {
			Moteur.MOTEUR_PINCE.setSpeed(36000);
			Moteur.MOTEUR_PINCE.forward();
			Delay.msDelay(1000);
			Moteur.MOTEUR_PINCE.stop();
			
			ouvert = true;
		}
	}
	public static void fermer() throws OuvertureException {
		if(ouvert==false) {
			//TO DO
			//envoyer message d'erreur ?
			throw new OuvertureException("Pinces d�j� ferm�es.");
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