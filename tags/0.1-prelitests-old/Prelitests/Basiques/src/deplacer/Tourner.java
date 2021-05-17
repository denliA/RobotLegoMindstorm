package deplacer;

import lejos.hardware.motor.Motor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.utility.Delay;
import suivreLigneCouleur.SuivreLigneCouleur;


public class Tourner {
	final static double DIST_ROUES_INCH = 3.90; // Pour le DifferentialPilot, mesure approximative 
    final static double DIAM_ROUE_INCH = 1.77165;
	//static DifferentialPilot pilot = new DifferentialPilot(DIST_ROUES_INCH, DIST_ROUES_INCH, Motor.A, Motor.B); //parametres en inches
	//final static int DEFAULT_SPEED = Motor.A.getSpeed();
	//final static int DEFAULT_ACCELERATION = Motor.B.getAcceleration();
	final static double RATIO_360_MOTEUR = 1; // Nombre de tours d'un des deux moteurs pour faire un tour complet. Pas encore mesuré.
	
/*	public static void turnDiffPilot(int angle) {
		int last_acceleration = Motor.A.getAcceleration();
		int last_speed = Motor.A.getSpeed();
		Motor.A.setSpeed(DEFAULT_SPEED);
		Motor.A.setAcceleration(DEFAULT_ACCELERATION);
		Motor.B.setSpeed(DEFAULT_SPEED);
		Motor.B.setAcceleration(DEFAULT_ACCELERATION);
		pilot.rotate(angle); //tourner en angles (inches)
		Motor.A.setSpeed(last_speed);
		Motor.A.setAcceleration(last_acceleration);
		Motor.B.setSpeed(last_speed);
		Motor.B.setAcceleration(last_acceleration);
		
	}*/
	
	public static void turnMotor(int angle) throws Exception {
		if (Droit.D.isMoving() || Droit.G.isMoving()) {
			throw new Exception("Erreur, tu veux faire tourner le moteur alors qu'il bouge\n");
		}
		if (angle > 0 )
			Droit.G.rotate((int) (angle*RATIO_360_MOTEUR), true);
		else
			Droit.D.rotate((int) (-angle*RATIO_360_MOTEUR), true);
	}
	
	public static void toLigne(float [] bornes) {
		toLigne(bornes, 0);
	}
	
	public static void toLigne(float [] bornes, int with_delay) {
		//System.out.println("Tourner.toLigne");
		Droit.G.setAcceleration(6000);
		Droit.D.setAcceleration(6000);
		float [] value = new float[3];
		int speed = 360;
		boolean gauche_avant = true;
		_toLigne(bornes, value, speed, gauche_avant, with_delay);
		while (value[2]*255<bornes[4] || value[2]*255>bornes[5] || value[1]*255<bornes[2] || value[1]*255>bornes[3] || value[0]*255<bornes[0] || value[0]*255>bornes[1]) {
			_toLigne(bornes, value, (speed = speed/10), (gauche_avant = !gauche_avant), 0);
		}
		
	}
	
	public static void _toLigne(float[] bornes, float [] value, int speed, boolean gauche_avant, int with_delay) {
		//System.out.println("_TOLIGNE");
		Droit.G.setSpeed(speed);
		Droit.D.setSpeed(speed);
		if (gauche_avant) {
			System.out.println("111");
			Droit.G.forward();
			Droit.D.backward();
			System.out.println("222");
		}
		else {
			Droit.D.forward();
			Droit.G.backward();
		}
		Delay.msDelay(with_delay);
		SuivreLigneCouleur.RGB.fetchSample(value, 0);
		System.out.println(value[2]*255+"<"+bornes[4]+" || "+value[2]*255+">"+bornes[5]+" || "+value[1]*255+"<"+bornes[2]+" || "+value[1]*255+">"+bornes[3]+" || "+value[0]*255+"<"+bornes[0]+" || "+value[0]*255+">"+bornes[1]);
		while (value[2]*255<bornes[4] || value[2]*255>bornes[5] || value[1]*255<bornes[2] || value[1]*255>bornes[3] || value[0]*255<bornes[0] || value[0]*255>bornes[1]) {
			SuivreLigneCouleur.RGB.fetchSample(value, 0);
			System.out.println(value[2]*255+"<"+bornes[4]+" || "+value[2]*255+">"+bornes[5]+" || "+value[1]*255+"<"+bornes[2]+" || "+value[1]*255+">"+bornes[3]+" || "+value[0]*255+"<"+bornes[0]+" || "+value[0]*255+">"+bornes[1]);

		}
		Droit.G.stop();
		Droit.D.stop();
		Droit.G.setSpeed(0);
		Delay.msDelay(0);
		SuivreLigneCouleur.RGB.fetchSample(value, 0);
	}
}