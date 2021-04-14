 package modeSolo;

import capteurs.*;
import exceptions.OuvertureException;
import lejos.utility.Delay;
import moteurs.Pilote;

public class TestModeSolo {

	public static void main(String[] args){
		
		try{  
			ModeSolo.ramasserPalet(9,false); //attention le robot démarre coté armoire (ligneRouge à gauche)	
		} catch (OuvertureException e) {
			System.out.println("Prob pour ouvrir pince");
			e.printStackTrace();
		}
	}
}
