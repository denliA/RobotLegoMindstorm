package interfaceEmbarquee;

import java.io.File;
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
	
	public static void startMusic(final String nameConfig,final String nameFile) throws InterruptedException{
		t = new Thread(new Runnable() {
			public void run() {
	            while(!t.isInterrupted()){
	            	LCD.clear();
	            	int res;
					File fichier=null;
					if(Configurations.musique.getVal().equals(nameConfig)) {
						fichier = new File(nameFile);
						if (fichier==null) {
							Sound.beep();
							LCD.drawString("Fichier pas ouvert", 3, 2);
							System.out.println("Fichier pas ouvert");
							return; //on sort de la fonction si le fichier n'est pas trouve
						}
						res = Sound.playSample(fichier, Sound.VOL_MAX); //attention : instruction bloquante
						LCD.clear();
						if (res<0)
							LCD.drawString("fichier.waw au mauvais format", 3, 4);
					}
					else {
						System.out.println(nameConfig+" n'est pas la configuration choisie");
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
    