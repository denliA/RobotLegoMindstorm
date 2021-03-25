package moteurs;
import java.util.Vector;

import capteurs.*;
import lejos.hardware.Button;
import lejos.robotics.RegulatedMotor;
import lejos.utility.Delay;


public class Pilote {
	static private boolean seDeplace = false;;

	
	//Le robot doit etre posé et suivre une ligne de couleur donnée par l'utilisateur
	public static void suivreLigne(CouleurLigne c) { //je mets en void car l'interface Deplacement n'est pas encore realisée. Je ne sais pas quoi retourner
		seDeplace = true;
		double def_acc=MouvementsBasiques.pilot.getLinearAcceleration();
		MouvementsBasiques.changeVitesseRobot(0.5);
		MouvementsBasiques.pilot.setLinearAcceleration(def_acc/7);
		long debut;
		long dureeRotation = 250; //millisecondes
		int cycles = 0;
		final int max_cycles = 4;
		Couleur.startScanAtRate(0); //Lance immediatement le timer qui execute update() toutes les 0.1 secondes. C'est une méthode qui scanne la couleur et met à jour les attrubuts statiques de la classe Couleur 
		MouvementsBasiques.avancer(); // Le robot commence à avancer tout droit sans arret
		float defaultSpeed = Moteur.MOTEUR_DROIT.getSpeed();
		while(seDeplace) {
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
				if(Couleur.getCouleurLigne()!=c && (cycles>= max_cycles)) {
					//gestion d'erreur le robot n'a pas pu se redresser sur une ligne de couleur et il est perdu. Il faut arreter le mouvement
					MouvementsBasiques.pilot.stop();
					try {
						seRedresserSurLigne(c, true, 45,500);
					}
					catch (Exception e) {
						break;
					}
					cycles = 0;
					MouvementsBasiques.avancer(); 	
				}
				else if (cycles<max_cycles) 
					cycles++;
			}
		}
		MouvementsBasiques.pilot.setLinearAcceleration(def_acc);
		MouvementsBasiques.arreter(); //Le robot s'arrete
	}
	

	
	//Le robot doit etre posé sur une ligne à suivre
	public static void suivreLigne() {
		suivreLigne(Couleur.getCouleurLigne());
	}
	
	
	public static void seRedresserSurLigne(CouleurLigne c, boolean gauche_bouge, float max_angle, int temps) throws Exception {
		boolean trouve;
		System.out.println("Linéar speed :"+MouvementsBasiques.pilot.getLinearSpeed());
		int iterations = 0;
		//float distance=0;
		while(Couleur.getCouleurLigne() != c) {
//			System.out.println("Itération "+ ++iterations + (gauche_bouge ? "Gauche Bouge":"DroiteBouge"));
//			System.out.println("Je tourne");
			trouve = tournerToCouleur(c, gauche_bouge, max_angle, temps);
			if (!trouve) {
				System.out.println("Pas trouvé,("+ Couleur.getCouleurLigne() + ") je m'apprête à tourner en sens inverse");
				//Button.waitForAnyEvent();
				tournerToCouleur(c, gauche_bouge, -max_angle, temps);
				
				gauche_bouge = !gauche_bouge;
				tournerToCouleur(c, gauche_bouge, max_angle, temps);
			}
			if (Couleur.getCouleurLigne()!=c) {
//				System.out.println("Toujours pas la bonne couleur ("+ Couleur.getCouleurLigne() + ") je m'apprête à faire une marche arrière");
				//Button.waitForAnyEvent();
				tournerToCouleur(c, gauche_bouge, trouve? -20 : max_angle/4, temps*2);
				if (Couleur.getCouleurLigne() != c)
					throw new Exception();
			}
//			System.out.println("Trouvée! J'avance pour vérifier que c'est la bonne");
//			System.out.println("Linéar speed :"+MouvementsBasiques.pilot.getLinearSpeed());
			//Button.waitForAnyEvent();
			MouvementsBasiques.pilot.travel(6);
//			System.out.println("J'inverse les moteurs");
			gauche_bouge = !gauche_bouge;
			if (iterations%2==0) {
				max_angle = max_angle/2;
				temps = (int) (temps/1.5);
			}
//			System.out.println(Couleur.getCouleurLigne());
//			System.out.println();
			
		}
	}
	
	private static boolean tournerToCouleur(CouleurLigne c, boolean gauche_bouge, double angle, int timeOut) {
		MouvementsBasiques.tourner(angle, timeOut, gauche_bouge);
		Vector<CouleurLigne> couleurs = new Vector<>();
		CouleurLigne t;
		long debut = System.currentTimeMillis();
		while((t=Couleur.getCouleurLigne()) != c && System.currentTimeMillis()-debut<timeOut)
			couleurs.add(t);
		Moteur.MOTEUR_GAUCHE.startSynchronization();
			Moteur.MOTEUR_DROIT.stop();
			Moteur.MOTEUR_GAUCHE.stop();
		Moteur.MOTEUR_GAUCHE.endSynchronization();
		return System.currentTimeMillis()-debut<timeOut;
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