package moteurs;

import java.awt.Robot;

import capteurs.*;
import capteurs.Couleur.BufferContexte;
import capteurs.CouleurLigne.ContextePID;
import lejos.hardware.Sound;
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

		//TODO calibrer
		MouvementsBasiques.pilot.setLinearSpeed(15);
		MouvementsBasiques.pilot.setLinearAcceleration(20);
		long dureeRotation = 150; //TODO calibrer, en fonction de la vitesse?
		final int max_cycles =3; // TODO calibrer

		float defaultSpeedGauche = Moteur.MOTEUR_GAUCHE.getSpeed();
		float defaultSpeedDroit = Moteur.MOTEUR_DROIT.getSpeed();

		Couleur.startScanAtRate(5);
		MouvementsBasiques.pilot.forward(); // Le robot commence à avancer tout droit sans arret
		
		
		ContextePID infos_gris = c.contexteGris;
		BufferContexte last, previous;
		float[] signes = c.dominations(CouleurLigne.GRIS, seDeplace)
		
		
		while(seDeplace) {
			long debut;
			int cycles = 0;
			if ((last=Couleur.buffer.getLast()).couleur_x!=c && seDeplace) {
				//tourner à gauche pendant dureeRotation
				debut = System.currentTimeMillis();
				Moteur.MOTEUR_DROIT.setSpeed(defaultSpeedDroit*1.27f); //TODO utiliser le repport des vitesses gauche et droit par défaut au lieu d'un 1.27/1.25?
				while((last=Couleur.buffer.getLast()).couleur_x!=c && ((System.currentTimeMillis() - debut) < dureeRotation) && seDeplace) {
					if (System.currentTimeMillis() - debut > 50 && !c.intersections.containsKey(last.couleur_x)) {
						float diff = last.ratios_x[infos_gris.indice] - previous.ratios_x[infos_gris.indice];
						if(diff!=0 && Math.signum(diff)!=signes[infos_gris.indice] )
							break;
						
					}
					previous = last;
				}
				Moteur.MOTEUR_DROIT.setSpeed(defaultSpeedDroit);
				if (Couleur.getLastCouleur()!=c) {
					//Sound.beep();
					//tourner à droite pendant dureeRotation*2
					debut = System.currentTimeMillis();
					Moteur.MOTEUR_GAUCHE.setSpeed(defaultSpeedGauche*1.25f);
					while((last=Couleur.buffer.getLast()).couleur_x!=c && /*!Couleur.estSurLigne(c, 'm')&&*/((System.currentTimeMillis() - debut) < (dureeRotation*2))&& seDeplace) {
						if (System.currentTimeMillis() - debut > 50 && !c.intersections.containsKey(last.couleur_x)) {
							float diff = last.ratios_x[infos_gris.indice] - previous.ratios_x[infos_gris.indice];
							if(diff!=0 && Math.signum(diff)!=signes[infos_gris.indice] )
								break;
							
						}
						previous = last;
					}
					Moteur.MOTEUR_GAUCHE.setSpeed(defaultSpeedGauche);
				}
				else 
					cycles=0;
				//liberer la ressource critique
				MouvementsBasiques.s1.release();
				if(Couleur.getLastCouleur()!=c && (cycles>= max_cycles) && seDeplace) {
					//gestion d'erreur le robot n'a pas pu se redresser sur une ligne de couleur et il est perdu. Il faut arreter le mouvement
					MouvementsBasiques.pilot.stop();
					try {
						seRedresserSurLigne(c, true, 45,60); // TODO à calibrer
					}
					catch (Exception e) {
						break;
					}
					cycles = 0;
					MouvementsBasiques.pilot.forward(); 	
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
	
	
	
	
	
	
	public static boolean seRedresserSurLigne(CouleurLigne c, boolean gauche_bouge, float max_angle, int vitesse_angulaire) {
		return seRedresserSurLigne(c, gauche_bouge, max_angle, vitesse_angulaire, 2);
	}
	
	
	
	public static boolean seRedresserSurLigne(CouleurLigne c, boolean gauche_bouge, double max_angle, double vitesse_angulaire, int max_iterations) {
		boolean trouve;
		
		// On garde les vitesses angulaires d'avant l'appel de cette fonction
		double def_acc = MouvementsBasiques.pilot.getLinearAcceleration();
		double def_speed = MouvementsBasiques.pilot.getLinearSpeed();
		double def_speed_angulaire = MouvementsBasiques.pilot.getAngularSpeed();
		double def_acc_angulaire = MouvementsBasiques.pilot.getAngularAcceleration();
		
		MouvementsBasiques.pilot.setAngularAcceleration(vitesse_angulaire*2); //TODO à calibrer pour avoir des mouvements ni trop lents ni trop brusques
		MouvementsBasiques.pilot.setLinearSpeed(15);
		MouvementsBasiques.pilot.setAngularSpeed(vitesse_angulaire);
		
		
		seDeplace = true;
		int iterations = 0;
		
		while(Couleur.getLastCouleur() != c && seDeplace && iterations < max_iterations) {
			trouve = tournerToCouleur(c, gauche_bouge, max_angle);
			if (!trouve && seDeplace) {
				trouve = tournerToCouleur(c, gauche_bouge, -max_angle);
				if(!trouve && seDeplace) {
					gauche_bouge = !gauche_bouge;
					trouve = tournerToCouleur(c, gauche_bouge, max_angle);
					if (!trouve && seDeplace) {
						trouve = tournerToCouleur(c, gauche_bouge, -max_angle);
						return false;
					}
				}
			}
			if (Couleur.getLastCouleur()!=c&&seDeplace) {
				MouvementsBasiques.pilot.setAngularSpeed(vitesse_angulaire/4);
				tournerToCouleur(c, gauche_bouge, -20);
				MouvementsBasiques.pilot.setAngularSpeed(vitesse_angulaire);
			}
			if (seDeplace && iterations == 0) {
				MouvementsBasiques.pilot.travel(6);
			}
			gauche_bouge = !gauche_bouge;
			iterations++;
			
		}
		
		// On remet les valeurs comme trouvées avant l'appel
		MouvementsBasiques.pilot.setAngularAcceleration(def_acc_angulaire);
		MouvementsBasiques.pilot.setLinearSpeed(def_speed);
		MouvementsBasiques.pilot.setLinearAcceleration(def_acc);
		MouvementsBasiques.pilot.setAngularSpeed(def_speed_angulaire);
		return true;
		
	}
	

	private static boolean tournerToCouleur(CouleurLigne c, boolean gauche_bouge, double angle) {
		float coef = gauche_bouge ? -1 : 1;
		double def_angular_acceleration = MouvementsBasiques.pilot.getAngularAcceleration();
		CouleurLigne t;
		
		
		MouvementsBasiques.pilot.arc(coef*6.24, angle, true);
		while((t=Couleur.getLastCouleur()) != c && seDeplace && MouvementsBasiques.pilot.isMoving() )
			;//On sort du while si le robot s'est redressé sur la bonne couleur ou si le temps est ecoulé.
		
		MouvementsBasiques.pilot.setAngularAcceleration(1000);
		MouvementsBasiques.pilot.stop();
		MouvementsBasiques.pilot.setAngularAcceleration(def_angular_acceleration);
		
		
		return (t==c);
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
	