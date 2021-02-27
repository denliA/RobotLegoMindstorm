package palet;
import pince.*;
import deplacer.*;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.robotics.SampleProvider;

public class DetecterPalet {
	final static Port TOUCH_SENSOR_PORT = LocalEV3.get().getPort("S1"); // Port de branchement du capteur de contact
	public static EV3TouchSensor touch_sensor = new EV3TouchSensor(TOUCH_SENSOR_PORT);
	static SampleProvider vtouch = touch_sensor.getTouchMode();
	static float[] touched = new float[vtouch.sampleSize()];
	static int angle = 30;
		
	
	public static boolean detecterPalet() {
		vtouch.fetchSample(touched, 0);
		if (touched[0]==1) {
			//Pince.pinceDegre(500); Contrôle de la pince à l'exterieur 
			//System.out.println("Trouvé palet??");
			return true;
		}
		return false;
		
	}
	
	public static void lacherPalet() {
		Pince.pinceDegre(angle);
	}
	
	public static void stopDetection() {
		touch_sensor.close();
	}
}
