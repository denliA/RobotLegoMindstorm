package capteurs;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;

public class TestUltrason {
	public static void main(String[] args) {
		//initialisation : création des sampler de la classe Capteur
		new Capteur();
		Ultrason.startScan();
		
		//nettoyage de l'ecran du robot afin d'afficher les messages sans parasite
		LCD.clear();
		
		while(Button.ENTER.isUp()) {
			float d;
			Ultrason.setDistance();
			d = Ultrason.getDistance();
			LCD.drawString("distance : "+d, 0, 0);
			Button.waitForAnyEvent();
			System.out.println("d : "+d);
			
			Ultrason.setBruitDetecte();
			boolean b = Ultrason.getBruitDetecte();
			if(b) LCD.drawString("Il y a un robot", 0, 2);
			else LCD.drawString("Y a pas de robot", 0, 2);
		}
		
		
	}
}
