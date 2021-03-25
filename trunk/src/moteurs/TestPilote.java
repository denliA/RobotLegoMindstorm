package moteurs;

import capteurs.Couleur;
import capteurs.CouleurLigne;
import lejos.hardware.Button;
import lejos.hardware.Sound;

public class TestPilote {
	public static void main(String args[]) throws Exception {
		MouvementsBasiques.pilot.rotate(-720);
		Couleur.setScanMode(Couleur.RGBMODE);
		Couleur.startScanAtRate(0);
		//testSeRedresserSurLigne(CouleurLigne.BLEUE) ;
		testSuivreLigne();
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
