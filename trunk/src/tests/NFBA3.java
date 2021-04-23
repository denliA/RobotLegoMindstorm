package tests;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import moteurs.MouvementsBasiques;
import moteurs.Pilote;
import moteurs.Pince;

import java.util.Vector;

import capteurs.*;
import carte.Carte;
import carte.Ligne;
import exceptions.OuvertureException;

/**
 * <p>Situation initiale :
 * 		<ul>
 * 			<li>le robot est déposé au début d'une ligne de couleur</li>
 * 			<li>le palet est déposé sur une des trois intersections de la ligne de départ</li>
 * 		</ul>
 * </p>
 * <p>Situation finale : le robot ramène le palet derrière la ligne blanche spécifiée par l'utilisateur et reculer de 5 cm.</p>
 * @see MouvementsBasiques#chassis
 * @see capteurs#Couleur
 */

public class NFBA3 implements interfaceEmbarquee.Lancable{
	
	public void lancer() {
		//Partie 1 : choix du camp par l'utilisateur
		int button = -1;
		LCD.clear();
		LCD.drawString("Porte ou fenetre?", 3, 1);
		LCD.drawString("Porte <<  >> Fenetre", 1, 3);
		String camp = null;
		
		while((button!=Button.ID_LEFT)&&(button!=Button.ID_RIGHT)) {
			button = Button.waitForAnyPress();
		}
		if (button == Button.ID_LEFT) {
			camp = "PORTE";
		}
		else if (button == Button.ID_RIGHT) {
			camp = "FENETRE";
		}

		//Partie 2 : attraper le palet quand il le touche en suivant la ligne
		new capteurs.Capteur();
		Couleur.startScanAtRate(0);
		Toucher.startScan();
		Vector<CouleurLigne> tab = new Vector<CouleurLigne>(2);
		int nbInter=0;
		CouleurLigne cc;
		CouleurLigne couleur = Couleur.getLastCouleur();
		Pilote.suivreLigne(couleur);
		while(!Toucher.getTouche()) {
			if((cc = Couleur.getLastCouleur())!=couleur&&cc!=CouleurLigne.GRIS&&nbInter<2) {
				tab.add(cc);
				nbInter++;
			}
		}
		try {
			Pince.fermer();
		}
		catch(OuvertureException pain) {
			;
		}
		
		//Partie 3 : retrouver le bon camp
		//Si c'est une ligne horizontale : retrouver une ligne verticale sans faire tomber le palet dans le vide
		
		//cas particulier de la noire verticale :
		if(tab.size()!=0&&couleur==CouleurLigne.NOIRE) {
			couleur = CouleurLigne.NOIREV;
		}
		else {
			couleur = CouleurLigne.NOIREH;
		}
		
		//Si la ligne est verticale :
		if(!Ligne.hashLignes.get(couleur).getDirection()) {
			while(tab.size()!=tab.capacity()) {
				if((cc = Couleur.getLastCouleur())!=couleur&&cc!=CouleurLigne.GRIS&&nbInter<2) {
					tab.add(cc);
					nbInter++;
				}
			}
			if(tab.size()==tab.capacity()) {
				Ligne.Etat etat = Ligne.hashPerdu.get(new Ligne.LCC(Ligne.hashLignes.get(couleur), tab.get(0),tab.get(1)));
				Carte.carteUsuelle.getRobot().setPosition(etat.position.getX(),etat.position.getY());
				Carte.carteUsuelle.getRobot().setDirection(etat.direction);
			}
		}
		
		//Si la ligne est horizontale :
		else {
			Pilote.SetSeDeplace(false);
			MouvementsBasiques.chassis.rotate(90);MouvementsBasiques.chassis.waitComplete();
			couleur = Couleur.getLastCouleur();
			Pilote.seRedresserSurLigne(couleur, false, 5, 50);
			Carte.carteUsuelle.calibrerPosition();
		}
		Pilote.rentrer(camp);
		try {
			Pince.ouvrir();
		}
		catch(OuvertureException aAAAAAAAAAAAAA) {
			;
		}
	}
	
	public String getTitre() {
		return "NFBA3 - Ramener palet";
	}
	
}