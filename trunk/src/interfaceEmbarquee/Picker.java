package interfaceEmbarquee;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;

public class Picker implements Lancable{
	String titre;
	Configurations configuration;
	
	public Picker(String titre, Configurations c) {
		this.titre=titre;
		this.configuration=c;
	}
	
	//affiche 1 seule configuration à la fois
	//defilement vertical entre les configurations
	//defilement horizontal pour afficher l'integralité de la chaine de caractères
	
	public void lancer() {
		LCD.clear();
		LCD.drawString(titre, 7, 1);
		int button = -1;
		int choix = 1;
		char[] buffer;
		int j,k;
		int debutColonne=0;
		while(button != Button.ID_ESCAPE) {
			LCD.clear(1,2, 100);			
			if (configuration.getVal().equals(configuration.s.get(choix-1))) {
				LCD.drawString("X", 0, 4);
			}
			buffer=configuration.s.get(choix-1).toCharArray();
			for (j=debutColonne, k=2;(j<buffer.length)&&(k<18);j++,k++) {
				LCD.drawChar(buffer[j], k, 4);
			}
			button = Button.waitForAnyPress();
			if (button == Button.ID_UP) {
				choix = ((choix == 1) ? configuration.s.size() :(choix-1)); //fonction précendente
				debutColonne=0;
				//lejos.hardware.Sound.beep();
			}
			else if (button == Button.ID_DOWN) {
				choix = (choix%configuration.s.size())+1; //foncion suivante
				debutColonne=0;
			}
			else if (button == Button.ID_RIGHT) {
				if (configuration.s.get(choix-1).length()>16) { //si le titre est trop grand pour l'ecran
					debutColonne = (15 == (configuration.s.get(choix-1).length()-1)-debutColonne)?debutColonne:++debutColonne;
					//debutColonne = (debutColonne == configuration.s.get(choix-1).length()-1)?debutColonne:++debutColonne;
				}
			}
			else if (button == Button.ID_LEFT) {
				if (configuration.s.get(choix-1).length()>16) { //si le titre est trop grand pour l'ecran
					debutColonne = (debutColonne == 0)?debutColonne:--debutColonne;
				}
			}
			else if (button == Button.ID_ENTER) {
				configuration.setVal(configuration.s.get(choix-1)); //on selectionne la fonction a lancer
			}
			Button.waitForAnyEvent();
		}
	}
	
	public String getTitre() {
		return titre;
	}
}
