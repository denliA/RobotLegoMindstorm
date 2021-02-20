


package linesDataCollection;
import lejos.hardware.sensor.*;
import lejos.robotics.SampleProvider;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.port.Port;
import lejos.hardware.lcd.*;
import lejos.hardware.motor.Motor;
import lejos.hardware.motor.NXTRegulatedMotor;

import java.io.*;


import lejos.hardware.Button;


public class LineScanner {
	
	final static double DIST_ROUES_INCH = 3.90; // Pour le DifferentialPilot, mesure approximative 
	final static double DIAM_ROUE_INCH = 1.77165; // //
	final static NXTRegulatedMotor ROUE_GAUCHE = Motor.A; // Port de branchement du moteur de la roue gauche
	final static NXTRegulatedMotor ROUE_DROITE = Motor.B; // Roue droite
	final static Port COLOR_SENSOR_PORT = LocalEV3.get().getPort("S3"); // Port de branchement du capteur de couleurs
	
	final static double inch_in_cm = 1 / 2.54; // Pour les fonctions de leJOS traitant des floats 
	final static String NOM_FICHIER = "ligne_%s.txt"; // nom du fichier en sortie 
	enum LIGNES {
		ROUGE ,
		BLEUE ,
		VERTE ,
		BLANCHE_PORTE,
		BLANCHE_FENETRE,
		NOIRE_LONGUE,
		NOIRE_LARGE
	}; // Différentes lignes à mesurer
	final static LIGNES choix = LIGNES.BLEUE;
	final static float INCREMENT = 1.0f; // pas entre chaque mesure
	final static float LONG_LIGNE = 174.0f; // longueur de la partie mesurée 
	public static void main(String [] args) {
		
		// Pilote (obsolète, à remplacer au plus tôt par MovePilot) contrôlant les moteurs pour avancer et tourner 
		DifferentialPilot pilot = new DifferentialPilot(DIAM_ROUE_INCH, DIST_ROUES_INCH, Motor.A, Motor.B); 
		
		
		FileWriter outputer = null;
        try {
			outputer = new FileWriter(String.format(NOM_FICHIER, choix.toString().toLowerCase()));
			outputer.write("\tLigne: %s\t\tPas: %fcm\t\tLongueur mesurée:%f\n".formatted(choix.toString(), INCREMENT, LONG_LIGNE));
			outputer.write("Mesure\tRouge\tVert\tBleu\n");
		} catch (IOException e) {
			System.err.println("Erreur à l'ouverture du fichier!");
			e.printStackTrace();
		}

		EV3ColorSensor color_sensor = new EV3ColorSensor(COLOR_SENSOR_PORT); // Object abstrait des appareils de mesure
		SampleProvider RGB = color_sensor.getRGBMode(); // Objet abstrait modélisant un mode da capture particulier de l'appariel (ici le mode RGB)
		float value[] = new float[RGB.sampleSize()]; // RGB.sampleSize() retourne le nombre de floats retournés par *une* capture à un instant t 
		
		int count = 0, stop_count = (int)(LONG_LIGNE / INCREMENT)+2; // nombre de mesures prises courant et d'arrêt
		while (Button.ENTER.isUp() && count <= stop_count) {
			count++;
			
			RGB.fetchSample(value, 0); // Prise de la mesure, met trois floats dans value, contenant les intensités rouge vert bleu normalisées entre 0 et 1
			
			/* Affichage de la dernière mesure sur l'écran du robot  */
			LCD.drawString("R : "+value[0]*255, 0, 4);
			LCD.drawString("G : "+value[1]*255, 0, 5);
			LCD.drawString("B : "+value[2]*255, 0, 6);
			
			
			try { //  écriture des mesures dans le fichier
				outputer.write(value[0]*255 + "\t"+value[1]*255+"\t"+value[2]*255+"\n");
			} catch (IOException e) {
				System.err.println("Erreur lors de l'écriture de la %iième mesure!".formatted(count));
				e.printStackTrace();
			}
			pilot.travel(INCREMENT*inch_in_cm); // Avancer d'un pas
		}
		
		try {
			outputer.close();
		} catch (IOException e) {
			System.err.println("Erreur dans la fermeture du fichier");
			e.printStackTrace();
		}
		color_sensor.close();
	}
	

}
