package modeCompetition;

import exceptions.EchecGarageException;
import exceptions.OuvertureException;

public class TestModeCompetition {

	public static void main(String[] args) {
		try {
			ModeCompetition.ramasserPalet(9,true);
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
