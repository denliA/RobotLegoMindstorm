package capteurs;

import exceptions.OuvertureException;
import moteurs.MouvementsBasiques;
import moteurs.Pince;
/**
 * Classe statique dont les méthodes permettent la recherche d'un palet grace au capteur ultrason
 * @see Capteur
 * @see Ultrason
 * @see Pince
 */
public class PaletUltrason {
	
	private static int angleTotal;
	private static float dDepart;
	
	/**Retourne l'angle total fait durant la recherche
	 * @return angle total en degrés fait durant la recherche*/
	public static int getAngle() {
		return(angleTotal);
	}
	
	/** Retourne la distance du palet trouvé par rapport au robot
	 * @return distance en mètres entre la position de départ du robot et le palet
	 * */
	public static float getDistance() {
		return dDepart;
	}
	/**
	 * Méthode de recherche de palet reposant sur la recherche séquentielle du minimum dans un vecteur de distances
	 * @throws OuvertureException dans le cas d'une mauvaise gestion des pinces
	 * @deprecated 
	 */
	public static void sequentielle() throws OuvertureException {
		
		/**
		 * initialiser l'infini
		 * <p> Il sera utilise pour verifier que le robot trouve un palet. Condition d'arrêt d'une boucle while.</p>
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
		 * <p> d est la distance mesurée par le capteur ultrasons.
		 * Elle est utilise pour orienter le robot vers le palet.</p> 
		 */
		Ultrason.setDistance();
		float d = Ultrason.getDistance();
		
		System.out.println("Premiere mesure de d : "+d);
		
			/**
			 * Premiere boucle while : recherche grossière du palet
			 * <p>Le but de cette boucle est de capter le palet : le robot tourne de 10 degrés dans le sens trigonométrique
			 *  jusqu'a ce que d soit inférieure a 2m (succès) ou jusqu'a avoir fait un tour sur lui-même (échec).</p>
			 */
			int angle = 0;
			while((d>2||d<=0)&&angle<360) {
				angle+=10;
				MouvementsBasiques.chassis.rotate(10); MouvementsBasiques.chassis.waitComplete();
				Ultrason.setDistance();
				d = Ultrason.getDistance();
				System.out.println(d);
			}

			/**
			 * Affinage de l'angle avec un tableau de mesures
			 * <p>But : trouver l'angle pour lequel la distance est la plus courte, 
			 * s'orienter de cet angle puis avancer de cette distance.</p>
			 * <ul>
			 * <li> Étape 1 : le robot se décale de 5 degrés pour arriver au milieu de la fenetre de captage
			 * <li> Étape 2 : créer le tableau distances qui recueille les valeurs mesurées
			 * <li> Étape 3 : mesures des valeurs tous les 2 degrés
			 * <li> Recherche de la distance minimale dans la même boucle 
			 * <li> Étape 4 : s'orienter correctement et avancer de la distance mesurée
			 * </ul>
			 */
			float dDepart = Ultrason.getDistance();
			System.out.println("dDepart vaut : "+dDepart);
			
			MouvementsBasiques.chassis.rotate(5); MouvementsBasiques.chassis.waitComplete();
			
			float[] distances = new float[10];
			
			MouvementsBasiques.chassis.rotate(-8); MouvementsBasiques.chassis.waitComplete();
			Ultrason.setDistance();
			distances[0]=Ultrason.getDistance();
			
			int indiceMin = 0;
			for(int i =1;i<10;i++) {
				MouvementsBasiques.chassis.rotate(2); MouvementsBasiques.chassis.waitComplete();
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

			MouvementsBasiques.chassis.rotate(-2*(8-indiceMin)); MouvementsBasiques.chassis.waitComplete();
			System.out.println(indiceMin+"\n"+distances[indiceMin]);
			
			//*100 parce que le capteur mesure en m et que la fonction prend en cm
			MouvementsBasiques.chassis.travel(distances[indiceMin]*100); MouvementsBasiques.chassis.waitComplete();
			
			Pince.fermer();
			
	}//fin sequentielle()
	
	
	/**
	 * Méthode de recherche de palet reposant sur la recherche dichotomique de la distance minimale
	 * @param range le rayon de recherche en mètre
	 * @return 0 s'il trouve le palet, 1 s'il rencontre le vide (cas du mur), 2 si pas de palet en vue
	 * @throws OuvertureException dans le cas d'une mauvaise gestion des pinces
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
		 * <p> d est la distance mesurée par le capteur ultrasons.
		 * Elle est utilisée pour orienter le robot vers le palet.</p> 
		 */
		Ultrason.setDistance();
		float d = Ultrason.getDistance();
		
		System.out.println("Premiere mesure de d : "+d);
		
		/**
		 * Premiere boucle while : recherche grossière du palet
		 * <p>Le but de cette boucle est de capter le palet : le robot tourne de 10 degrés dans le sens trigonométrique
		 *  jusqu'a ce que d soit inférieure a 2m (succès) ou jusqu'a avoir fait un tour sur lui-même (échec).</p>
		 */
		int angle = 0;
		while((d>range||d<=0)&&angle<360) {
			angle+=10;
			MouvementsBasiques.chassis.rotate(10); MouvementsBasiques.chassis.waitComplete();
			Ultrason.setDistance();
			d = Ultrason.getDistance();
			System.out.println(d);
		}
//			if(angle==350) {
//				return 2;
//			}
		angleTotal = angle;
		System.out.println("[paletUltrason] Angle total fait : "+angleTotal);
		boolean trouve = false;
		if(d<range) {
			/**
			 * L'algorithme d'affinement de l'angle 
			 * <p>Il ne s'exécute que si le robot a capte un palet (d!=infini). 
			 * Il consiste en une série de comparaisons de distances mesurées.</p>
			 * <ul>
			 * <li> Étape 1 : sauvegarder la premiere distance captée dans dDepart.
			 * <li> Étape 2 : tour est l'angle de rotation : il est divise par deux a chaque passage dans la boucle while
			 * <li> Étape 3 : Boucle : on mesure a nouveau d
			 * <ul> 
			 * <li> Si d<dDepart : dDepart vaut d
			 * <li> Sinon : le robot se remet a sa position précédente
			 * </ul>
			 * </ul>
			 */
			trouve = true;
			dDepart = d;
			interfaceEmbarquee.Musique.startMusic("TargetLocked.wav");
			System.out.println("dDepart vaut : "+dDepart);
			//On met 10.5 degrés pour palier au problèmes mécaniques de décalages
			double tour = 10.5;
			while(tour>1) {
				MouvementsBasiques.chassis.rotate(tour);MouvementsBasiques.chassis.waitComplete();
				Ultrason.setDistance();
				d = Ultrason.getDistance();
				if(d<=dDepart) {
					dDepart=d;
					System.out.println("oui : "+d);
					angleTotal+=tour;
				}
				else {
					MouvementsBasiques.chassis.rotate(-tour);MouvementsBasiques.chassis.waitComplete();
					angleTotal-=tour;
					System.out.println("non : "+d);
				}
				tour=tour/2;
			}
			
			//*100 parce que le capteur mesure en m et que la fonction prend en cm
			MouvementsBasiques.chassis.travel(dDepart*100);
			//cas ou le robot capte le vide : arrêt de dichotomique
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
	 * <p>Il faut ouvrir les pinces avant de lancer cette méthode.</p>
	 * @return true si le capteur de contact est actif
	 */
	public static boolean verif() {
		Toucher.startScan();
		MouvementsBasiques.chassis.travel(-10); MouvementsBasiques.chassis.waitComplete();
		MouvementsBasiques.chassis.travel(13);
		while(MouvementsBasiques.chassis.isMoving()&&!Toucher.getTouche()) {
			;
		}
		return Toucher.getTouche();
	}
}
