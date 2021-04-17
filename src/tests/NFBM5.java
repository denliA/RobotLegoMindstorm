package tests;

import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

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