package tests;

import java.util.Vector;

import capteurs.Couleur;
import capteurs.CouleurLigne;
import capteurs.Toucher;
import carte.Carte;
import carte.Ligne;
import exceptions.OuvertureException;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import moteurs.MouvementsBasiques;
import moteurs.Pilote;
import moteurs.Pince;

/**
 * <p>Situation initiale :
 * 		<ul>
 * 			<li>le robot est déposé au début d'une ligne de couleur</li>
 * 			<li>le palet est déposé sur une des trois intersections de la ligne de départ</li>
 * 		</ul>
 * </p>
 * <p>Situation finale : le robot ramène le palet derrière la ligne blanche spécifiée par l'utilisateur et reculer de 5 cm.</p>
 * @see MouvementsBasiques#chassis
 * @see Couleur
 */

public class NFBA3 implements interfaceEmbarquee.Lancable{
	
	public void lancer() {
		//Partie 1 : choix du camp par l'utilisateur
		int button = -1;
		LCD.clear();
		LCD.drawString("Deposer ou?", 3, 1);
		LCD.drawString("Devant \\/",3,2);
		LCD.drawString("Derriere /\\", 3, 3);
		String camp = null;
		while((button!=Button.ID_UP)&&(button!=Button.ID_DOWN)) {
			button = Button.waitForAnyPress();
		}
		if (button == Button.ID_UP) {
			camp = "derriere";
		}
		else if (button == Button.ID_DOWN) {
			camp = "devant";
		}
		
		//Partie 2 : attraper le palet quand il le touche en suivant la ligne
		new capteurs.Capteur();
		Couleur.startScanAtRate(0);
		Toucher.startScan();
		CouleurLigne couleur = Couleur.getLastCouleur();
		Pilote.lancerSuivi(couleur);
		while(!Toucher.getTouche()) {
			//on ne fait rien, le robot avance
		}
		try {
			Pince.fermer();
		}
		catch(OuvertureException pain) {
			;
		}
		
		//Partie 3 : deposer le palet dans le bon camp
		if (camp=="derriere") {
			//arreter le suivi de ligne
			Pilote.arreterSuivi();
			//demi tour
			Pilote.tournerJusqua(couleur, true,250);
			Pilote.tournerJusqua(couleur, false, 50,50);
			//lancer le suivi de ligne
			Pilote.lancerSuivi(couleur);
		}
		//reinitialiser le boolean qui indique si le blanc a été détecté
		Couleur.blacheTouchee();
		while(!Couleur.blacheTouchee()) {
			//on ne fait rien
		}
		Pilote.arreterSuivi();
		//ouvrir les pinces
		try {
			Pince.ouvrir();
		}catch(OuvertureException pain) {
			;
		}
		//recule
		MouvementsBasiques.chassis.travel(-5);
	}
	
	public String getTitre() {
		return "NFBA3 - Ramener palet";
	}
	
}