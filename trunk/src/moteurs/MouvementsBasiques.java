package moteurs;

import java.util.concurrent.Semaphore;

import lejos.robotics.RegulatedMotor;
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
	
	public static MovePilot pilot = new MovePilot (new WheeledChassis(
			new WheeledChassis.Modeler[] { 
				WheeledChassis.modelWheel(Moteur.MOTEUR_GAUCHE, leftWheelDiameter).offset(trackWidth / 2).invert(false),
				WheeledChassis.modelWheel(Moteur.MOTEUR_DROIT, rightWheelDiameter).offset(-trackWidth / 2).invert(false) },
			WheeledChassis.TYPE_DIFFERENTIAL));
	
	public static boolean isMovingPilot() {
		return pilot.isMoving();
	}
	
	public static double getVitesseRobot() {
		return pilot.getLinearSpeed(); //Vitesse de déplacement du robot lors d'un forward ou backward ou travel. Toutes les méthodes du MovePilot
	}
	
	public static double getAccelerationRobot() {
		return pilot.getLinearAcceleration(); //Acceleration du robot
	}
	
	public static void setVitesseRobot(double v) {
		pilot.setLinearSpeed(v); //Regler la vitesse du robot
	}
	
	public static void setAccelerationRobot(double a) {
		pilot.setLinearAcceleration(a); //Regler l'acceleration du robot
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
	
	//pas de freinage brusque
	public static void arreterMoteurs() {
		try {
			s1.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Moteur.MOTEUR_GAUCHE.setSpeed(0);
		Moteur.MOTEUR_DROIT.setSpeed(0);
		s1.release();
	}
	
	//le mouvement peut etre interrompu par un autre mouvement si immediateReturn==true
	public static void avancerTravel(double vitesse, double acceleration, double distance,boolean immediateReturn) {
		try {
			s1.acquire(); //proteger les moteurs du pilot avec une mutex (semaphore à exclusion mutuelle)
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pilot.setLinearSpeed(vitesse);
		pilot.setLinearAcceleration(acceleration);
		pilot.travel(distance,immediateReturn);
		s1.release(); //liberer le mutex
	}
	
	public static void avancerTravel(double vitesse, double acceleration, double distance) {
		avancerTravel(vitesse, acceleration, distance, false); //le mouvement ne peut etre interrompu par un autre mouvement
	}
	
	public static void avancerTravel(double acceleration, double distance) {
		avancerTravel(pilot.getLinearSpeed(), acceleration, distance, false); //le mouvement ne peut etre interrompu par un autre mouvement
	}
	
	public static void avancerTravel(double distance) {
		avancerTravel(pilot.getLinearSpeed(), pilot.getLinearAcceleration(), distance, false); //le mouvement ne peut etre interrompu par un autre mouvement
	}
	
	public static void avancerTravel(double distance,boolean immediateReturn) {
		avancerTravel(pilot.getLinearSpeed(), pilot.getLinearAcceleration(), distance, immediateReturn);
	}
	
	public static void avancerToutDroit(int vitesse, int acceleration) throws InterruptedException{
		s1.acquire();
		//Calibrer la vitesse et l'acceleration des moteurs
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
	
	//angle>0 : tourne à gauche, angle<0 : tourne à droite
	
	//le mouvement peut etre interrompu par un autre mouvement si immediateReturn==true
	public static void tourner(double vitesse, double acceleration, double angle,boolean immediateReturn) throws InterruptedException {
		s1.acquire();
		pilot.setAngularAcceleration(acceleration);
		pilot.setAngularSpeed(vitesse);
		pilot.rotate(angle,immediateReturn);
		s1.release();
	}
	
	public static void tourner(double angle) {
		pilot.rotate(angle,false); //le mouvement ne peut etre interrompu par un autre mouvement
	}
	
	public static void tourner(double angle,boolean immediateReturn) {
		pilot.rotate(angle,immediateReturn);
	}
	
	@SuppressWarnings("resource")
	//methode utilisée pour seRedresserSurLigne(). Pour éviter que le robot n'avance trop loin pendant qu'il se redresse, on va tourner une seule roue en meme temps.
	public static void tourner(double angle, int duree, boolean moteur_gauche) {
		double vitesse = 2*trackWidth/DIAM_ROUE_INCH*angle/duree*1000;
		RegulatedMotor moteur = (moteur_gauche ? Moteur.MOTEUR_GAUCHE : Moteur.MOTEUR_DROIT);
		moteur.setSpeed((int)vitesse);
		if (vitesse >= 0)
			moteur.forward(); //avancer
		else
			moteur.backward(); //reculer
		
	}
}
