package moteurs;

import java.util.Vector;

import javax.swing.event.ListSelectionEvent;

import capteurs.Capteur;
import capteurs.Couleur;
import capteurs.CouleurLigne;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.Sounds;
import lejos.robotics.chassis.WheeledChassis;
import lejos.utility.Delay;

public class TestPilote {
	static WheeledChassis chassis = MouvementsBasiques.chassis;
	public static void main(String args[]) throws Exception {
		new Capteur();
		Couleur.startScanAtRate(0); //commence le scan de la couleur immediatement. Quand une tache est finie, une autre est relancée sans délai.
		MouvementsBasiques.chassis.setLinearAcceleration(10);
		MouvementsBasiques.chassis.setLinearSpeed(20);
		//testSeRedresserSurLigne(CouleurLigne.JAUNE);

		
		//MouvementsBasiques.setAccelerationRobot(MouvementsBasiques.getAccelerationRobot()/5);
		//MouvementsBasiques.setVitesseRobot(MouvementsBasiques.getVitesseRobot()/2);
		testSeRedresserSurLigne(CouleurLigne.JAUNE) ;
		//testSuivreLigne(CouleurLigne.JAUNE);
		//testPID(CouleurLigne.JAUNE, 25);
		//testSuivreLigne(CouleurLigne.JAUNE);
		//Pilote.tournerJusqua(CouleurLigne.JAUNE, true, 100, 0,90);
		
		
		
		//testPID(CouleurLigne.VERTE);
		
		//chassisIntensif();
	
		
		//Pilote.chercheLigne(CouleurLigne.JAUNE, 25, 30, 180);
		
//		Vector <CouleurLigne> couleurs = new Vector <CouleurLigne>();
//		couleurs.add(CouleurLigne.ROUGE);
//		couleurs.add(CouleurLigne.NOIRE);
//		couleurs.add(CouleurLigne.JAUNE);
//		Pilote.chercheLigne(couleurs, 25, 30, 180);
	}
	
	
	public static void chassisIntensif() {
		while(true) {
			Pilote.tournerJusqua(CouleurLigne.JAUNE, true,360);
			Button.waitForAnyEvent(); Button.waitForAnyEvent();
//			//chassis.waitComplete();
//			while(chassis.isMoving());
//			chassis.arc(0, 180);
//			//chassis.waitComplete();
//			while(chassis.isMoving());
		}
//		Delay.msDelay(1500);
//		chassis.stop();
//		chassis.travel(Float.POSITIVE_INFINITY);
//		Delay.msDelay(1500);
		
	}
	
	public static void testSeRedresserSurLigne(CouleurLigne ligne) {
		while(Button.ESCAPE.isUp()) {
			int button = Button.waitForAnyPress();
			if (button == Button.ID_LEFT || button == Button.ID_RIGHT) {
				try {
					Pilote.seRedresserSurLigne(ligne, button==Button.ID_LEFT, 90, 90);
				}
				catch (Exception e) {
					Sound.beep();
				}
			}
		}
	}
	
//	public static void testPID(final CouleurLigne c, final float vitesse) {
//		new Thread(new Runnable() { public void run() {Pilote.suivreLignePID(c, vitesse);}}).run();
//		long debut = System.currentTimeMillis();
//		while(System.currentTimeMillis() - debut < 5000 && Button.ENTER.isUp());
//		Pilote.SetSeDeplace(false);
//		MouvementsBasiques.pilot.stop();
//	}
	public static void testPID(final CouleurLigne c) {
		//new Thread(new Runnable() { public void run() {Pilote.suivreLignePID(c);}}).run();
		long debut = System.currentTimeMillis();
		while(System.currentTimeMillis() - debut < 5000 && Button.ENTER.isUp());
		Pilote.SetSeDeplace(false);
		MouvementsBasiques.chassis.stop();
	}
	
	public static void testSuivreLigne(CouleurLigne c) {
		Pilote.suivreLigne(c);
	}
}
