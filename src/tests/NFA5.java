package tests;

/**
 * <p>Situation initiale : le robot est déposé sur une ligne de couleur de la table</p>
 * <p>Situation finale : le robot dépose le palet derrière la ligne blanche spécifiée par l'utilisateur et il ouvre ses pinces.</p>
 * <p>Ce test est realisé en suivant des lignes de couleur.</p>
 * @see capteurs#Couleur
 */

public class NFA5 implements interfaceEmbarquee.Lancable{
	
	public void lancer() {
	}
	
	public String getTitre() {
		return "NFA5 - Ramener palet position connue";
	}
	
}