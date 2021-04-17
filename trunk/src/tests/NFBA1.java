package tests;

import capteurs.Capteur;
import capteurs.Couleur;
import moteurs.MouvementsBasiques;

public class NFBA1 implements interfaceEmbarquee.Lancable{
	
	public void lancer() {
		new Capteur();
		Couleur.startScanAtRate(0);
		moteurs.MouvementsBasiques.chassis.travel(Double.POSITIVE_INFINITY);
		Couleur.blacheTouchee();
		while(!Couleur.blacheTouchee());
		MouvementsBasiques.chassis.stop();
		MouvementsBasiques.chassis.waitComplete();
	}
	
	public String getTitre() {
		return "NFBA1 - Avancer tout droit";
	}
	
}