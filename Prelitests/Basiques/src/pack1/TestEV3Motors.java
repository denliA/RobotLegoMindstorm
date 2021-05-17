package pack1;

//import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.MovePilot;
import lejos.utility.*;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;

public class TestEV3Motors {
  	static EV3LargeRegulatedMotor moteur_gauche = new EV3LargeRegulatedMotor(MotorPort.B);
	static EV3LargeRegulatedMotor moteur_droite = new EV3LargeRegulatedMotor(MotorPort.A);
	static EV3MediumRegulatedMotor moteur_pince = new EV3MediumRegulatedMotor(MotorPort.C);
	final static double DIST_ROUES_INCH = 12.280002254568; // Pour le DifferentialPilot, mesure approximative 
    final static double DIAM_ROUE_INCH = 5.6;
	static double trackWidth = DIST_ROUES_INCH;
	static double leftWheelDiameter = DIAM_ROUE_INCH*1.002;
	static double rightWheelDiameter = DIAM_ROUE_INCH;
	static MovePilot pilot = new MovePilot (new WheeledChassis(
			new WheeledChassis.Modeler[] { 
				WheeledChassis.modelWheel(moteur_gauche, leftWheelDiameter).offset(trackWidth / 2).invert(false),
				WheeledChassis.modelWheel(moteur_droite, rightWheelDiameter).offset(-trackWidth / 2).invert(false) },
			WheeledChassis.TYPE_DIFFERENTIAL));
	static {
		moteur_gauche.synchronizeWith(new EV3LargeRegulatedMotor[] {moteur_droite});
	}
	public static void main (String [] args) {
		int gauche_speed=360, gauche_acceleration=3000, droite_speed=360, droite_acceleration=3000, pince_speed=36000, pince_acceleration=6000, pince_rotation=360;
		int avancer_delay=3000, pince_delay=1000;
		int choice = 1;
		int button;
		
		
		while (Button.ESCAPE.isUp()) {
			LCD.drawString("Mode?", 7, 1); //17x8
			LCD.drawString("->", 1, choice+1);
			LCD.drawString("Pince", 3, 2);
			LCD.drawString("Roues", 3, 3);
			button = Button.waitForAnyPress();
			if (button == Button.ID_DOWN || button == Button.ID_UP) {
				LCD.clear(1, choice+1, 2); 
				choice = (choice%2)+1;
			}
			else if (button == Button.ID_ENTER) {
				if (choice == 1)
					menuPince(pince_speed, pince_acceleration, pince_rotation, pince_delay);
				else 
					menuRoues(new int[] {gauche_speed,gauche_acceleration, droite_speed, droite_acceleration, avancer_delay, 0},
							new int[] {1,1,1,1,50,1},
							new String[] {"VitG", "AccG", "VitD", "AccD", "DélaiOUangle", "rotate?"});
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
		int button = -1;
		int rotate = 0;
		int choix = 1;
		while(button != Button.ID_ESCAPE) {
			LCD.clear(1,2, 100);
			//Delay.msDelay(200);
			LCD.drawString("->", 1, choix+1);
			LCD.drawString("Vit: "+pince_speed, 3, 2);
			LCD.drawString("Acc: "+pince_acceleration, 3, 3);
			LCD.drawString("T : "+pince_delay, 3, 4);
			LCD.drawString("Rot: "+pince_rotation, 3,5);
			LCD.drawString("Rot? " +rotate,3,6);
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
					pince_speed = intButtonInput(pince_speed, 1, "Vitesse pince");
					break;
				case 2:
					pince_acceleration = intButtonInput(pince_acceleration, 1, "Acceleration pince");
					break;
				case 3:
					pince_delay = intButtonInput(pince_delay, 10,"Temps pince");
					break;
				case 4:
					pince_rotation = intButtonInput(pince_rotation, 10, "Rotation pince");
				case 5:
					rotate = intButtonInput(rotate, 1, "Rotate?");
				}
			}
			else if (button == Button.ID_LEFT || button == Button.ID_RIGHT) {
				moteur_pince.setSpeed(pince_speed);
				moteur_pince.setAcceleration(pince_acceleration);
				if(rotate >0) {
					if (button==Button.ID_LEFT)
						moteur_pince.backward();
					else
						moteur_pince.forward();
					Delay.msDelay(pince_delay);
					moteur_pince.setSpeed(0);
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
		int choix = 1, button=-1;
		while (button != Button.ID_ESCAPE) {
			LCD.clear(1, 2, 100);
			LCD.drawString("->", 1, choix+1);
			for (int i=0; i<options.length; i++) {
				LCD.drawString(noms[i]+" "+options[i], 3, i+2);
			}
			button=Button.waitForAnyPress();
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
//				moteur_gauche.setSpeed(options[0]);
//				moteur_gauche.setAcceleration(options[1]);
//				moteur_droite.setSpeed(options[2]);
//				moteur_droite.setAcceleration(options[3]);
//				if(options[5]==0) {
//					moteur_gauche.startSynchronization();
//					if (button==Button.ID_LEFT) {
//						moteur_gauche.backward();
//						moteur_droite.backward();
//					}
//					else {
//						moteur_gauche.forward();
//						moteur_droite.forward();
//					}
//					moteur_gauche.endSynchronization();
//					Delay.msDelay(options[4]);
//					moteur_gauche.startSynchronization();
//					moteur_gauche.setSpeed(0);
//					moteur_droite.setSpeed(0);
//					moteur_gauche.endSynchronization();
//					
				pilot.travel(60);
				}
				else {
					moteur_gauche.startSynchronization();
					if(button==Button.ID_LEFT) {
						moteur_gauche.rotate(options[4], true);
						moteur_droite.rotate(options[4], true);
					}
					else {
						moteur_gauche.rotate(-options[4], true);
						moteur_droite.rotate(-options[4], true);
					}
					moteur_gauche.endSynchronization();
				}
				LCD.drawString("Gauche: " + moteur_gauche.getTachoCount(), 1, 3);
				LCD.drawString("Droite: " + moteur_droite.getTachoCount(), 1, 4);
				Button.waitForAnyEvent();
				moteur_gauche.resetTachoCount();
				moteur_droite.resetTachoCount();
				Button.waitForAnyEvent();
			}
			Button.waitForAnyEvent();
		}
		//Button.waitForAnyEvent();
		
	//}
	
	
	public static int intButtonInput(int default_value, int pas, String name) {
		LCD.clear();
		LCD.drawString(name, 4, 1);
		LCD.drawInt(default_value, 3, 4);
		Delay.msDelay(500);
		int button = -1, event=-1;
		while (button != Button.ID_ENTER) {
			if (event!=0)
				button = Button.waitForAnyPress();
			if (Button.ESCAPE.isDown()) 
				return -1;
			else if (button == Button.ID_DOWN) {
				default_value -=10*pas;
			}
			else if (button==Button.ID_UP) {
				default_value += 10*pas;
			}
			else if (button == Button.ID_RIGHT) {
				default_value+=1*pas;
				
			}
			else if (button == Button.ID_LEFT) {
				default_value-= pas;
			}
			LCD.clear(3,4,10);
			LCD.drawInt(default_value, 3, 4);
			event = Button.waitForAnyEvent(50);
		}
		return default_value;
	}
}
