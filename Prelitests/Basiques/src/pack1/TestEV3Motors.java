package pack1;

//import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.utility.*;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;

public class TestEV3Motors {
	static EV3LargeRegulatedMotor moteur_gauche = new EV3LargeRegulatedMotor(MotorPort.A);
	static EV3LargeRegulatedMotor moteur_droite = new EV3LargeRegulatedMotor(MotorPort.B);
	static EV3MediumRegulatedMotor moteur_pince = new EV3MediumRegulatedMotor(MotorPort.C);
	static {
		moteur_gauche.synchronizeWith(new EV3LargeRegulatedMotor[] {moteur_droite});
	}
	public static void main (String [] args) {
		int gauche_speed=360, gauche_acceleration=360, droite_speed=360, droite_acceleration=6000, pince_speed=36000, pince_acceleration=6000, pince_rotation=360;
		int avancer_delay=3000, pince_delay=1000;
		int choice = 1;
		int button;
		
		
		while (Button.ESCAPE.isUp()) {
			LCD.drawString("Mode?", 7, 1); //17x8
			LCD.drawString("->", 1, choice+1);
			LCD.drawString("Pince", 3, 2);
			LCD.drawString("Roues", 3, 3);
			button = Button.waitForAnyPress();
			if (button == Button.ID_DOWN || button == Button.ID_UP)
				choice = (choice%2)+1;
			else if (button == Button.ID_ENTER) {
				if (choice == 1)
					menuPince(pince_speed, pince_acceleration, pince_rotation, pince_delay);
				else 
					menuRoues(new int[] {gauche_speed,gauche_acceleration, droite_speed, droite_acceleration, avancer_delay, 0},
							new int[] {1,1,1,1,50,1},
							new String[] {"Vitesse gauche", "Acceleration gauche", "Vitesse droite", "Acceleration drote", "DélaiOUangle", "rotate?"});
				Button.waitForAnyEvent();
			}
			
		}
/*	
		while (Button.ESCAPE.isUp()) {
			if ((def = intButtonInput(def, 1)) == -1)
				continue;
			delay = intButtonInput(delay,10);
			moteur_gauche.setSpeed(def);
			moteur_droite.setSpeed(720);
			moteur_gauche.setAcceleration(720);
			moteur_droite.setAcceleration(720);
			moteur_droite.forward();
			moteur_gauche.forward();
			Delay.msDelay(delay);
			
			moteur_gauche.setSpeed(0);
			moteur_droite.setSpeed(0);
			
		}
 */	
	}
	
