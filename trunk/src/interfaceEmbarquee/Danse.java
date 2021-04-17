package interfaceEmbarquee;

import java.io.File;

import capteurs.Couleur;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import moteurs.MouvementsBasiques;
import moteurs.Pilote;
import interfaceEmbarquee.Musique;

public class Danse {
static Thread t;
	
	//lance le bruitage choisi dans le picker
	public static void startDance(){
		startDance(Configurations.bruitage.getVal());
	}
	
	//lance le son dont le fichier.wav est passé en paramètre
	public static void startDance(final String name) {
		t = new Thread(new Runnable() {
			public void run(){
				while(!Thread.interrupted()){
					LCD.clear();
					LCD.drawString("Dance time", 3, 3);
					if (name=="victoire") {
						victoire();
					}
					else if(name=="defaite") {
						defaite();
					}
				}
			}
		});
		t.start();		
	}
	
	public static void stopDance(){
		t.interrupt(); //va tenter d'arreter le thread en cours d'execution
    }
	
	public static void victoire() {
		
	}
	
	public static void defaite() {
		Couleur.startScanAtRate(10);
		Couleur.videTouche();
		double speed = MouvementsBasiques.chassis.getLinearSpeed()/4;
		MouvementsBasiques.chassis.setLinearSpeed(speed);
		MouvementsBasiques.chassis.travel(Double.POSITIVE_INFINITY);
		while(!Couleur.videTouche()) {
			//on continue d'avancer tout droit
		}
		Musique.startMusic("Nani.wav");
		MouvementsBasiques.chassis.stop(); MouvementsBasiques.chassis.waitComplete();
		MouvementsBasiques.chassis.travel(-10); MouvementsBasiques.chassis.waitComplete();
		MouvementsBasiques.chassis.rotate(180); MouvementsBasiques.chassis.waitComplete();
		
		Pilote.stopVide();
		Couleur.stopScan();
	}
}
