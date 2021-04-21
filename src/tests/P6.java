package tests;

import capteurs.Couleur;
import capteurs.PaletUltrason;
import carte.Carte;
import exceptions.OuvertureException;
import moteurs.MouvementsBasiques;
import moteurs.Pilote;
import moteurs.Pince;

public class P6 implements interfaceEmbarquee.Lancable{
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
	 * @see capteurs#Ultrason
	 * 
	 */
	public void lancer() {
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
		System.out.println(palet);
		String camp = null;
		int angleCamp = 90;
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
				if(PaletUltrason.getAngle()<=180) {
					camp = "PORTE";
				}
				else {
					angleCamp = 270;
					camp = "FENETRE";
				}
				//On redresse le robot de maniere a ce qu'il soit face au camp ou il souhaite aller
				MouvementsBasiques.chassis.rotate(angleCamp-(Carte.carteUsuelle.getRobot().getDirection()+PaletUltrason.getAngle())%360);
				MouvementsBasiques.chassis.travel(Float.POSITIVE_INFINITY);
				while(Couleur.blacheTouchee()) {
					;
				}
				MouvementsBasiques.chassis.stop();
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
			//rieng
			case 2:
				try {
					Pince.fermer();
				}
				catch(OuvertureException e) {
					;
				}
				Pilote.allerVersPoint(1, 0);
				try {
					Pince.ouvrir();
				}
				catch(OuvertureException e) {
					;
				}
				palet = PaletUltrason.dichotomique(1);
			}
			
		}while(!succes);
		//gestion du "pas de palet" : cas ou le robot est trop proche du palet pour le voir avec le capteur ultrason
		
	}
	
	public String getTitre() {
		return "P6";
	}
	
}
