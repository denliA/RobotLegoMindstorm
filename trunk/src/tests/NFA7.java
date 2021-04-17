package tests;

import exceptions.OuvertureException;

public class NFA7 implements interfaceEmbarquee.Lancable{
	
	public void lancer() {
		try {
			modeSolo.ModeSolo.ramasserPalet(9, false);
		} catch (OuvertureException e) {
			System.out.println("Prob ouvrir pinces");
			e.printStackTrace();
		}
	}
	
	public String getTitre() {
		return "NFA7 - Chemin predefini 9 palets";
	}
	
}