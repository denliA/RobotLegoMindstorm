package modeCompetition;

import exceptions.EchecGarageException;
import exceptions.OuvertureException;

public class TestModeCompetition {

	public static void main(String[] args) {
		try {
			ModeCompetition.ramasserPalet(9,true);
		} catch (OuvertureException e) {
			System.out.println("Prob d'ouverture des pinces");
			e.printStackTrace();
		} catch (InterruptedException e) {
			System.out.println("Prob pour lancer musique");
			e.printStackTrace();
		}

	}

}
