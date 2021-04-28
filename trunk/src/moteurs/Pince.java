package moteurs;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import exceptions.OuvertureException;
import lejos.hardware.Sound;
import lejos.utility.Delay;

/**
 * Classe statique permettant de contrôler les pinces, ainsi que de mémoriser l'état des pinces
 * durant l'execution du programme, et <b>entre l'execution de deux programmes</b> grâce à un fichier qui mémorise l'état.
 */
public class Pince {
	
	//Attribut
	//les pinces s'ouvriront toujours de la meme valeur par defaut
	private static final long OUVERTURE = 1000;//TODO d'apres EV3CONTROL l'angle pour passer de ouverture (grande) a ferme (les pinces se touchent) est d'environ 1735 donc je pense que l'on peut se base entre 1000 et 1500 pour avoir une ouverture assez grande pour les palets et ne pas perdre de temps
	private static boolean ouvert=false;
	static FileWriter outputer = null;
	static FileReader inputer = null;
	static Thread t;
	static {
		Moteur.MOTEUR_PINCE.setSpeed(36000);
		Moteur.MOTEUR_PINCE.setAcceleration(9000);
		try {
			inputer = new FileReader("statutPince");
			ouvert = (inputer.read() == '1');
			inputer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void saveState() {
		try {
			outputer = new FileWriter("statutPince");
			outputer.write(ouvert? '1':'0');
			outputer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Retourne l'état des pinces
	 * @return true si les pinces sont ouvertes, faux sinon
	 */
	public static boolean getOuvert() {
		return ouvert;
	}
	
	/**
	 * Permet d'ouvrir les pinces.
	 * @throws OuvertureException Si les pinces sont déjà ouvertes
	 */
	public static void ouvrir() throws OuvertureException {
		if(ouvert) {
			throw new OuvertureException("Pinces deja ouvertes.");
		}
		else {
			Moteur.MOTEUR_PINCE.setSpeed(36000);
			Moteur.MOTEUR_PINCE.forward();
			ouvert = true;
			saveState();
			Delay.msDelay(OUVERTURE);
			Moteur.MOTEUR_PINCE.stop();
		}
	}
	
	/**
	 * Permet de fermer les pinces 
	 * @throws OuvertureException Si les pinces sont déjà fermées
	 */
	public static void fermer() throws OuvertureException {
		if(!ouvert) {
			//TO DO
			//envoyer message d'erreur ?
			throw new OuvertureException("Pinces deja fermees.");
		}
		else {
			Moteur.MOTEUR_PINCE.backward();
			ouvert = false;
			saveState();
			Delay.msDelay((long)(OUVERTURE*1.5));
			Moteur.MOTEUR_PINCE.stop();
			
		}
	}
	
	
	/**
	 * Permet d'ouvrir les pinces sans bloquer l'appelant (sans attendre que les pinces soient totalement ouvertes)
	 * @param delai temps maximal avant de retourner la main à l'appelant
	 * @throws OuvertureException si les pinces sont déjà ouvertes
	 */
	public static void ouvrir(int delai) throws OuvertureException {
		if(ouvert) {
			//envoyer message d'erreur ?
			throw new OuvertureException("Pinces deja ouvertes.");
		}
		else {
			t = new Thread(new Runnable() { 
				public void run() {
					Moteur.MOTEUR_PINCE.forward();
					ouvert = true;
					saveState();
					Delay.msDelay(OUVERTURE);
					Moteur.MOTEUR_PINCE.stop();
				}
			});
			t.start();
			Delay.msDelay(delai);
		}
	}
	
	/**
	 * Permet de fermer les pinces sans bloquer l'appelant (sans attendre que les pinces soient totalement fermées)
	 * @param delai temps maximal avant de retourner la main à l'appelant
	 * @throws OuvertureException si les pinces sont déjà fermées
	 */
	public static void  fermer(int delai) throws OuvertureException {
		if(!ouvert) {
			//TO DO
			//envoyer message d'erreur ?
			throw new OuvertureException("Pinces deja fermees.");
		}
			
		else {
			t = new Thread(new Runnable() { 
				public void run() {
					Moteur.MOTEUR_PINCE.backward();
					ouvert = false;
					saveState();
					Delay.msDelay((long)(OUVERTURE*1.5));
					Moteur.MOTEUR_PINCE.stop();
				}
			});
			t.start();
			Delay.msDelay(delai);
		}
	}
	
	
}


class TestPince{
	public static void main(String[] args) {
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