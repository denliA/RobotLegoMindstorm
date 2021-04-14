package moteurs;

import java.util.Vector;

import capteurs.*;
import capteurs.Couleur.BufferContexte;
import capteurs.CouleurLigne.ContextePID;
import lejos.hardware.Sound;
import lejos.utility.Delay;
import lejos.utility.Timer;
import lejos.utility.TimerListener;


public class Pilote {
	static private boolean seDeplace = false;
	static private boolean suiviLigne = false;

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
			if(MouvementsBasiques.isMovingPilot()) { //si le robot bouge via MovePilot, on invoque la méthode sstop de MovePilot qui perturbe les RegulatedMoteurs suivants
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
	
	
	
	
	
	
	
	/**
	 * Fonction pour suivre une ligne de couleur sans s'en décaler
	 * 
	 * @param c
	 */
	public static void suivreLigne(CouleurLigne c) {
		seDeplace = true;
		suiviLigne = true;
		// vitesses d'avant l'appel, à remettre à la fin
		double def_acc=MouvementsBasiques.chassis.getLinearAcceleration();
		double def_speed = MouvementsBasiques.chassis.getLinearSpeed();
		
		
		//Paramètres de calibration
		final float coef_gauche = 1.15f; // TODO calibrer
		final float coef_droit = 1.15f;//TODO calibrer
		final long dureeRotation = 250; //TODO calibrer, en fonction de la vitesse? (200 bien mais se décale vers la gauche des fois)
		MouvementsBasiques.chassis.setLinearSpeed(20);
		MouvementsBasiques.chassis.setLinearAcceleration(5);
		final int max_cycles = 2; // TODO calibrer (cycles commence à 0)
		
		
		long debut;
		int iterations = 0;

		//Vitesses des moteurs
		MouvementsBasiques.chassis.travel(.1); // Pour que le chassis change automatiquement les vitesses des moteurs
		float defaultSpeedGauche = Moteur.MOTEUR_GAUCHE.getSpeed();
		float defaultSpeedDroit = Moteur.MOTEUR_DROIT.getSpeed();

		Couleur.startScanAtRate(1);
		MouvementsBasiques.chassis.travel(Float.POSITIVE_INFINITY); // Le robot commence à avancer tout droit sans arrêt
		
		
		// Si dès le début, le robot se décale de la ligne dès le début, c'est qu'il n'était pas bien placé, et on essaie de le faire se redresser avec tournerJusqua
		boolean sorti;
		int angle=10, vitesse_roues = 50;
		do {
			sorti = false;
			debut = System.currentTimeMillis();
			boolean trouve=false;
			while(System.currentTimeMillis()-debut < dureeRotation*2 && seDeplace) // au tout début, on laisse le robot avancer tout droit pendant une période (dureeRotation)
				;
			if(Couleur.getLastCouleur() != c&&seDeplace) { // Si après cette durée, il s'est déjà décalé
				sorti = true;
				MouvementsBasiques.chassis.stop(); // On arrête le mouvement
				while(MouvementsBasiques.chassis.isMoving() && seDeplace);
				// On se redresse grâce à tournerJusqua (en se limitant à 40° à l'aller, et 80° au retour, valeurs à possiblement corriger) 
				trouve = tournerJusqua(c, true, vitesse_roues, 0, angle);
				trouve|= tournerJusqua(c, false, 50, 0, angle*2);
				MouvementsBasiques.chassis.travel(Float.POSITIVE_INFINITY); // On relance le mouvement
				if (!trouve) { angle = angle+10; vitesse_roues+=30;}
			}
			
		} while(sorti && seDeplace); // On répète l'opération jusuqu'à ce que le robot puisse rester sur la ligne pendant au moins une période
		
		
		BufferContexte last;
		
		
		// On commence le vrai suivi ici
		int cycles = 0; // Compte le nombre de fois qu'on a tourné à droite puis à gauche sans tomber sur la couleur
		while(seDeplace) {
			System.out.println("Début de l'itération "+iterations);
			boolean dec=false;
			if ((last=Couleur.buffer.getLast()).couleur_x!=c && seDeplace) { // Si on est pas sur la couleur, on entre en phase de realignment
				// On tourne d'abord vers la droite, et ce jusqu'à ce qu'on tombe sur la ligne ou que dureeRotation millisecondes se soient écoulées
				debut = System.currentTimeMillis();
				Moteur.MOTEUR_DROIT.setSpeed(defaultSpeedDroit*coef_droit); // On augmente la vitesse de la roue droite
				//System.out.println("	Entrée dans le premier while et cycles="+cycles+"et couleur="+last.couleur_x);
				while((last=Couleur.buffer.getLast()).couleur_x!=c && (((System.currentTimeMillis() - debut) < dureeRotation) || Couleur.variationDistanceDe(c)<0)&& seDeplace) {
					if(c.intersections.containsKey(last.couleur_x)) { // Si on est dans une intersection de la couleur recherchée, arrêter de tourner
						cycles--; dec = true; // Si on est sur l'intersection, on ne compte pas ça comme un cycle sans trouver de couleur.
						break;
					}
				}
				Moteur.MOTEUR_DROIT.setSpeed(defaultSpeedDroit); // On remet le moteur à la bonne vitesse après avoir fini
				
				// Si on ne voit toujours pas la couleur, on tourne dans la direction opposée pendant deux fois plus de temps
				if ((last=Couleur.buffer.getLast()).couleur_x!=c) {
					debut = System.currentTimeMillis();
					Moteur.MOTEUR_GAUCHE.setSpeed(defaultSpeedGauche*coef_gauche);
					//System.out.println("	Entrée dans le second while et cycles="+cycles+"et couleur="+last.couleur_x);
					while((last=Couleur.buffer.getLast()).couleur_x!=c &&(((System.currentTimeMillis() - debut) < (dureeRotation*2.5))||Couleur.variationDistanceDe(c)<0)&& seDeplace) {
						if(c.intersections.containsKey(last.couleur_x)) {// Si on est dans une intersection de la couleur recherchée, arrêter de tourner
							if(!dec) cycles--;
							break;
						}
					}
					Moteur.MOTEUR_GAUCHE.setSpeed(defaultSpeedGauche);
				}
				else {
					cycles=0; // dès qu'on voit la couleur, on met cycles à 0.
				}
				
				// Si on n'a pas trouvé la couleur et qu'on cherche depuis max_cycles fois, on essaie de se redresser à l'arrêt.
				if((last=Couleur.buffer.getLast()).couleur_x!=c && (cycles>= max_cycles) && seDeplace) { 
					//gestion d'erreur le robot n'a pas pu se redresser sur une ligne de couleur et il est perdu. Il faut arrêter le mouvement
					System.out.println("	entrée dans le code de coreection");
					MouvementsBasiques.chassis.setLinearAcceleration(1000);
					MouvementsBasiques.chassis.stop();
					MouvementsBasiques.chassis.waitComplete();
					MouvementsBasiques.chassis.setLinearAcceleration(10);
					cycles = 0;
					seRedresserSurLigne(c, false, 90,60); // TODO à calibrer (l'angle max et la vitesse de rotation)
					MouvementsBasiques.chassis.travel(Float.POSITIVE_INFINITY); 	
				}
				else if (cycles<max_cycles && seDeplace) { // On incrémente le nombre de cycles passés sans trouver la couleur.
					if (last.couleur_x==CouleurLigne.GRIS || dec) { cycles++; } 
				}
			}
			else {
				cycles = 0; // dès qu'on voit la couleur, on met cycles à 0.
			}
			iterations++;
		}
		
		// On arrête le déplacement, et on remet les valeurs par défaut.
		suiviLigne = false;
		MouvementsBasiques.chassis.stop();
		MouvementsBasiques.chassis.setLinearAcceleration(def_acc);
		MouvementsBasiques.chassis.setLinearSpeed(def_speed);
		
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
		
		System.out.println("	ENTREE DANS SE REDRESSER ("+MouvementsBasiques.pilot.getLinearSpeed()+" / "+MouvementsBasiques.pilot.getLinearAcceleration()+")");
		// On garde les vitesses angulaires d'avant l'appel de cette fonction
		double def_acc = MouvementsBasiques.chassis.getLinearAcceleration();
		double def_speed = MouvementsBasiques.chassis.getLinearSpeed();
		double def_speed_angulaire = MouvementsBasiques.chassis.getAngularSpeed();
		double def_acc_angulaire = MouvementsBasiques.chassis.getAngularAcceleration();
		
		MouvementsBasiques.chassis.setAngularAcceleration(vitesse_angulaire*2); //TODO à calibrer pour avoir des mouvements ni trop lents ni trop brusques
		MouvementsBasiques.chassis.setLinearSpeed(15);
		MouvementsBasiques.chassis.setAngularSpeed(vitesse_angulaire);
		
		boolean retour = true;
		
		if (!suiviLigne)
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
				MouvementsBasiques.chassis.travel(6);
				MouvementsBasiques.chassis.waitComplete();
			}
			
			if (seDeplace && iterations == 0) {
				max_angle = max_angle*2;
			}
			else if(seDeplace && iterations == 1) {
				max_angle = max_angle/4;
			}
			
			gauche_bouge = !gauche_bouge;
			iterations++;
			
		}
		
