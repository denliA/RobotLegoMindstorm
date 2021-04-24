package tests;
import capteurs.Toucher;
import capteurs.Capteur;
import capteurs.Couleur;
import capteurs.Toucher;
import moteurs.MouvementsBasiques;
import moteurs.Pilote;

/**
 * <p>Situation initiale :
 * 		<ul>
 * 			<li>le robot est déposé au début d'une ligne de couleur</li>
 * 			<li>le palet est déposé sur une des trois intersections de la ligne de départ</li>
 * 		</ul>
 * </p>
 * <p>Situation finale : le robot avance tout droit jusqu’à ce qu’il touche le palet et s’arrête instantanément.</p>
 * @see MouvementsBasiques#chassis
 * @see Toucher
 */

public class NFBM3 implements interfaceEmbarquee.Lancable{
	
	public void lancer() {
		new Capteur();
		Couleur.startScanAtRate(0);
		Toucher.startScan();
		
		Pilote.lancerSuivi(Couleur.getLastCouleur());
		while(!Toucher.getTouche()) {
			
		}
		Pilote.arreterSuivi();
	}
	
	public String getTitre() {
		return "NFBM3 - Détecter palet";
	}
	
}