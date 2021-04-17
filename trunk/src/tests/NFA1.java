package tests;

import lejos.hardware.lcd.LCD;

public class NFA1 implements interfaceEmbarquee.Lancable{
	
	public void lancer() {
		LCD.clear();
		LCD.drawString("Placer robot sur intersection noire verte", 4, 2);
	}
	
	public String getTitre() {
		return "NFA1 - Rectangle";
	}
	
}