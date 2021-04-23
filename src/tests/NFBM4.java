package tests;

import capteurs.Couleur;
import interfaceEmbarquee.Musique;
import lejos.hardware.Button;
import moteurs.MouvementsBasiques;

/**
 * <p>Situation initiale : le robot est déposé au début d'une ligne de couleur</p>
 * <p>Situation finale : le robot avance jusqu'à ce qu'il détecte du vide. Il  s’arrête et recule de 10 cm puis tourne de 180 degrés.</p>
 * @see MouvementsBasiques#chassis
 * @see Couleur
 */

public class NFBM4 implements interfaceEmbarquee.Lancable{
	
	public void lancer() {
		int button = -1;
		Couleur.startScanAtRate(10);
		while (button != Button.ID_ESCAPE) {
			button = Button.waitForAnyPress();
			if (button == Button.ID_ENTER) {
				Couleur.videTouche();
				MouvementsBasiques.chassis.travel(Double.POSITIVE_INFINITY);
				while(!Couleur.videTouche()) {
					//on continue d'avancer tout droit
				}
				Musique.startMusic("Nani.wav");
				MouvementsBasiques.chassis.stop(); MouvementsBasiques.chassis.waitComplete();
				MouvementsBasiques.chassis.travel(-10); MouvementsBasiques.chassis.waitComplete();
				MouvementsBasiques.chassis.rotate(180); MouvementsBasiques.chassis.waitComplete();
			}
			Button.waitForAnyEvent();
		}
		Couleur.stopScan();
	}
	
	
	public String getTitre() {
		return "NFBM4 - Detecter vide";
	}
	
}