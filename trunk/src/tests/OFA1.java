package tests;

/**
 * <p>Situation initiale : le robot est déposé n'importe où sur la table</p>
 * <p>Situation finale :
 * 		<ol>
 * 			<li>Le robot va analyser la situation dans laquelle il est puis, calibrer sa position en allant trouver une ligne proche puis, il suit cette ligne vers un croisement</li>
 * 			<li>Lorsqu’il atteint un croisement, le robot commence à afficher ses coordonnées et son orientation en temps réel</li>
 * 			<li>Le robot avance et tourne avec des angles et des distances aléatoires, tout en continuant à afficher sa position pendant 30 secondes</li>
 * 			<li>Le robot se remet sur le centre de la table, en pointant vers la direction à 0°. Il s'arrête.</li>
 * 		</ol>
 * </p>
 * @see carte
 */

public class OFA1 implements interfaceEmbarquee.Lancable{
	
	public void lancer() {
	}
	
	public String getTitre() {
		return "OFA1 - Carte virtuelle";
	}
	
}