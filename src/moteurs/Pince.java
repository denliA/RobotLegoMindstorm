package moteurs;

import exceptions.OuvertureException;
import lejos.hardware.Sound;
import lejos.utility.Delay;
public class Pince {
	
	//Attribut
	//les pinces s'ouvriront toujours de la meme valeur par defaut
	private static final float OUVERTURE = 1000;//TODO d'apres EV3CONTROL l'angle pour passer de ouverture (grande) a ferme (les pinces se touchent) est d'environ 1735 donc je pense que l'on peut se base entre 1000 et 1500 pour avoir une ouverture assez grande pour les palets et ne pas perdre de temps
	private static boolean ouvert;
	
	//Methodes
	public static boolean getOuvert() {
		return ouvert;
	}
	
	//Ouverture et fermeture des pinces
	//methodes modifiant la valeur de ouvert simultanement
	public static void ouvrir() throws OuvertureException {
		if(ouvert) {
			//envoyer message d'erreur ?
			throw new OuvertureException("Pinces deja ouvertes.");
		}
		else {
			Moteur.MOTEUR_PINCE.setSpeed(36000);
			Moteur.MOTEUR_PINCE.forward();
			Delay.msDelay(750);
			Moteur.MOTEUR_PINCE.stop();
			ouvert = true;
		}
	}
	
	public static void fermer() throws OuvertureException {
		if(!ouvert) {
			//TO DO
			//envoyer message d'erreur ?
			throw new OuvertureException("Pinces deja fermees.");
		}
		else {
			Moteur.MOTEUR_PINCE.setSpeed(36000);
			Moteur.MOTEUR_PINCE.backward();
			Delay.msDelay(750);
			Moteur.MOTEUR_PINCE.stop();
			ouvert = false;
			
		}
	}
}

/*Le test si dessous renvoyait un message d'erreur : 
 * Exception in thread "main" java.lang.NoClassDefFoundError: lejos/hardware/motor/EV3LargeRegulatedMotor
 *  at moteurs.Moteur.<clinit>(Moteur.java:8)
 *  at moteurs.Pince.ouvrir(Pince.java:26)
 *  at moteurs.TestPince.main(Pince.java:66)
 * Caused by: java.lang.ClassNotFoundException: lejos.hardware.motor.EV3LargeRegulatedMotor
 *  at java.base/jdk.internal.loader.BuiltinClassLoader.loadClass(BuiltinClassLoader.java:606)
 *  at java.base/jdk.internal.loader.ClassLoaders$AppClassLoader.loadClass(ClassLoaders.java:168)
 *  at java.base/java.lang.ClassLoader.loadClass(ClassLoader.java:522)
 *  ... 3 more
 */

class TestPince{
	public static void main(String[] args) {
//		new Pince();  //juste pour voir s'il fallait charger la classe avant avec un new dans le vide
		try {
			Pince.ouvrir();
		} catch (OuvertureException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			Sound.beep();
		}
		try {
			Pince.fermer();
		}
		catch(OuvertureException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			Sound.beep();
		}
		try {
			Pince.ouvrir();
		} catch (OuvertureException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			Sound.beep();
		}
		try {
			Pince.ouvrir();
		} catch (OuvertureException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			Sound.beep();
		}
		try {
			Pince.fermer();
		}
		catch(OuvertureException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			Sound.beep();
		}
	}
}