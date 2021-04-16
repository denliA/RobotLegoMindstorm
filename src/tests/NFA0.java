package tests;

import capteurs.Capteur;
import capteurs.Couleur;
import moteurs.Pilote;

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