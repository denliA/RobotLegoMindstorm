package tests;

import moteurs.MouvementsBasiques;

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
	}
	
	public String getTitre() {
		return "NFBA3 - Ramener palet";
	}
	
}