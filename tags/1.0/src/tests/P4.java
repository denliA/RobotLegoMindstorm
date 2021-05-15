package tests;
import capteurs.Couleur;
import capteurs.Ultrason;
import capteurs.Toucher;

import carte.*;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;

/**
 * <p>Situation initiale :
 * 		<ol>
 * 			<li>Le camp adverse est désigné au robot : Est ou Ouest</li>
 * 			<li>Un palet est déposé au hasard sur une des 9 intersections de la table</li>
 * 			<li>Le robot est déposé au hasard n'importe où sur la table exceptée sur une ligne</li>
 * 		</ol>
 * </p>
 * 
 * <p>Situation finale : Le robot franchit la ligne blanche du camp adverse avec le palet, s'arrête et ouvre ses pinces.</p>
 * @see Couleur
 * @see Ultrason
 * @see Toucher
 */

public class P4 extends P3 {
	
	Carte carte = Carte.carteUsuelle;
	Robot robot = carte.getRobot();
	
	
	public String getTitre() {
		return "P4";
	}
	
	@Override
	public float choixDirection() {
		int button = -1;
		LCD.clear();
		LCD.drawString("Porte ou fenêtre?", 3, 1);
		LCD.drawString("Porte <<  >> Fenêtre", 1, 3);
		int angleCamp = 90;
		//Choix du camp ou deposer le palet
		while((button!=Button.ID_LEFT)&&(button!=Button.ID_RIGHT)) {
			button = Button.waitForAnyPress();
		}
		if (button == Button.ID_LEFT) {
			angleCamp = 90;
		}
		else if (button == Button.ID_RIGHT) {
			angleCamp = 270;
		}
		return angleCamp;
	}
	
}
