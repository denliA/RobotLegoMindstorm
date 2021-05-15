package modeCompetition;

import exceptions.OuvertureException;

public class TestModeCompetition {

	public static void main(String[] args) {
		try {
			ModeCompetition.ramasserPalet(9,true);
		} catch (OuvertureException e) {
			System.out.println("Prob d'ouverture des pinces");
			e.printStackTrace();
		}
	}

}
