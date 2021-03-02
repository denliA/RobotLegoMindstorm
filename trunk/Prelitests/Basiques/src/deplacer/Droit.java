package deplacer;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.navigation.DifferentialPilot;


public class Droit {
	public static EV3LargeRegulatedMotor G = new EV3LargeRegulatedMotor(MotorPort.A);
	public static EV3LargeRegulatedMotor D = new EV3LargeRegulatedMotor(MotorPort.B);
	final static public int DEFAULT_SPEED = G.getSpeed();
	final static double DIST_ROUES_INCH = 3.90; // Pour le DifferentialPilot, mesure approximative 
    final static double DIAM_ROUE_INCH = 1.77165;
    //static DifferentialPilot pilot = new DifferentialPilot(DIAM_ROUE_INCH, DIST_ROUES_INCH, Motor.A, Motor.B); //parametres en inches
    //static int acceleration = 3000;
    
	public static void droitMoteur(int acceleration, int vitesse){
		G.setSpeed(Math.abs(vitesse));
		D.setSpeed(Math.abs(vitesse));
		G.setAcceleration(acceleration);
		D.setAcceleration(acceleration);
		if (vitesse>0) {
			G.forward();
			D.forward();
		}
		else if (vitesse<0) {
			G.backward();
			D.backward();
		}
	}
	
		
	public static void arreter(){
		G.setSpeed(0); // robot s'arrete sans dévier
		D.setSpeed(0);
	}
	
	public static void fermerMoteur() {
		G.close();
		D.close();
	}
		
		
	
/*	public static void droitRotate(int angle) { //Une seule roue tourne
		G.setAcceleration(1500);
		D.setAcceleration(1500);
		G.rotate(angle,true);
		D.rotate(angle,true);
		G.close();
		D.close();
		
	}*/
	
	/*
	public static void DroitDiffPilot(float distance) {
		pilot.travel(distance); //avancer tout droit (inches)	
	}*/
}

