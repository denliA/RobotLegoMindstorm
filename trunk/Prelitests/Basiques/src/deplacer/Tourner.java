package deplacer;

import lejos.hardware.motor.Motor;
import lejos.robotics.navigation.DifferentialPilot;


public class Tourner {
	final static double DIST_ROUES_INCH = 3.90; // Pour le DifferentialPilot, mesure approximative 
    final static double DIAM_ROUE_INCH = 1.77165;
	static DifferentialPilot pilot = new DifferentialPilot(DIST_ROUES_INCH, DIST_ROUES_INCH, Motor.A, Motor.B); //parametres en inches
	final static int DEFAULT_SPEED = Motor.A.getSpeed();
	final static int DEFAULT_ACCELERATION = Motor.B.getAcceleration();
	
	public static void turnDiffPilot(int angle) {
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
		
	}
}