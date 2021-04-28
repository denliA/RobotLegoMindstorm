package moteurs;


import capteurs.Capteur;
import capteurs.Couleur;
import capteurs.CouleurLigne;
import carte.Carte;
import carte.Point;
import carte.Robot;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.robotics.chassis.WheeledChassis;

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
		//testSeRedresserSurLigne(CouleurLigne.JAUNE) ;
		//testSuivreLigne(CouleurLigne.JAUNE);
		//testPID(CouleurLigne.JAUNE, 25);
		//testSuivreLigne(CouleurLigne.JAUNE);
		//Pilote.tournerJusqua(CouleurLigne.JAUNE, true, 100, 0,90);
		//testPositions();
		//testRentrer(1, 2, 270, "fenetre", true, -1,0);
//		new tests.P3().lancer();
		//carte.Carte.carteUsuelle.calibrerPosition();
		
		
//		carte.Carte.carteUsuelle.getRobot().setPosition(1, -1);
//		carte.Carte.carteUsuelle.getRobot().setDirection(270);
//		Pilote.allerVersPoint(1, 1);
		
		//testPID(CouleurLigne.VERTE);
		
		//chassisIntensif();
	
		
		//Pilote.chercheLigne(CouleurLigne.JAUNE, 25, 30, 180);
		
//		Vector <CouleurLigne> couleurs = new Vector <CouleurLigne>();
//		couleurs.add(CouleurLigne.ROUGE);
//		couleurs.add(CouleurLigne.NOIRE);
//		couleurs.add(CouleurLigne.JAUNE);
//		Pilote.chercheLigne(couleurs, 25, 30, 180);
		
		
//		testVerifierPalet(1,1,90);
//		testTrouverPalet(1,-2,90);
//		testTrouverPalet(0,-2,90);
//		testTrouverPalet(1,0,270);
//		System.out.println(Pilote.chercherPosition());
		
//		Pilote.lancerSuivi(CouleurLigne.JAUNE);
//		Delay.msDelay(5000);
//		Pilote.arreterSuivi();
//		Delay.msDelay(100);
//		Couleur.buffer.toCSV("./CouleursScannees");
		
		
		carte.Carte.carteUsuelle.calibrerPosition();
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
					Pilote.seRedresserSurLigne(ligne, button==Button.ID_LEFT, 90, 180);
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
	
	public static void testPositions() {
		Carte c = carte.Carte.carteUsuelle;
		Robot r = c.getRobot();
		System.out.println("Avant calibration : " + r);
		c.calibrerPosition();
		System.out.println("Après calibration : "+r);
	}
	
	public static void testRentrer(int x, int y, int dir, String direction, boolean avancee, int xf, int yf) {
		new Capteur();
		Couleur.startScanAtRate(0);
		Carte c = carte.Carte.carteUsuelle;
		Robot r = c.getRobot();
		System.out.println("Avant : " + r);
		r.setPosition(x, y);
		r.setDirection(270);
		if(!avancee)
			Pilote.rentrer(direction);
		else
			Pilote.allerVersPoint(xf, yf);
		System.out.println("Après : " + r);
		
	}
	
	public static void testVerifierPalet(float x, float y, float direction) {
		new Capteur();
		Couleur.startScanAtRate(0);
		Carte c = carte.Carte.carteUsuelle;
		Robot r = c.getRobot();
		r.setDirection(direction);  r.setPosition(x, y);
		Point P = Pilote.verifierPalet();
		System.out.println("Point trouvé : "  + P);
		System.out.println("Direction :" + r.getDirection());
		
	}
	
	public static void testTrouverPalet(float x, float y, float direction) {
		new Capteur();
		Couleur.startScanAtRate(0);
		Carte c = carte.Carte.carteUsuelle;
		Robot r = c.getRobot();
		r.setDirection(direction);  r.setPosition(x, y);
		Point P = Pilote.trouverPalet();
		System.out.println("[testTrouverPalet]  point trouvé : "+P);
	}
	
}
