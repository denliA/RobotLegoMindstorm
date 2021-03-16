package suivreLigneCouleur;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import deplacer.Droit;
import lejos.hardware.Button;
import palet.DetecterPalet;

public class Main_PID_LineFollower {
	public static volatile boolean foundPalet = false;
	public static volatile boolean gotToWhite = false;
	static int scoredPalet = 0;
	static int maxPalet = 3;
	static int acceleration = 1500;
	static SuivreLigneCouleur e = new SuivreLigneCouleur();
	
	public static void main(String[] args) {
		
		ScheduledExecutorService service = Executors.newScheduledThreadPool(3);
		Future<?> suivre_couleur = service.scheduleWithFixedDelay(new TaskA(),0,1,TimeUnit.MILLISECONDS);
		Future<?> ligne_blanche = service.scheduleWithFixedDelay(new TaskB(),0,100,TimeUnit.MILLISECONDS);
		//Future<?> palet = service.scheduleWithFixedDelay(new TaskC(),0,100,TimeUnit.MILLISECONDS);
		
		Droit.droitMoteur(acceleration, Droit.DEFAULT_SPEED);
		while(!gotToWhite);
		SuivreLigneCouleur.stopMesure(); // ferme port du capteur couleur
		Droit.fermerMoteur(); // ferme port du moteur
		service.shutdownNow(); // fin du threadpool
		
	/*	
	while( (gotToWhite&&!(foundPalet)) || (scoredPalet==maxPalet) ); // on sort si le robot a atteint la ligne blanche sans trouver de palet ou si il ramene tous les palets
	Droit.arreter();
	SuivreLigneCouleur.stopMesure(); // ferme port du capteur couleur
	Droit.fermerMoteur(); // ferme port du moteur
	service.shutdownNow(); // fin du threadpool
	*/
	}
}
	

class TaskA implements Runnable{
	
	public void run() {
		SuivreLigneCouleur.Ligne_PID();
	}
}

class TaskB implements Runnable{
	
	public void run() {
		float [] value = new float[3];
		SuivreLigneCouleur.RGB.fetchSample(value, 0);
		Main_PID_LineFollower.gotToWhite = (value[0]*255> MainSuivreLigne.LimitBlancLigneRougeR); //True si robot sur blanc
	}
}

class TaskC implements Runnable{
	public void run(){
		try {
			SuivreLigneCouleur.ramenerPaletSoloPID();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}