package moteurs;
import java.util.Vector;

import capteurs.*;
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
		double acceleration = MouvementsBasiques.getAccelerationRobot();
		float[] RGB = Couleur.getRGB();
		if(RGB[0] < 2 &&RGB[1] < 2 &&RGB[2] < 2) {
			if(MouvementsBasiques.isMovingPilot()) { //si le robot bouge via MovePilot, on invoque la méthode stop de MovePilot qui perturbe les RegulatedMoteurs suivants
				MouvementsBasiques.setAccelerationRobot(150);
				MouvementsBasiques.arreter();
				Sound.twoBeeps();
			}else {
				Moteur.MOTEUR_DROIT.setAcceleration(10000);
				Moteur.MOTEUR_GAUCHE.setAcceleration(10000);
				MouvementsBasiques.arreterMoteurs(); //on met la vitesse des moteurs à zero si le robot utilise directement les moteurs
				Sound.beep();
			}
			seDeplace=false;
			MouvementsBasiques.avancerTravel(acceleration,-15); //robot recule
			MouvementsBasiques.tourner(180); //demi-tour
		}
	}
	
	//Le robot doit etre posé et suivre une ligne de couleur donnée par l'utilisateur
	public static void suivreLigne(CouleurLigne c) { //je mets en void car l'interface Deplacement n'est pas encore realisée. Je ne sais pas quoi retourner
		seDeplace = true;
		double def_acc=MouvementsBasiques.pilot.getLinearAcceleration();
		double def_speed = MouvementsBasiques.pilot.getLinearSpeed();
		//MouvementsBasiques.changeVitesseRobot(0.5);
		//MouvementsBasiques.pilot.setLinearAcceleration(def_acc/7);
		MouvementsBasiques.pilot.setLinearSpeed(15);
		MouvementsBasiques.pilot.setLinearAcceleration(20);
		//System.out.println("Linéar speed :"+MouvementsBasiques.getVitesseRobot());
		//System.out.println("Linear acceleration:"+MouvementsBasiques.getAccelerationRobot());
		long debut;
		long dureeRotation = 150; //millisecondes
		int cycles = 0;
		final int max_cycles =3;
		Couleur.startScanAtRate(5); //Lance immediatement le timer qui execute update() toutes les 0.1 secondes. C'est une méthode qui scanne la couleur et met à jour les attrubuts statiques de la classe Couleur 
		MouvementsBasiques.avancer(); // Le robot commence à avancer tout droit sans arret
		float defaultSpeed = Moteur.MOTEUR_GAUCHE.getSpeed();
		while(seDeplace) {
			if (Couleur.getLastCouleur()!=c && /*!Couleur.estSurLigne(c, 'm') &&*/ seDeplace) { //Retourne sur quelle couleur le robot est posé en fonction des attributs statiques de Couleur. C'est une aapproximation en fonction de probabilités.
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
				Moteur.MOTEUR_DROIT.setSpeed(defaultSpeed*1.27f);
				while(Couleur.getLastCouleur()!=c && /*!Couleur.estSurLigne(c, 'm')&&*/((System.currentTimeMillis() - debut) < dureeRotation) && seDeplace) {
					;
				}
				Moteur.MOTEUR_DROIT.setSpeed(defaultSpeed*1.03f);
				if (Couleur.getLastCouleur()!=c) {
					//Sound.beep();
					//tourner à droite pendant dureeRotation*2
					debut = System.currentTimeMillis();
					Moteur.MOTEUR_GAUCHE.setSpeed(defaultSpeed*1.25f);
					while(Couleur.getLastCouleur()!=c && /*!Couleur.estSurLigne(c, 'm')&&*/((System.currentTimeMillis() - debut) < (dureeRotation*2))&& seDeplace) {
						;
					}
					Moteur.MOTEUR_GAUCHE.setSpeed(defaultSpeed);
				}
				else 
					cycles=0;
				//liberer la ressource critique
				MouvementsBasiques.s1.release();
				if(Couleur.getLastCouleur()!=c /*&& !Couleur.estSurLigne(c, 'm') */ && (cycles>= max_cycles) && seDeplace) {
					//gestion d'erreur le robot n'a pas pu se redresser sur une ligne de couleur et il est perdu. Il faut arreter le mouvement
					MouvementsBasiques.arreter();
					try {
						seRedresserSurLigne(c, true, 45,750);
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
		if (MouvementsBasiques.isMovingPilot())
			MouvementsBasiques.arreter(); //Le robot s'arrete
		MouvementsBasiques.pilot.setLinearSpeed(def_speed);
	}
	

	
	//Le robot doit etre posé sur une ligne à suivre
	public static void suivreLigne() {
		suivreLigne(Couleur.getLastCouleur());
	}
	
	
	public static void seRedresserSurLigne(CouleurLigne c, boolean gauche_bouge, float max_angle, int temps) throws exceptions.EchecGarageException {
		boolean trouve;
		double def_acc = MouvementsBasiques.pilot.getLinearAcceleration();
		double def_speed = MouvementsBasiques.pilot.getLinearSpeed();
		double def_speed_angulaire = MouvementsBasiques.pilot.getAngularSpeed();
		double def_acc_angulaire = MouvementsBasiques.pilot.getAngularAcceleration();
		MouvementsBasiques.pilot.setAngularAcceleration(90);
		MouvementsBasiques.pilot.setLinearSpeed(15);
		double bonne_angulaire = max_angle/temps*1000;
		MouvementsBasiques.pilot.setAngularSpeed(bonne_angulaire);
		seDeplace = true;
		int iterations = 0;
		
		while(Couleur.getLastCouleur() != c && seDeplace && iterations < 2) {
			//System.out.println("Itération"+iterations);
			trouve = tournerToCouleur(c, gauche_bouge, max_angle, temps);
			if (!trouve && seDeplace) {
				//System.out.println("If 1 "+gauche_bouge);
				trouve = tournerToCouleur(c, gauche_bouge, -max_angle, temps);
				if(!trouve && seDeplace) {
					gauche_bouge = !gauche_bouge;
					trouve = tournerToCouleur(c, gauche_bouge, max_angle, temps);
					if (!trouve && seDeplace) {
						return;
//						tournerToCouleur(c, gauche_bouge, -max_angle, temps);
//						max_angle = max_angle * 1.5f;
//						gauche_bouge = !gauche_bouge;
//						continue;
					}
				}
			}
			if (Couleur.getLastCouleur()!=c&&seDeplace) {
				//System.out.println("If 2 "+gauche_bouge);
				//if (!Couleur.estSurLigne(c, 'm')) {MouvementsBasiques.avancerTravel(5);continue;}
				MouvementsBasiques.pilot.setAngularSpeed(bonne_angulaire/4);
				tournerToCouleur(c, gauche_bouge, -20, temps);
				MouvementsBasiques.pilot.setAngularSpeed(bonne_angulaire);
				
				//if (Couleur.getLastCouleur() != c && iterations >4);
					//throw new exceptions.EchecGarageException();
			}
			if (seDeplace && iterations == 0) {
				//System.out.println("If 3 "+gauche_bouge);
				MouvementsBasiques.pilot.travel(6);
//				if (Couleur.estSurLigne(c, 'm')) {
//					max_angle = 5;
//				}
			}
			gauche_bouge = !gauche_bouge;
			iterations++;
			
		}
		MouvementsBasiques.pilot.setAngularAcceleration(def_acc_angulaire);
		MouvementsBasiques.pilot.setLinearSpeed(def_speed);
		MouvementsBasiques.pilot.setLinearAcceleration(def_acc);
		MouvementsBasiques.pilot.setAngularSpeed(def_speed_angulaire);
	}
	
	private static boolean tournerToCouleur(CouleurLigne c, boolean gauche_bouge, double angle, int timeOut) { //timeOut = timer qui indique la durée maximale de la rotation d'une roue
		//MouvementsBasiques.tourner(angle, timeOut, gauche_bouge);
		float coef = gauche_bouge ? -1 : 1;
		CouleurLigne t;
		long debut = System.currentTimeMillis(); //temps reel à l'instant ou cette instruction est executée
		MouvementsBasiques.pilot.arc(coef*6.24, angle, true);
		while((t=Couleur.getLastCouleur()) != c && seDeplace && MouvementsBasiques.pilot.isMoving() );//On sort du while si le robot s'est redressé sur la bonne couleur ou si le temps est ecoulé.
		boolean found = t == c;
		long diff = System.currentTimeMillis()-debut;
//		RegulatedMotor moteur = (gauche_bouge? Moteur.MOTEUR_GAUCHE : Moteur.MOTEUR_DROIT);
//		moteur.setAcceleration(7000);
//			moteur.stop();
		MouvementsBasiques.pilot.stop();
		//return diff<timeOut;//retourne la durée du déplacement de la roue
		return found;
	}
	
	
	public static void suivreLignePID(CouleurLigne c, float vitesse) {
		float KP = 40;
		float KD = 25;
		float KI =0;
		seDeplace = true;
		float sign = 1;
		MouvementsBasiques.setVitesseRobot(vitesse);
		MouvementsBasiques.setAccelerationRobot(20);
		MouvementsBasiques.avancerTravel(0.2);
		float DEFAULT_SPEED = Moteur.MOTEUR_DROIT.getSpeed();
		float MAX_SPEED = Moteur.MOTEUR_DROIT.getMaxSpeed();
		float ERROR_MARGIN = 0.01f;
		CouleurLigne.ContextePID contexte = c.contexteGris; // {mode : 0 pour RGB 1 pour Ratios ; indice pour le tableau du mode ; target}
		if (contexte.mode_rgb) {
			KP = KP /255;
			KD = KD/255;
			KI = KI/255;
		}
		System.out.println("Default speed : " + DEFAULT_SPEED);
		System.out.println(c+":  "+c.IRatios);
		System.out.println(CouleurLigne.GRIS+":  "+CouleurLigne.GRIS.IRatios);
		System.out.println("Contexte: "+contexte);
		
		
		float error = 0f;
		float previousError = 0f;
		float integral = 0f;
		float derivative = 0f;
		float correction = 0f;
		float [] RGB, ratios;
		MouvementsBasiques.avancer();
		do {
			RGB = Couleur.getRGB();
			ratios = Couleur.getRatios();
			error = (contexte.mode_rgb ? Couleur.getRGB() : Couleur.getRatios())[contexte.indice] - contexte.target;
			if (true) { //on ne fait rien si l'erreur est negligeable
				if (Float.isNaN(correction)) return;
				integral += error;
				derivative = error - previousError;
				if(derivative>0.15) {
					continue;
				}
				correction = sign*((error * KP)+(integral * KI)+(derivative * KD)); //PID control
				previousError = error;
				System.out.println("Erreur: " +  error + "  Derivee: "+derivative);
				// limiter le output 
				if (correction>MAX_SPEED)
					correction = MAX_SPEED;
				else if (correction<-MAX_SPEED)
					correction = -MAX_SPEED;
				// tourner robot
				Moteur.MOTEUR_GAUCHE.setSpeed(DEFAULT_SPEED+correction);
				Moteur.MOTEUR_DROIT.setSpeed(DEFAULT_SPEED-correction);
//				if(derivative*error > 0  && derivative > .1*error)
//					sign = -sign;
				System.out.println("Corrigée : "+(DEFAULT_SPEED+correction)+"\n");
			}
		}
		while(seDeplace);
	}
}
	