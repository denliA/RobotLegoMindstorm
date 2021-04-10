package interfaceEmbarquee;

import java.io.File;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;

public class Musique{
    private static volatile boolean running = true;
    
    public static boolean getRunning() {
    	return running;
    }

    public static void setRunning(boolean b) {
    	running = b;
    }
    
    public static void stopMusic() {
        running = false;
    }
    
    public static void startMusic(final String nameConfig,final String nameFile) throws InterruptedException{
    	Thread t = new Thread(new Runnable(){
            public void run() {
                while(!Thread.currentThread().isInterrupted()){
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
    					res = Sound.playSample(fichier, Sound.VOL_MAX);
    					if (res<0)
    						System.out.println("fichier.waw au mauvais format");
    						LCD.drawString("fichier.waw au mauvais format", 3, 4);
    					
    				}
    				else {
    					System.out.println(nameConfig+" n'est pas la configuration choisie");
    				}
    				running=false; //la musique s'est jouée jusqu'au bout
                }   
            }});
    	
    	t.start();
        running=true;
        while(running);//j'attends la fin de l'execution
     
        // Sleep a second, and then interrupt
        try {
            Thread.sleep(1000); //seuls les threads en etat de sleep ou de wait arretent immediatement leur executions si interrompus
        } catch (InterruptedException e) {}
        t.interrupt();
    }
}
