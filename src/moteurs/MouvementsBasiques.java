package moteurs;

import java.util.concurrent.Semaphore;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.MovePilot;
import lejos.utility.Delay;

public class MouvementsBasiques {
	final static double DIST_ROUES_INCH = 12.280002254568; // Pour le MovePilot, mesure approximative 
    final static double DIAM_ROUE_INCH = 5.6;
	static double trackWidth = DIST_ROUES_INCH;
	/* Moteur gauche tourne plus vite que le moteur droit. Pour compenser, on cosidère que la roue gauche est plus grande que celle de droite.
	 * Elle nécessite donc plus de rotations du moteur pour réaliser un tour complet. Les moteurs gauche et droit reçoivent la meme vitesse de rotation.
	 */
	static double leftWheelDiameter = DIAM_ROUE_INCH*1.0045; //1.0045 bon calibrage
	static double rightWheelDiameter = DIAM_ROUE_INCH;
	public static Semaphore s1 = new Semaphore(1);
	
	private static MovePilot pilot = new MovePilot (new WheeledChassis(
			new WheeledChassis.Modeler[] { 
				WheeledChassis.modelWheel(Moteur.MOTEUR_GAUCHE, leftWheelDiameter).offset(trackWidth / 2).invert(false),
				WheeledChassis.modelWheel(Moteur.MOTEUR_DROIT, rightWheelDiameter).offset(-trackWidth / 2).invert(false) },
			WheeledChassis.TYPE_DIFFERENTIAL));
	
	public static double getVitesseRobot() {
		return pilot.getLinearSpeed();
	}
	
	public static double getAccelerationRobot() {
		return pilot.getLinearAcceleration();
	}
	
	public static void setVitesseRobot(double v) {
		pilot.setLinearSpeed(v);
	}
	
	public static void setAccelerationRobot(double a) {
		pilot.setLinearAcceleration(a);
	}
	
	public static void changeVitesseRobot( double ratio) {
		pilot.setLinearSpeed(pilot.getLinearSpeed()*ratio);
	}
	
	public static void avancer() {
		pilot.forward();
	}
	
	public static void reculer() {
		pilot.backward();
	}
	
	public static void arreter() {
		pilot.stop();
	}
	
	public static void avancerTravel(double vitesse, double acceleration, double distance) {
		try {
			s1.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pilot.setLinearSpeed(vitesse);
		pilot.setLinearAcceleration(acceleration);
		pilot.travel(distance);
		s1.release();
	}
	
	public static void avancerTravel(double vitesse, double distance) {
		try {
			s1.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pilot.setLinearSpeed(vitesse);
		pilot.setLinearAcceleration(pilot.getLinearAcceleration()/3); //robot va accelerer en debut du mouvement et decelerer a la fin du mouvement avec cette valeur trouvée par expérimentation. Le robot ne se décale pas trop de sa position lors du début du mouvement
		pilot.travel(distance);
		s1.release();
	}
	
	public static void avancerToutDroit(int vitesse, int acceleration) throws InterruptedException{
		s1.acquire();
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
		s1.release();
	}
	
	public static void avancerToutDroit(int vitesse, int acceleration, double distance) throws InterruptedException{ //distance en cm
		s1.acquire();
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
		s1.release();
	}
	
	public static void tourner(double vitesse, double acceleration, double angle) throws InterruptedException {
		s1.acquire();
		pilot.setLinearAcceleration(acceleration);
		pilot.setLinearSpeed(vitesse);
		pilot.rotate(angle);
		s1.release();
	}
	
	public static void tourner(double vitesse, double acceleration, double angle, boolean deux_roues) throws InterruptedException {
		//methode sans chassis à definir eventuellement si on veut faire bouger les roues differement
		s1.acquire();
		s1.release();
	}
	
	/*
	public static void vide() {
		if () {
			
		}
	}*/

}
