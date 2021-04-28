package moteurs;

import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.RegulatedMotor;

/**
 * Classe contenant les constantes représentant les moteurs de l'appareil.
 * S'occupe d'ouvrir les moteurs, et de les fermer à la fin du programme.
 *
 */
public class Moteur {
	//ouvre les moteurs qui sont réutilisables dans les autres classes
	/** Moteur contrôlant la roue gauche*/
	public static EV3LargeRegulatedMotor MOTEUR_GAUCHE = new EV3LargeRegulatedMotor(MotorPort.B);
	/** Moteur contrôlant la roue droite*/
	public static EV3LargeRegulatedMotor MOTEUR_DROIT = new EV3LargeRegulatedMotor(MotorPort.A);
	static {Moteur.MOTEUR_GAUCHE.synchronizeWith(new RegulatedMotor[] {Moteur.MOTEUR_DROIT});}
	/** Moteur contrôlant les pinces */
	public static EV3MediumRegulatedMotor MOTEUR_PINCE = new EV3MediumRegulatedMotor(MotorPort.C);
	
	
	/**
	 * Permet de fermer tous les moteurs après la fin des opérations
	 */
	public static void fermerMoteurs() {
		MOTEUR_GAUCHE.close();
		MOTEUR_DROIT.close();
		MOTEUR_PINCE.close();

	}
}
