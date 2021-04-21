package tests;

import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

/**
 * <p>Situation initiale : le robot est déposé n'importe où sur la table</p>
 * <p>Situation finale : après 5 min écoulées, le robot arrête son affichage.</p>
 * <p>Le but est de prouver qu'on peut interrompre un mode competition qui dure plus de 5 min. Ici la tache longue est un affichage</p>
 */

public class NFBM5 implements interfaceEmbarquee.Lancable{
	
	public void lancer() {
		long debut;
		long fin = 5*60*1000;
		LCD.clear();
		LCD.drawString("Debut match", 3, 1);
		Delay.msDelay(3000); //laisser le temps de lire
		debut = System.currentTimeMillis();
		while(System.currentTimeMillis()-debut<fin) {
			//fais quelque chose
			LCD.clear();
			LCD.drawString("En cours", 4, 2);
		}
		LCD.clear();
		LCD.drawString("Fin match", 5, 4);
	}
	
	public String getTitre() {
		return "NFBM5 - Arret apres 5 min";
	}
	
}