package moteurs;

import capteurs.Couleur;

public class TestSuivreLigne {
	public static void main(String args[]) throws InterruptedException {
		Couleur.setScanMode(Couleur.RGBMODE);
		Couleur.startScanAtRate(0);
		Pilote.suivreLigne();
		
	}
}
