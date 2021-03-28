package modeSolo;

import capteurs.*;
import exceptions.OuvertureException;

public class TestModeSolo {

	public static void main(String[] args){
		Couleur.startScanAtRate(0);
		Toucher.startScan();
		Ultrason.startScan();
		try{  
			ModeSolo.ramasserPalet(9,true); //attention le robot demarre coté armoire (ligneRouge à gauche)
		}catch(InterruptedException e) {
			System.out.println("Prob pour tourner le robot");
		}catch(Exception e){
			System.out.println("Prob pour se redresser sur ligne de couleur");	
		}
	}
}
