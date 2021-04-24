package interfaceEmbarquee;
import tests.*;
import exceptions.OuvertureException;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;
import modeCompetition.ModeCompetition;
import modeSolo.ModeSolo;

/**
 * <p>InterfaceTextuelle est une classe qui instancie tous les Lancables (Menu,Picker,lancerSolo...) de l'interfaceEmbarquee.</p>
 * 
 * <p>Elle possède une méthode main qui lance l'InterfaceTextuelle depuis laquelle tous les Menus et Pickers sont accessibles.</p>
 * 
 * @see Menu
 * @see Lancable
 * @see Picker
 * 
 */

public class InterfaceTextuelle {
	
	/**
	 * <p>Menu des scénarios nécessaires basiques de la recette.</p>
	 */
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
	
	/**
	 * <p>Menu des scénarios nécessaires avancés de la recette.</p>
	 */
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
	
	/**
	 * <p>Menu des scénarios optionnels de la recette qu'on a pu réaliser dans le temps imparti.</p>
	 */
	Menu optionnels = new Menu("Optionnels",new Lancable[] {
			new IN(),
			new OFA1()	
	});
	
	/**
	 * <p>Menu des scénarios imposés par l'encadrant.</p>
	 */
	Menu imposes = new Menu("Imposes",new Lancable [] {new P1(),new P2(),new P3(),new P4(),new P5(),new P6(),new P7()});	
	
	/**
	 * <p>Lancable qui exécute la stratégie du modeSolo quand l'utilisateur appuie sur "Lancer".</p>
	 * @see ModeSolo#ramasserPalet(int, boolean)
	 */
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
	
	/**
	 * <p>Lancable qui exécute la stratégie du modeCompetition quand l'utilisateur appuie sur "Lancer".</p>
	 * @see ModeCompetition#ramasserPalet(int, boolean)
	 */
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
	
	/**
	 * <p>Lancable qui exécute la musique qui correspond à la configuration choisie quand l'utilisateur appuie sur "Lancer".</p>
	 * 
	 * <p>La musique est lancée dans un thread. On revient dans une interface qui permet à l'utilisateur d'interrompre cette musique, si il appuie sur la touche "ESCAPE".</p>
	 * 
	 * @see Musique
	 * @see Configurations
	 */
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
	
	/**
	 * <p>Lancable qui exécute le bruitage qui correspond à la configuration choisie quand l'utilisateur appuie sur "Lancer".</p>
	 * 
	 * <p>Le bruitage est lancé dans un thread. L'effet sonore est court, il ne peut donc pas être interrompu.</p>
	 * 
	 * @see Musique
	 * @see Configurations
	 */
	Lancable lancerBruitage = new Lancable() {
		//les fichiers.waw doivent etre mono,8000Hz et unsigned 8 bit
		public void lancer() {
			Musique.startSound();
		}
		
		public String getTitre() {
			return "Lancer";
		}
	};
	
	
	/**
	 * <p>Lancable qui exécute la danse qui correspond à la configuration choisie quand l'utilisateur appuie sur "Lancer".</p>
	 * 
	 * <p>La danse ne peut pas être interrompue car elle n'est pas lancée dans un thread.</p>
	 * 
	 * @see Danse
	 * @see Configurations
	 */
	Lancable lancerDanse = new Lancable() {
		//les fichiers.waw doivent être mono,8000Hz et unsigned 8 bit
		public void lancer() {
			Danse.startDance();
		}
		
		public String getTitre() {
			return "Lancer";
		}
	};
	
	
	/**
	 * <p>Picker qui permet à l'utilisateur de choisir la stratégie du ModeSolo à lancer parmi les Configurations. Pour l'instant, une seule stratégie a été codée pour le mode solo.</p>
	 * 
	 * @see ModeSolo#ramasserPalet(int, boolean)
	 * @see Configurations
	 */
	Picker strategieSolo = new Picker("Strategies",Configurations.strategieSolo);
	
	/**
	 * <p>Picker qui permet à l'utilisateur de choisir la stratégie du ModeCompetition à lancer parmi les Configurations. Pour l'instant, une seule stratégie a été codée pour le mode competition.</p>
	 * 
	 * @see ModeCompetition#ramasserPalet(int, boolean)
	 * @see Configurations
	 */
	Picker strategieDuo = new Picker("Strategies",Configurations.strategieDuo);
	
