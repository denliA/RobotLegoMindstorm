package interfaceEmbarquee;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import exceptions.OuvertureException;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;
import modeCompetition.ModeCompetition;

public class Musique{
	static ExecutorService executor = Executors.newSingleThreadExecutor();
	static Future<Void> future;
	
	static Thread t;
	
	
	public static void jouerSon(final String name) throws InterruptedException {
		t = new Thread(new Runnable() {
			public void run() {
				boolean end=false;
	            while((!t.isInterrupted())&&(!end)){
	            	LCD.clear();
	            	int res;
	            	File fichier= new File(name);
					res = Sound.playSample(fichier, Sound.VOL_MAX); //attention : instruction bloquante
					LCD.clear();
					if (res<0) {
						LCD.drawString("Erreur musique", 3, 5);
					}
					else {
						end=true;
					}
	            }
	            if (t.isInterrupted()) {
	            	LCD.clear();
	            	LCD.drawString("Musique interrompue",3,5);
	            	//throw new InterruptedException();
	            }
	            else {
					LCD.drawString("Musique finie",3,5);
				} 
			}
		});
		t.start();		
	}
	
	//lance le bruitage choisi dans le picker
	public static void startSound(){
		startMusic(ValeursConfig.bruitagesConfig.get(Configurations.bruitage.getVal()));
	}
	
	//lance la musique choisie dans le picker
	public static void startMusic(){
		startMusic(ValeursConfig.musiquesConfig.get(Configurations.musique.getVal()));
	}
	
	//lance le son dont le fichier.wav est passé en paramètre
	public static void startMusic(final String name){
		try {
			jouerSon(name);
		} catch (InterruptedException e) {
			LCD.clear();
        	LCD.drawString("Musique interrompue",3,5);
			e.printStackTrace();
		} finally {
			LCD.drawString("Musique finie",3,5);
		}
	}
	
	public static void stopMusic(){
		t.interrupt(); //va tenter d'arreter le thread en cours d'execution
    }
}
    