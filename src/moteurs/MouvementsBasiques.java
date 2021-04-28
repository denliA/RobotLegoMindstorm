package moteurs;

import java.lang.reflect.Field;
import java.util.concurrent.Semaphore;

import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.MovePilot;



/**
 * Classe gérant les mouvements basiques du robot : Avancer et tourner
 * Dû à des problèmes de stabilité des méthodes fournies, elle repose désormais entièrement sur le Chassis fourni par LeJOS
 * @see lejos.robotics.chassis.Chassis
 */
public class MouvementsBasiques {
	final static double DIST_ROUES_INCH = 12.280002254568; // Pour le Chassis, mesure approximative 
    final static double DIAM_ROUE_INCH = 5.6; // Diamètre de chaque roue
	static double trackWidth = DIST_ROUES_INCH; // Distance entre les deux roues
	/* Moteur gauche tourne plus vite que le moteur droit. Pour compenser, on considère que la roue gauche est plus grande que celle de droite.
	 * Elle nécessite donc plus de rotations du moteur pour réaliser un tour complet. Les moteurs gauche et droit reçoivent la même vitesse de rotation.
	 */
	static double leftWheelDiameter = DIAM_ROUE_INCH*1.01; //1.0045 bon calibrage
	static double rightWheelDiameter = DIAM_ROUE_INCH;
	protected static Semaphore s1 = new Semaphore(1);
	/**
	 * Contrôleur principal du mouvement du robot
	 * @see lejos.robotics.chassis.Chassis
	 */
	public static WheeledChassis chassis = new WheeledChassis2(
			new WheeledChassis.Modeler[] { 
					WheeledChassis.modelWheel(Moteur.MOTEUR_GAUCHE, leftWheelDiameter).offset(trackWidth / 2).invert(false),
					WheeledChassis.modelWheel(Moteur.MOTEUR_DROIT, rightWheelDiameter).offset(-trackWidth / 2).invert(false) },
				WheeledChassis.TYPE_DIFFERENTIAL);
	
	@Deprecated 
	public static MovePilot pilot = new MovePilot (chassis);
	
}

class WheeledChassis2 extends WheeledChassis{

	public WheeledChassis2(Wheel[] wheels, int dim) {
		super(wheels, dim);
	}
}



