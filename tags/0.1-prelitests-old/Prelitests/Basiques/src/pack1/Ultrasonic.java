package pack1;

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

public class Ultrasonic {
	public static void main(String [] args) {
		Port portS2 = LocalEV3.get().getPort("S2");
		EV3UltrasonicSensor us = new EV3UltrasonicSensor(portS2);
		us.enable();
		SampleProvider sp = us.getDistanceMode();
		float [] tab = new float[sp.sampleSize()];
		while (Button.ENTER.isUp()) {
			
			sp.fetchSample(tab, 0);
			for(int i = 0;i<sp.sampleSize(); i++) {
				LCD.drawString(Float.toString(tab[i]), 0, 4+i);
			}
			Delay.msDelay(250);
			//LCD.clear();
		}
		us.close();
	}
}