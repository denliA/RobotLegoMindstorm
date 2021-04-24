package tests;
import capteurs.Couleur;
import capteurs.PaletUltrason;
import capteurs.Toucher;
import capteurs.Ultrason;
import carte.Carte;
import exceptions.OuvertureException;
import moteurs.MouvementsBasiques;
import moteurs.Pilote;
import moteurs.Pince;

/**
 * <p>
 * 		<ol>
 * 			<li>Un palet est déposé au hasard n'importe où sur la table, excepté sur une des 9 intersections de la table et excepté sur une ligne</li>
 * 			<li>Le robot est déposé au hasard n'importe où sur la table, excepté sur une ligne.</li>
 * 		</ol>
 * </p>
 * 
 * <p>
 * Le robot franchit une des deux lignes blanches avec le palet, s'arrête et ouvre ses pinces.
 * </p>
 * @see Ultrason
 * 
 */

public class P6 implements interfaceEmbarquee.Lancable{
	
	public void lancer() {
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
						//Deposer le palet derriere la ligne blanche
						try {
							Pince.fermer();
						}
						catch(OuvertureException e) {
							;
						}
						//On redresse le robot de maniere a ce qu'il soit face au camp ou il souhaite aller
						System.out.println("debut angle \t\t" + "Direction : " + Carte.carteUsuelle.getRobot().getDirection() + "\tAngle fait : " + PaletUltrason.getAngle() + " A faire : " + (90-(Carte.carteUsuelle.getRobot().getDirection()+PaletUltrason.getAngle())%360));
						MouvementsBasiques.chassis.rotate(90-(Carte.carteUsuelle.getRobot().getDirection()+PaletUltrason.getAngle())%360); MouvementsBasiques.chassis.waitComplete();
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
			System.out.println("debut angle \t\t" + "Direction : " + Carte.carteUsuelle.getRobot().getDirection() + "\tAngle fait : " + PaletUltrason.getAngle() + " A faire : " + (90-(Carte.carteUsuelle.getRobot().getDirection()+PaletUltrason.getAngle())%360));
			MouvementsBasiques.chassis.rotate(90-(Carte.carteUsuelle.getRobot().getDirection()+PaletUltrason.getAngle())%360); MouvementsBasiques.chassis.waitComplete();
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
		return "P6";
	}
	
}
