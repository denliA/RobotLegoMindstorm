package moteurs;

import java.awt.Robot;

import capteurs.*;
import capteurs.Couleur.BufferContexte;
import capteurs.CouleurLigne.ContextePID;
import lejos.hardware.Sound;
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
		
		System.out.println("DEBUT DE SUIVRE LIGNE ("+MouvementsBasiques.pilot.getLinearSpeed()+" / "+MouvementsBasiques.pilot.getLinearAcceleration()+")");
		
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

		Couleur.startScanAtRate(1);
		MouvementsBasiques.chassis.travel(Float.POSITIVE_INFINITY); // Le robot commence à avancer tout droit sans arret
		
		
		//ContextePID infos_gris = c.contexteGris;
		BufferContexte last, previous=null;
		//float[] signes = c.dominations(CouleurLigne.GRIS, seDeplace);
		
		
		while(seDeplace) {
			long debut;
			int cycles = 0;
			if ((last=Couleur.buffer.getLast()).couleur_x!=c && seDeplace && (c.estEntreDeux(CouleurLigne.GRIS, last.rgb_x, last.ratios_x)||last.couleur_x==CouleurLigne.GRIS)) {
				System.out.println("  Entrée dans le premier while du suivi");
				//tourner à gauche pendant dureeRotation
				debut = System.currentTimeMillis();
				float first_distance=c.distanceDe(last.rgb_x, true), distance;
				Moteur.MOTEUR_DROIT.setSpeed(defaultSpeedDroit*1.27f); //TODO utiliser le repport des vitesses gauche et droit par défaut au lieu d'un 1.27/1.25?
				while((last=Couleur.buffer.getLast()).couleur_x!=c && ((System.currentTimeMillis() - debut) < dureeRotation) && seDeplace && (c.estEntreDeux(CouleurLigne.GRIS, last.rgb_x, last.ratios_x)||last.couleur_x==CouleurLigne.GRIS)) {
					if (!c.estEntreDeux(CouleurLigne.GRIS, last.rgb_x, last.ratios_x)&&last.couleur_x!=CouleurLigne.GRIS) {
						Sound.beep();
						break;
					}
//					distance = c.distanceDe(last.rgb_x, true);
//					if (distance > first_distance - 5 || c.intersections.containsKey(last.couleur_x)) //TODO expérimentale
//						break;
				}
				Moteur.MOTEUR_DROIT.setSpeed(defaultSpeedDroit);
				if ((last=Couleur.buffer.getLast()).couleur_x!=c) {
					//tourner à droite pendant dureeRotation*2
					debut = System.currentTimeMillis();
					Moteur.MOTEUR_GAUCHE.setSpeed(defaultSpeedDroit*1.25f);
					first_distance=c.distanceDe(last.rgb_x, true);
					System.out.println("  Entrée dans le second while du suivi");
					while((last=Couleur.buffer.getLast()).couleur_x!=c &&((System.currentTimeMillis() - debut) < (dureeRotation*2))&& seDeplace && (c.estEntreDeux(CouleurLigne.GRIS, last.rgb_x, last.ratios_x)||last.couleur_x==CouleurLigne.GRIS)) {
						if (!c.estEntreDeux(CouleurLigne.GRIS, last.rgb_x, last.ratios_x)&&last.couleur_x!=CouleurLigne.GRIS) {
							Sound.beep();
							break;
						}
//						distance = c.distanceDe(last.rgb_x, true);
//						if (distance > first_distance - 5 || c.intersections.containsKey(last.couleur_x)) //TODO expérimentale
//							break;
					}
					Moteur.MOTEUR_GAUCHE.setSpeed(defaultSpeedDroit);
				}
				else 
					cycles=0;
				if(Couleur.getLastCouleur()!=c && (cycles>= max_cycles) && seDeplace) {
					//gestion d'erreur le robot n'a pas pu se redresser sur une ligne de couleur et il est perdu. Il faut arreter le mouvement
					MouvementsBasiques.chassis.stop();
					try {
						seRedresserSurLigne(c, true, 45,45); // TODO à calibrer
					}
					catch (Exception e) {
						break;
					}
					cycles = 0;
					MouvementsBasiques.chassis.travel(Float.POSITIVE_INFINITY); 	
				}
				else if (cycles<max_cycles && seDeplace) 
					cycles++;
			}
			else {
				cycles = 0;
			}
		}
		MouvementsBasiques.pilot.setLinearAcceleration(def_acc);
		MouvementsBasiques.chassis.stop(); //Le robot s'arrete
		MouvementsBasiques.pilot.setLinearSpeed(def_speed);
		
		System.out.println("FIN DE SUIVRE LIGNE ("+MouvementsBasiques.pilot.getLinearSpeed()+" / "+MouvementsBasiques.pilot.getLinearAcceleration()+")");
	}
	
	//Le robot doit etre posé sur une ligne à suivre
	public static void suivreLigne() {
		suivreLigne(Couleur.getLastCouleur());
	}
	
	
	
	
	
	
	public static boolean seRedresserSurLigne(CouleurLigne c, boolean gauche_bouge, float max_angle, int vitesse_angulaire) {
		return seRedresserSurLigne(c, gauche_bouge, max_angle, vitesse_angulaire, 3);
	}
	
	
	
	public static boolean seRedresserSurLigne(CouleurLigne c, boolean gauche_bouge, double max_angle, double vitesse_angulaire, int max_iterations) {
		boolean trouve;
		
		System.out.println("	ENTREE DANS SE REDRESSER ("+MouvementsBasiques.pilot.getLinearSpeed()+" / "+MouvementsBasiques.pilot.getLinearAcceleration()+")");
		// On garde les vitesses angulaires d'avant l'appel de cette fonction
		double def_acc = MouvementsBasiques.pilot.getLinearAcceleration();
		double def_speed = MouvementsBasiques.pilot.getLinearSpeed();
		double def_speed_angulaire = MouvementsBasiques.pilot.getAngularSpeed();
		double def_acc_angulaire = MouvementsBasiques.pilot.getAngularAcceleration();
		
		MouvementsBasiques.pilot.setAngularAcceleration(vitesse_angulaire*2); //TODO à calibrer pour avoir des mouvements ni trop lents ni trop brusques
		MouvementsBasiques.pilot.setLinearSpeed(15);
		MouvementsBasiques.pilot.setAngularSpeed(vitesse_angulaire);
		
		boolean retour = true;
		
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
						retour = false;
						break;
					}
				}
			}
			if (Couleur.getLastCouleur()!=c&&seDeplace) {
				MouvementsBasiques.pilot.setAngularSpeed(vitesse_angulaire/4);
				tournerToCouleur(c, gauche_bouge, -20);
				MouvementsBasiques.pilot.setAngularSpeed(vitesse_angulaire);
			}
			if (seDeplace && iterations < max_iterations) {
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
		
		System.out.println("	SORTIE DE SE REDRESSER ("+MouvementsBasiques.pilot.getLinearSpeed()+" / "+MouvementsBasiques.pilot.getLinearAcceleration()+")\"");
		
		return retour;
		
	}
	

	private static boolean tournerToCouleur(CouleurLigne c, boolean gauche_bouge, double angle) {
		System.out.println("		Entrée dans tournertoCouleur");
		float coef = gauche_bouge ? -1 : 1;
		double def_angular_acceleration = MouvementsBasiques.pilot.getAngularAcceleration();
		CouleurLigne t;
		
		Object a;
		MouvementsBasiques.chassis.arc(coef*6.24, angle);
		while((t=Couleur.getLastCouleur()) != c && seDeplace && MouvementsBasiques.pilot.isMoving() )
			;//On sort du while si le robot s'est redressé sur la bonne couleur ou si le temps est ecoulé.
		
		MouvementsBasiques.chassis.setAngularAcceleration(1000);
		System.out.println("    		Entrée dans le stop");
		MouvementsBasiques.chassis.stop();
		Delay.msDelay(500);
		System.out.println("    		Sortie du stop");
		MouvementsBasiques.chassis.setAngularAcceleration(def_angular_acceleration);
		
		System.out.println("		Sortie de tournertoCouleur ("+MouvementsBasiques.pilot.getLinearSpeed()+" / "+MouvementsBasiques.pilot.getLinearAcceleration()+")");
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
	