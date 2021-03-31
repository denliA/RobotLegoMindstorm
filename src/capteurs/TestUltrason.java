package capteurs;

import exceptions.OuvertureException;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

public class TestUltrason {
	public static void main(String[] args) throws OuvertureException {
		//initialisation : cr�ation des sampler de la classe Capteur
		new Capteur();
		Ultrason.startScan();
		Toucher.startScan();
		
		//nettoyage de l'ecran du robot afin d'afficher les messages sans parasite
		LCD.clear();
		
		//variable qui stocke la distance
		float d;
		
		while(Button.ENTER.isUp()) {
			//recuperation de la distance au palet
			Ultrason.setDistance();
			d = Ultrason.getDistance();
			LCD.drawString("distance : "+d, 0, 0);
			//Delay.msDelay(1000);
			System.out.println("distance : "+d);
			Ultrason.setBruitDetecte();
			boolean b = Ultrason.getBruitDetecte();
			if(b) LCD.drawString("Il y a un robot", 0, 2);
			else LCD.drawString("Y a pas de robot", 0, 2);
		}
		
		//on va attraper un palet, pinces ouvertes
		moteurs.Pince.ouvrir();
		
		Ultrason.setDistance();
		d = Ultrason.getDistance();
		//tant qu'on capte le palet et qu'on ne le touche pas, on avance
		moteurs.MouvementsBasiques.avancer();
		//Delay.msDelay(2000);
		LCD.clear();
		
		if(d<0||Toucher.getTouche()) {
			moteurs.MouvementsBasiques.arreter();
			moteurs.Pince.fermer();
		}
		else {
			moteurs.MouvementsBasiques.arreter();
			int angle = 0;
			while(d>10&&angle<360) {
				moteurs.MouvementsBasiques.tourner(10);
				angle+=10;
			}
		}
		
	}
}
