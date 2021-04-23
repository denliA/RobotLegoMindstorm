package tests;

import capteurs.Couleur;
import capteurs.CouleurLigne;
import lejos.hardware.Button;

/**
 * <p>Situation initiale : Le robot est depose au hasard sur une des lignes de la table.</p>
 * <p>Situation finale : le robot indique la couleur de la ligne ou il a été déposé.</p>
 * <p>Scénario répété avec des conditions d'éclairage différentes.</p>
 * @see Couleur
 */

public class P1 implements interfaceEmbarquee.Lancable{
	
	public void lancer() {
		int button = -1;
		CouleurLigne couleur;
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
		return "P1";
	}
	
}
