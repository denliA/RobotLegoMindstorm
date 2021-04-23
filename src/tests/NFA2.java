package tests;

import java.util.Vector;
import capteurs.Capteur;
import capteurs.Couleur;
import capteurs.CouleurLigne;
import moteurs.Pilote;

/**
 * <p>Situation initiale : le robot est déposé sur une zone grise de la table</p>
 * <p>Situation finale : le robot se gare sur une ligne de couleur</p>
 * @see Couleur
 */

public class NFA2 implements interfaceEmbarquee.Lancable{
	
	public void lancer() {
		new Capteur();
		Couleur.startScanAtRate(0);
		Vector <CouleurLigne> couleurs = new Vector <CouleurLigne>();
		couleurs.add(CouleurLigne.ROUGE);
		couleurs.add(CouleurLigne.NOIRE);
		couleurs.add(CouleurLigne.JAUNE);
		couleurs.add(CouleurLigne.BLEUE);
		couleurs.add(CouleurLigne.VERTE);
		couleurs.add(CouleurLigne.BLANCHE);
		Pilote.chercheLigne(couleurs, 25, 30, 180, true);
		Couleur.stopScan();
	}
	
	public String getTitre() {
		return "NFA2 - Creneau";
	}
	
}