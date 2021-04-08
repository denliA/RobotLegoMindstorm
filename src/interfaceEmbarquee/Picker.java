package interfaceEmbarquee;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;

public class Picker implements Lancable{
	String titre;
	Configurations configuration;
	Scenarios scenarios;
	Bruitages bruitages;
	Visages visages;
	
	
	public Picker(String titre, Configurations c) {
		this.titre=titre;
		this.configuration=c;
		this.scenarios=null;
		this.bruitages=null;
		this.visages=null;
	}
	
	public Picker(String titre, Scenarios c) {
		this.titre=titre;
		this.configuration=null;
		this.scenarios=c;
		this.bruitages=null;
		this.visages=null;
	}
	
	public Picker(String titre, Bruitages c) {
		this.titre=titre;
		this.configuration=null;
		this.scenarios=null;
		this.bruitages=c;
		this.visages=null;
	}
	
	public Picker(String titre, Visages c) {
		this.titre=titre;
		this.configuration=null;
		this.scenarios=null;
		this.bruitages=null;
		this.visages=c;
	}
	
	public void lancer() {
		LCD.clear();
		LCD.drawString(titre, 7, 1);
		int button = -1;
		int choix = 1;
		while(button != Button.ID_ESCAPE) {
			LCD.clear(1,2, 100);
			if (configuration.getVal().equals(configuration.s.get(choix-1))) {
				LCD.drawString("X", 1, 4);
			}
			LCD.drawString(configuration.s.get(choix-1), 3, 4);
			button = Button.waitForAnyPress();
			if (button == Button.ID_LEFT) {
				choix = ((choix == 1) ? configuration.s.size() :(choix-1)); //fonction précendente
				//lejos.hardware.Sound.beep();
			}
			else if (button == Button.ID_RIGHT)
				choix = (choix%configuration.s.size())+1; //foncion suivante
			else if (button == Button.ID_ENTER) {
				configuration.setVal(configuration.s.get(choix-1)); //on selectionne la fonction a lancer
			}
			Button.waitForAnyEvent();
		}
	}
	
	public void lancer2() {
		LCD.clear();
		int button = -1;
		int choix = 1;
		char[][] buffer = new char[6][]; //represente la taille de la zone ou seront affiches les lancables
		int i,j,k,l;
		int debutColonne=0;
		int debutLigne=0;
		while(button != Button.ID_ESCAPE) {
			LCD.clear(1,2, 100);
			if (configuration.getVal().equals(configuration.s.get(choix-1))) {
				LCD.drawString("X", 0, choix+1);
			}
			LCD.drawString("->", 2, ((choix%6)==0?6:choix%6)+1);
			LCD.drawString(titre, 3, 0);
			for (i=debutLigne,l=0;i<configuration.s.size()-2;i++,l++) { //i=nombre de lignes
				buffer[i]=configuration.s.get(i).toCharArray(); //taille variable du titre
				for (j=debutColonne, k=0;j<buffer[i].length;j++,k++)
					LCD.drawChar(buffer[i][j], 3+k, l+2);
			}
			button = Button.waitForAnyPress();
			if (button == Button.ID_DOWN) {
				choix = (choix%configuration.s.size())+1;
				if (configuration.s.size()>6&&choix>6) {
					debutLigne = (debutLigne == configuration.s.size()-3)?0:++debutLigne;
				}
			}
			else if (button == Button.ID_UP) {
				choix = (choix == 1) ? configuration.s.size() :(choix-1);
				if (configuration.s.size()>6&&choix>6) {
					debutLigne = (debutLigne == 0)?configuration.s.size()-3:--debutLigne;
				}
			}
			else if (button == Button.ID_ENTER) {
				configuration.setVal(configuration.s.get(choix-1));
			}
			else if (button == Button.ID_RIGHT) {
				if (configuration.s.get(choix-1).length()>15) { //si le titre est trop grand pour l'ecran
					debutColonne = (debutColonne == configuration.s.get(choix-1).length()-1)?debutColonne:++debutColonne;
				}
			}
			else if (button == Button.ID_LEFT) {
				if (configuration.s.get(choix-1).length()>15) { //si le titre est trop grand pour l'ecran
					debutColonne = (debutColonne == 0)?debutColonne:--debutColonne;
				}
			}
			Button.waitForAnyEvent();
		}
	}
	
	public String getTitre() {
		return titre;
	}
}
