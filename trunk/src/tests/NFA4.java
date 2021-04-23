package tests;
import capteurs.Couleur;

/**
 * <p>Situation initiale : le robot est déposé n'importe où sur la table</p>
 * <p>Situation finale : le robot trouve l'intersection demandée</p>
 * @see Couleur
 */

public class NFA4 implements interfaceEmbarquee.Lancable{
	
	public void lancer() {
	}
	
	public String getTitre() {
		return "NFA4 - Intersection partout";
	}
	
}