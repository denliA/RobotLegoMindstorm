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

	public void lancer() {
		//18 caracteres par ligne
		//8 lignes
		LCD.clear();
		int button = -1;
		int choix = 1;
		int page = 0;
		char[][] buffer = new char[6][];
		int i,j,k,l;
		int debutColonne=0;
		long max=0;
		while(button != Button.ID_ESCAPE) {
			LCD.clear(1,2, 100);
			LCD.drawString("->", 0, choix+1);
			LCD.drawString(titre, 3, 0);
			for (i=0; i<6 && i+6*page<tab.length;i++) { //i=nombre de lignes
				buffer[i]=tab[i+page*6].getTitre().toCharArray();
				max= (max<buffer[i].length ? buffer[i].length : max);
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
				tab[choix+6*page-1].lancer();
			}
			else if (button == Button.ID_RIGHT) {
				if (tab[choix+6*page-1].getTitre().length()>15) { //si le titre est trop grand pour l'ecran
					debutColonne = (debutColonne == tab[choix-1].getTitre().length())?debutColonne:++debutColonne;
				}
			}
			else if (button == Button.ID_LEFT) {
				if (tab[choix+6*page-1].getTitre().length()>15) { //si le titre est trop grand pour l'ecran
					debutColonne = (debutColonne == 0)?debutColonne:--debutColonne;
				}
			}
			Button.waitForAnyEvent();
		}
	}
}
