//		DifferentialPilot dp = new DifferentialPilot(1.77165, 3.90, Motor.A, Motor.B);
package pack1;

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

public class Color {
	public static void main(String [] args) {
		Port portS3 = LocalEV3.get().getPort("S3");
		EV3ColorSensor cs = new EV3ColorSensor(portS3);
		SampleProvider sp = cs.getRGBMode();
		float [] tab = new float[sp.sampleSize()];
		while (Button.ENTER.isUp()) {
			
			sp.fetchSample(tab, 0);
			for(int i = 0;i<sp.sampleSize(); i++) {
				LCD.drawString(Float.toString(tab[i]), 0, 4+i);
			}
			Delay.msDelay(250);
			//LCD.clear();
		}
		//cs.close();
	}
}
