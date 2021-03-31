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
		
		//variable qui stocke la distance
		float d;
		
		while(Button.ENTER.isUp()) {
			//recuperation de la distance au palet
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
		
		//on va attraper un palet, pinces ouvertes
		if(!moteurs.Pince.getOuvert()) {
			moteurs.Pince.ouvrir();
		}
		Ultrason.setDistance();
		d = Ultrason.getDistance();
		//tant qu'on capte le palet et qu'on ne le touche pas, on avance
		while(d>0&&!Toucher.getTouche()) {
			moteurs.MouvementsBasiques.avancer();
		}
		if(Toucher.getTouche()) {
			moteurs.Pince.fermer();
		}
		else {
			int angle = 0;
			while(d>10&&angle<360) {
				moteurs.MouvementsBasiques.tourner(10);
				angle+=10;
			}
		}
		
	}
}
