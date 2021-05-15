package tests;
import modeSolo.ModeSolo;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import exceptions.OuvertureException;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

/**
 * <p>Situation initiale : le robot est déposé sur un des six points de départ de la table</p>
 * <p>Situation finale : le robot dépose les 9 palets derrière la ligne blanche de l'adversaire et il ouvre ses pinces.</p>
 * @see ModeSolo
 */

public class NFA7 implements interfaceEmbarquee.Lancable{
	
	public void lancer() {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<Void> future;
		
		//choisir le camp de départ
		boolean camp = true;
		int button = -1;
		LCD.clear();
		LCD.drawString("RougeAGauche?", 3, 1);
		LCD.drawString("vrai <<  >> faux", 1, 3);
		while((button!=Button.ID_LEFT)&&(button!=Button.ID_RIGHT)) {
			button = Button.waitForAnyPress();
		}
		if (button == Button.ID_LEFT) {
			camp=true;
		}
		else if (button == Button.ID_RIGHT) {
			camp=false;
		}
		final boolean cote = camp;
		
		//placer le robot sur une des 6 positions de depart
		LCD.clear();
		LCD.drawString("poser robot sur 1", 1, 1);
		LCD.drawString("position de depart", 1, 2);
		LCD.drawString("pressez sur entree", 1, 5);
		LCD.drawString("pour demarrer", 1, 6);
		while(button!=Button.ID_ENTER) {
			button = Button.waitForAnyPress();
		}
		LCD.clear();
		LCD.drawString("Debut match", 3, 1);
		Delay.msDelay(2000); //laisser le temps de lire
		
		//appeler la fonction a executer dans un thread
		future = executor.submit(new ArgCallable(cote));
		
		//verifier le temps
		try {  
		    future.get(5*60, TimeUnit.SECONDS); //interrompt le thread au bout de 5 min
		} catch (TimeoutException e) {
		    future.cancel(true);
		    LCD.clear();
			LCD.drawString("Fin match", 5, 4);
			LCD.drawString("cancelled: " + future.isCancelled(),1,7);
		    LCD.drawString("done: " + future.isDone(),1,8); 
		    Delay.msDelay(3*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} finally {
			executor.shutdown();  
		}
	}
	
	public String getTitre() {
		return "NFA7 - Chemin predefini 9 palets";
	}
	
}

/**
 * <p>Dans un thread, on n'a pas acces a des variables d'instances d'autres classes or ici, on a besoin de passer la couleur en parametre
 *  de suivreLigne() qui sera executée dans la methode call() du thread</p>
 * 
 * <p>Pour contourner ça, voici une classe qui permet de passer un parametre à un Callable</p>
 */

class ArgCallable implements Callable<Void> {
	boolean truc;
	public ArgCallable(boolean truc) {
		this.truc = truc;
	}
	public Void call() throws OuvertureException {
        modeSolo.ModeSolo.ramasserPalet(9, truc);
        return null;
    }
}

