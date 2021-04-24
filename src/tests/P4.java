package tests;
import capteurs.Couleur;
import capteurs.Ultrason;
import capteurs.Toucher;

import capteurs.Capteur;
import capteurs.Couleur;
import capteurs.Toucher;
import carte.*;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import moteurs.Pilote;
import moteurs.Pince;

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

public class P4 implements interfaceEmbarquee.Lancable{
	
	Carte carte = Carte.carteUsuelle;
	Robot robot = carte.getRobot();
	
	@Override	
	public void lancer() {
		
		new Capteur(); Couleur.startScanAtRate(0); Toucher.startScan();
		if(Pince.getOuvert()) Pince.fermer();
		float angle = choixDirection();
		
		carte.calibrerPosition();
		Pilote.rentrer("");
		Point p = Pilote.trouverPalet();
		if(p == Point.INCONNU) {
			System.out.println("Pas de palet trouvé!"); return;
		}
		Pilote.lancerSuivi((robot.getDirection()%180==0) ? Ligne.yToLongues.get(robot.getPosition().getY()) : Ligne.xToLongues.get(robot.getPosition().getX()));
		while(!Toucher.getTouche()) { /* rien */ }
		Pilote.arreterSuivi(); 
		Pince.fermer(500);
		Pilote.rentrer(angle);
//		ModeSolo.ramasserPalet(1, carte.getRobot().getDirection()==90); Valeur sûre mais met trop de temps.
		
	}
	
	public String getTitre() {
		return "P4";
	}
	
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
