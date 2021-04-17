package interfaceEmbarquee;
import tests.*;
import exceptions.OuvertureException;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;
import modeCompetition.ModeCompetition;
import modeSolo.*;

public class InterfaceTextuelle {
	
	Menu basiques = new Menu("Basiques",new Lancable[] {
			new NFBM1(),
			new NFBA1(),
			new NFBA2(),
			new NFBM2(),
			new NFBM3(),
			new NFBA3(),
			new NFBM4(),
			new NFBM5(),
			new NFBM6()		
	});
	
	Menu avances = new Menu("Avances",new Lancable[] {
			new NFA0(),
			new NFA1(),
			new NFA2(),
			new NFA3(),
			new NFA4(),
			new NFA5(),
			new NFA6(),
			new NFA7()	
	});
	
	Menu optionnels = new Menu("Optionnels",new Lancable[] {
			new IN(),
			new OFA1()	
	});
	
	Menu imposes = new Menu("Imposes",new Lancable [] {new P1(),new P2(),new P3(),new P4(),new P5(),new P6(),new P7()});	
	
	Lancable lancerSolo = new Lancable() {
		public void lancer() {
			if(Configurations.strategieSolo.getVal().equals("ramasserPalets")) {
				//choisir le camp de départ
				boolean camp = true;
				int button = -1;
				LCD.clear();
				LCD.drawString("RougeAGauche?", 3, 1);
				LCD.drawString("vrai <<  >> faux", 1, 3);
				while((button!=Button.ID_LEFT)&&(button!=Button.ID_RIGHT)) {
					button = Button.waitForAnyPress();
				}
				if (button == Button.ID_LEFT) {
					camp=true;
				}
				else if (button == Button.ID_RIGHT) {
					camp=false;
				}
				//placer le robot sur une des 6 positions de depart
				LCD.clear();
				LCD.drawString("poser robot sur 1", 1, 1);
				LCD.drawString("position de depart", 1, 2);
				LCD.drawString("pressez sur entree", 1, 5);
				LCD.drawString("pour demarrer", 1, 6);
				while(button!=Button.ID_ENTER) {
					button = Button.waitForAnyPress();
				}
				//appeler la fonction a executer
				try {
					ModeSolo.ramasserPalet(9, camp);
				} catch (OuvertureException e) {
					System.out.println("Prob pour ouvrir pince");
					e.printStackTrace();
				}
			}
		}
		
		public String getTitre() {
			return "Lancer";
		}
	};
	Lancable lancerDuo = new Lancable() {
		public void lancer() {
			if(Configurations.strategieDuo.getVal().equals("ramasserPaletsDuo")) {
				//choisir le camp de départ
				boolean camp = true;
				int button = -1;
				LCD.clear();
				LCD.drawString("RougeAGauche?", 3, 1);
				LCD.drawString("vrai <<  >> faux", 1, 3);
				while((button!=Button.ID_LEFT)&&(button!=Button.ID_RIGHT)) {
					button = Button.waitForAnyPress();
				}
				if (button == Button.ID_LEFT) {
					camp=true;
				}
				else if (button == Button.ID_RIGHT) {
					camp=false;
				}
				//placer le robot sur une des 6 positions de depart
				LCD.clear();
				LCD.drawString("poser robot sur 1", 1, 1);
				LCD.drawString("position de depart", 1, 2);
				LCD.drawString("pressez sur entree", 1, 5);
				LCD.drawString("pour demarrer", 1, 6);
				while(button!=Button.ID_ENTER) {
					button = Button.waitForAnyPress();
				}
				//appeler la fonction a executer
				try {
					ModeCompetition.ramasserPalet(9, camp);
				} catch (OuvertureException e) {
					System.out.println("Prob pour ouvrir pince");
					e.printStackTrace();
				}
			}
		}
		
		public String getTitre() {
			return "Lancer";
		}
	};
	
	Lancable lancerMusique = new Lancable() {
		//les fichiers.waw doivent etre mono,8000Hz et unsigned 8 bit
		public void lancer() {
			int button = -1;
			Musique.startMusic(); //lance la musique dans un thread 
			Delay.msDelay(2000);
			LCD.clear();
			LCD.drawString("Arreter?", 3, 3);
			LCD.drawString("Pressez sur Entree", 3, 4);
			while((button!=Button.ID_ENTER)&&(button!=Button.ID_ESCAPE)) {
				button = Button.waitForAnyPress();
			}
			if (button==Button.ID_ENTER) {
				Musique.stopMusic();	
			}
		}
		
		public String getTitre() {
			return "Lancer";
		}
	};
	
	Lancable lancerBruitage = new Lancable() {
		//les fichiers.waw doivent etre mono,8000Hz et unsigned 8 bit
		public void lancer() {
			Musique.startSound();
		}
		
		public String getTitre() {
			return "Lancer";
		}
	};
	
	Lancable lancerExpression = new Lancable() {
		//les fichiers.waw doivent etre mono,8000Hz et unsigned 8 bit
		public void lancer() {
			//TODO	
		}
		
		public String getTitre() {
			return "Lancer";
		}
	};
	
	
	
	Picker strategieSolo = new Picker("Strategies",Configurations.strategieSolo);
	Picker strategieDuo = new Picker("Strategies",Configurations.strategieDuo);
	Picker visages = new Picker("Visages",Configurations.expression);
	Picker musiques = new Picker("Musiques",Configurations.musique);
	Picker bruitages = new Picker("Bruitages",Configurations.bruitage);
	
	
	Menu songs = new Menu("Musiques",new Lancable[] {lancerMusique,musiques});
	Menu sounds = new Menu("Bruitages",new Lancable[] {lancerBruitage,bruitages});
	Menu expressions = new Menu("Expressions",new Lancable[] {lancerExpression,visages});
	
	Menu modeSolo = new Menu("Mode Solo",new Lancable[] {lancerSolo,strategieSolo});
	Menu modeCompetition = new Menu("Mode Competition",new Lancable[] {lancerDuo,strategieDuo});
	public Menu scenarios = new Menu("Scenarios",new Lancable[] {imposes,basiques,avances,optionnels});
	Menu statistiques = new Menu("Statistiques"); //pas le temps de les faire?
	Menu reglages = new Menu("Reglages",new Lancable[] {songs,sounds,expressions});
	
	Menu menuPrincipal = new Menu("Menu Principal",new Lancable[] {modeSolo,modeCompetition,scenarios,statistiques,reglages});
	
	public void lancer() {
		menuPrincipal.lancer();
	}
	
	public static void main(String[] args) {
		new InterfaceTextuelle().lancer();
	}
}