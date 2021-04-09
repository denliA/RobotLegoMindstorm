package moteurs;

import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.RegulatedMotor;

public class Moteur {
	//ouvre les moteurs qui sont reutilisables dans les autres classes
	public static EV3LargeRegulatedMotor MOTEUR_GAUCHE = new EV3LargeRegulatedMotor(MotorPort.B);
	public static EV3LargeRegulatedMotor MOTEUR_DROIT = new EV3LargeRegulatedMotor(MotorPort.A);
	static {Moteur.MOTEUR_GAUCHE.synchronizeWith(new RegulatedMotor[] {Moteur.MOTEUR_DROIT});}
	public static EV3MediumRegulatedMotor MOTEUR_PINCE = new EV3MediumRegulatedMotor(MotorPort.C);
	
	public static void fermerMoteurs() {
		MOTEUR_GAUCHE.close();
		MOTEUR_DROIT.close();
		MOTEUR_PINCE.close();

	}
}
