package pack1;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.navigation.MovePilot;
import lejos.utility.Delay;
import lejos.robotics.chassis.WheeledChassis;

public class TestMovePilot {
	
	static EV3LargeRegulatedMotor moteur_droite = new EV3LargeRegulatedMotor(MotorPort.A);
	static EV3LargeRegulatedMotor moteur_gauche = new EV3LargeRegulatedMotor(MotorPort.B);
	final static double DIST_ROUES_INCH = 12.280002254568; // Pour le DifferentialPilot, mesure approximative 
    final static double DIAM_ROUE_INCH = 5.6;
	
	public static void main(String[] args) {
		double trackWidth = DIST_ROUES_INCH;
		double leftWheelDiameter = DIAM_ROUE_INCH*1.003;
		double rightWheelDiameter = DIAM_ROUE_INCH;
		MovePilot pilot = new MovePilot (new WheeledChassis(
				new WheeledChassis.Modeler[] { 
					WheeledChassis.modelWheel(moteur_gauche, leftWheelDiameter).offset(trackWidth / 2).invert(false),
					WheeledChassis.modelWheel(moteur_droite, rightWheelDiameter).offset(-trackWidth / 2).invert(false) },
				WheeledChassis.TYPE_DIFFERENTIAL));
		pilot.setLinearAcceleration(pilot.getLinearAcceleration()/2);
		pilot.setLinearSpeed(pilot.getLinearSpeed()/2);
		pilot.travel(180);

	}

}
