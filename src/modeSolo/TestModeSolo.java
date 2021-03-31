package modeSolo;

import capteurs.*;
import exceptions.OuvertureException;

public class TestModeSolo {

	public static void main(String[] args){
		Couleur.startScanAtRate(0);
		Toucher.startScan();
		Ultrason.startScan();
		try{  
			ModeSolo.ramasserPalet(6,false); //attention le robot demarre coté armoire (ligneRouge à gauche)
		}catch(InterruptedException e) {
			System.out.println("Prob pour tourner le robot");
		}catch(exceptions.EchecGarageException e){
			System.out.println("Prob pour se redresser sur ligne de couleur");	
		} catch (OuvertureException e) {
			System.out.println("Prob pour ouvrir pince");
			e.printStackTrace();
		}
	}
}
