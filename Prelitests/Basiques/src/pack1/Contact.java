package pack1;

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.navigation.DifferentialPilot;

public class Contact {
	public static void main(String[] args) {
		Port portS1 = LocalEV3.get().getPort("S1");
		EV3TouchSensor ts = new EV3TouchSensor(portS1);
		SampleProvider sp = ts.getTouchMode();
		boolean touche = false;
		boolean sortie = false;
		DifferentialPilot pilot = new DifferentialPilot(1.77165,3.90,Motor.A,Motor.B);
		while(Button.ENTER.isUp()) {
			while((touche=is(sp) && !sortie)) {
				pilot.travel(10);
			}
			sortie = true;
		}
	}
	public static boolean is(SampleProvider sp) {
		float[] touch = new float[1];
		sp.fetchSample(touch, 0);
		LCD.drawString(Float.toString(touch[0]), 0, 4);
		return(touch[0]<0.5);
	}
}