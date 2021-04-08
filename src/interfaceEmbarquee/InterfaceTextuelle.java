package interfaceEmbarquee;

import java.io.File;

import exceptions.EchecGarageException;
import exceptions.OuvertureException;
import lejos.hardware.Sound;


public class InterfaceTextuelle {
	
	public static void main(String[] args) {
		Picker strategieSolo = new Picker("Strategies",Configurations.strategieSolo);
		Picker strategieDuo = new Picker("Strategies",Configurations.strategieDuo);
		Picker basiques = new Picker("Basiques",Scenarios.basiques);
		Picker avances = new Picker("Avances",Scenarios.avances);
		Picker optionnels = new Picker("Optionnels",Scenarios.optionnels);
		Picker bruitages = new Picker("Bruitages",Bruitages.undertale);
		Picker visages = new Picker("Visages",Visages.content);
		
		Lancable lancerSolo = new Lancable() {
							public void lancer() {
								if(Bruitages.undertale.getVal().equals("megalovania")) {
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
			public void lancer() {
				if(Bruitages.undertale.getVal().equals("megalovania")) {
					Sound.playSample(new File("MEGALOVANIA3.wav"), Sound.VOL_MAX);
				}
				else if(Bruitages.chill.getVal().equals("glitzAtTheRitz")) {
					Sound.playSample(new File("glitzAtTheRitz.wav"), Sound.VOL_MAX);
				}
				
				//expressions des visages pas encore codees
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