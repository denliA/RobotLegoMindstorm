package tests;

/**
 * <p>Situation initiale : le robot est déposé sur une ligne de couleur de la table</p>
 * <p>Situation finale : le robot avanve jusqu'à la prochaine intersection avec une autre ligne et affiche la position du point sur lequel il se trouve.</p>
 * @see carte
 * @see capteurs#Couleur
 */

public class NFBM2 implements interfaceEmbarquee.Lancable{
	
	public void lancer() {
	}
	
	public String getTitre() {
		return "NFBM2 - Reconnaître intersections";
	}
	
}