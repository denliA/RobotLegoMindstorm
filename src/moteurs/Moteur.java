package moteurs;

import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.utility.Delay;

public class Moteur {
	//ouvre les moteurs qui sont r�utilisables dans les autres classes
	public static EV3LargeRegulatedMotor MOTEUR_GAUCHE = new EV3LargeRegulatedMotor(MotorPort.B);
	public static EV3LargeRegulatedMotor MOTEUR_DROIT = new EV3LargeRegulatedMotor(MotorPort.A);
	public static EV3MediumRegulatedMotor MOTEUR_PINCE = new EV3MediumRegulatedMotor(MotorPort.C);
	
	public static void fermerMoteurs() {
		MOTEUR_GAUCHE.close();
		MOTEUR_DROIT.close();
		MOTEUR_PINCE.close();

	}
}