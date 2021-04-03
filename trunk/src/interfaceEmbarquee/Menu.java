package interfaceEmbarquee;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;

public class Menu implements Lancable{
	String titre;
	Lancable[] tab;
	
	public Menu(Lancable tab[]) {
		this.tab = tab;
	}
	
	public String getTitre() {
		return titre;
	}
	
	public void lancer() {
		LCD.clear();
		LCD.drawString(titre, 7, 1);
		int button = -1;
		int choix = 1;
		while(button != Button.ID_ESCAPE) {
			LCD.clear(1,2, 100);
			LCD.drawString("->", 1, choix+1);
			for (int i=0;i<tab.length;i++) {
				LCD.drawString(tab[i].getTitre(), 3, i+1);
			}
			button = Button.waitForAnyPress();
			if (button == Button.ID_DOWN) {
				choix = (choix%tab.length)+1;
				//lejos.hardware.Sound.beep();
			}
			else if (button == Button.ID_UP)
				choix = (choix == 1) ? tab.length :(choix-1);
			else if (button == Button.ID_ENTER) {
				tab[choix].lancer();
			}
			Button.waitForAnyEvent();
		}	
	}
}
