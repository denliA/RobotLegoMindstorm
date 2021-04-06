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
			//on n'a pas trouve de palet : sortie de boucle
			if(angle>=354) {
				break;
			}

			//on sauvegarde la distance de depart une fois le palet repere
			float dDepart = Ultrason.getDistance();
			System.out.println("dDepart vaut : "+dDepart);
			
			//on a repere le palet, on va affiner la direction du robot
			//on se met au milieu de notre fenetre de captage de palet
			MouvementsBasiques.tourner(5);
			
			//But : trouver l'angle pour lequel la distance est la plus courte, puis avancer de cette distance
			
			float[] distances = new float[10];
			
			//Mesure de la premiere valeur du tableau
			MouvementsBasiques.tourner(-8);
			Ultrason.setDistance();
			distances[0]=Ultrason.getDistance();
			//les valeurs suivantes mesurees tous les deux degres
			//recherche du min simultanee
			int indiceMin = 0;
			for(int i =1;i<10;i++) {
				MouvementsBasiques.tourner(2);
				Ultrason.setDistance();
				float distance = Ultrason.getDistance();
				distances[i]=distance;
				System.out.println("indice "+i+" Distance "+distance);
				if(distance<=distances[indiceMin]) {
					indiceMin=i;
				}
			}
			if(distances[indiceMin]==infini) {
				break;
			}
			//on se dirige avec l'angle correspondant à minIndice
			MouvementsBasiques.tourner(-2*(8-indiceMin));
			//on avance de la distance
			System.out.println(indiceMin+"\n"+distances[indiceMin]);
			//*100 parce que le capteur mesure en m et que la fonction prend en cm
			MouvementsBasiques.avancerTravel(distances[indiceMin]*100);
			
			Pince.fermer();
			
		}
		
		
	}
	
}