	/**
	 * <p>Picker qui permet à l'utilisateur de choisir la musique à lancer parmi les Configurations.</p>
	 * <p>On peut lancer des musiques depuis le menu songs dans les reglages pour les tester.</p>
	 * <p>Sinon, les musiques ont déjà été intégrées aux modes solo et competition en fin de partie.</p>
	 * @see Musique
	 * @see Configurations
	 * @see ModeSolo#ramasserPalet(int, boolean)
	 * @see ModeCompetition#ramasserPalet(int, boolean)
	 * 
	 */
	Picker musiques = new Picker("Musiques",Configurations.musique);
	
	/**
	 * <p>Picker qui permet à l'utilisateur de choisir le bruitage à lancer parmi les Configurations.</p>
	 * <p>On peut lancer des bruitages depuis le menu sounds dans les reglages pour les tester.</p>
	 * <p>Sinon, les bruitages ont déjà été intégrés aux modes solo et competition. Ils surviennent quand un événement précis (palet ramassé, robot perdu, vide détecté...) arrive.</p>
	 * 
	 * @see Musique
	 * @see Configurations
	 * @see ValeursConfig
	 * @see ModeSolo#ramasserPalet(int, boolean)
	 * @see ModeCompetition#ramasserPalet(int, boolean)
	 */
	Picker bruitages = new Picker("Bruitages",Configurations.bruitage);
	
	/**
	 * <p>Picker qui permet à l'utilisateur de choisir la danse à lancer parmi les Configurations.</p>
	 * <p>On peut lancer des danses depuis le menu danses dans les reglages pour les tester.</p>
	 * <p>Sinon, les danses ont déjà été intégrées aux modes solo et competition en fin de partie.</p>
	 * 
	 * @see Danse
	 * @see Configurations
	 * @see ValeursConfig
	 * @see ModeSolo#ramasserPalet(int, boolean)
	 * @see ModeCompetition#ramasserPalet(int, boolean)
	 */
	Picker dances = new Picker("Danses",Configurations.danse);
	
	/**
	 * <p>Menu qui permet à l'utilisateur de tester les musiques présentes dans la mémoire du robot.</p>
	 * 
	 * @see Musique
	 * @see Configurations
	 * @see ValeursConfig
	 */
	Menu songs = new Menu("Musiques",new Lancable[] {lancerMusique,musiques});
	
	/**
	 * <p>Menu qui permet à l'utilisateur de tester les bruitages présents dans la mémoire du robot.</p>
	 * 
	 * @see Musique
	 * @see Configurations
	 * @see ValeursConfig
	 */
	Menu sounds = new Menu("Bruitages",new Lancable[] {lancerBruitage,bruitages});
	
	/**
	 * <p>Menu qui permet à l'utilisateur de tester les danses présentes dans la mémoire du robot.</p>
	 * 
	 * @see Danse
	 * @see Configurations
	 * @see ValeursConfig
	 */
	Menu danses = new Menu("Danses",new Lancable[] {lancerDanse,dances});
	
	/**
	 * <p>Menu qui permet à l'utilisateur de tester le mode solo.</p>
	 * 
	 * @see ModeSolo#ramasserPalet(int, boolean)
	 */
	Menu modeSolo = new Menu("Mode Solo",new Lancable[] {lancerSolo,strategieSolo});
	
	/**
	 * <p>Menu qui permet à l'utilisateur de tester le mode competition.</p>
	 * 
	 * @see ModeCompetition#ramasserPalet(int, boolean)
	 */
	Menu modeCompetition = new Menu("Mode Competition",new Lancable[] {lancerDuo,strategieDuo});
	
	
	public Menu scenarios = new Menu("Scenarios",new Lancable[] {imposes,basiques,avances,optionnels});
	
	/**
	 * <p>Menu qui permet à l'utilisateur de tester les musiques, bruitages et danses.</p>
	 * @see Musique
	 * @see Danse
	 * 
	 */
	Menu reglages = new Menu("Reglages",new Lancable[] {songs,sounds,danses});
	
	/**
	 * <p>Menu principal depuis lequel on peut lancer tous les autres Lancables.</p>
	 * <p>Il apparait lorsqu'on lance l'interface textuelle.</p>
	 * 
	 */
	Menu menuPrincipal = new Menu("Menu Principal",new Lancable[] {modeSolo,modeCompetition,scenarios,reglages});
	
	
	/**
	 * <p>Méthode qui lance l'interface textuelle.</p>
	 * 
	 */
	public void lancer() {
		menuPrincipal.lancer();
	}
	
	/**
	 * <p>Point d'entrée du programme.</p>
	 * 
	 */
	public static void main(String[] args) {
		new InterfaceTextuelle().lancer();
	}
}