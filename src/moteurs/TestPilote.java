package moteurs;

import capteurs.Capteur;
import capteurs.Couleur;
import capteurs.CouleurLigne;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.utility.Delay;

public class TestPilote {
	public static void main(String args[]) throws Exception {
		try{
			new capteurs.Capteur();
			}catch(IllegalArgumentException e){
				Sound.beepSequenceUp();
				System.err.println(e.toString());
				e.printStackTrace(System.err);
				Capteur.ouvrirCapteurCouleur();
			}
		Couleur.setScanMode(Couleur.RGBMODE); //precise que les scans de la couleur se feront en mode RGB
		Couleur.startScanAtRate(0); //commence le scan de la couleur immediatement. Quand une tache est finie, une autre est relancée sans délai.
//		Pilote.startVideAtRate(0); //commence de tester si le robot detecte du vide. Quand une tache est finie, une autre est relancée sans délai.
//		MouvementsBasiques.setAccelerationRobot(20);
//		MouvementsBasiques.setVitesseRobot(15);
		
		//MouvementsBasiques.avancerTravel(90, true);
		//Delay.msDelay(10000);
		//il faut que le robot rencontre du vide
		
		
		MouvementsBasiques.setAccelerationRobot(MouvementsBasiques.getAccelerationRobot()/5);
		MouvementsBasiques.setVitesseRobot(MouvementsBasiques.getVitesseRobot()/2);
		testSeRedresserSurLigne(CouleurLigne.JAUNE) ;
		//testSuivreLigne();
	}
	
	public static void testSeRedresserSurLigne(CouleurLigne ligne) {
		while(Button.ESCAPE.isUp()) {
			int button = Button.waitForAnyPress();
			if (button == Button.ID_LEFT || button == Button.ID_RIGHT) {
				try {
					Pilote.seRedresserSurLigne(ligne, button==Button.ID_LEFT, 90, 1500);
				}
				catch (Exception e) {
					Sound.beep();
				}
			}
		}
	}
	
	public static void testSuivreLigne() {
		Pilote.suivreLigne();
	}
}
