package moteurs;
import capteurs.*;


public class Pilote {
	static private boolean seDeplace = false;;

	
	//Le robot doit etre posé et suivre une ligne de couleur donnée par l'utilisateur
	public static void suivreLigne(CouleurLigne c) { //je mets en void car l'interface Deplacement n'est pas encore realisée. Je ne sais pas quoi retourner
		seDeplace = true;
		float defaultSpeed;
		MouvementsBasiques.changeVitesseRobot(0.5);
		long debut;
		long dureeRotation = 100; //millisecondes
		Couleur.startScanAtRate(0); //Lance immediatement le timer qui execute update() toutes les 0.1 secondes. C'est une méthode qui scanne la couleur et met à jour les attrubuts statiques de la classe Couleur 
		MouvementsBasiques.avancer(); // Le robot commence à avancer tout droit sans arret
		while(seDeplace) {
			defaultSpeed = Moteur.MOTEUR_DROIT.getSpeed();
			if (Couleur.getCouleurLigne()!=c) { //Retourne sur quelle couleur le robot est posé en fonction des attributs statiques de Couleur. C'est une aapproximation en fonction de probabilités.
				//arreter le timer lanceur
				//restreindre l'acces a la ressource citique du moteur grace à la semaphore. Il ne faut pas que MovePilot et LargeRegulatedMotor accèdent au meme moteur en meme temps
				try {
					MouvementsBasiques.s1.acquire();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//tourner à gauche pendant dureeRotation
				debut = System.currentTimeMillis();
				Moteur.MOTEUR_GAUCHE.setSpeed(defaultSpeed*1.2f);
				while((Couleur.getCouleurLigne()!=c)&&((System.currentTimeMillis() - debut) < dureeRotation)) {
					;
				}
				Moteur.MOTEUR_GAUCHE.setSpeed(defaultSpeed);
				if (Couleur.getCouleurLigne()!=c) {
					//tourner à droite pendant dureeRotation*2
					debut = System.currentTimeMillis();
					Moteur.MOTEUR_DROIT.setSpeed(defaultSpeed*1.2f);
					while((Couleur.getCouleurLigne()!=c)&&((System.currentTimeMillis() - debut) < (dureeRotation*2))) {
						;
					}
					Moteur.MOTEUR_DROIT.setSpeed(defaultSpeed);
				}
				//liberer la ressource critique
				MouvementsBasiques.s1.release();
				//On relance le timer lanceur immediatement
				if(Couleur.getCouleurLigne()!=c) {
					//gestion d'erreur le robot n'a pas pu se redresser sur une ligne de couleur et il est perdu. Il faut arreter le mouvement
					seDeplace = false|true;
				}
			}
		}
		MouvementsBasiques.arreter(); //Le robot s'arrete
	}
	

	
	//Le robot doit etre posé sur une ligne à suivre
	public static void suivreLigne() {
		suivreLigne(Couleur.getCouleurLigne());
	}
	
	
	public static void seRedresserSurLigne(CouleurLigne c, boolean gauche_bouge) {
		//int def_gauche = Moteur.MOTEUR_GAUCHE.getSpeed();
		//int def_droit = Moteur.MOTEUR_DROIT.getSpeed();
		boolean trouve; 
		while(Couleur.getCouleurLigne() != c) {
			trouve = tournerToCouleur(c, gauche_bouge, 90, 1000);
			if (!trouve) {
				tournerToCouleur(c, gauche_bouge, -180, 1000);
			}
			MouvementsBasiques.avancerTravel(MouvementsBasiques.getVitesseRobot(), 2);
			gauche_bouge = !gauche_bouge;
			
		}
	}
	
	private static boolean tournerToCouleur(CouleurLigne c, boolean gauche_bouge, double angle, int timeOut) {
		MouvementsBasiques.tourner(angle, timeOut, gauche_bouge);
		long debut = System.currentTimeMillis();
		while(Couleur.getCouleurLigne() != c && System.currentTimeMillis()-debut<timeOut);
		(gauche_bouge ? Moteur.MOTEUR_GAUCHE : Moteur.MOTEUR_DROIT).stop();
		return Couleur.getCouleurLigne() == c;
	}
	
	/*
	public Deplacement versIntersection(Ligne ligne1, Ligne ligne2) {
		//TO DO
	}
	
	public Deplacement seRedresserSurLigne() {
		//TO DO
	}
	
	public Deplacement diagonale(Point arrivee) {
		//TO DO
	}
	*/
/*
	Deplacement suivreLigne(ConditionArret condition) {
		return new DeplacementSuivreLigne(condition);
	}
}




class DeplacementSuivreLigne implements Deplacement {
	boolean status;
	ConditionArret condition_arret;
	Thread action = new Thread(new Runnable() {
		@Override
		public void run() {
			MouvementsBasiques.pilot.forward();
		}
	});
	
	public DeplacementSuivreLigne(ConditionArret c) {
		condition_arret = c;
		status = false;
	}
	public DeplacementSuivreLigne() {
		this(new ConditionArret() { 
				public boolean event() {
					return false;}}); } 
		
	public void lancer() {
		
	}
	public void interrompre() {
		
	}
	public void arreter() {
		
	}
	public boolean getStatus() {
		return status;
	}
 */
}