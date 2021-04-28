package capteurs;

import lejos.hardware.Button;
import lejos.utility.Delay;
import lejos.utility.Timer;
import lejos.utility.TimerListener;

/**
 * Classe gérant le capteur de contact.
 * L'attribut status indique si le capteur est actif et touche indique si le capteur touche un palet.
 */
public class Toucher{
	private static boolean status;
	private static boolean touche;
	private static boolean souvenir;
	
	/**
	 * Le timer permet de lancer des analyse de manière périodique (à chaque fois qu'un certain temps s'ecoule).
	 */
	private static Timer lanceur = 
		new Timer(100, new TimerListener() {
			//au bout du temps imparti (100 ms)
				public void timedOut() {
					//appel a la fonction qui scanne effectivement
					setTouche();
				}
		}
	);
	
	/**
	 * Getters de la classe Toucher.
	 * @return L'attribut d'interet : touche ou status.
	 */
	public static boolean getTouche() {
		return(touche);
	}
	
	public static boolean getStatus() {
		return(status);
	}
	
	/**
	 * Setter de l'attribut touche
	 */
	public static void setTouche() {
		//creation du tableau recueillant les données du sampler
		float[] touched = new float[Capteur.TOUCHER.sampleSize()];
		//Récupération des données
		Capteur.TOUCHER.fetchSample(touched, 0);
		touche = (touched[0]==1);
		souvenir|=touche;
	}
	
	/**
	 * startScan() permet d'activer la lecture de l'information envoyée par le scanner.
	 */
	public static void startScan() {
		lanceur.setDelay(50);
		lanceur.start();
		status = true;
	}
	
	/**
	 * stopScan() Permet d'arreter le scanner lance par la méthode @see Toucher#startScan().
	 */
	public static void stopScan() {
		lanceur.stop();
		status = false;
		touche = false;
	}
	
	/**
	 * Dès que le bouton retourne true, on garde l'information qu'il s'est activé à un moment jusuqu'à ce qu'elle soit traitée par une fonction extérieure.
	 * @return true si le bouton était à true à un moment et que cette fonction n'a pas été appelée depuis, false sinon
	 */
	public static boolean aToucheAUnMoment() {
		if(souvenir) {
			souvenir = false; 
			return true;
		}
		else return false;
	}
}


/**
 * Une simple classe permettant de tester le capteur de contact manuellement.
 * Elle lance le capteur puis affiche a l'ecran LCD si le palet touche ou non.
 */
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
