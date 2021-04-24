package tests;

import capteurs.Couleur;
import capteurs.CouleurLigne;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;


/**
 * <p>Situation initiale : le robot est déposé n'importe où sur la table</p>
 * <p>Situation finale : le robot affiche la couleur sur laquelle il est posé et quitte le programme quand l'utilisateur appuie sur "ESCAPE".</p>
 * @see Couleur
 */

public class NFBM1 implements interfaceEmbarquee.Lancable{
	
	public void lancer() {
		int button = -1;
		CouleurLigne couleur;
		Couleur.startScanAtRate(0);
		LCD.clear();
		while (button != Button.ID_ESCAPE) {
			LCD.drawString("Couleur detectee", 1, 0);
			button = Button.waitForAnyPress();
			if (button == Button.ID_ENTER) {
				LCD.clear(2,3,10);
				couleur = Couleur.getLastCouleur();
				LCD.drawString(couleur.toString(), 2, 3); 
			}
			Button.waitForAnyEvent();
		}
		Couleur.stopScan();
	}
	
	public String getTitre() {
		return "NFBM1 - Reconnaitre couleur";
	}
	
}