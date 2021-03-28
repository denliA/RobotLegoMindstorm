package moteurs;
import java.util.Vector;

import capteurs.*;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.robotics.RegulatedMotor;
import lejos.utility.Delay;
import lejos.utility.Timer;
import lejos.utility.TimerListener;


public class Pilote {
	static private boolean seDeplace = false;

	public static boolean getSeDeplace(){
		return seDeplace;
	}
	
	public static void SetSeDeplace(boolean b){
		seDeplace=b;
	}
	
	// Pour lancer périodiquement une fonction qui test si le robot detecte du vide
	private static Timer videListener = new Timer(100, 
			new TimerListener() {
		public void timedOut() {
				vide();
		}
	});
	
	public static void startVideAtRate(int delay) {
		videListener.setDelay(delay);
		videListener.start();
	}
	
	/**
	 * Arrête la prise de mesure périodique.
	 */
	public static void stopVide() {
		videListener.stop();
	}
	
	public static void vide() {
		double acceleration = MouvementsBasiques.getAccelerationRobot()/5;
		if(Couleur.getCouleurLigne()==CouleurLigne.VIDE) {
			MouvementsBasiques.arreter();
			MouvementsBasiques.avancerTravel(acceleration,-5); //robot recule
			MouvementsBasiques.tourner(180); //demi-tour
		}
	}
	
	//Le robot doit etre posé et suivre une ligne de couleur donnée par l'utilisateur
	public static void suivreLigne(CouleurLigne c) { //je mets en void car l'interface Deplacement n'est pas encore realisée. Je ne sais pas quoi retourner
		seDeplace = true;
		double def_acc=MouvementsBasiques.pilot.getLinearAcceleration();
		//MouvementsBasiques.changeVitesseRobot(0.5);
		//MouvementsBasiques.pilot.setLinearAcceleration(def_acc/7);
		MouvementsBasiques.setVitesseRobot(15);
		MouvementsBasiques.setAccelerationRobot(20);
		System.out.println("Linéar speed :"+MouvementsBasiques.pilot.getLinearSpeed());
		System.out.println("Linear acceleration:"+MouvementsBasiques.pilot.getLinearAcceleration());
		long debut;
		long dureeRotation = 125; //millisecondes
		int cycles = 0;
		final int max_cycles = 4;
		Couleur.startScanAtRate(0); //Lance immediatement le timer qui execute update() toutes les 0.1 secondes. C'est une méthode qui scanne la couleur et met à jour les attrubuts statiques de la classe Couleur 
		MouvementsBasiques.avancer(); // Le robot commence à avancer tout droit sans arret
		float defaultSpeed = Moteur.MOTEUR_DROIT.getSpeed();
		while(seDeplace) {
			if (Couleur.getCouleurLigne()!=c && seDeplace) { //Retourne sur quelle couleur le robot est posé en fonction des attributs statiques de Couleur. C'est une aapproximation en fonction de probabilités.
				//arreter le timer lanceur
				//restreindre l'acces a la ressource citique du moteur grace à la semaphore. Il ne faut pas que MovePilot et LargeRegulatedMotor accèdent au meme moteur en meme temps
				//Sound.beep();
				try {
					MouvementsBasiques.s1.acquire();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//tourner à gauche pendant dureeRotation
				debut = System.currentTimeMillis();
				Moteur.MOTEUR_DROIT.setSpeed(defaultSpeed*1.25f);
				while((Couleur.getCouleurLigne()!=c)&&((System.currentTimeMillis() - debut) < dureeRotation) && seDeplace) {
					;
				}
				Moteur.MOTEUR_DROIT.setSpeed(defaultSpeed);
				if (Couleur.getCouleurLigne()!=c) {
					//Sound.beep();
					//tourner à droite pendant dureeRotation*2
					debut = System.currentTimeMillis();
					Moteur.MOTEUR_GAUCHE.setSpeed(defaultSpeed*1.25f);
					while((Couleur.getCouleurLigne()!=c)&&((System.currentTimeMillis() - debut) < (dureeRotation*2))&& seDeplace) {
						;
					}
					Moteur.MOTEUR_GAUCHE.setSpeed(defaultSpeed);
				}
				else 
					cycles=0;
				//liberer la ressource critique
				MouvementsBasiques.s1.release();
				if(Couleur.getCouleurLigne()!=c && (cycles>= max_cycles) && seDeplace) {
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
				else if (cycles<max_cycles && seDeplace) 
					cycles++;
			}
			else {
				cycles = 0;
			}
		}
		MouvementsBasiques.pilot.setLinearAcceleration(def_acc);
		if (MouvementsBasiques.pilot.isMoving())
			MouvementsBasiques.arreter(); //Le robot s'arrete
		MouvementsBasiques.changeVitesseRobot(2);
	}
	

	
	//Le robot doit etre posé sur une ligne à suivre
	public static void suivreLigne() {
		suivreLigne(Couleur.getCouleurLigne());
	}
	
	
	public static void seRedresserSurLigne(CouleurLigne c, boolean gauche_bouge, float max_angle, int temps) throws Exception {
		boolean trouve;
		System.out.println("Linéar speed :"+MouvementsBasiques.pilot.getLinearSpeed());
		System.out.println("Linear acceleration:"+MouvementsBasiques.pilot.getLinearAcceleration());
		double def_acc = MouvementsBasiques.pilot.getLinearAcceleration();
		double def_speed = MouvementsBasiques.pilot.getLinearSpeed();
		MouvementsBasiques.pilot.setLinearSpeed(10);
		MouvementsBasiques.pilot.setLinearAcceleration(10);
		int iterations = 0;
		//float distance=0;
		while(Couleur.getCouleurLigne() != c) {
//			System.out.println("Itération "+ ++iterations + (gauche_bouge ? "Gauche Bouge":"DroiteBouge"));
//			System.out.println("Je tourne");
			trouve = tournerToCouleur(c, gauche_bouge, max_angle, temps);
			if (!trouve) {
				//System.out.println("Pas trouvé,("+ Couleur.getCouleurLigne() + ") je m'apprête à tourner en sens inverse");
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
			if (iterations%4==0) {
				max_angle = max_angle/1.5f;
				temps = (int) (temps/1.5);
			}
//			System.out.println(Couleur.getCouleurLigne());
//			System.out.println();
			
		}
		MouvementsBasiques.pilot.setLinearSpeed(10);
		MouvementsBasiques.pilot.setLinearAcceleration(10);
	}
	
	private static boolean tournerToCouleur(CouleurLigne c, boolean gauche_bouge, double angle, int timeOut) { //timeOut = timer qui indique la durée maximale de la rotation d'une roue
		MouvementsBasiques.tourner(angle, timeOut, gauche_bouge);
		Vector<CouleurLigne> couleurs = new Vector<>();
		CouleurLigne t;
		long debut = System.currentTimeMillis(); //temps reel à l'instant ou cette instruction est executée
		while((t=Couleur.getCouleurLigne()) != c && System.currentTimeMillis()-debut<timeOut)//On sort du while si le robot s'est redressé sur la bonne couleur ou si le temps est ecoulé.
			couleurs.add(t);
		Moteur.MOTEUR_GAUCHE.setAcceleration(7000);
		Moteur.MOTEUR_DROIT.setAcceleration(7000);
		Moteur.MOTEUR_GAUCHE.startSynchronization();
			Moteur.MOTEUR_DROIT.stop();
			Moteur.MOTEUR_GAUCHE.stop();
		Moteur.MOTEUR_GAUCHE.endSynchronization();
		return System.currentTimeMillis()-debut<timeOut;//retourne la durée du déplacement de la roue
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