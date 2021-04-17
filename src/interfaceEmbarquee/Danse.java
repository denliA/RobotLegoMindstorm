package interfaceEmbarquee;

import java.io.File;

import capteurs.Couleur;
import exceptions.OuvertureException;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;
import moteurs.MouvementsBasiques;
import moteurs.Pilote;
import moteurs.Pince;
import interfaceEmbarquee.Musique;

public class Danse {
static Thread d;
	
	//lance le bruitage choisi dans le picker
	public static void startDance(){
		startDance(Configurations.bruitage.getVal());
	}
	
	//lance le son dont le fichier.wav est passé en paramètre
	public static void startDance(final String name) {
		LCD.clear();
		LCD.drawString("Dance time", 3, 3);
		if (name=="victoire") {
			victoire();
		}
		else if(name=="defaite") {
			defaite();
		}		
	}
	
	public static void victoire() {
		long debut;
		long fin = 23*1000;
		double speed = MouvementsBasiques.chassis.getLinearSpeed();
		double acceleration = MouvementsBasiques.chassis.getLinearAcceleration();
		MouvementsBasiques.chassis.setLinearSpeed(speed);
		//on l'eloigne de la ligne blanche
		MouvementsBasiques.chassis.travel(100);
		//Pilote.allerVersPoint(0, 0);
		Musique.startMusic("VictorySong.wav");
		debut = System.currentTimeMillis();
		//on attend le debut de la musique
		Delay.msDelay(2000);
		while(System.currentTimeMillis()-debut<fin) {
			//mouvements brusques
			MouvementsBasiques.chassis.setLinearSpeed(speed*2);
			MouvementsBasiques.chassis.setLinearAcceleration(acceleration*2);
			MouvementsBasiques.chassis.travel(5); MouvementsBasiques.chassis.waitComplete();
			Delay.msDelay(1000);
			MouvementsBasiques.chassis.travel(-5); MouvementsBasiques.chassis.waitComplete();
			//mouvements lents
			MouvementsBasiques.chassis.setLinearSpeed(speed/10);
			MouvementsBasiques.chassis.setLinearAcceleration(acceleration/10);
			try {
				Pince.ouvrir();
			}
			catch(OuvertureException e) {
				;
			}
			MouvementsBasiques.chassis.rotate(360);
			Delay.msDelay(1000);
			
			//l'autre sens
			//mouvements brusques
			MouvementsBasiques.chassis.setLinearSpeed(speed*2);
			MouvementsBasiques.chassis.setLinearAcceleration(acceleration*2);
			MouvementsBasiques.chassis.travel(3); MouvementsBasiques.chassis.waitComplete();
			Delay.msDelay(1000);
			MouvementsBasiques.chassis.travel(-3); MouvementsBasiques.chassis.waitComplete();
			//mouvements lents
			MouvementsBasiques.chassis.setLinearSpeed(speed/10);
			MouvementsBasiques.chassis.setLinearAcceleration(acceleration/10);
			try {
				Pince.fermer();
			}
			catch(OuvertureException e) {
				;
			}
			MouvementsBasiques.chassis.rotate(-360);
			Delay.msDelay(1000);
		}
		//on remet les vitesses normales
		MouvementsBasiques.chassis.setLinearSpeed(speed);
		MouvementsBasiques.chassis.setLinearAcceleration(acceleration);
	}
	
	public static void defaite() {
		new capteurs.Capteur();
		Musique.startMusic("LosingSong.wav");
		Couleur.startScanAtRate(10);
		double speed = MouvementsBasiques.chassis.getLinearSpeed();
		double acceleration = MouvementsBasiques.chassis.getLinearAcceleration();
		double accAng = MouvementsBasiques.chassis.getAngularAcceleration();
		MouvementsBasiques.chassis.setLinearSpeed(speed/4);
		Couleur.videTouche();
		MouvementsBasiques.chassis.travel(Double.POSITIVE_INFINITY);
		while(!Couleur.videTouche()) {
			//on continue d'avancer tout droit
		}
		MouvementsBasiques.chassis.setLinearAcceleration(200);
		MouvementsBasiques.chassis.stop(); MouvementsBasiques.chassis.waitComplete();
		MouvementsBasiques.chassis.travel(-10); MouvementsBasiques.chassis.waitComplete();
		MouvementsBasiques.chassis.rotate(180); MouvementsBasiques.chassis.waitComplete();
		Couleur.stopScan();
	}
}
