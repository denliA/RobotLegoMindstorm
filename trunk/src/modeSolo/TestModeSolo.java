package modeSolo;

import capteurs.*;
import exceptions.OuvertureException;

public class TestModeSolo {

	public static void main(String[] args) throws Exception {
		Couleur.startScanAtRate(0);
		Toucher.startScan();
		Ultrason.startScan();
		try{  
			ModeSolo.ramasserPalet(9,true); //attention le robot demarre coté armoire (ligneRouge à gauche)
		}catch(OuvertureException e){
			System.out.println("Prob d'ouverture de pince");
		}catch(InterruptedException e) {
			System.out.println("Prob pour tourner le robot");
		}	
	}

}
