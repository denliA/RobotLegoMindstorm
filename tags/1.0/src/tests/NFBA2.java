package tests;

import moteurs.MouvementsBasiques;

/**
 * <p>Situation initiale : le robot est déposé sur une intersection de lignes de couleur</p>
 * <p>Situation finale : le robot tourne de 90 degrés et s'arrête quand il est perpendiculaire à la ligne de départ.</p>
 * @see MouvementsBasiques#chassis
 */

public class NFBA2 implements interfaceEmbarquee.Lancable{
	
	public void lancer() {
		moteurs.MouvementsBasiques.chassis.rotate(90);
	}
	
	public String getTitre() {
		return "NFBA2 - Faire angle droit";
	}
	
}