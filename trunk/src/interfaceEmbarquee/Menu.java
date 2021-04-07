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
		char[][] buffer = new char[8][18];
		int i,j,k,l;
		int fleche=0;
		int debutColonne=0;
		int debutLigne=0;
		while(button != Button.ID_ESCAPE) {
			LCD.clear(1,2, 100);
			LCD.drawString("->", 1, choix+fleche+1);
			LCD.drawString(titre, 3, 0);
			for (i=debutLigne,l=0;i<tab.length-2;i++,l++) { //i=nombre de lignes
				for (j=debutColonne, k=0;j<buffer[i].length;j++,k++) {
					buffer[i]=tab[i].getTitre().toCharArray();
					LCD.drawChar(buffer[i][j], 3+k, l+2);
				}
			}
			button = Button.waitForAnyPress();
			if (button == Button.ID_DOWN) {
				if (tab.length>6&&choix>=6) {
					debutLigne = (debutLigne == tab.length-3)?0:++debutLigne;
				}
				choix = (choix%tab.length)+1;
				fleche = (fleche == tab.length-3)?0:++fleche;
				//lejos.hardware.Sound.beep();
			}
			else if (button == Button.ID_UP) {
				if (tab.length>6&&choix>=6) {
					debutLigne = (debutLigne == 0)?tab.length-3:--debutLigne;
				}
				choix = (choix == 1) ? tab.length :(choix-1);
				fleche = (fleche == 0)?tab.length-3:--fleche;
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
