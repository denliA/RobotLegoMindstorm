package capteurs;

import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.sensor.*;
import lejos.robotics.SampleProvider;


public class Capteur {
	
	//on initialise les objets Sensor de leJos en precisant le port de branchement
	//ouverture des capteurs
	private static EV3ColorSensor COLOR_SENSOR; // Port de branchement du capteur de couleurs
	private static EV3TouchSensor TOUCH_SENSOR;
	private static EV3UltrasonicSensor ULTRASONIC_SENSOR;
	
	/*Creation et initialisation des objets de type SampleProvider.
	Ils permettent la recuperation des donnees des capteurs.
	Ces objets sont utilises par les autres classes du package pour obtenir les valeurs mesurees*/
	
	//Capteur toucher pour la classe Toucher
	public static SampleProvider TOUCHER;
	
	//sample providers du capteur de couleur pour la classe Couleur
	public static SampleProvider RGB;
	public static SampleProvider LUMIERE_AMBIANTE;
	public static SampleProvider ID_COULEUR;
	public static SampleProvider ROUGE;
	
	//sample providers pour la classe Ultrason
	public static SampleProvider ULTRASON;
	public static SampleProvider ECOUTE;
	
	static {
		ouvrirCapteurCouleur();
	}
	
	//ouvrir capteur
	public static void ouvrirCapteurCouleur() {
		while (COLOR_SENSOR==null || TOUCH_SENSOR==null || ULTRASONIC_SENSOR==null) {
			try {
			if (COLOR_SENSOR==null) COLOR_SENSOR = new EV3ColorSensor(LocalEV3.get().getPort("S3"));
			if (TOUCH_SENSOR==null)  TOUCH_SENSOR = new EV3TouchSensor(LocalEV3.get().getPort("S1"));
			if (ULTRASONIC_SENSOR==null)  ULTRASONIC_SENSOR = new EV3UltrasonicSensor(LocalEV3.get().getPort("S2"));
			RGB = COLOR_SENSOR.getRGBMode();
			LUMIERE_AMBIANTE = COLOR_SENSOR.getAmbientMode();
			ID_COULEUR = COLOR_SENSOR.getColorIDMode();
			ROUGE = COLOR_SENSOR.getRedMode();
			ULTRASON = ULTRASONIC_SENSOR.getDistanceMode();
			ECOUTE = ULTRASONIC_SENSOR.getListenMode();
			TOUCHER = TOUCH_SENSOR.getTouchMode();
			}catch(IllegalArgumentException e){
				System.err.println("C'est arrivé!");
				Sound.beepSequenceUp();
				System.err.println(e.toString());
				e.printStackTrace(System.err);
				Capteur.ouvrirCapteurCouleur(); // S'appelle récursivement tant qu'il y a une erreur à l'ouverture d'un des capteurs
				System.err.println("L'occurence de l'erreur est finie !");
			}
		}
	}
	
	//Methode pour fermer les capteurs ouverts au debut de la classe. 
	public static void fermerCapteurs() {
		COLOR_SENSOR.close();
		TOUCH_SENSOR.close();
		ULTRASONIC_SENSOR.close();
	}
}
