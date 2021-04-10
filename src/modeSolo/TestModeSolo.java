package modeSolo;

import capteurs.*;
import exceptions.OuvertureException;
import lejos.utility.Delay;
import moteurs.Pilote;

public class TestModeSolo {

	public static void main(String[] args){
		Toucher.startScan();
		Ultrason.startScan();
		new Couleur();
		Couleur.startScanAtRate(0);
		///Pilote.startVideAtRate(10);
		try{  
			ModeSolo.ramasserPalet(9,true); //attention le robot demarre coté armoire (ligneRouge à gauche)
		}catch(InterruptedException e) {
			System.out.println("Prob pour tourner le robot");
		}catch(exceptions.EchecGarageException e){
			System.out.println("Prob pour se redresser sur ligne de couleur");	
		} catch (OuvertureException e) {
			System.out.println("Prob pour ouvrir pince");
			e.printStackTrace();
		}
		Toucher.stopScan();
		Ultrason.stopScan();
		Couleur.stopScan();
	}
}
