package suivreLigneCouleur;
import palet.*;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import deplacer.*;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

class MainSuivreLigne {
	public static volatile boolean keepRunning = true;
	public static long mDebut=0;
	public static long mPalet=0;
	public final static float LimitBlancLigneRougeR = 50f;
	public final static float LimitBlancLigneJauneR = 35f;
	public final static float LimitBlancLigneJauneB = 29f;
	static SuivreLigneCouleur e = new SuivreLigneCouleur();
	
	public static void main(String[] args) {
	long dureeDepl = 13; //secondes
	long dureeRest;
	int acceleration = 1500;
	
	//SuivreLigneCouleur.creerFichier("Ligne_grise.txt");
	
	/* Robot commence à avancer droit */
	Droit.droitMoteur(acceleration);
	/* Mesure le début du mouvement */
	mDebut = System.currentTimeMillis();
	
	
	ScheduledExecutorService service = Executors.newScheduledThreadPool(4);
	service.schedule(new Task1_1(),dureeDepl,TimeUnit.SECONDS); // Programme l'arret du mouvement et de la mesure de couleur
	Future<?> future = service.scheduleWithFixedDelay(new Task3(),0,100,TimeUnit.MILLISECONDS);// Mesure couleur toutes les 0.1 secondes après fin de tache
	service.scheduleWithFixedDelay(new Task1_3(),0,100,TimeUnit.MILLISECONDS); // Detecte palet toutes les 0.1 secondes après fin de tache
	//Future<?> future2 = service.scheduleWithFixedDelay(new Task4_2(),0,100,TimeUnit.MILLISECONDS);
	/*while(keepRunning&&(mPalet==0)) {
	
	}
	if (mPalet!=0) {
		future.cancel(true);
		Sound.beepSequenceUp();
		Delay.msDelay(2000);
		future2.cancel(true);
		dureeRest = dureeDepl - (mPalet - mDebut);
		service.schedule(new Task4_1(),dureeRest,TimeUnit.MILLISECONDS); // Programme l'arret du mouvement et de la mesure de couleur et de contact
	}
	else 
		service.shutdownNow(); // fin du threadpool
		*/
	}
	
}

class Task1_1 implements Runnable{
	public void run() {
		Droit.arreter();
		SuivreLigneCouleur.stopMesure();
		MainSuivreLigne.keepRunning = false;
	}
}
class Task1_2 implements Runnable{
	public void run() {
		SuivreLigneCouleur.mesurerCouleurAff();
	}	
}

class Task1_3 implements Runnable{
	public void run() {
		float [] value = new float[3];
		SuivreLigneCouleur.RGB.fetchSample(value, 0);
		if ((value[0]*255> MainSuivreLigne.LimitBlancLigneJauneR)&&(value[2]*255> MainSuivreLigne.LimitBlancLigneJauneB)) {
			(new Task1_1()).run();
		}
	}	
}

class Task2_1 implements Runnable{
	public void run() {
		Droit.arreter();
		SuivreLigneCouleur.fermerFichier();
		SuivreLigneCouleur.stopMesure();
		MainSuivreLigne.keepRunning = false;
	}
}
class Task2_2 implements Runnable{
	public void run() {
		SuivreLigneCouleur.mesurerCouleurFich();
	}	
}

class Task3 implements Runnable{
	public void run() {
		SuivreLigneCouleur.Ligne();
	}
}

class Task4_1 implements Runnable{
	public void run() {
		Droit.arreter();
		DetecterPalet.lacherPalet();
		DetecterPalet.stopDetection();
		SuivreLigneCouleur.stopMesure();
		MainSuivreLigne.keepRunning = false;
	}
}


class Task4_2 implements Runnable{
	public void run() {
		SuivreLigneCouleur.ramenerPaletSolo();
	}
}


