package pince;

import lejos.hardware.Button;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;


public class Pince {
	public static EV3MediumRegulatedMotor pliers = new EV3MediumRegulatedMotor(MotorPort.C);
	
	public static void pinceStalOuvrir() { //à éviter endommage les pinces qui s'ouvrent trop
		pliers.forward();
		while(!(pliers.isStalled())&&Button.ENTER.isUp()) {
		}
		pliers.stop();
		pliers.close();
	}
	
	public static void pinceStalFermer() { //ferme les pinces jusqu'à une résistance
		pliers.backward();
		while(!(pliers.isStalled())&&Button.ENTER.isUp()) {
		}
		pliers.stop();
		pliers.close();
	}
	
	public static void pinceDegre(int angle) { //angle positif pour ouvrir, négatif pour fermer
		pliers.setSpeed(360*245);
		pliers.rotate(angle);
		pliers.stop();
		pliers.close();
	}
}
