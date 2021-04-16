package tests;

import capteurs.Couleur;
import capteurs.CouleurLigne;
import lejos.hardware.Button;
import moteurs.MouvementsBasiques;
import moteurs.Pilote;

public class NFBM4 implements interfaceEmbarquee.Lancable{
	
	public void lancer() {
		int button = -1;
		Pilote.startVideAtRate(0);
		Couleur.startScanAtRate(10);
		while (button != Button.ID_ESCAPE) {
			button = Button.waitForAnyPress();
			if (button == Button.ID_ENTER) {
				Couleur.videTouche();
				MouvementsBasiques.chassis.travel(Double.POSITIVE_INFINITY);
				while(!Couleur.videTouche()) {
					//on continue d'avancer tout droit
				}
				MouvementsBasiques.chassis.stop(); MouvementsBasiques.chassis.waitComplete();
				MouvementsBasiques.chassis.travel(-10); MouvementsBasiques.chassis.waitComplete();
				MouvementsBasiques.chassis.rotate(180); MouvementsBasiques.chassis.waitComplete();
			}
			Button.waitForAnyEvent();
		}
		Pilote.stopVide();
		Couleur.stopScan();
	}
	
	
	public String getTitre() {
		return "NFBM4 - Detecter vide";
	}
	
}