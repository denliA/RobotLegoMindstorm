package tests;

import capteurs.Capteur;
import capteurs.Couleur;
import capteurs.CouleurLigne;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import moteurs.MouvementsBasiques;

/**
 * <p>Situation initiale : le robot est déposé sur n’importe quelle intersection de lignes de couleurs</p>
 * <p>Situation finale : le robot réalise un carré et revient sur son intersection de départ</p>
 * @see capteurs#Couleur
 * @see MouvementsBasiques#chassis
 */

public class NFA1 implements interfaceEmbarquee.Lancable{
	
	public void lancer() {
		new Capteur();
		Couleur.startScanAtRate(0);
		
		//choisir le camp de départ
		boolean rougeAgauche = true;
		boolean gauche=false;
		boolean droite=false;
		int angle=90;
		int button = -1;
		LCD.clear();
		LCD.drawString("RougeAGauche?", 3, 1);
		LCD.drawString("vrai <<  >> faux", 1, 3);
		while((button!=Button.ID_LEFT)&&(button!=Button.ID_RIGHT)) {
			button = Button.waitForAnyPress();
		}
		if (button == Button.ID_LEFT) {
			rougeAgauche=true;
		}
		else if (button == Button.ID_RIGHT) {
			rougeAgauche=false;
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
		CouleurLigne couleur = Couleur.getLastCouleur();
		if (rougeAgauche) { //robot demarre coté armoire
			if (couleur==CouleurLigne.ROUGE)
				droite=true; //je bifurque tjrs vers la ligne de droite
			else if(couleur==CouleurLigne.NOIRE) {
				droite=true;
			}
			else if(couleur==CouleurLigne.JAUNE)
				gauche=true; //je bifurque tjrs vers la ligne de gauche
		}else { //robot demarre coté porte
			if (couleur==CouleurLigne.ROUGE)
				gauche=true; //je bifurque tjrs vers la ligne de gauche
			else if(couleur==CouleurLigne.NOIRE) {
				droite=true;
			}
			else if(couleur==CouleurLigne.JAUNE)
				droite=true; //je bifurque tjrs vers la ligne de droite
		}
		if (gauche) {
			angle=90;
		}
		else if (droite) {
			angle=-90;
		}
		MouvementsBasiques.chassis.travel(60); MouvementsBasiques.chassis.waitComplete();
		MouvementsBasiques.chassis.rotate(angle); MouvementsBasiques.chassis.waitComplete();
		MouvementsBasiques.chassis.travel(50); MouvementsBasiques.chassis.waitComplete();
		MouvementsBasiques.chassis.rotate(angle); MouvementsBasiques.chassis.waitComplete();
		MouvementsBasiques.chassis.travel(60); MouvementsBasiques.chassis.waitComplete();
		MouvementsBasiques.chassis.rotate(angle); MouvementsBasiques.chassis.waitComplete();
		MouvementsBasiques.chassis.travel(50); MouvementsBasiques.chassis.waitComplete();
		
		Couleur.stopScan();
		
	}
	
	public String getTitre() {
		return "NFA1 - Rectangle";
	}
	
}