package moteurs;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import exceptions.OuvertureException;
import lejos.hardware.Sound;
import lejos.utility.Delay;
public class Pince {
	
	//Attribut
	//les pinces s'ouvriront toujours de la meme valeur par defaut
	private static final long OUVERTURE = 1000;//TODO d'apres EV3CONTROL l'angle pour passer de ouverture (grande) a ferme (les pinces se touchent) est d'environ 1735 donc je pense que l'on peut se base entre 1000 et 1500 pour avoir une ouverture assez grande pour les palets et ne pas perdre de temps
	private static boolean ouvert=false;
	static FileWriter outputer = null;
	static FileReader inputer = null;
	static {
		try {
			inputer = new FileReader("statutPince");
			ouvert = (inputer.read() == '1');
			inputer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
//	Methodes
	
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
	
	public static boolean getOuvert() {
		return ouvert;
	}
	
	//Ouverture et fermeture des pinces
	//methodes modifiant la valeur de ouvert simultanement
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
	
	public static void fermer() throws OuvertureException {
		if(!ouvert) {
			//TO DO
			//envoyer message d'erreur ?
			throw new OuvertureException("Pinces deja fermees.");
		}
		else {
			Moteur.MOTEUR_PINCE.setSpeed(36000);
			Moteur.MOTEUR_PINCE.backward();
			ouvert = false;
			saveState();
			Delay.msDelay((long)(OUVERTURE*1.5));
			Moteur.MOTEUR_PINCE.stop();
			
		}
	}
}


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