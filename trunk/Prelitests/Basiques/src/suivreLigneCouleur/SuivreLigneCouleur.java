package suivreLigneCouleur;
import java.io.FileWriter;
import java.io.IOException;
import deplacer.Droit;
import deplacer.Tourner;
import palet.*;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;
import lejos.hardware.Sound;
import lejos.utility.Delay;
import lejos.hardware.motor.BaseRegulatedMotor;
import lejos.hardware.motor.EV3LargeRegulatedMotor;


public class SuivreLigneCouleur {
	final static Port COLOR_SENSOR_PORT = LocalEV3.get().getPort("S3"); // Port de branchement du capteur de couleurs
	final static float INCREMENT = 1.0f; // pas entre chaque mesure
    final static float LONG_LIGNE = 174.0f; // longueur de la partie mesurée
    static EV3ColorSensor color_sensor = new EV3ColorSensor(COLOR_SENSOR_PORT); 
    public static SampleProvider RGB = color_sensor.getRGBMode(); 
    static float value[] = new float[RGB.sampleSize()];
    static FileWriter outputer = null;
    
    final static float targetLigneRougeR = 25f; // a mesurer le rgb sur bord ligne
	final static float KP = 1f; //if small, robot smooth turns
	final static float KI = 1f;
	final static float KD = 1f;
    
	
	public static void mesurerCouleurAff(){
		/* Prise de la mesure */
        RGB.fetchSample(value, 0); 
        /* Affichage de la dernière mesure sur l'écran du robot  */
		LCD.drawString("R : "+value[0]*255, 0, 4);
		LCD.drawString("G : "+value[1]*255, 0, 5);
		LCD.drawString("B : "+value[2]*255, 0, 6);
		//System.out.println("R: "+value[0]*255+"\tG :"+value[1]*255+"\tB : "+value[2]*255);
		Delay.msDelay(200);
	}
	
	public static void mesurerCouleurFich() {
		mesurerCouleurAff();
		/* Ecriture des mesures dans le fichier déjà ouvert */
		try {
			outputer.write(value[0]*255 + "\t"+value[1]*255+"\t"+value[2]*255+"\n");
		} catch (IOException e) {
			System.err.println("Erreur lors de l'écriture de la mesure!");
			e.printStackTrace();
		}	
	}
	
	public static void stopMesure() {
		color_sensor.close();
	}
	
	public static void creerFichier(String nomFichier) {
		/* Création du fichier */
        try {
			outputer = new FileWriter(nomFichier);	
		} catch (IOException e) {
			System.err.println("Erreur à l'ouverture du fichier!");
			e.printStackTrace();
		}
	}
	
	public static void fermerFichier() {
		try {
			outputer.close();
		} catch (IOException e) {
			System.err.println("Erreur à la fermeture du fichier");
			e.printStackTrace();
		}
	}
	
	public static void Ligne(float[] bornes) {
			
		//float Lignejaune_BleuMin = 35; guillaume
		//float Lignejaune_BleuMax = 43;
		int DEFAULT_SPEED = Droit.G.getSpeed();
		long debut;
		//Prise de la mesure 
        RGB.fetchSample(value, 0); 
        //Verification que robot ne sort pas de sa ligne
        if (value[2]*255<bornes[4] || value[2]*255>bornes[5] || value[1]*255<bornes[2] || value[1]*255>bornes[3] || value[0]*255<bornes[0] || value[0]*255>bornes[1]) {
        	Sound.twoBeeps();
        	debut = System.currentTimeMillis();
        	Droit.G.setSpeed((float)(DEFAULT_SPEED*1.2));
        	while ((value[2]*255<bornes[4] || value[2]*255>bornes[5] || value[1]*255<bornes[2] || value[1]*255>bornes[3] || value[0]*255<bornes[0] || value[0]*255>bornes[1]) && System.currentTimeMillis() - debut < 250) {
        		RGB.fetchSample(value, 0);
        	}
        	Droit.G.setSpeed(DEFAULT_SPEED);
        }
        RGB.fetchSample(value, 0); 
        if (value[2]*255<bornes[4] || value[2]*255>bornes[5] || value[1]*255<bornes[2] || value[1]*255>bornes[3] || value[0]*255<bornes[0] || value[0]*255>bornes[1]) {
        	Sound.beep();
        	debut = System.currentTimeMillis();
        	Droit.D.setSpeed((float)(DEFAULT_SPEED*1.2));
        	while ((value[2]*255<bornes[4] || value[2]*255>bornes[5] || value[1]*255<bornes[2] || value[1]*255>bornes[3] || value[0]*255<bornes[0] || value[0]*255>bornes[1]) && System.currentTimeMillis() - debut < 500) {
        		RGB.fetchSample(value, 0);
        	}
        	Droit.D.setSpeed(DEFAULT_SPEED);
        }
	}
	
	public static void Ligne_PID() {
		float DEFAULT_SPEED = Droit.D.getSpeed();
		float MAX_SPEED = Droit.D.getMaxSpeed();
		float ERROR_MARGIN = 0.1f;
		float error = 0f;
		float previousError = 0f;
		float integral = 0f;
		float derivative = 0f;
		float correction = 0f;
		do {
		RGB.fetchSample(value, 0); //input de la couleur mesurée
		error = (targetLigneRougeR - value[0]);
		if (Math.abs(error)>ERROR_MARGIN) { //on ne fait rien si l'erreur est negligeable
			integral += error;
			derivative = error - previousError;
			correction = (error * KP)+(integral * KI)+(derivative * KD); //PID control
			previousError = error;
			// limiter le output 
			if (correction>MAX_SPEED)
				correction = MAX_SPEED;
			else if (correction<-MAX_SPEED)
				correction = -MAX_SPEED;
			// tourner robot
			if (error<0) { // robot sur ligne rouge
				Droit.G.setSpeed(DEFAULT_SPEED+correction);
				Droit.D.setSpeed(DEFAULT_SPEED-correction);
			}
			else if (error>0) { // robot sur zone grise
				Droit.D.setSpeed(DEFAULT_SPEED+correction);
				Droit.G.setSpeed(DEFAULT_SPEED-correction);
			}
		}
		}while(Math.abs(error)>ERROR_MARGIN); // sortie de boucle quand robot s'est redressé sur l'entre ligne rouge-grise
		/* robot avance tout droit */
		Droit.G.setSpeed(DEFAULT_SPEED);
		Droit.D.setSpeed(DEFAULT_SPEED);	
	}
	
	public static void ramenerPaletSolo() throws Exception {
		boolean res = DetecterPalet.detecterPalet();
		if (res) {
			MainSuivreLigne.mPalet = System.currentTimeMillis();
		}
	}
	
	public static void ramenerPaletSoloPID() throws Exception {
		boolean res = DetecterPalet.detecterPalet();
		if (res) {
			Main_PID_LineFollower.foundPalet = true;
		}
	}
}
