package tests;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

/**
 * <p>Situation initiale : le robot est déposé n'importe où sur la table</p>
 * <p>Situation finale : après 5 min écoulées, le robot arrête son affichage.</p>
 * <p>Le but est de prouver qu'on peut interrompre un mode competition qui dure plus de 5 min. Ici la tache longue est un affichage</p>
 */

public class NFBM5 implements interfaceEmbarquee.Lancable{
	
	public void lancer() {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<Object> future; 
		
		LCD.drawString("Debut match", 3, 4);
		
		//appeler la fonction a executer dans un thread
		future = executor.submit(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				boolean tache = true;
				LCD.clear();
				LCD.drawString("En cours", 4, 4);
				while(tache) {
					//tache bloquante
				}
				return null;
			}
		});
		
		//verifier le temps
		try {  
			future.get(5*60, TimeUnit.SECONDS); //interrompt le thread au bout de 5 min
		}catch (TimeoutException e) {
		future.cancel(true);
	    LCD.clear();
	    LCD.drawString("Fin match", 5, 4);
	    LCD.drawString("cancelled: " + future.isCancelled(),1,7);
	    LCD.drawString("done: " + future.isDone(),1,8); 
	    Delay.msDelay(3*1000);
	    }catch (InterruptedException e) {
	    	e.printStackTrace();
	    }catch (ExecutionException e) {
			e.printStackTrace();
	    }finally {
	    	executor.shutdown();  
		}
	}
	
	public String getTitre() {
		return "NFBM5 - Arret apres 5 min";
	}
	
}