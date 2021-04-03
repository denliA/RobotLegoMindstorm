package interfaceEmbarquee;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

public class InterfaceEmbarquee {
	
	public static void menuPrincipal() {
		LCD.clear();
		LCD.drawString("Pince", 7, 1);
		int button = -1;
		//int rotate = 0;
		int choix = 1;
		while(button != Button.ID_ESCAPE) {
			LCD.clear(1,2, 100);
			//Delay.msDelay(200);
			LCD.drawString("->", 1, choix+1);
			LCD.drawString("Mode solo", 3, 2);
			LCD.drawString("Mode competition", 3, 3);
			button = Button.waitForAnyPress();
			if (button == Button.ID_DOWN) {
				choix = (choix%5)+1;
				//lejos.hardware.Sound.beep();
			}
			else if (button == Button.ID_UP)
				choix = (choix == 1) ? 5 :(choix-1);
			else if (button == Button.ID_ENTER) {
				switch (choix) {
				case 1:
					modeSolo();
					break;
				case 2:
					modeCompetition();
					break;
				}
			}
			Button.waitForAnyEvent();
		}
		
	}
	
	public static void modeSolo() {
		
	}
	
	public static void modeCompetition() {
		
	}
	
	
}
