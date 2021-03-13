package capteurs;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.sensor.*;
import lejos.robotics.SampleProvider;


public class Capteur {
	final private static EV3ColorSensor COLOR_SENSOR = new EV3ColorSensor(LocalEV3.get().getPort("S3")); // Port de branchement du capteur de couleurs
	final private static EV3TouchSensor TOUCH_SENSOR = new EV3TouchSensor(LocalEV3.get().getPort("S1"));
	final private static EV3UltrasonicSensor ULTRASONIC_SENSOR = new EV3UltrasonicSensor(LocalEV3.get().getPort("S2"));
	final public static SampleProvider TOUCHER = TOUCH_SENSOR.getTouchMode();
	final public static SampleProvider RGB = COLOR_SENSOR.getRGBMode();
	final public static SampleProvider LUMIERE_AMBIANTE = COLOR_SENSOR.getAmbientMode();
	final public static SampleProvider ID_COULEUR = COLOR_SENSOR.getColorIDMode();
	final public static SampleProvider ROUGE = COLOR_SENSOR.getRedMode();
	final public static SampleProvider ULTRASON = ULTRASONIC_SENSOR.getDistanceMode();
	final public static SampleProvider ECOUTE = ULTRASONIC_SENSOR.getListenMode();
	
}
