package tests;

import capteurs.Capteur;
import capteurs.Couleur;
import moteurs.MouvementsBasiques;

/**
 * <p>Situation initiale : le robot est déposé sur le début d'une ligne de couleur</p>
 * <p>Situation finale : le robot avance tout droit jusqu'à la ligne blanche adverse et s'arrête.</p>
 * @see Couleur
 * @see MouvementsBasiques#chassis
 */

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