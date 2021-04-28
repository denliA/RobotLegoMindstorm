package capteurs;

import exceptions.OuvertureException;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import moteurs.MouvementsBasiques;

public class TestUltrason {
	public static void main(String[] args) throws OuvertureException {
		//initialisation : création des sampler de la classe Capteur
		new Capteur();
		Ultrason.startScan();
		Toucher.startScan();
		
		//nettoyage de l'écran du robot afin d'afficher les messages sans parasite
		LCD.clear();
		
		//variable qui stocke la distance
		float d;
		
		while(Button.ENTER.isUp()) {
			//Récupération de la distance au palet
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
		LCD.clear();
		//on va attraper un palet, pinces ouvertes
		//moteurs.Pince.ouvrir();

		Ultrason.setDistance();
		d = Ultrason.getDistance();
		
		float infini = Float.POSITIVE_INFINITY;
		//tant qu'on capte le palet et qu'on ne le touche pas, on avance
		MouvementsBasiques.chassis.travel(Float.POSITIVE_INFINITY);
		while(d<infini&&d>0&&!Toucher.getTouche()) {
			LCD.drawString("avance", 0, 0);
		}
		
		if(Toucher.getTouche()) {
			moteurs.MouvementsBasiques.chassis.stop();
			moteurs.Pince.fermer();
		}
		else {
			moteurs.MouvementsBasiques.chassis.stop();
			int angle = 0;
			while(d>10&&angle<360) {
				moteurs.MouvementsBasiques.chassis.rotate(10); MouvementsBasiques.chassis.waitComplete();
				angle+=10;
			}
		}
		
	}
}
