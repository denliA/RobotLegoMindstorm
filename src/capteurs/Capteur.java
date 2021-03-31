package capteurs;

import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.sensor.*;
import lejos.robotics.SampleProvider;


public class Capteur {
	
	//on initialise les objets Sensor de leJos en precisant le port de branchement
	//ouverture des capteurs
	private static EV3ColorSensor COLOR_SENSOR = new EV3ColorSensor(LocalEV3.get().getPort("S3")); // Port de branchement du capteur de couleurs
	private static EV3TouchSensor TOUCH_SENSOR = new EV3TouchSensor(LocalEV3.get().getPort("S1"));
	private static EV3UltrasonicSensor ULTRASONIC_SENSOR = new EV3UltrasonicSensor(LocalEV3.get().getPort("S2"));
	
	/*Creation et initialisation des objets de type SampleProvider.
	Ils permettent la recuperation des donnees des capteurs.
	Ces objets sont utilises par les autres classes du package pour obtenir les valeurs mesurees*/
	
	//Capteur toucher pour la classe Toucher
	public static SampleProvider TOUCHER = TOUCH_SENSOR.getTouchMode();
	
	//sample providers du capteur de couleur pour la classe Couleur
	public static SampleProvider RGB = COLOR_SENSOR.getRGBMode();
	public static SampleProvider LUMIERE_AMBIANTE = COLOR_SENSOR.getAmbientMode();
	public static SampleProvider ID_COULEUR = COLOR_SENSOR.getColorIDMode();
	public static SampleProvider ROUGE = COLOR_SENSOR.getRedMode();
	
	//sample providers pour la classe Ultrason
	public static SampleProvider ULTRASON = ULTRASONIC_SENSOR.getDistanceMode();
	public static SampleProvider ECOUTE = ULTRASONIC_SENSOR.getListenMode();
	
	//ouvrir capteur
	public static void ouvrirCapteurCouleur() {
		if (COLOR_SENSOR!=null) {
			COLOR_SENSOR.close();
		}
		try {
		COLOR_SENSOR = new EV3ColorSensor(LocalEV3.get().getPort("S3"));
		TOUCH_SENSOR = new EV3TouchSensor(LocalEV3.get().getPort("S1"));
		ULTRASONIC_SENSOR = new EV3UltrasonicSensor(LocalEV3.get().getPort("S2"));
		RGB = COLOR_SENSOR.getRGBMode();
		LUMIERE_AMBIANTE = COLOR_SENSOR.getAmbientMode();
		ID_COULEUR = COLOR_SENSOR.getColorIDMode();
		ROUGE = COLOR_SENSOR.getRedMode();
		ULTRASONIC_SENSOR.getDistanceMode();
		ECOUTE = ULTRASONIC_SENSOR.getListenMode();
		TOUCHER = TOUCH_SENSOR.getTouchMode();
		}catch(IllegalArgumentException e){
			Sound.beepSequenceUp();
			System.err.println(e.toString());
			e.printStackTrace(System.err);
			Capteur.ouvrirCapteurCouleur();
		}
	}
	
	//Methode pour fermer les capteurs ouverts au debut de la classe. 
	public static void fermerCapteurs() {
		COLOR_SENSOR.close();
		TOUCH_SENSOR.close();
		ULTRASONIC_SENSOR.close();
	}
}
