package tests;

import capteurs.Couleur;
import capteurs.CouleurLigne;
import lejos.hardware.Button;

/**
 * Situation initiale : Le robot est depose au hasard sur une des lignes de la table.
 * Situation finale : le robot indique la couleur de la ligne ou il a ete depose.
 * @see capteurs#Couleur
 */
public class P1 implements interfaceEmbarquee.Lancable{
	
	public void lancer() {
		int button = -1;
		CouleurLigne couleur;
		Couleur.setScanMode(Couleur.RGBMODE);
		Couleur.startScanAtRate(10);
		while (button != Button.ID_ESCAPE) {
			button = Button.waitForAnyPress();
			if (button == Button.ID_ENTER) {
				couleur = Couleur.getLastCouleur();
				System.out.println(couleur.toString());
			}
			Button.waitForAnyEvent();
		}
	}
	
	public String getTitre() {
		return "P1";
	}
	
}
