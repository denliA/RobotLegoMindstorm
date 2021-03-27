package moteurs;

import capteurs.Couleur;
import capteurs.CouleurLigne;
import lejos.hardware.Button;
import lejos.hardware.Sound;

public class TestPilote {
	public static void main(String args[]) throws Exception {
		Couleur.setScanMode(Couleur.RGBMODE); //precise que les scans de la couleur se feront en mode RGB
		Couleur.startScanAtRate(0); //commence le scan de la couleur immediatement. Quand une tache est finie, une autre est relancée sans délai.
		//MouvementsBasiques.setAccelerationRobot(MouvementsBasiques.getAccelerationRobot()/5);
		//MouvementsBasiques.setVitesseRobot(MouvementsBasiques.getVitesseRobot()/2);
		testSeRedresserSurLigne(CouleurLigne.JAUNE) ;
		//testSuivreLigne();
	}
	
	public static void testSeRedresserSurLigne(CouleurLigne ligne) {
		while(Button.ESCAPE.isUp()) {
			int button = Button.waitForAnyPress();
			if (button == Button.ID_LEFT || button == Button.ID_RIGHT) {
				try {
					Pilote.seRedresserSurLigne(ligne, button==Button.ID_LEFT, 90, 1000);
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
