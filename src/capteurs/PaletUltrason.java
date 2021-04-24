package capteurs;

import exceptions.OuvertureException;
import moteurs.MouvementsBasiques;
import moteurs.Pince;
/**
 * Classe statique dont les methodes permettent la recherche d'un palet grace au capteur ultrason
 * @see Capteur
 * @see Ultrason
 * @see Pince
 */
public class PaletUltrason {
	
	private static int angleTotal;
	private static float dDepart;
	
	public static int getAngle() {
		return(angleTotal);
	}
	public static float getDistance() {
		return dDepart;
	}
	/**
	 * methode de recherche de palet reposant sur la recherche sequentielle du minimum dans un vecteur de distances
	 * @throws OuvertureException
	 * @deprecated 
	 */
	public static void sequentielle() throws OuvertureException {
		
		/**
		 * initialiser l'infini
		 * <p> Il sera utilise pour verifier que le robot trouve un palet. Condition d'arr�t d'une boucle while.</p>
		 */
		float infini = Float.POSITIVE_INFINITY;
		
		/**
		 * ouverture des pinces si besoin
		 */
		if(!Pince.getOuvert()) {
			Pince.ouvrir();
		}
				
		/**
		 * Lancer les capteurs
		 */
		new Capteur();
		/**
		 * initialiser d
		 * <p> d est la distance mesuree par le capteur ultrasons.
		 * Elle est utilisee pour orienter le robot vers le palet.</p> 
		 */
		Ultrason.setDistance();
		float d = Ultrason.getDistance();
		
		System.out.println("Premiere mesure de d : "+d);
		
			/**
			 * Premiere boucle while : recherche grossiere du palet
			 * <p>Le but de cette boucle est de capter le palet : le robot tourne de 10 degres dans le sens trigonometrique
			 *  jusqu'a ce que d soit inferieure a 2m (succes) ou jusqu'a avoir fait un tour sur lui-meme (echec).</p>
			 */
			int angle = 0;
			while((d>2||d<=0)&&angle<360) {
				angle+=10;
				MouvementsBasiques.tourner(10);
				Ultrason.setDistance();
				d = Ultrason.getDistance();
				System.out.println(d);
			}

			/**
			 * Affinage de l'angle avec un tableau de mesures
			 * <p>But : trouver l'angle pour lequel la distance est la plus courte, 
			 * s'orienter de cet angle puis avancer de cette distance.</p>
			 * <ul>
			 * <li> Etape 1 : le robot se decale de 5 degres pour arriver au milieu de la fenetre de captage
			 * <li> Etape 2 : creer le tableau distances qui recueille les valeurs mesurees
			 * <li> Etape 3 : mesures des valeurs tous les 2 degres
			 * <li> Recherche de la distance minimale dans la meme boucle 
			 * <li> Etape 4 : s'orienter correctement et avancer de la distance mesuree
			 * </ul>
			 */
			float dDepart = Ultrason.getDistance();
			System.out.println("dDepart vaut : "+dDepart);
			
			MouvementsBasiques.tourner(5);
			
			float[] distances = new float[10];
			
			MouvementsBasiques.tourner(-8);
			Ultrason.setDistance();
			distances[0]=Ultrason.getDistance();
			
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
				//TODO
			}

			MouvementsBasiques.tourner(-2*(8-indiceMin));
			System.out.println(indiceMin+"\n"+distances[indiceMin]);
			
			//*100 parce que le capteur mesure en m et que la fonction prend en cm
			MouvementsBasiques.avancerTravel(distances[indiceMin]*100);
			
			Pince.fermer();
			
	}//fin sequentielle()
	
	
	/**
	 * methode de recherche de palet reposant sur la recherche dichotomique de la distance minimale
	 * @param range le rayon de recherche en metre
	 * @return 0 s'il trouve le palet, 1 s'il rencontre le vide (cas du mur), 2 si pas de palet en vue
	 * @throws OuvertureException
	 */
	public static int dichotomique(int range) throws OuvertureException {
		
//		/**
//		 * ouverture des pinces si besoin
//		 */
//		try {
//			Pince.ouvrir();
//		}
//		catch(OuvertureException e) {
//			;
//		}
		/**
		 * Lancer les capteurs
		 */
		new Capteur();
		/**
		 * initialiser d
		 * <p> d est la distance mesuree par le capteur ultrasons.
		 * Elle est utilisee pour orienter le robot vers le palet.</p> 
		 */
		Ultrason.setDistance();
		float d = Ultrason.getDistance();
		
		System.out.println("Premiere mesure de d : "+d);
		
		/**
		 * Premiere boucle while : recherche grossiere du palet
		 * <p>Le but de cette boucle est de capter le palet : le robot tourne de 10 degres dans le sens trigonometrique
		 *  jusqu'a ce que d soit inferieure a 2m (succes) ou jusqu'a avoir fait un tour sur lui-meme (echec).</p>
		 */
		int angle = 0;
		while((d>range||d<=0)&&angle<360) {
			angle+=10;
			MouvementsBasiques.tourner(10);
			Ultrason.setDistance();
			d = Ultrason.getDistance();
			System.out.println(d);
		}
//			if(angle==350) {
//				return 2;
//			}
		angleTotal = angle;
		boolean trouve = false;
		if(d<range) {
			/**
			 * L'algorithme d'affinement de l'angle 
			 * <p>Il ne s'ex�cute que si le robot a capte un palet (d!=infini). 
			 * Il consiste en une serie de comparaisons de distances mesurees.</p>
			 * <ul>
			 * <li> Etape 1 : sauvegarder la premiere distance captee dans dDepart.
			 * <li> Etape 2 : tour est l'angle de rotation : il est divise par deux a chaque passage dans la boucle while
			 * <li> Etape 3 : Boucle : on mesure a nouveau d
			 * <ul> 
			 * <li> Si d<dDepart : dDepart vaut d
			 * <li> Sinon : le robot se remet a sa position precedente
			 * </ul>
			 * </ul>
			 */
			trouve = true;
			dDepart = d;
			System.out.println("dDepart vaut : "+dDepart);
			
			int tour = 10;
			while(tour>1) {
				MouvementsBasiques.tourner(tour);
				Ultrason.setDistance();
				d = Ultrason.getDistance();
				if(d<=dDepart) {
					dDepart=d;
					System.out.println("oui : "+d);
					angleTotal+=tour;
				}
				else {
					MouvementsBasiques.tourner(-tour);
					angleTotal-=tour;
					System.out.println("non : "+d);
				}
				tour=tour/2;
			}
			
			//*100 parce que le capteur mesure en m et que la fonction prend en cm
			MouvementsBasiques.chassis.travel(dDepart*100);
			//cas ou le robot capte le vide : arret de dichotomique
			while(MouvementsBasiques.chassis.isMoving()) {
				if(Couleur.videTouche()) {
					MouvementsBasiques.chassis.stop();
					return(1);
				}
			}
		}
//			Pince.fermer();
		if(trouve) {
			return(0);
		}
		return(2);
			
	}//fin dichotomique
	
	/**
	 * Permet de verifier que le robot est bien aligne avec le palet.
	 * <p>Il faut ouvrir les pinces avant de lancer cette methode.</p>
	 * @return true si le capteur de contact est actif
	 */
	public static boolean verif() {
		MouvementsBasiques.chassis.travel(-30);
		MouvementsBasiques.chassis.travel(40);
		return Toucher.getTouche();
	}
}
