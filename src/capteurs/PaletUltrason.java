package capteurs;

import exceptions.OuvertureException;

public class PaletUltrason {
	public static void main(String[] args) throws OuvertureException {
		//Lancer les capteurs
		new Capteur();
		Ultrason.startScan();
		Toucher.startScan();
		//initialiser l'infini
		float infini = Float.POSITIVE_INFINITY;
		//initialiser d
		Ultrason.setDistance();
		float d = Ultrason.getDistance();
		
		if(d<infini) {
			moteurs.MouvementsBasiques.avancer();
		}
		else {
			if(Toucher.getTouche()) {
				moteurs.Pince.fermer();
			}
			else {
				int angle = 0;
				while(d==infini&&!Toucher.getTouche()&&angle<360) {
					angle+=10;
					moteurs.MouvementsBasiques.tourner(10);
				}
			}
		}
	}
	
}
