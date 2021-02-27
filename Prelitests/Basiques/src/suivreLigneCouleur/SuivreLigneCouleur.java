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


public class SuivreLigneCouleur {
	final static Port COLOR_SENSOR_PORT = LocalEV3.get().getPort("S3"); // Port de branchement du capteur de couleurs
	final static float INCREMENT = 1.0f; // pas entre chaque mesure
    final static float LONG_LIGNE = 174.0f; // longueur de la partie mesurée
    static EV3ColorSensor color_sensor = new EV3ColorSensor(COLOR_SENSOR_PORT); 
    public static SampleProvider RGB = color_sensor.getRGBMode(); 
    static float value[] = new float[RGB.sampleSize()];
    static FileWriter outputer = null;
    
	
	public static void mesurerCouleurAff(){
		/* Prise de la mesure */
        RGB.fetchSample(value, 0); 
        /* Affichage de la dernière mesure sur l'écran du robot  */
		LCD.drawString("R : "+value[0]*255, 0, 4);
		LCD.drawString("G : "+value[1]*255, 0, 5);
		LCD.drawString("B : "+value[2]*255, 0, 6);
		//System.out.println("R: "+value[0]*255+"\tG :"+value[1]*255+"\tB : "+value[2]*255);
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
		int default_speed = Droit.G.getSpeed();
		long debut;
		//Prise de la mesure 
        RGB.fetchSample(value, 0); 
        //Verification que robot ne sort pas de sa ligne
        if (value[2]*255<bornes[4] || value[2]*255>bornes[5] || value[1]*255<bornes[2] || value[1]*255>bornes[3] || value[0]*255<bornes[0] || value[0]*255>bornes[1]) {
        	Sound.twoBeeps();
        	debut = System.currentTimeMillis();
        	Droit.G.setSpeed((float)(default_speed*1.2));
        	while ((value[2]*255<bornes[4] || value[2]*255>bornes[5] || value[1]*255<bornes[2] || value[1]*255>bornes[3] || value[0]*255<bornes[0] || value[0]*255>bornes[1]) && System.currentTimeMillis() - debut < 250) {
        		RGB.fetchSample(value, 0);
        	}
        	Droit.G.setSpeed(default_speed);
        }
        RGB.fetchSample(value, 0); 
        if (value[2]*255<bornes[4] || value[2]*255>bornes[5] || value[1]*255<bornes[2] || value[1]*255>bornes[3] || value[0]*255<bornes[0] || value[0]*255>bornes[1]) {
        	Sound.beep();
        	debut = System.currentTimeMillis();
        	Droit.D.setSpeed((float)(default_speed*1.2));
        	while ((value[2]*255<bornes[4] || value[2]*255>bornes[5] || value[1]*255<bornes[2] || value[1]*255>bornes[3] || value[0]*255<bornes[0] || value[0]*255>bornes[1]) && System.currentTimeMillis() - debut < 500) {
        		RGB.fetchSample(value, 0);
        	}
        	Droit.D.setSpeed(default_speed);
        }
        if (value[2]*255<bornes[4] || value[2]*255>bornes[5] || value[1]*255<bornes[2] || value[1]*255>bornes[3] || value[0]*255<bornes[0] || value[0]*255>bornes[1]) {
        	int tmp = Droit.G.getSpeed();
        	int tmp2 = Droit.G.getAcceleration();
        	Droit.arreter();
        	Tourner.toLigne(bornes);
        	Droit.droitMoteur(tmp2, tmp);
        	
        }
	}
	
	public static void LigneRouge1() {
		float Lignejaune_BleuMin = 27;
		float Lignejaune_BleuMax = 35;
		int angle = 10;
		int default_speed = Droit.G.getSpeed();
		//Prise de la mesure 
        RGB.fetchSample(value, 0); 
        //Verification que robot ne sort pas de sa ligne
        System.out.println(value[0]*255);
        RGB.fetchSample(value, 0);
        System.out.println(value[0]*255);
        if (value[0]*255<Lignejaune_BleuMin || value[0]*255>Lignejaune_BleuMax) {
        	System.out.println("entre");
        	//Tourner.turnDiffPilot(angle);
        	Droit.G.setSpeed((float)(default_speed*1.2));
        	Delay.msDelay(250);
        	Droit.G.setSpeed(default_speed);
        }
        RGB.fetchSample(value, 0); 
        System.out.println("Deuxieme : "+value[0]*255);
        if (value[0]*255<Lignejaune_BleuMin || value[0]*255>Lignejaune_BleuMax) {
        	//Tourner.turnDiffPilot(-2*angle);
        	System.out.println("Entree 2");
        	Droit.D.setSpeed((float)(default_speed*1.2));
    		Delay.msDelay(500);
    		Droit.D.setSpeed(default_speed);
        }
	}
	
	public static void ramenerPaletSolo() throws Exception {
		boolean res = DetecterPalet.detecterPalet();
		if (res) {
			System.out.println("Trouvé palet!!");
			MainSuivreLigne.mPalet = System.currentTimeMillis();
			//Droit.arreter();
			//Tourner.turnDiffPilot(180);
			//Tourner.turnMotor(360);
			//Droit.droitMoteur(1500, Droit.DEFAULT_SPEED);
		}
	}
}
