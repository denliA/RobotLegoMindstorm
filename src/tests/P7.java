package tests;

import capteurs.Couleur;
import capteurs.PaletUltrason;
import capteurs.Toucher;
import capteurs.Ultrason;
import carte.Carte;
import exceptions.OuvertureException;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import moteurs.MouvementsBasiques;
import moteurs.Pilote;
import moteurs.Pince;

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
 * @see Couleur
 * @see Toucher
 * @see Ultrason
 * 
 */

public class P7 implements interfaceEmbarquee.Lancable{
	
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
		//Ouvrir les pinces pour mieux calibrer (les pinces fermees le poids en avant donne un risque de leger bascule qui fausse la mesure (rarement)
		//De plus on peut trouver le palet et l'entrainer avec nous sans le faire expr�s, nous v�rifions cela en allant en (0,0) plus bas.
		try {
			Pince.ouvrir();
		}
		//Rien a faire en cas d'exception (car cela signifie que les pinces sont deja ouvertes), on continue programme
		catch(OuvertureException e) {
			;
		}
		//Se reperer sur le terrain
		Carte.carteUsuelle.calibrerPosition();
		//Aller au centre du terrain
		Pilote.allerVersPoint(0, 0);
		//V�rification de la pr�sence ou non d'un palet entraine par les pinces du robot :
		boolean done = false;
		try {
			Pince.fermer();
			Pince.ouvrir();
			done = PaletUltrason.verif();
		}
		catch(OuvertureException Pourquoiiiiii) {
			System.out.println("???????????");
		}
		if(!done) {
			//Aller chercher le palet
			int palet = PaletUltrason.dichotomique(1);
			System.out.println("palet : "+palet);
			boolean succes = false;
			//gestion du vide : cas ou le robot capte le mur
			do{
				switch(palet) {
				//on a trouve un palet
				case 0:
					//On verifie que le palet est bien la
					//fermeture puis reouverture des pinces necessaires avant de lancer la methode verif (permet d'aligner le palet avec le capteur contact)
					try {
						Pince.fermer();
					}
					catch(OuvertureException e) {
						;
					}
					try {
						Pince.ouvrir();
					}
					catch(OuvertureException e) {
						;
					}
					//si on a bien un palet 
					if(PaletUltrason.verif()) {
						interfaceEmbarquee.Musique.startMusic("TargetAcquired.wav");
						//Deposer le palet derriere la ligne blanche
						try {
							Pince.fermer();
						}
						catch(OuvertureException e) {
							;
						}
						//On redresse le robot de maniere a ce qu'il soit face au camp ou il souhaite aller
						System.out.println("debut angle \t\t" + "Direction : " + Carte.carteUsuelle.getRobot().getDirection() + "\tAngle fait : " + PaletUltrason.getAngle() + " A faire : " + (angleCamp-(Carte.carteUsuelle.getRobot().getDirection()+PaletUltrason.getAngle())%360));
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
					}
					//si faux-positif : on repart dans la boucle 
					else {
						MouvementsBasiques.chassis.travel(-(PaletUltrason.getDistance()*100));MouvementsBasiques.chassis.waitComplete();
						MouvementsBasiques.chassis.rotate(-(PaletUltrason.getAngle()));MouvementsBasiques.chassis.waitComplete();
						palet = PaletUltrason.dichotomique(1);
					}
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
					boolean found = Pilote.tournerJusqua(capteurs.CouleurLigne.NOIRE, true, 50, 10, 30);
					if(!found) {
						MouvementsBasiques.chassis.travel(5);MouvementsBasiques.chassis.waitComplete();
					}
					float y = Carte.carteUsuelle.getRobot().getPosition().getY();
					if(y == 0) {
						Pilote.allerVersPoint(0, 1);
					}
					else if(y == 1){
						Pilote.allerVersPoint(0, -1);
					}
					else if(y == -1) {
						Pilote.allerVersPoint(0, 0);
					}
					MouvementsBasiques.chassis.travel(5);
	//				MouvementsBasiques.chassis.travel(60); MouvementsBasiques.chassis.waitComplete();
					
					palet = PaletUltrason.dichotomique(1);
				}
				
			}while(!succes);
		}
		else {
			try {
				Pince.fermer();
			}
			catch(OuvertureException e) {
				;
			}
			//On redresse le robot de maniere a ce qu'il soit face au camp ou il souhaite aller
			System.out.println("debut angle \t\t" + "Direction : " + Carte.carteUsuelle.getRobot().getDirection() + "\tAngle fait : " + PaletUltrason.getAngle() + " A faire : " + (angleCamp-(Carte.carteUsuelle.getRobot().getDirection()+PaletUltrason.getAngle())%360));
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
		}
		System.out.println("Sorti");
		Couleur.stopScan();
		Toucher.stopScan();
		
	}
	
	public String getTitre() {
		return "P7";
	}
	
}
