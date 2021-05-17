package pince;

import lejos.hardware.Button;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.utility.Delay;


public class Pince {
	public static EV3MediumRegulatedMotor pliers = new EV3MediumRegulatedMotor(MotorPort.C);
	
/*	public static void pinceStalOuvrir() { //à éviter endommage les pinces qui s'ouvrent trop
		pliers.forward();
		while(!(pliers.isStalled())&&Button.ENTER.isUp()) {
		}
		pliers.stop();
		pliers.close();
	}*/
	
	public static void pinceStalFermer() { //ferme les pinces jusqu'à une résistance
		pliers.backward();
		while(!(pliers.isStalled())&&Button.ENTER.isUp()) {
		}
	}
	
	public static void pinceDegre(int angle) { //angle positif pour ouvrir, négatif pour fermer
		//System.out.println("Pince.fermer");
		pliers.setSpeed(36000);
		if (angle>0)
			pliers.forward();
		else
			pliers.backward();
		Delay.msDelay(Math.abs(angle));
		pliers.stop();
		//System.out.println("BLBLBLBL");
	}
}
