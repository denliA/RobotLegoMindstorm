package moteurs;

import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.MovePilot;
import lejos.utility.Delay;

public class MouvementsBasiques {
	final static double DIST_ROUES_INCH = 12.280002254568; // Pour le DifferentialPilot, mesure approximative 
    final static double DIAM_ROUE_INCH = 5.6;
	static double trackWidth = DIST_ROUES_INCH;
	static double leftWheelDiameter = DIAM_ROUE_INCH*1.0045;
	//1.0045 bien
	static double rightWheelDiameter = DIAM_ROUE_INCH;
	
	public static MovePilot pilot = new MovePilot (new WheeledChassis(
			new WheeledChassis.Modeler[] { 
				WheeledChassis.modelWheel(Moteur.MOTEUR_GAUCHE, leftWheelDiameter).offset(trackWidth / 2).invert(false),
				WheeledChassis.modelWheel(Moteur.MOTEUR_DROIT, rightWheelDiameter).offset(-trackWidth / 2).invert(false) },
			WheeledChassis.TYPE_DIFFERENTIAL));
	
	
	public static void avancerToutDroit(int vitesse, int acceleration){
		Moteur.MOTEUR_GAUCHE.setSpeed(Math.abs(vitesse));
		Moteur.MOTEUR_DROIT.setSpeed(Math.abs(vitesse));
		Moteur.MOTEUR_GAUCHE.setAcceleration(acceleration);
		Moteur.MOTEUR_DROIT.setAcceleration(acceleration);
		if (vitesse>0) {
			Moteur.MOTEUR_GAUCHE.forward();
			Moteur.MOTEUR_DROIT.forward();
		}
		else if (vitesse<0) {
			Moteur.MOTEUR_GAUCHE.backward();
			Moteur.MOTEUR_DROIT.backward();
		}		
	}
	
	public static void avancerToutDroit(int vitesse, int acceleration, long distance){ //distance en quelle unité? cm?
		Moteur.MOTEUR_GAUCHE.setSpeed(Math.abs(vitesse)); // vitesse de rotation du moteur pas de deplacement du robot
		Moteur.MOTEUR_DROIT.setSpeed(Math.abs(vitesse));
		Moteur.MOTEUR_GAUCHE.setAcceleration(acceleration);
		Moteur.MOTEUR_DROIT.setAcceleration(acceleration);
		float k=1/1000; // coeff de proportionalite a definir
		float vitesseRobot = k*vitesse; //en secondes
		long dureeDepl = (long)(distance/vitesseRobot); //en secondes
		
		if (vitesse>0) {
			Moteur.MOTEUR_GAUCHE.forward();
			Moteur.MOTEUR_DROIT.forward();
		}
		else if (vitesse<0) {
			Moteur.MOTEUR_GAUCHE.backward();
			Moteur.MOTEUR_DROIT.backward();
		}
		//Delay.msDelay(dureeDepl*1000);
		Delay.msDelay(10000);
		Moteur.MOTEUR_GAUCHE.setSpeed(0);
		Moteur.MOTEUR_DROIT.setSpeed(0);
	}
	
	public static void tourner(int vitesse, int acceleration, int angle) {
		pilot.setLinearAcceleration(acceleration);
		pilot.setLinearSpeed(vitesse);
		pilot.rotate(angle);
	}
	
	public static void tourner(int vitesse, int acceleration, int angle, boolean deux_roues) {
		//methode sans chassis à definir eventuellement si on veut faire bouger les roues differement
	}
	
	/*
	public static void vide() {
		if () {
			
		}
	}*/

}
