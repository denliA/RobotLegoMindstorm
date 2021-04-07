package interfaceEmbarquee;

import exceptions.EchecGarageException;
import exceptions.OuvertureException;


public class InterfaceTextuelle {
	
	public static void main(String[] args) {
		Picker strategies = new Picker("Strategies",Configurations.strategieSolo);
		Lancable lancer = new Lancable() {
							public void lancer() {
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
		Menu modeSolo = new Menu("Mode Solo",new Lancable[] {lancer,strategies});
		Menu modeCompetition = new Menu("Mode Competition");
		Menu scenarios = new Menu("Scenarios");
		Menu statistiques = new Menu("Statistiques");
		Menu reglages = new Menu("Reglages");
		Menu A = new Menu("A");
		Menu B = new Menu("B");
		Menu C = new Menu("C");
		Menu D = new Menu("D");
		Menu E = new Menu("E");
		Menu menuPrincipal = new Menu("Menu Principal",new Lancable[] {modeSolo,modeCompetition,scenarios,statistiques,reglages,A,B,C,D,E});
		
		menuPrincipal.lancer();

	}
}