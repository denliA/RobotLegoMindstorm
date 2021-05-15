package tests;
import capteurs.Couleur;

import capteurs.Capteur;
import capteurs.Toucher;
import carte.Carte;
import carte.Robot;
import interfaceEmbarquee.Configurations;
import interfaceEmbarquee.Picker;
import moteurs.Pilote;
import moteurs.Pince;

/**
 * <p>Situation initiale : le robot est déposé sur une ligne de couleur de la table. Les intersections de départ et d'arrivée sont précisées</p>
 * <p>Situation finale : le robot dépose le palet derrière la ligne blanche spécifiée par l'utilisateur et il ouvre ses pinces.</p>
 * <p>Ce test est réalisé en suivant des lignes de couleur.</p>
 * @see Couleur
 */
public class NFA5 implements interfaceEmbarquee.Lancable{
	Carte carte = Carte.carteUsuelle;
	Robot robot = carte.getRobot();

	public void lancer() {
		new Capteur(); Couleur.startScanAtRate(0); Toucher.startScan();
		
		new Picker("Colonne départ?", Configurations.departX, true).lancer();
		new Picker("Ligne départ ?", Configurations.departY, true).lancer();
		new Picker("Direction ?", Configurations.departD, true).lancer();
		new Picker("Colonne palet?", Configurations.arriveeX, true).lancer();
		new Picker("Ligne palet?", Configurations.arriveeY, true).lancer();
		new Picker("Camp adverse ?", Configurations.campAdverse, true).lancer();
		
		robot.setPosition(Float.parseFloat(Configurations.departX.getVal()), Float.parseFloat(Configurations.departY.getVal()));
		robot.setDirection((Configurations.departD.getVal() == "porte" ? 90 : 270));
		
		Pilote.allerVersPoint(Float.parseFloat(Configurations.arriveeX.getVal()), Float.parseFloat(Configurations.arriveeY.getVal()));
		Pince.fermer();
		Pilote.rentrer(Configurations.campAdverse.getVal(), false);
		Pince.ouvrir();
		
	}
	
	public String getTitre() {
		return "NFA5 - Ramener palet position connue";
	}
	
}