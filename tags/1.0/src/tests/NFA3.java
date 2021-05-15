package tests;
import capteurs.Couleur;

import capteurs.Capteur;
import capteurs.Couleur;
import carte.Carte;
import moteurs.Pilote;
import interfaceEmbarquee.*;

/**
 * <p>Situation initiale : le robot est déposé sur une ligne de couleur de la table</p>
 * <p>Situation finale : le robot trouve l'intersection demandée</p>
 * @see Couleur
 */

public class NFA3 implements interfaceEmbarquee.Lancable{
	
	
	Carte carte = Carte.carteUsuelle;
	
	public void lancer() {
		new Capteur(); Couleur.startScanAtRate(0);
		
		new Picker("Colonne ?", Configurations.arriveeX, true).lancer();
		new Picker("Ligne ?", Configurations.arriveeY, true).lancer();
		
		carte.calibrerPosition();
		
		Pilote.allerVersPoint(Float.parseFloat(Configurations.arriveeX.getVal()), Float.parseFloat(Configurations.arriveeY.getVal()));
	}
	
	public String getTitre() {
		return "NFA3 - Intersection ligne";
	}
	
}