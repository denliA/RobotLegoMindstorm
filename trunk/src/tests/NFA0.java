package tests;

import capteurs.Capteur;
import capteurs.Couleur;
import moteurs.Pilote;

/**
 * <p>Situation initiale : le robot est déposé sur la table derrière une ligne blanche</p>
 * <p>Situation finale : le robot avance en ligne droite et franchit la ligne blanche du camp adverse</p>
 * <p>Ce test doit être réalisé en moins de 3 min.</p>
 * @see capteurs#Couleur
 */

public class NFA0 implements interfaceEmbarquee.Lancable{
	
	public void lancer() {
		new Capteur();
		Couleur.setScanMode(Couleur.RGBMODE);
		Couleur.startScanAtRate(0);
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				Pilote.suivreLigne();
			}	
		});
		t.start(); //lance le suivi de ligne
		Couleur.blacheTouchee();
		while(!Couleur.blacheTouchee());
		Pilote.SetSeDeplace(false); //arrete le suivi de ligne
		
	}
	
	public String getTitre() {
		return "NFA0 - Ligne blanche adverse/Suivre ligne couleur";
	}
	
}