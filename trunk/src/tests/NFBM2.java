package tests;
import capteurs.Couleur;

import capteurs.Capteur;
import capteurs.Couleur;
import carte.Carte;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;

/**
 * <p>Situation initiale : le robot est déposé sur une ligne de couleur de la table</p>
 * <p>Situation finale : le robot avance jusqu'à la prochaine intersection avec une autre ligne et affiche la position du point sur lequel il se trouve.</p>
 * @see carte
 * @see Couleur
 */

public class NFBM2 implements interfaceEmbarquee.Lancable{
	Carte carte = Carte.carteUsuelle;
	public void lancer() {
		LCD.clear();
		new Capteur();
		Couleur.startScanAtRate(0);
		carte.calibrerPosition();
		LCD.drawString("Position: " + carte.getRobot().getPosition(), 3, 1);
		Button.waitForAnyPress();
	}
	
	public String getTitre() {
		return "NFBM2 - Reconnaître intersections";
	}
	
}