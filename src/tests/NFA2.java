package tests;

import java.util.Vector;

import capteurs.Capteur;
import capteurs.Couleur;
import capteurs.CouleurLigne;
import moteurs.Pilote;

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