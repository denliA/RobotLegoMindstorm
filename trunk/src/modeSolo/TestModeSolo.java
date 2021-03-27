package modeSolo;

import capteurs.*;
import exceptions.OuvertureException;

public class TestModeSolo {

	public static void main(String[] args) throws OuvertureException, InterruptedException {
		Couleur.startScanAtRate(0);
		Toucher.startScan();
		Ultrason.startScan();
		try{  
			ModeSolo.ramasserPalet(3);
		}catch(OuvertureException e){
			System.out.println("Prob d'ouverture de pince");
		}catch(InterruptedException e) {
			System.out.println("Prob pour tourner le robot");
		}	
	}

}
