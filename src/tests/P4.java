package tests;
import capteurs.Couleur;
import capteurs.Ultrason;
import capteurs.Toucher;

/**
 * <p>Situation initiale :
 * 		<ol>
 * 			<li>Le camp adverse est désigné au robot : Est ou Ouest</li>
 * 			<li>Un palet est déposé au hasard sur une des 9 intersections de la table</li>
 * 			<li>Le robot est déposé au hasard n'importe où sur la table exceptée sur une ligne</li>
 * 		</ol>
 * </p>
 * 
 * <p>Situation finale : Le robot franchit la ligne blanche du camp adverse avec le palet, s'arrête et ouvre ses pinces.</p>
 * @see Couleur
 * @see Ultrason
 * @see Toucher
 */

public class P4 implements interfaceEmbarquee.Lancable{
	
	public void lancer() {
	}
	
	public String getTitre() {
		return "P4";
	}
	
}
