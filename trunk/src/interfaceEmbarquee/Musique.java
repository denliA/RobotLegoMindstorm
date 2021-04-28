package interfaceEmbarquee;

import java.io.File;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;

/**
 * <p>Classe qui permet de lancer un son dans un thread et de l'interrompre si besoin.</p>
 * 
 * @see ValeursConfig
 * @see Configurations
 * 
 */

public class Musique{
	
	/**
	 * <p>Declaration de l'unique thread qui lancera un effet sonore. Mis en static pour pouvoir etre interrompu par la suite.</p>
	 * 
	 */
	static Thread t;
	
	/**
	 * <p>Lance le bruitage choisi dans le picker.</p>
	 * <p>Le nom du fichier qui correspond à la Configuration choisie se trouve dans ValeursConfig.</p>
	 * @see Picker
	 * @see Configurations
	 * @see ValeursConfig
	 * 
	 */
	public static void startSound(){
		startMusic(ValeursConfig.bruitagesConfig.get(Configurations.bruitage.getVal()));
	}
	
	/**
	 * <p>Lance la musique choisie dans le picker.</p>
	 * <p>Le nom du fichier qui correspond à la Configuration choisie se trouve dans ValeursConfig.</p>
	 * @see Picker
	 * @see Configurations
	 * @see ValeursConfig
	 * 
	 */
	public static void startMusic(){
		startMusic(ValeursConfig.musiquesConfig.get(Configurations.musique.getVal()));
	}
	
	/**
	 * <p>Lance la musique dont le nom du fichier.wav est passé en paramètres.</p>
	 * <p>Lancer la musique dans un thread permet de le faire en meme temps qu'une autre action du robot sans perturber son fonctionnement.</p>
	 * @param name
	 * 				nom du fichier du son à lancer
	 * 
	 */
	public static void startMusic(final String name) {
		//initialise la variable t et instancie le thread
		t = new Thread(new Runnable() {
			public void run(){
				while(!Thread.interrupted()){
					LCD.clear(); //vide l'écran
					int res;
					File fichier= new File(name);
					if (!fichier.exists()) {
						LCD.drawString("Pas de fichier", 3, 5);
						return; //on n'essaye pas de jouer une musique qui n'est existe pas
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
		//commence l'exécution du thread et donc lance la musique
		t.start();
	}
	
	/**
	 * <p>Tente d'arreter le son en cours d'execution en interrompant le thread t.</p>
	 * 
	 */
	public static void stopMusic(){
		t.interrupt();
    }
}
    