package tests;

import capteurs.Couleur;
import capteurs.PaletUltrason;
import carte.Carte;
import exceptions.OuvertureException;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import moteurs.MouvementsBasiques;
import moteurs.Pilote;
import moteurs.Pince;

public class P7 implements interfaceEmbarquee.Lancable{
	/**
	 * <p>
	 * 		<ol>
	 * 			<li>Le camp adverse est désigné au robot : Est ou Ouest</li>
	 * 			<li>Un palet est déposé au hasard n'importe où sur la table, excepté sur une des 9 intersections de la table et excepté sur une ligne</li>
	 * 			<li>Le robot est déposé au hasard n'importe où sur la table, excepté sur une ligne</li>
	 * 		</ol>
	 * </p>
	 * 
	 * <p>
	 * Le robot franchit la ligne blanche du camp adverse avec le palet, s'arrête et ouvre ses pinces.
	 * </p>
	 * @see capteurs
	 * 
	 */
	
	public void lancer() {
		int button = -1;
		LCD.clear();
		LCD.drawString("Porte ou fen�tre?", 3, 1);
		LCD.drawString("Porte <<  >> Fen�tre", 1, 3);
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
		new capteurs.Capteur();
		capteurs.Couleur.startScanAtRate(0);
		//Fermer les pinces pour eviter d'entrainer le palet avec le robot.
		try {
			Pince.fermer();
		}
		//Rien a faire en cas d'exception (car cela signifie que les pinces sont deja fermees), on continue programme
		catch(OuvertureException e) {
			;
		}
		//Se reperer sur le terrain
		Carte.carteUsuelle.calibrerPosition();
		//Aller au centre du terrain
		Pilote.allerVersPoint(0, 0);
		//Aller chercher le palet
		try {
			Pince.ouvrir();
		}
		catch(OuvertureException e) {
			;
		}
		int palet = PaletUltrason.dichotomique(1);
		System.out.println("palet : "+palet);
		boolean succes = false;
		//gestion du vide : cas ou le robot capte le mur
		do{
			switch(palet) {
			case 0:
				//Deposer le palet derriere la ligne blanche
				try {
					Pince.fermer();
				}
				catch(OuvertureException e) {
					;
				}
				//On redresse le robot de maniere a ce qu'il soit face au camp ou il souhaite aller
				System.out.println("debut angle");
				MouvementsBasiques.chassis.rotate(angleCamp-(Carte.carteUsuelle.getRobot().getDirection()+PaletUltrason.getAngle())%360); MouvementsBasiques.chassis.waitComplete();
				System.out.println("fin angle");
				MouvementsBasiques.chassis.travel(Float.POSITIVE_INFINITY);
				System.out.println("Avance");
				Couleur.blacheTouchee();
				while(!Couleur.blacheTouchee()) {
					;
				}
				MouvementsBasiques.chassis.stop();
				try{
					Pince.ouvrir();
				}
				catch(OuvertureException e) {
					;
				}
				succes = true;
				break;
			//vide
			case 1:
				MouvementsBasiques.chassis.setLinearAcceleration(200);
				MouvementsBasiques.chassis.stop();
				//il, le robot, retourne ou il etait (son point de depart, le centre de la table)
				MouvementsBasiques.chassis.travel(-PaletUltrason.getDistance()); MouvementsBasiques.chassis.waitComplete();
				MouvementsBasiques.chassis.rotate(-PaletUltrason.getAngle()+180);MouvementsBasiques.chassis.waitComplete();
				palet = PaletUltrason.dichotomique(1);
				break;
			//gestion du "pas de palet" : cas ou le robot est trop proche du palet pour le voir avec le capteur ultrason
			case 2:
				try {
					Pince.fermer();
				}
				catch(OuvertureException e) {
					;
				}
				MouvementsBasiques.chassis.travel(60); MouvementsBasiques.chassis.waitComplete();
				try {
					Pince.ouvrir();
				}
				catch(OuvertureException e) {
					;
				}
				palet = PaletUltrason.dichotomique(1);
			}
			
		}while(!succes);
		
	}
	
	public String getTitre() {
		return "P7";
	}
	
}
