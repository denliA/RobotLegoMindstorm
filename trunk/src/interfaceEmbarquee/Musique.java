package interfaceEmbarquee;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

public class Musique{
	static ExecutorService executor = Executors.newSingleThreadExecutor();
	static Future<Void> future;
	
	static Thread t;
	
	
	
	
	//lance le bruitage choisi dans le picker
	public static void startSound() throws InterruptedException{
		startMusic(ValeursConfig.bruitagesConfig.get(Configurations.bruitage.getVal()));
	}
	
	//lance la musique choisie dans le picker
	public static void startMusic() throws InterruptedException{
		startMusic(ValeursConfig.musiquesConfig.get(Configurations.musique.getVal()));
	}
	
	//lance le son dont le fichier.wav est passé en paramètre
	public static void startMusic(final String name) throws InterruptedException{
		t = new Thread(new Runnable() {
			public void run() {
				boolean end=false;
	            while((!t.isInterrupted())&&(!end)){
	            	LCD.clear();
	            	int res;
	            	File fichier=null;
	            	fichier = new File(name);
					if (fichier==null) {
						LCD.drawString("Fichier pas ouvert", 3, 5);
						return; //on sort de la fonction si le fichier n'est pas trouve
					}
					res = Sound.playSample(fichier, Sound.VOL_MAX); //attention : instruction bloquante
					LCD.clear();
					if (res<0) {
						LCD.drawString("Erreur musique", 3, 5);
					}
					else {
						LCD.drawString("Musique finie",3,5);
						end=true;
					} 
	            }
			}
		});
		t.start();
}
	public static void stopMusic() {
		t.interrupt(); //va tenter d'arreter le thread en cours d'execution
    }
}
    