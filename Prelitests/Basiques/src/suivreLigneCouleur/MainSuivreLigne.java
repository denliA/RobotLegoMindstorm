package suivreLigneCouleur;
import palet.*;
import pince.Pince;

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
	public final static float [] LigneRougeBornes = { 19f, 35f, 6f, 10f, 3f, 8f }; //{ 27f, 35f, 7f, 10f, 4f, 8f };
	public final static float [] LigneJauneBornes = { 0f, 255f, 0f, 255f, 9.5f, 11.5f};
	public final static float LimitBlancLigneRougeR = 42f; // 50
	public final static float LimitBlancLigneJauneR = 35f;
	public final static float LimitBlancLigneJauneB = 29f;
	public static volatile boolean gotToWhite = false;
	static SuivreLigneCouleur e = new SuivreLigneCouleur();
	
	public static void main(String[] args) {
		long dureeDepl = 13; //secondes
		long dureeRest;
		int acceleration = 1500;
		ScheduledExecutorService service = Executors.newScheduledThreadPool(4);
		//service.scheduleWithFixedDelay(new Task1_2(), 0, 200, TimeUnit.MILLISECONDS); // afficher les couleurs
		//SuivreLigneCouleur.creerFichier("Ligne_grise.txt");
		/* Mesure le début du mouvement */
		mDebut = System.currentTimeMillis();
		
		
		//service.schedule(new Task1_1(),dureeDepl,TimeUnit.SECONDS); // Programme l'arret du mouvement et de la mesure de couleur
		for (int i = 0; i<3;i++) {
			/* Robot commence à avancer droit */

			Droit.droitMoteur(acceleration, Droit.DEFAULT_SPEED);
			Future<?> suivre_couleur = service.scheduleWithFixedDelay(new Task3(),0,100,TimeUnit.MILLISECONDS);// Mesure couleur toutes les 0.1 secondes après fin de tache
			Future<?> ligne_blanche = service.scheduleWithFixedDelay(new Task1_3(),0,100,TimeUnit.MILLISECONDS); // Detecte palet toutes les 0.1 secondes après fin de tache
			Future<?> palet = service.scheduleWithFixedDelay(new Task4_2(),0,100,TimeUnit.MILLISECONDS);
			while(keepRunning&&(mPalet==0)) {
			
			}
			if (mPalet!=0) {
				//System.out.println("on entre dans la phase de tournage");
				suivre_couleur.cancel(true);
				palet.cancel(true);
				Droit.arreter();
				//System.out.println("Fermeture de la pince");
				Pince.pinceDegre(-500);
				//System.out.println("Pince fermée, tournage");
				Tourner.toLigne(LigneRougeBornes, 200);
				//dureeRest = dureeDepl - (mPalet - mDebut);
				//service.schedule(new Task4_1(),dureeRest,TimeUnit.MILLISECONDS); // Programme l'arret du mouvement et de la mesure de couleur et de contact
				//System.out.println("Tournage fini, avancement jusqu'à blanc");
				Droit.droitMoteur(acceleration, Droit.DEFAULT_SPEED);
				suivre_couleur = service.scheduleWithFixedDelay(new Task3(),0,100,TimeUnit.MILLISECONDS);
				while (!gotToWhite)
					;
				Sound.beepSequence();
				//System.out.println("Blanc atteint");
				suivre_couleur.cancel(true);
				Droit.arreter();
				//Droit.droitMoteur(acceleration, Droit.DEFAULT_SPEED/4);
				//Delay.msDelay(500);
				//Droit.arreter();
				Pince.pinceDegre(500);
				Droit.droitMoteur(acceleration, -Droit.DEFAULT_SPEED);
				Delay.msDelay(400);
				Droit.arreter();
				Tourner.toLigne(LigneRougeBornes, 200);
				mPalet = 0;
			}
			else 
				service.shutdownNow(); // fin du threadpool
		}
	
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
		MainSuivreLigne.gotToWhite = (value[0]*255> MainSuivreLigne.LimitBlancLigneRougeR)/*&&(value[2]*255> MainSuivreLigne.LimitBlancLigneJauneB*/;
		if ((value[0]*255> MainSuivreLigne.LimitBlancLigneRougeR)/*&&(value[2]*255> MainSuivreLigne.LimitBlancLigneJauneB)*/) {
			//(new Task1_1()).run();
			
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
		SuivreLigneCouleur.Ligne(MainSuivreLigne.LigneRougeBornes);
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
	public void run(){
		try {
			SuivreLigneCouleur.ramenerPaletSolo();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}


