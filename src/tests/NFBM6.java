package tests;
import capteurs.Couleur;
import capteurs.Ultrason;
/**
 * <p>Situation initiale :
 * 		<ul>
 * 			<li>le robot est déposé au début d'une ligne de couleur quelconque</li>
 * 			<li>le palet est déposé sur une des 9 intersections des lignes de couleur</li>
 * 		</ul>
 * </p>
 * <p>Situation finale : le robot avance d’une certaine distance puis s’arrête lorsqu’il est à moins d’un mètre du palet. Il affiche sur l’écran quelle intersection contient le palet.</p>
 * <p>Si le palet n’est pas sur une intersection mais sur une partie quelconque de la ligne, le robot affiche un message adéquat.</p>
 * @see Couleur
 * @see Ultrason
 * @see carte
 */

public class NFBM6 implements interfaceEmbarquee.Lancable{
	
	public void lancer() {
	}
	
	public String getTitre() {
		return "NFBM6 - Capteur ultrason";
	}
	
}