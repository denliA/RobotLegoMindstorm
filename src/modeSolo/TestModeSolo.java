package modeSolo;

import exceptions.OuvertureException;

public class TestModeSolo {

	public static void main(String[] args){
		try{  
			ModeSolo.ramasserPalet(3);
		}catch(OuvertureException e){
			System.out.println("Prob d'ouverture de pince");
		}catch(InterruptedException e) {
			System.out.println("Prob pour tourner le robot");
		}
		
	}

}
