package interfaceEmbarquee;

import java.io.File;

import lejos.hardware.Audio;
import lejos.hardware.BrickFinder;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.internal.ev3.EV3Audio;





public class Musique{
	static Thread t;
	
	//lance le bruitage choisi dans le picker
	public static void startSound(){
		startMusic(ValeursConfig.bruitagesConfig.get(Configurations.bruitage.getVal()));
	}
	
	//lance la musique choisie dans le picker
	public static void startMusic(){
		startMusic(ValeursConfig.musiquesConfig.get(Configurations.musique.getVal()));
	}
	
	//lance le son dont le fichier.wav est passé en paramètre
	public static void startMusic(final String name) {
		t = new Thread(new Runnable() {
			public void run(){
				while(!Thread.interrupted()){
					LCD.clear();
					int res;
					File fichier= new File(name);
					if (!fichier.exists()) {
						return; //On n'essaye pas de jouer une musique qui n'est existe pas
					}
					res = Sound.playSample(fichier, Sound.VOL_MAX); //attention : instruction bloquante
					if (res<0) {
						LCD.drawString("Prob jouer musique", 3, 5);
					}
					else {
						LCD.drawString("Musique finie", 3, 5);
						return;
					}
				}
			}
		});
		t.start();		
	}
	
	public static void stopMusic(){
		t.interrupt(); //va tenter d'arreter le thread en cours d'execution
    }
}
    