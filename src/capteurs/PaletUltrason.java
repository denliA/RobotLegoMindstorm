package capteurs;

import exceptions.OuvertureException;
import lejos.utility.Delay;
import moteurs.Pince;
import moteurs.MouvementsBasiques;
import lejos.hardware.Button;

public class PaletUltrason {
	
	public static void main(String[] args) throws OuvertureException {
		//Lancer les capteurs
		new Capteur();
		//Ultrason.startScan();
		Toucher.startScan();
		//initialiser l'infini
		float infini = Float.POSITIVE_INFINITY;
		//initialiser d
		Ultrason.setDistance();
		float d = Ultrason.getDistance();
		
		System.out.println("Premiere mesure de d : "+d);
		
		//ouverture des pinces
		if(!Pince.getOuvert()) {
			Pince.ouvrir();
		}
		
		//tant qu'on n'a pas attrape le palet
		while(Pince.getOuvert()&&Button.ENTER.isUp()) {
			//se mettre dans la direction du palet en evitant de capter le decor
			int angle = 0;
			while((d>2||d<=0)&&angle<360) {
				angle+=10;
				MouvementsBasiques.tourner(10);
				Ultrason.setDistance();
				d = Ultrason.getDistance();
				System.out.println(d);
			}
			if(angle>=354) {
				break;
			}
			
			//on sauvegarde la distance de depart une fois le palet repere
			float dDepart = Ultrason.getDistance();
			System.out.println("dDepart vaut : "+dDepart);
			
			//on avance tant qu'on ne depasse pas le palet
			MouvementsBasiques.avancer();
			while(d<=(dDepart+0.5)&&!Toucher.getTouche()) {
				Ultrason.setDistance();
				d = Ultrason.getDistance();
				System.out.println(d);
			}
			MouvementsBasiques.arreter();
			//si on le touche, on attrappe
			
			Pince.fermer();
				
		}
		
//		//avancer vers le palet tant qu'on le capte
//		moteurs.MouvementsBasiques.avancer();
//		while(((d=Ultrason.getDistance())>0&&d!=infini)&&!Toucher.getTouche()) {
//			System.out.println(d);
//		}
//		moteurs.MouvementsBasiques.arreter();
//		
//		//selon s'il est en contact avec le palet
//		if(Toucher.getTouche()) {
//			Pince.fermer();
//		}
//		else {
//			int angle = 0;
//			while(((d=Ultrason.getDistance())==infini||d<=0)&&!Toucher.getTouche()&&angle<360) {
//				angle+=10;
//				moteurs.MouvementsBasiques.tourner(10);
//				System.out.println(d);
//				}
//			}
		
	}
	
}
