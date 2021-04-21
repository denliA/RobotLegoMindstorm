package tests;

import capteurs.Couleur;
import capteurs.CouleurLigne;
import lejos.hardware.Button;


/**
 * <p>Situation initiale : le robot est déposé n'importe où sur la table</p>
 * <p>Situation finale : le robot affiche la couleur sur laquelle il est posé et quitte le programme quand l'utilisateur appuie sur "ESCAPE".</p>
 * @see capteurs#Couleur
 */

public class NFBM1 implements interfaceEmbarquee.Lancable{
	
	public void lancer() {
		int button = -1;
		CouleurLigne couleur;
		Couleur.setScanMode(Couleur.RGBMODE);
		Couleur.startScanAtRate(10);
		while (button != Button.ID_ESCAPE) {
			button = Button.waitForAnyPress();
			if (button == Button.ID_ENTER) {
				couleur = Couleur.getLastCouleur();
				System.out.println(couleur);
			}
			Button.waitForAnyEvent();
		}
		Couleur.stopScan();
	}
	
	public String getTitre() {
		return "NFBM1 - Reconnaitre couleur";
	}
	
}