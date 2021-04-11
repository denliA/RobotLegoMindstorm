package interfaceEmbarquee;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import exceptions.EchecGarageException;
import exceptions.OuvertureException;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;
import moteurs.Pilote;

public class InterfaceTextuelle {
	
	public static void main(String[] args) {
		Picker strategieSolo = new Picker("Strategies",Configurations.strategieSolo);
		Picker strategieDuo = new Picker("Strategies",Configurations.strategieDuo);
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
		
		
		Picker bruitages = new Picker("Bruitages",Configurations.musique);
		Picker visages = new Picker("Visages",Configurations.expression);
		
		Lancable lancerSolo = new Lancable() {
							public void lancer() {
								if(Configurations.musique.getVal().equals("megalovania")) {
									Sound.playSample(new File("MEGALOVANIA3.wav"), Sound.VOL_MAX);
								}
								if(Configurations.strategieSolo.getVal().equals("ramasserPalets")) {
									try {
										modeSolo.ModeSolo.ramasserPalet(1, false);
									} catch (EchecGarageException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (OuvertureException e) {
										// TODO Auto-generated catch block
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
					//fonction a coder
				}
			}
			
			public String getTitre() {
				return "Lancer";
			}
		};
		Lancable lancerReglages = new Lancable() {
			//les fichiers.waw doivent etre mono,8000Hz et unsigned 8 bit
			public void lancer() {
				int button = -1;
				try {
					Musique.startMusic("megalovania","MEGALOVANIA.wav");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} //lance la musique dans un executor
				LCD.clear();
				LCD.drawString("arreter?", 3, 5);
				while(button != Button.ID_ENTER);//je ne fais rien
				Musique.stopMusic();	
			}

			public String getTitre() {
				return "Lancer";
			}
		};
		
		
		Menu modeSolo = new Menu("Mode Solo",new Lancable[] {lancerSolo,strategieSolo});
		Menu modeCompetition = new Menu("Mode Competition",new Lancable[] {lancerDuo,strategieDuo});
		Menu scenarios = new Menu("Scenarios",new Lancable[] {basiques,avances,optionnels});
		Menu statistiques = new Menu("Statistiques"); //pas le temps de les faire?
		Menu reglages = new Menu("Reglages",new Lancable[] {lancerReglages,bruitages,visages});
		
		Menu menuPrincipal = new Menu("Menu Principal",new Lancable[] {modeSolo,modeCompetition,scenarios,statistiques,reglages});
		menuPrincipal.lancer();

	}
}