		// On remet les valeurs comme trouvées avant l'appel
		MouvementsBasiques.chassis.setAngularAcceleration(def_acc_angulaire);
		MouvementsBasiques.chassis.setLinearSpeed(def_speed);
		MouvementsBasiques.chassis.setLinearAcceleration(def_acc);
		MouvementsBasiques.chassis.setAngularSpeed(def_speed_angulaire);
		
		
		return retour;
		
	}
	

	private static boolean tournerToCouleur(CouleurLigne c, boolean gauche_bouge, double angle) {
		float coef = gauche_bouge ? -1 : 1;
		double def_angular_acceleration = MouvementsBasiques.chassis.getAngularAcceleration();
		CouleurLigne t;
		
		MouvementsBasiques.chassis.arc(coef*6.24, angle);
		while((t=Couleur.getLastCouleur()) != c && seDeplace && MouvementsBasiques.chassis.isMoving() )
			;//On sort du while si le robot s'est redressé sur la bonne couleur ou si le temps est écoulé.
		
		MouvementsBasiques.chassis.setAngularAcceleration(1000);
		System.out.println("    		Entrée dans le stop de tounrerToCouleur");
		MouvementsBasiques.chassis.stop(); MouvementsBasiques.chassis.waitComplete();
		MouvementsBasiques.chassis.setAngularAcceleration(def_angular_acceleration);
		
		System.out.println("		Sortie de tournertoCouleur ("+MouvementsBasiques.pilot.getLinearSpeed()+" / "+MouvementsBasiques.pilot.getLinearAcceleration()+")");
		return (t==c);
	}
	
	public static void chercheLigne(CouleurLigne c,double vitesseLineaire,double accelerationLineaire,double vitesseAngulaire, boolean adroite) {
		MouvementsBasiques.chassis.setAngularSpeed(vitesseAngulaire);
		MouvementsBasiques.chassis.setLinearSpeed(vitesseLineaire);
		MouvementsBasiques.chassis.setLinearAcceleration(accelerationLineaire);
		
		MouvementsBasiques.chassis.travel(Double.POSITIVE_INFINITY);
		while(Couleur.getLastCouleur()!=c);
		MouvementsBasiques.chassis.travel(10); //avance de 10 cm
		MouvementsBasiques.chassis.waitComplete();
		tournerJusqua(c,adroite,300,200);
		tournerJusqua(c, !adroite, 40);
		
	}
	
	public static CouleurLigne chercheLigne(Vector <CouleurLigne> c,double vitesseLineaire,double accelerationLineaire,double vitesseAngulaire) {
		MouvementsBasiques.chassis.setAngularSpeed(vitesseAngulaire);
		MouvementsBasiques.chassis.setLinearSpeed(vitesseLineaire);
		MouvementsBasiques.chassis.setLinearAcceleration(accelerationLineaire);
		
		MouvementsBasiques.chassis.travel(Double.POSITIVE_INFINITY);
		CouleurLigne t;
		while((t=Couleur.getLastCouleur())==CouleurLigne.GRIS || !c.contains(t));
		MouvementsBasiques.chassis.travel(10); //avance de 10 cm
		MouvementsBasiques.chassis.waitComplete();
		tournerJusqua(t,true,300,200);
		tournerJusqua(t, false, 40);
		return t;
		
	}
	
	
	
	/**
	 * Appelle la fonction tournerJusqua() avec une attente initiale par défaut de 300 milliSecondes et sans limite d'angle (4000, largement plus que le tour)
	 * @see #tournerJusqua(CouleurLigne, boolean, int, int, int)
	 */
	public static boolean tournerJusqua(CouleurLigne c, boolean adroite, int vitesse) {
		return tournerJusqua(c, adroite, vitesse, 300);
	}
	
	
	/**
	 * Appelle la fonction {@link #tournerJusqua(CouleurLigne, boolean, int, int, int)} sans limite d'angle (4000, largement plus que necessaire)
	 * @see #tournerJusqua(CouleurLigne, boolean, int, int, int)
	 */
	public static boolean tournerJusqua(CouleurLigne c, boolean adroite, int vitesse, int temps_deb) {
		return tournerJusqua(c, adroite, vitesse, temps_deb, 4000);
	}
	
	
	
	
	/**
	 * Fonction pour tourner sur soi jusqu'à ce qu'on tombe sur la bonne couleur. 
	 * @param c couleur vers laquelle on veut tourner
	 * @param adroite précise si le robot tourne dans le sens anti-horaire (true) ou horaire (false)
	 * @param vitesse vitesse à laquelle on tourne (vitesse des roues, en angle/s). Plus on est lent, plus on est précis
	 * @param temps_deb temps à attendre après avoir commencé à tourner avant de commencer à vérifier la couleur. Utile quand on veut faire un 180° sur une couleur,
	 * auquel cas il ne faut pas commencer à vérifier immédiatement car en se trouve sur la couleur au départ
	 * @param max_angle 
	 */
	public static boolean tournerJusqua(CouleurLigne c,boolean adroite, int vitesse, int temps_deb, int max_angle) {
		
		// Mise en place de la vérification de non dépassement de l'angle maximal
		double max_angle_roues = 1.1*MouvementsBasiques.trackWidth*max_angle/5.6; // Calcul de l'angle maximal que doit faire une roue selon l'angle maximal du robot.
		int tacho_debut = Moteur.MOTEUR_DROIT.getTachoCount();
		
		
		// Si on est déjà sur la bonne couleur ET que l'on veut attendre 0 secondes avant de commencer à vérifier (c'est à dire qu'on veut juste retrouver la ligne),
		// alors on retourne directement
		if(Couleur.getLastCouleur() == c && temps_deb ==0) {
			return true;
		}
		
		// Mise en place des conditions initiales
		if(!suiviLigne)	{
			seDeplace = true;
		}
		Moteur.MOTEUR_DROIT.setAcceleration(20*vitesse);
		Moteur.MOTEUR_GAUCHE.setAcceleration(20*vitesse);
		Moteur.MOTEUR_DROIT.setSpeed(vitesse);
		Moteur.MOTEUR_GAUCHE.setSpeed(vitesse);
		
		
		// Lancement de la rotation
		Moteur.MOTEUR_GAUCHE.startSynchronization();
		if(adroite) {
			Moteur.MOTEUR_DROIT.forward();
			Moteur.MOTEUR_GAUCHE.backward();
		}
		else {
			Moteur.MOTEUR_DROIT.backward();
			Moteur.MOTEUR_GAUCHE.forward();
		}
		Moteur.MOTEUR_GAUCHE.endSynchronization();
		
		
		// Attente de quelques mS pour être sûrs de ne pas tomber immédiatement sur la ligne 
		long debut = System.currentTimeMillis();
		while(System.currentTimeMillis()-debut<temps_deb && seDeplace && Math.abs(Moteur.MOTEUR_DROIT.getTachoCount()-tacho_debut) <= max_angle_roues)
			;
		CouleurLigne coul;
		// Arrêt de la rotation juste après la détection de la ligne
		while((coul=Couleur.getLastCouleur())!=c&&seDeplace && Math.abs(Moteur.MOTEUR_DROIT.getTachoCount()-tacho_debut) <= max_angle_roues);
		Moteur.MOTEUR_GAUCHE.startSynchronization();
			Moteur.MOTEUR_DROIT.stop();
			Moteur.MOTEUR_GAUCHE.stop();
		Moteur.MOTEUR_GAUCHE.endSynchronization();
		Delay.msDelay(50);
		if(Moteur.MOTEUR_DROIT.getTachoCount()-tacho_debut>max_angle_roues) {
			//System.out.println("((a dépassé l'angle)) (("+Moteur.MOTEUR_DROIT.getTachoCount()+",   "+tacho_debut);
		}
		return coul==c;
			
	}
	

}