	public static void menuPince(int pince_speed, int pince_acceleration, int pince_rotation, int pince_delay) {
		LCD.clear();
		LCD.drawString("Pince", 7, 1);
		int button;
		boolean rotate = false;
		while(Button.ESCAPE.isUp()) {
			LCD.clear(1,2, 100);
			int choix = 1;
			LCD.drawString("->", 1, choix+1);
			LCD.drawString("Vitesse: "+pince_speed, 3, 2);
			LCD.drawString("Acceleration: "+pince_acceleration, 3, 3);
			LCD.drawString("temps : "+pince_delay, 3, 4);
			LCD.drawString("Rotation de : "+pince_rotation, 3,5);
			LCD.drawString("Rotate? " +rotate,3,6);
			button = Button.waitForAnyPress();
			if (button == Button.ID_DOWN) {
				choix = (choix%5)+1;
			}
			else if (button == Button.ID_UP)
				choix = (choix == 1) ? 5 :(choix-1);
			else if (button == Button.ID_ENTER) {
				switch (choix) {
				case 1:
					pince_speed = intButtonInput(pince_speed, 10, "Vitesse pince");
					break;
				case 2:
					pince_acceleration = intButtonInput(pince_acceleration, 10, "Acceleration pince");
					break;
				case 3:
					pince_delay = intButtonInput(pince_delay, 10,"Temps pince");
					break;
				case 4:
					pince_rotation = intButtonInput(pince_rotation, 10, "Rotation pince");
				}
			}
			else if (button == Button.ID_LEFT || button == Button.ID_RIGHT) {
				moteur_pince.setSpeed(pince_speed);
				moteur_pince.setAcceleration(pince_acceleration);
				if(!rotate) {
					if (button==Button.ID_LEFT)
						moteur_pince.backward();
					else
						moteur_pince.forward();
					Delay.msDelay(pince_delay);
				}
				else {
					if(button==Button.ID_LEFT)
						moteur_pince.rotate(pince_delay, true);
					else
						moteur_pince.rotate(-pince_delay,true);
				}
			}
			Button.waitForAnyEvent();
			
		}
		
	}
	// options; {"Vitesse gauche", "Acceleration gauche", "Vitesse droite", "Acceleration drote", "DélaiOUangle", "rotate?"}
	public static void menuRoues(int [] options, int[] pas, String [] noms) {
		LCD.clear();
		LCD.drawString("Roues", 8, 1);
		int choix = 1, button;
		while (Button.ESCAPE.isUp()) {
			LCD.clear(1, 2, 100);
			LCD.drawString("->", 1, choix+1);
			button=Button.waitForAnyPress();
			boolean rotate = options[5]>0;
			for (int i=0; i<options.length; i++) {
				LCD.drawString(noms[i]+" "+options[i], 3, i+2);
			}
			if (button == Button.ID_DOWN) {
				choix = (choix%options.length)+1;
			}
			else if (button == Button.ID_UP)
				choix = (choix == 1) ? options.length :(choix-1);
			else if (button == Button.ID_ENTER) {
				options[choix-1] = intButtonInput(options[choix-1], pas[choix-1], noms[choix-1]);
			}
			else if (button == Button.ID_LEFT || button == Button.ID_RIGHT) {
				LCD.clear(1,2,100);
				moteur_gauche.setSpeed(options[0]);
				moteur_gauche.setAcceleration(options[1]);
				moteur_droite.setSpeed(options[2]);
				moteur_droite.setAcceleration(options[3]);
				if(!rotate) {
					moteur_gauche.startSynchronization();
					if (button==Button.ID_LEFT) {
						moteur_gauche.backward();
						moteur_droite.backward();
					}
					else {
						moteur_gauche.forward();
						moteur_droite.forward();
					}
					moteur_gauche.endSynchronization();
					Delay.msDelay(options[4]);
					moteur_gauche.startSynchronization();
					moteur_gauche.stop(true);
					moteur_droite.stop(true);
					moteur_gauche.endSynchronization();
					
				}
				else {
					moteur_gauche.startSynchronization();
					if(button==Button.ID_LEFT) {
						moteur_gauche.rotate(options[4], true);
						moteur_droite.rotate(options[4], true);
					}
					else {
						moteur_gauche.rotate(options[4], true);
						moteur_droite.rotate(options[4], true);
					}
					moteur_gauche.endSynchronization();
				}
				LCD.drawString("Gauche: " + moteur_gauche.getTachoCount(), 1, 3);
				LCD.drawString("Droite: " + moteur_droite.getTachoCount(), 1, 4);
			}
			Button.waitForAnyEvent();
		}
		Button.waitForAnyEvent();
		
	}
	
	
	public static int intButtonInput(int default_value, int pas, String name) {
		LCD.clear();
		LCD.drawString(name, 4, 1);
		LCD.drawInt(default_value, 3, 4);
		Delay.msDelay(500);
		while (Button.ENTER.isUp()) {
			//int button = Button.waitForAnyPress();
			if (Button.ESCAPE.isDown()) 
				return -1;
			else if (Button.DOWN.isDown()) {
				default_value -=10*pas;
				LCD.drawInt(default_value, 3, 4);
				Delay.msDelay(50);
			}
			else if (Button.UP.isDown()) {
				default_value += 10*pas;
				LCD.drawInt(default_value, 3, 4);
				Delay.msDelay(50);
			}
			else if (Button.RIGHT.isDown()) {
				default_value+=1*pas;
				LCD.drawInt(default_value, 3, 4);
				Delay.msDelay(50);
				
			}
			else if (Button.LEFT.isDown()) {
				default_value-= pas;
				LCD.drawInt(default_value, 3, 4);
				Delay.msDelay(50);
			}
		}
		Button.waitForAnyEvent();
		return default_value;
	}
}
