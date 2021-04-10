package interfaceEmbarquee;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;

public class Menu implements Lancable{
	String titre;
	Lancable[] tab;
	
	public Menu(String titre,Lancable tab[]) {
		this.titre=titre;
		this.tab = tab;
	}
	
	public Menu(String titre) {
		this.titre=titre;
		this.tab = null;
	}
	
	public String getTitre() {
		return titre;
	}
	
	public void setTab(Lancable tab[]) {
		this.tab = tab;
	}
	
	public void lancer2() {
		//18 caracteres par ligne
		//8 lignes
		LCD.clear();
		int button = -1;
		int choix = 1;
		char[][] buffer = new char[6][]; //represente la taille de la zone ou seront affiches les lancables
		int i,j,k,l;
		int debutColonne=0;
		int debutLigne=0;
		while(button != Button.ID_ESCAPE) {
			LCD.clear(1,2, 100);
			//LCD.drawString("->", 1, ((choix%6)==0?6:choix%6)+1);
			LCD.drawString(titre, 3, 0);
			for (i=debutLigne,l=0;i<tab.length;i++,l++) { //i=nombre de lignes
				buffer[i]=tab[i].getTitre().toCharArray(); //taille variable du titre
				for (j=debutColonne, k=0;j<buffer[i].length;j++,k++)
					LCD.drawChar(buffer[i][j], 3+k, l+2);
			}
			button = Button.waitForAnyPress();
			if (button == Button.ID_DOWN) {
				choix = (choix%tab.length)+1;
				if (tab.length>6&&choix>6) {
					debutLigne = (debutLigne == tab.length-3)?0:++debutLigne;
				}
				//lejos.hardware.Sound.beep();
			}
			else if (button == Button.ID_UP) {
				choix = (choix == 1) ? tab.length :(choix-1);
				if (tab.length>6&&choix>6) {
					debutLigne = (debutLigne == 0)?tab.length-3:--debutLigne;
				}
			}
			else if (button == Button.ID_ENTER) {
				tab[choix-1].lancer();
			}
			else if (button == Button.ID_RIGHT) {
				if (tab[choix-1].getTitre().length()>15) { //si le titre est trop grand pour l'ecran
					debutColonne = (debutColonne == tab[choix-1].getTitre().length()-1)?debutColonne:++debutColonne;
				}
			}
			else if (button == Button.ID_LEFT) {
				if (tab[choix-1].getTitre().length()>15) { //si le titre est trop grand pour l'ecran
					debutColonne = (debutColonne == 0)?debutColonne:--debutColonne;
				}
			}
			Button.waitForAnyEvent();
		}
	}
	
	
	public void lancer() {
		//18 caracteres par ligne
		//8 lignes
		LCD.clear();
		int button = -1;
		int choix = 1;
		int page = 0;
		char[][] buffer = new char[6][18];
		int i,j,k,l;
		int debutColonne=0;
		while(button != Button.ID_ESCAPE) {
			LCD.clear(1,2, 100);
			LCD.drawString("->", 0, choix+1);
			LCD.drawString(titre, 3, 0);
			for (i=0; i<6 && i+6*page<tab.length;i++) { //i=nombre de lignes
				buffer[i]=tab[i+page*6].getTitre().toCharArray();
				for (j=debutColonne, k=0;j<buffer[i].length;j++,k++) {
					LCD.drawChar(buffer[i][j], 2+k, i+2);
				}
			}
			button = Button.waitForAnyPress();
			if (button == Button.ID_DOWN) {
				if (choix==Math.min(tab.length-6*page, 6)) {
					if (page==Math.floor(tab.length/ 6))
						page=0;
					else 
						page++;
					choix = 1;
				}
				else
					choix = (choix%Math.min(tab.length-6*page, 6))+1;
				//lejos.hardware.Sound.beep();
			}
			else if (button == Button.ID_UP) {
				if(choix==1) {
					if(page>0)
						page--;
					else {
						page = (int) Math.floor(tab.length/6);
					}
				}
				choix = (choix == 1) ? Math.min(tab.length-6*page, 6) :(choix-1);
			}
			else if (button == Button.ID_ENTER) {
				tab[choix-1].lancer();
			}
			else if (button == Button.ID_RIGHT) {
				if (tab[choix-1].getTitre().length()>15) { //si le titre est trop grand pour l'ecran
					debutColonne = (debutColonne == tab[choix-1].getTitre().length())?debutColonne:++debutColonne;
				}
			}
			else if (button == Button.ID_LEFT) {
				if (tab[choix-1].getTitre().length()>15) { //si le titre est trop grand pour l'ecran
					debutColonne = (debutColonne == 0)?debutColonne:--debutColonne;
				}
			}
			Button.waitForAnyEvent();
		}
	}
}
