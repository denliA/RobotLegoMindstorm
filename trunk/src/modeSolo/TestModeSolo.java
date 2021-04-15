 package modeSolo;

import capteurs.*;
import exceptions.OuvertureException;


public class TestModeSolo {

	public static void main(String[] args){
		
		try{  
			ModeSolo.ramasserPalet(9,false); //attention le robot démarre coté armoire (ligneRouge à gauche)	
		} catch (OuvertureException e) {
			System.out.println("Prob pour ouvrir pince");
			e.printStackTrace();
		} catch (InterruptedException e) {
			System.out.println("Prob pour lancer musique");
			e.printStackTrace();
		}
	}
}
