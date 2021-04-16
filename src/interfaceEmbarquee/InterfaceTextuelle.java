package interfaceEmbarquee;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import capteurs.CouleurLigne;
import capteurs.CouleurLigne.ContextePID;
import exceptions.EchecGarageException;
import exceptions.OuvertureException;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;
import moteurs.Pilote;

public class InterfaceTextuelle {
	
	public static void main(String[] args) {
		
		Menu basiques = new Menu("Basiques",new Lancable[] {
				new Lancable() {
					public void lancer() {}
					public String getTitre() {
						return "NFBM1 - Reconnaitre couleur";
				
					}
				},
				new Lancable() {
					public void lancer() {}
					public String getTitre() {
						return "NFBA1 - Avancer tout droit";
					}
				},
				new Lancable() {
					public void lancer() {}
					public String getTitre() {
						return "NFBA2 - Faire angle droit";
					}
				},
				new Lancable() {
					public void lancer() {}
					public String getTitre() {
						return "NFBM2 - Reconnaître intersections";
					}
				},
				new Lancable() {
					public void lancer() {}
					public String getTitre() {
						return "NFBM3 - Détecter palet";
					}
				},
				new Lancable() {
					public void lancer() {}
					public String getTitre() {
						return "NFBA3 - Ramener palet";
					}
				},
				new Lancable() {
					public void lancer() {}
					public String getTitre() {
						return "NFBM4 - Detecter vide";
					}
				},
				new Lancable() {
					public void lancer() {}
					public String getTitre() {
						return "NFBM5 - Arret apres 5 min";
					}
				},
				new Lancable() {
					public void lancer() {}
					public String getTitre() {
						return "NFBM6 - Capteur ultrason";
					}
				}		
		});
		
		Menu avances = new Menu("Avances",new Lancable[] {
				new Lancable() {
					public void lancer() {}
					public String getTitre() {
						return "NFA0 - Ligne blanche adverse";
				
					}
				},
				new Lancable() {
					public void lancer() {}
					public String getTitre() {
						return "NFA1 - Rectangle";
					}
				},
				new Lancable() {
					public void lancer() {}
					public String getTitre() {
						return "NFA2 - Creneau";
					}
				},
				new Lancable() {
					public void lancer() {}
					public String getTitre() {
						return "NFA3 - Intersection ligne";
					}
				},
				new Lancable() {
					public void lancer() {}
					public String getTitre() {
						return "NFA4 - Intersection partout";
					}
				},
				new Lancable() {
					public void lancer() {}
					public String getTitre() {
						return "NFA5 - Ramener palet position connue";
					}
				},
				new Lancable() {
					public void lancer() {}
					public String getTitre() {
						return "NFA6 - Ramener palet ultrason";
					}
				},
				new Lancable() {
					public void lancer() {}
					public String getTitre() {
						return "NFA7 - Chemin predefini 9 palets";
					}
				}		
		});
		
		Menu optionnels = new Menu("Optionnels",new Lancable[] {
				new Lancable() {
					public void lancer() {}
					public String getTitre() {
						return "OFBA1 - Angles et distances";
				
					}
				},
				new Lancable() {
					public void lancer() {}
					public String getTitre() {
						return "OFA1 - Carte virtuelle";
					}
				},
				new Lancable() {
					public void lancer() {}
					public String getTitre() {
						return "OFA2 - Diagonales";
					}
				},
				new Lancable() {
					public void lancer() {}
					public String getTitre() {
						return "IF1 - Strategies victoire";
					}
				},
				new Lancable() {
					public void lancer() {}
					public String getTitre() {
						return "IF2 - Sabotage";
					}
				}		
		});
		
		
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
					//appeler la fonction a executer
					try {
						modeSolo.ModeSolo.ramasserPalet(9, camp);
					} catch (OuvertureException e) {
						System.out.println("Prob pour ouvrir pince");
						e.printStackTrace();
					} catch (InterruptedException e) {
						System.out.println("Prob pour lancer musique");
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
					//appeler la fonction a executer
					try {
						modeCompetition.ModeCompetition.ramasserPalet(9, camp);
					} catch (OuvertureException e) {
						System.out.println("Prob pour ouvrir pince");
						e.printStackTrace();
					} catch (InterruptedException e) {
						System.out.println("Prob pour lancer musique");
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
				if (button!=Button.ID_ENTER) {
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
		Menu scenarios = new Menu("Scenarios",new Lancable[] {basiques,avances,optionnels});
		Menu statistiques = new Menu("Statistiques"); //pas le temps de les faire?
		Menu reglages = new Menu("Reglages",new Lancable[] {songs,sounds,expressions});
		
		Menu menuPrincipal = new Menu("Menu Principal",new Lancable[] {modeSolo,modeCompetition,scenarios,statistiques,reglages});
		menuPrincipal.lancer();

	}
}