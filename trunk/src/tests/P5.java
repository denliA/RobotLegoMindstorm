package tests;

import exceptions.OuvertureException;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import modeCompetition.ModeCompetition;

/**
 * <p>Situation initiale :
 * 		<ol>
 * 			<li>Le camp adverse est désigné au robot : Est ou Ouest</li>
 * 			<li>Les 9 palets sont déposés sur les 9 intersections de la table</li>
 * 			<li>Le robot est déposé au hasard n'importe sur une des 3 positions départ du camp qui lui a été désignée</li>
 * 		</ol>
 * </p>
 * 
 * <p>Situation finale : Le robot dépose chacun des palets derrière la ligne blanche du camp adverse puis s'arrête et ouvre ses pinces.</p>
 * @see ModeCompetition
 */

public class P5 implements interfaceEmbarquee.Lancable {
	
	public void lancer() {
		//choisir le camp de départ
		boolean camp = true;
		int button = -1;
		LCD.clear();
		LCD.drawString("RougeAGauche?", 3, 1);
		LCD.drawString("vrai <<  >> faux", 1, 3);
		while((button!=Button.ID_LEFT)&&(button!=Button.ID_RIGHT)) {
			button = Button.waitForAnyPress();
		}
		if (button == Button.ID_LEFT) {
			camp=true;
		}
		else if (button == Button.ID_RIGHT) {
			camp=false;
		}
		//placer le robot sur une des 6 positions de depart
		LCD.clear();
		LCD.drawString("poser robot sur 1", 1, 1);
		LCD.drawString("position de depart", 1, 2);
		LCD.drawString("pressez sur entree", 1, 5);
		LCD.drawString("pour demarrer", 1, 6);
		while(button!=Button.ID_ENTER) {
			button = Button.waitForAnyPress();
		}
		//appeler la fonction a executer
		try {
			modeCompetition.ModeCompetition.ramasserPalet(9, camp);
		} catch (OuvertureException e) {
			System.out.println("Prob pour ouvrir pince");
			e.printStackTrace();
		}
	}
	
	public String getTitre() {
		return "P5";
	}
}
