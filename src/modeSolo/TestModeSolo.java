 package modeSolo;

import capteurs.*;
import exceptions.OuvertureException;
import lejos.hardware.Sound;


public class TestModeSolo {

	public static void main(String[] args){
		Sound.setVolume(Sound.VOL_MAX/4);
		try{  
			ModeSolo.ramasserPalet(9,1,false, false, true); //attention le robot démarre coté armoire (ligneRouge à gauche)	
		} catch (OuvertureException e) {
			System.out.println("Prob pour ouvrir pince");
			e.printStackTrace();
		}
	}
}
