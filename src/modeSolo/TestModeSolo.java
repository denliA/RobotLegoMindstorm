 package modeSolo;

import exceptions.OuvertureException;


public class TestModeSolo {

	public static void main(String[] args){
		try{  
			ModeSolo.ramasserPalet(3,true); //attention le robot démarre coté armoire (ligneRouge à gauche)	
		} catch (OuvertureException e) {
			System.out.println("Prob pour ouvrir pince");
			e.printStackTrace();
		}
	}
}
