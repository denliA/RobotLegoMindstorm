package moteurs;

import java.util.Arrays;
import java.util.Vector;

import capteurs.Couleur;
import capteurs.Couleur.BufferContexte;
import capteurs.CouleurLigne;
import capteurs.Ultrason;
import carte.Carte;
import carte.Ligne;
import carte.Ligne.LCC;
import carte.Point;
import carte.Robot;
import exceptions.PositionPasCalibreeException;
import interfaceEmbarquee.Musique;
import lejos.hardware.Sound;
import lejos.robotics.chassis.Chassis;
import lejos.utility.Delay;

/**
 * Classe contrôlant tous les déplacements intermédiaires utilisés par différents algorithmes de décision.
 * Elle permet à partir de mouvements basiques (avancer, tourner, changer la vitesse des moteurs) d'éffectuer plusieurs types de trajectoires et de déplacements:
 * <ul>
 * <li> Suivre une ligne de couleur
 * <li> Se redresser sur une ligne de couleur
 * <li> Effectuer la trajectoire nécessaire pour collecter les informations se repérer sur la table (ces informations sont renvoyées à la carte qui les interprète)
 * <li> Dans le cas ou le robot connaît sa position, rentrer dans un des deux camps, et/ou se déplacer vers une intersection précise
 * </ul>
 * Note: Une amélioration possible du pilote serait d'y intégrer la classe Deplacement, qu'on a pas pu intégrer faute de temps et qui pourrait rendre
 * la gestion des déplacements composés, et des arrêts de déplacements beaucoup plus efficaces.
 *  
 *
 */
public class Pilote {
	
	/*
	 * Dépendances des autres classes, la carte pour se repérer et le chassis pour se déplacer. 
	 */
	static private Carte carte = Carte.carteUsuelle;
	static private Robot robot = carte.getRobot();
	private static Chassis chassis = MouvementsBasiques.chassis;
	final private static float INF = Float.POSITIVE_INFINITY;
	
	
	
	static volatile private boolean seDeplace = false;
	static volatile private boolean suiviLigne = false; // Permet de s'assurer qu'on ne lance jamais deux suivis de ligne en même temps
	/**
	 * Permet de savoir si le robot est en cours de déplacement
	 * @return true ssi le robot se déplace
	 */
	public static boolean getSeDeplace(){
		return seDeplace;
	}
	
	/**
	 * permet d'arrêter un certain déplacement avant sa fin
	 * @param b false pour arrêter le robot
	 */
	public static void SetSeDeplace(boolean b){
		seDeplace=b;
	}
	


	private volatile static boolean recursion = false; // Permet d'éviter les problèmes de concurrence avec la récursion.
	public volatile static boolean correctionSuivi = false;
	/**
	 * Fonction pour suivre une ligne de couleur sans s'en décaler
	 * <p> La fonction doit être interrompue manuellement par l'appelant en appelant la méthode {@link #SetSeDeplace(boolean)}
	 * 
	 * @param c couleurLigne qu'on doit suivre
	 */
	public static void suivreLigne(CouleurLigne c) {
		if(recursion) {
			seDeplace = true;
			suiviLigne = true;
		}
		else {
			recursion = false;
		}
		// vitesses d'avant l'appel, à remettre à la fin
		double def_acc=MouvementsBasiques.chassis.getLinearAcceleration();
		double def_speed = MouvementsBasiques.chassis.getLinearSpeed();
		
		
		//Paramètres de calibration
		final float coef_gauche = 1.23f;
		final float coef_droit = 1.20f;
		final long dureeRotation = 250; //possibilité de mieux calibrer, en fonction de la vitesse? (200 bien mais se décale vers la gauche des fois)
		MouvementsBasiques.chassis.setLinearSpeed(20);
		MouvementsBasiques.chassis.setLinearAcceleration(5);
		final int max_cycles = 1; //nombre de fois ou il ne trouve pas la couleur avant d'appeler seRedresserSurLigne. Cycles commence à 0.
		
		long debut;

		//Vitesses des moteurs
		MouvementsBasiques.chassis.travel(.1); // Pour que le chassis change automatiquement les vitesses des moteurs
		float defaultSpeedGauche = Moteur.MOTEUR_GAUCHE.getSpeed();
		float defaultSpeedDroit = Moteur.MOTEUR_DROIT.getSpeed();

		Couleur.startScanAtRate(1);
		MouvementsBasiques.chassis.travel(Float.POSITIVE_INFINITY); // Le robot commence à avancer tout droit sans arrêt
		
		correctionSuivi = true;
		// Si dès le début, le robot se décale de la ligne dès le début, c'est qu'il n'était pas bien placé, et on essaie de le faire se redresser avec tournerJusqua
		boolean sorti;
		do {
			int angle=10, vitesse_roues = 50;
			sorti = false;
			debut = System.currentTimeMillis();
			boolean trouve=false;
			while(System.currentTimeMillis()-debut < dureeRotation*2 && seDeplace){
				// au tout début, on laisse le robot avancer tout droit pendant une période (dureeRotation)
			};
			if(Couleur.getLastCouleur() != c&&seDeplace) { // Si après cette durée, il s'est déjà décalé
				sorti = true;
				MouvementsBasiques.chassis.stop(); // On arrête le mouvement
				while(MouvementsBasiques.chassis.isMoving() && seDeplace);
				// On se redresse grâce à tournerJusqua (en se limitant à 40° à l'aller, et 80° au retour, valeurs à possiblement corriger) 
				do {
					trouve = tournerJusqua(c, true, vitesse_roues, 0, angle);
					trouve|= tournerJusqua(c, false, 50, 0, angle*2);
					if(!trouve) {
						if(c.intersections.containsKey(Couleur.getLastCouleur())) {
							chassis.travel(5); chassis.waitComplete(); continue;
						}
						trouve = tournerJusqua(c, true, vitesse_roues, 0, angle);
					}
				} while(!trouve && (angle = angle*2)!=0 && (vitesse_roues=(int)(vitesse_roues*1.5))!=0 && seDeplace);
				MouvementsBasiques.chassis.travel(Float.POSITIVE_INFINITY); // On relance le mouvement
			}
			
		} while(sorti && seDeplace); // On répète l'opération jusuqu'à ce que le robot puisse rester sur la ligne pendant au moins une période
		correctionSuivi = false;
		
		BufferContexte last;
		
		
		// On commence le vrai suivi ici
		int cycles = 0; // Compte le nombre de fois qu'on a tourné à droite puis à gauche sans tomber sur la couleur
		while(seDeplace) {
			boolean dec=false;
			if ((last=Couleur.buffer.getLast()).couleur_x!=c && seDeplace) { // Si on est pas sur la couleur, on entre en phase de realignment
				// On tourne d'abord vers la droite, et ce jusqu'à ce qu'on tombe sur la ligne ou que dureeRotation millisecondes se soient écoulées
				debut = System.currentTimeMillis();
				Moteur.MOTEUR_DROIT.setSpeed(defaultSpeedDroit*coef_droit); // On augmente la vitesse de la roue droite
				while((last=Couleur.buffer.getLast()).couleur_x!=c && (((System.currentTimeMillis() - debut) < dureeRotation) || Couleur.variationDistanceDe(c)<0)&& seDeplace) {
					if(c.intersections.containsKey(last.couleur_x)) { // Si on est dans une intersection de la couleur recherchée, arrêter de tourner
						cycles--; dec = true; // Si on est sur l'intersection, on ne compte pas ça comme un cycle sans trouver de couleur.
						break;
					}
				}
				Moteur.MOTEUR_DROIT.setSpeed(defaultSpeedDroit); // On remet le moteur à la bonne vitesse après avoir fini
				
				// Si on ne voit toujours pas la couleur, on tourne dans la direction opposée pendant deux fois plus de temps
				if ((last=Couleur.buffer.getLast()).couleur_x!=c && seDeplace) {
					int nb_pas_bien = 0; boolean pas_bien;
					debut = System.currentTimeMillis();
					Moteur.MOTEUR_GAUCHE.setSpeed(defaultSpeedGauche*coef_gauche);
					while((last=Couleur.buffer.getLast()).couleur_x!=c     &&    (((System.currentTimeMillis() - debut) < (dureeRotation*2.5))||Couleur.variationDistanceDe(c)<0)&& seDeplace) {
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
					Musique.startMusic("OhNo.wav");
					
					//gestion d'erreur le robot n'a pas pu se redresser sur une ligne de couleur et il est perdu. Il faut arrêter le mouvement
					System.out.println("	entrée dans le code de correction");
					MouvementsBasiques.chassis.setLinearAcceleration(40);
					MouvementsBasiques.chassis.travel(-25);
					MouvementsBasiques.chassis.waitComplete();
					cycles = 0;
					MouvementsBasiques.chassis.setLinearAcceleration(5);
					boolean trouve;
					trouve = tournerJusqua(c, true, 100, 0, 90);
					trouve|= tournerJusqua(c, false, 100, 0, 90*2);
					recursion = true;
					suivreLigne(c);
					return;
				}
				else if (cycles<max_cycles && seDeplace) { // On incrémente le nombre de cycles passés sans trouver la couleur.
					if (true ||last.couleur_x==CouleurLigne.GRIS || dec) { cycles++; } 
				}
			}
			else {
				cycles = 0; // dès qu'on voit la couleur, on met cycles à 0.
			}
		}
		
		// On arrête le déplacement, et on remet les valeurs par défaut.
		suiviLigne = false;
		MouvementsBasiques.chassis.stop();
		MouvementsBasiques.chassis.setLinearAcceleration(def_acc);
		MouvementsBasiques.chassis.setLinearSpeed(def_speed);
		
	}
	
	
	/**
	 * Suit la ligne sur laquelle il est déjà
	 * @see #suivreLigne(CouleurLigne)
	 */
	public static void suivreLigne() {
		suivreLigne(Couleur.getLastCouleur());
	}
	
	/**
	 * Permet de lancer un suivi de ligne de manière non bloquante
	 * @param c couleur à suivre
	 */
	public static void lancerSuivi(CouleurLigne c) {
		if(seDeplace||suiviLigne) {
			throw new RuntimeException("DEUXIEME SUIVI EN MEME TEMPS " + seDeplace + "  " + suiviLigne);
		}
		new Thread(new ArgRunnable(c) {
			public void run() {
				suivreLigne((CouleurLigne)truc);
			}
		}).start();
	}
	
	/**
	 * Permet d'arrêter le suivi de ligne lancé
	 */
	public static void arreterSuivi() {
		while(recursion)
			; // Si la fonction de suivi de ligne s'est appelée récursivement, on attend la fin de la transition entre les deux fonctions avant d'arrêter.
		seDeplace = false; // On envoie le signal d'arrêt
		chassis.waitComplete();
		while(suiviLigne) // On ne rend pas la main à la fonction appelante tant que la fonction du suivi de ligne n'a pas totalement fini.
			Thread.yield();
	}
	
	/**
	 * Se redresser sur la ligne précisée en faisant deux cycles de redressement
	 * @see #seRedresserSurLigne(CouleurLigne, boolean, double, double, int)
	 */
	public static boolean seRedresserSurLigne(CouleurLigne c, boolean gauche_bouge, float max_angle, int vitesse_angulaire) {
		return seRedresserSurLigne(c, gauche_bouge, max_angle, vitesse_angulaire, 2);
	}
	
	/**
	 * Se redresser sur une ligne de couleur. Ne marche que si on est près de la ligne 
	 * 
	 * @param c couleurLigne sur laquelle on souhaite se redresser
	 * @param gauche_bouge si à true, le robot essaie d'abord de tourner dans le sens horaire pour trouver la ligne
	 * @param max_angle angle maximum de recherche. Pour que le robot ne change pas de direction, mettre à 90°
	 * @param vitesse_angulaire vitesse angulaire du redressement, inversement proportionnelle à la précision
	 * @param max_iterations
	 * @return true si on a pu se redresser 
	 */
	public static boolean seRedresserSurLigne(CouleurLigne c, boolean gauche_bouge, double max_angle, double vitesse_angulaire, int max_iterations) {
		boolean trouve;
		
		// On garde les vitesses angulaires d'avant l'appel de cette fonction
		double def_acc = MouvementsBasiques.chassis.getLinearAcceleration();
		double def_speed = MouvementsBasiques.chassis.getLinearSpeed();
		double def_speed_angulaire = MouvementsBasiques.chassis.getAngularSpeed();
		double def_acc_angulaire = MouvementsBasiques.chassis.getAngularAcceleration();
		
		MouvementsBasiques.chassis.setAngularAcceleration(10); //TODO à calibrer pour avoir des mouvements ni trop lents ni trop brusques
		MouvementsBasiques.chassis.setLinearSpeed(15);
		MouvementsBasiques.chassis.setAngularSpeed(vitesse_angulaire);
		
		boolean retour = true;
		
		if (!suiviLigne)
			seDeplace = true;
		int iterations = 0;
		
		while(Couleur.getLastCouleur() != c && seDeplace && iterations < max_iterations) {
			trouve = tournerJusqua(c, !gauche_bouge, (int)vitesse_angulaire, 0, (int)max_angle); // Pn essaie de tourner une fois vers la direction donnée 
			if (!trouve && seDeplace) {
				trouve = tournerJusqua(c, gauche_bouge, (int)vitesse_angulaire, 0, (int)max_angle); // Si on ne trouve pas, on revient à la position de départ
				if(!trouve && seDeplace) {
					gauche_bouge = !gauche_bouge; // On inverse le sens de rotation
					trouve = tournerJusqua(c, !gauche_bouge, (int)vitesse_angulaire, 0, (int)max_angle); // Puis on essaie de trouver de l'autre coté 
					if (!trouve && seDeplace) {
						trouve = tournerJusqua(c, gauche_bouge, (int)vitesse_angulaire, 0, (int)max_angle); // Si on ne trouve toujours pas, on revient à la position de départ et on quitte la fonction en retorunant false
						retour = false;
						break;
					}
				}
			}
			/*
			 * Si on a trouvé pendant la recherche, mais qu'on a légèrement dépassé, on revient en arrière très lentement pour retrouver la bonne position
			 */
			if (Couleur.getLastCouleur()!=c&&seDeplace) {
				MouvementsBasiques.chassis.setAngularSpeed(vitesse_angulaire/4);
				trouve = tournerJusqua(c, gauche_bouge, (int)(vitesse_angulaire/2), 0, (int)20);
				MouvementsBasiques.chassis.setAngularSpeed(vitesse_angulaire);
			}
			if (seDeplace && iterations < max_iterations) { // Après avoir trouvé, on avance de 10cm pour vérifier si l'on sort ou pas
				MouvementsBasiques.chassis.travel(10);
				MouvementsBasiques.chassis.waitComplete();
			}
			
			if (seDeplace && iterations == 0) { // La deuxième fois qu'on recherche, on augmente l'angle de recherche.
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
	

	/**
	 * Cherche une ligne donnée en avançant 
	 * <p> une fois la ligne trouvée, le robot de redresse sur celle ci
	 * 
	 * @see #chercheLigne(Vector, double, double, double, boolean)
	 * @param c coulerLigne cherchée
	 */
	public static CouleurLigne chercheLigne(CouleurLigne c,double vitesseLineaire,double accelerationLineaire,double vitesseAngulaire, boolean adroite) {
		Vector<CouleurLigne> v = new Vector<>();
		v.add(c);
		return chercheLigne(v, vitesseLineaire, accelerationLineaire, vitesseAngulaire, adroite);
		
	}
	
	
	/**
	 * Cherche une des lignes passées en paramètre
	 * <p> Avance jusqu'à ce qu'il tombe sur une des ligne ou qu'il voit le vide. Si une ligne est trouvée,
	 * se gare dessus.
	 * @param c tableau des lignes cherchées
	 * @param vitesseLineaire vitesse linéaire pendant la recherche
	 * @param accelerationLineaire accélération linéaire pendant la recherche
	 * @param vitesseAngulaire vitesse angulaire pendant le redressement sur la ligne
	 * @param adroite direction du redressement
	 * @return la ligne trouvée si une ligne est trouvée, {@link capteurs.CouleurLigne.VIDE}
	 */
	public static CouleurLigne chercheLigne(Vector <CouleurLigne> c,double vitesseLineaire,double accelerationLineaire,double vitesseAngulaire, boolean adroite) {
		
		// On met les vitesse indiquées en paramètre.
		MouvementsBasiques.chassis.setAngularSpeed(vitesseAngulaire);
		MouvementsBasiques.chassis.setLinearSpeed(vitesseLineaire);
		MouvementsBasiques.chassis.setLinearAcceleration(accelerationLineaire);
		
		CouleurLigne t=CouleurLigne.INCONNU; // Si rien n'est trouvé, on retourne CouleurLigne.INCONNU
		boolean vide;
		do {
			MouvementsBasiques.chassis.travel(Double.POSITIVE_INFINITY); // On lance le robot tout droit
			vide = Couleur.videTouche(); // On remet le buffer du vide à 0
			while(!(vide=Couleur.videTouche()) && (!c.contains(t=Couleur.getLastCouleur()))); // Tant qu'on a pas trouvé la bonne couleur, et qu'on ne voit pas du vide, on continue
			if (vide) { // Si on voie du vide, on s'arrête, on se retourne (180°) et on retourne CouleurLigne.VIDE à l'appelant
				MouvementsBasiques.chassis.setLinearAcceleration(250);
				MouvementsBasiques.chassis.stop(); MouvementsBasiques.chassis.waitComplete();
				MouvementsBasiques.chassis.setLinearAcceleration(accelerationLineaire);
				MouvementsBasiques.chassis.travel(-10); MouvementsBasiques.chassis.waitComplete();
				MouvementsBasiques.chassis.rotate(180); MouvementsBasiques.chassis.waitComplete();
				return CouleurLigne.VIDE;
			}
			else { // Sinon, on a trouvé la bonne couleur, alors on avance un peu pour centrer les roues du robot sur la ligne trouvée
				MouvementsBasiques.chassis.travel(10); //avance de 10 cm
				MouvementsBasiques.chassis.waitComplete();
			}
		} while(vide);
		tournerJusqua(t,adroite,250,50); // Puis on tourne jusuqu'à la bonne couleur.
		tournerJusqua(t, !adroite, 40, 0); // On tourne plus lentement de l'autre coté pour s'assurer qu'on n'a pas dépassé la couleur.
		return t;
		
	}
	
	
	
	/**
	 * Appelle la fonction tournerJusqua() avec une attente initiale par défaut de 300 milliSecondes et sans limite d'angle (4000, largement plus que le tour)
	 * @see #tournerJusqua(CouleurLigne, boolean, int, int, int)
	 */
	public static boolean tournerJusqua(CouleurLigne c, boolean adroite, int vitesse) {
		return tournerJusqua(c, adroite, vitesse, 350);
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
		while((coul=Couleur.getLastCouleur())!=c&&seDeplace && Math.abs(Moteur.MOTEUR_DROIT.getTachoCount()-tacho_debut) <= max_angle_roues);
		// Arrêt de la rotation juste après la détection de la ligne
		Moteur.MOTEUR_GAUCHE.startSynchronization();
			Moteur.MOTEUR_DROIT.stop();
			Moteur.MOTEUR_GAUCHE.stop();
		Moteur.MOTEUR_GAUCHE.endSynchronization();
		Delay.msDelay(50);
		if(Moteur.MOTEUR_DROIT.getTachoCount()-tacho_debut>max_angle_roues) {
			//System.out.println("((a dépassé l'angle)) (("+Moteur.MOTEUR_DROIT.getTachoCount()+",   "+tacho_debut);
		}
		if(!suiviLigne)
			seDeplace = false;
		return coul==c;
			
	}
	
	private static Vector<CouleurLigne> longues = new Vector<>(Arrays.asList(new CouleurLigne[] {CouleurLigne.ROUGE, CouleurLigne.JAUNE}));
	private static Vector<CouleurLigne> courtes = new Vector<>(Arrays.asList(new CouleurLigne[] {CouleurLigne.VERTE, CouleurLigne.BLEUE, CouleurLigne.BLANCHE}));
	

	
	/**
	 * Se déplace sur la table de manière à trouver les informations necessaries pour pouvoir se repérer. 
	 * <p> Une fois les informations trouvées, retourne celles-ci
	 * <p> Est utilisé par la carte pour calibrer la position du robot.
	 * @return Une structure de type {@link carte.Ligne.LCC} (ligne-couleur-couleur) indiquant quelle ligne il a suivi, et sur quelles intersections il est passé
	 */
	public static LCC chercherPosition() {
		
		// Configuration des vitesses initiales de recherche
		int vitesse = 15;
		int acceleration = 10;
		chassis.setLinearSpeed(vitesse);
		chassis.setLinearAcceleration(acceleration);
		Delay.msDelay(1000);
		Couleur.videTouche(); Couleur.blacheTouchee(); // Remise à 0 des buffers de blanc et de vide
		
		CouleurLigne ligne = null, inter1=null, inter2 = null; // Les trois informations qu'on cherche à trouver
		
		CouleurLigne c = Couleur.getLastCouleur(); // Couleur initiale sur laquelle se trouve le robot
		
		if (c!=CouleurLigne.GRIS && c!=CouleurLigne.NOIRE && c!=CouleurLigne.INCONNU) { // Si la couleur initiale est déjà une ligne intéressante
			if(longues.contains(c)||courtes.contains(c)) { // Si c'est l'une des lignes qu'on cherche, on essaie de se mettre droit sur cette ligne
				chassis.travel(10); chassis.waitComplete();
				Pilote.tournerJusqua(c, true, 250, 10);
				Pilote.tournerJusqua(c, false, 50, 10);
			}
			else return null;
		}
		
		boolean bcouleur=false,bblanche=false,bvide=false; // booléens qui vont contenir les informations de la recherche
		int times=1;
		do {
			chassis.travel(INF); // On fonce tout droit
			while( !(bblanche=Couleur.blacheTouchee()) && (times==2 ||!(bvide=Couleur.videTouche()))  &&  ((bcouleur=(c=Couleur.getLastCouleur())==CouleurLigne.GRIS)||(bcouleur|=c==CouleurLigne.INCONNU)||(bcouleur=c==CouleurLigne.NOIRE)))
				; // Tant qu'on ne voit pas de couleur intéressante ni le vide, on continue d'avancer 
			
			if (times==1&&bvide) { // Si on voit du vide pour la premère fois, on s'arrête en urgence et on fait un 180°
				chassis.setLinearAcceleration(100); chassis.stop(); chassis.waitComplete(); chassis.setLinearAcceleration(acceleration);
				chassis.travel(-10); chassis.waitComplete();
				chassis.rotate(180); chassis.waitComplete();
			}
			else { // Si c'est autre chose que du vide qui nous a fait sortir, on s'arrête sans se retourner
				chassis.stop();
			}
			times++;
		} while(bvide && times <=2); // On refait l'opération si on est tombé sur du vide
		
		
		
		if (courtes.contains(c)||bblanche) { // Si c'est une des lignes verticales qui a été vue (blanche, verte, bleue)
			inter1 = bblanche ? CouleurLigne.BLANCHE :c; // On mémorise le fait qu'on a vu cette ligne pour la suite
			System.out.println("Inter 1 = "+inter1 + "(bvide="+ bvide+")");
			chassis.travel(10); // On avance de 10 pour pouvoir se redresser sur la dite ligne
			chassis.waitComplete();
			Pilote.tournerJusqua(inter1, true, 250, 30); // On se redresse sur la ligne
			Pilote.tournerJusqua(inter1, false, 50, 30);
			int coef = bvide ? (-1) : 1; // On calcule la direction dans laquelle on va tourner qui dépend de si on a déjà croisé le vide ou pas. 
			/*
			 * On se positionne de manière parallèle à la ligne verticale, pour pouvoir ensuite chercher une ligne horizontale.
			 */
			chassis.rotate(coef*90); chassis.waitComplete(); 
			chassis.travel(15); chassis.waitComplete();
			chassis.rotate(coef*-90); chassis.waitComplete();
			boolean direction_rotation = true; // Direction de rotation après avoir trouvé la ligne horizontale. On calcule cette direction de manière à ce que le robot se trouve à l'opposé de la ligne verticale trouvée au début, pour ne pas perdre du temps
			int iter = 0;
			do {
				if (iter==2) { // Si on croise le vide deux fois : Situation ne pouvant arriver que si on est derrière la ligne de départ, et qu'on marche parallèlement à la ligne blanche. Il faut alors aller de l'autre coté de la ligne pour pouvoir chercher.
					chassis.rotate(coef*-90); chassis.waitComplete();
					chassis.travel(30); chassis.waitComplete();
					chassis.rotate(coef*90); chassis.waitComplete();
				}
				c = Pilote.chercheLigne(longues, 15, 10, 180, direction_rotation);
				direction_rotation = false;
				iter++;
			} while(c==CouleurLigne.VIDE); // On continue la recherche jusuqu'à ce qu'on trouve une des deux lignes (rouge, jaune)
			ligne = c; // On marque quelle ligne on a trouvé 
		}
		
		else if(longues.contains(c)) { // Sinon, si on a directement croisé une ligne horizontale
			ligne = c; // On marque la ligne qu'on va suivre
			chassis.travel(10); chassis.waitComplete(); // On avance pour se redresser sur la dite ligne 
			Pilote.tournerJusqua(c, true, 250, 0);
			Pilote.tournerJusqua(c, false, 50, 0);
		}
		
		int trouvees = (inter1 == null? 0 : 1); // On marque le nombre d'intersections trouvées jusqu'à maintenant
		lancerSuivi(ligne); // On commence à suivre la ligne à la recherche d'intersections
		Couleur.blacheTouchee(); Couleur.videTouche(); // On remet à 0 la detection du vide et du blanc.
		System.out.println("Ligne ="+ligne);
		boolean cblanche = false;
		while(trouvees<2) { // Tant qu'on est pas tombé sur deux intersections
			if((cblanche=Couleur.blacheTouchee()) || ligne.intersections.containsKey(c=Couleur.getLastCouleur())) { // Si on a trouvé une intersection
				if(trouvees==0) {
					inter1 = cblanche ? CouleurLigne.BLANCHE : c; // On vérifie si c'est la blanche 
					// Si c'est la blanche, on arrête le suivi de ligne, et on se retrouve pour chercher l'intersection de l'autre coté.
					// Puis on relance le suivi dans la direction opposée 
					if(cblanche) { 
						arreterSuivi(); 
						System.out.println("Blanche en 1??");
						Pilote.tournerJusqua(ligne, true, 250, 30);
						Pilote.tournerJusqua(ligne, false, 50, 30); 
						cblanche = Couleur.blacheTouchee()&Couleur.blacheTouchee();
						lancerSuivi(ligne);
					}
					System.out.println("Inter 1 = "+inter1);
				}
				else if (cblanche&&(inter1==CouleurLigne.BLANCHE)){ // Si on voit la blanche pour la deuxième fois, on se retourne aussi
					arreterSuivi();
					Pilote.tournerJusqua(ligne, true, 250, 30);
					Pilote.tournerJusqua(ligne, false, 50, 30); 
					cblanche = Couleur.blacheTouchee()&Couleur.blacheTouchee();
					lancerSuivi(ligne);
					continue;
				}
				else if(inter1==c) // Si on tombe deux fois sur la même intersection pour une raison inconnue, on ignore.
					continue;
				else {
					inter2=cblanche ? CouleurLigne.BLANCHE : c; // On mémorise la dernière intersection.
					System.out.println("Inter 2 = "+inter2);
				}
				trouvees++;
			}
		}
		
		/*
		 * Une fois toutes les informations colléctées, on arrête le suivi de ligne, et on renvoie la ligne ainsi que les deux intersections trouvées.
		 */
		arreterSuivi();
		return new LCC(Ligne.hashLignes.get(ligne), inter1, inter2);
			
	}
	
	
	/**
	 * Permet de déplacer un robot <b>calibré</b> d'un point (intersection) à un autre de la table.
	 * <p> Si le robot n'est pas calibré, il se calibre d'abord avant d'aller à la position souhaitée.
	 * <p> Les coordonnées sont dans le repère (IntersectionNoire, IntersectionNoire-->IntersectionJauneVert, IntersectionNoire-->IntersectionNoireVert)
	 * @param x coordonnée x de la destination
	 * @param y coordonnée y de la destination
	 */
	public static void allerVersPoint(float x, float y) {
		
		// Mise en place des conditions initiales.
		boolean sens_change=false;
		chassis.setLinearSpeed(20);
		chassis.setLinearAcceleration(30);
		
		if(robot.getPosition()==Point.INCONNU) { // Si la position du robot n'est pas calibrée, on la calibre avant d'aller au point
			carte.calibrerPosition();
		}
		Point position = robot.getPosition();
		float direction = robot.getDirection();
		float x_depart = position.getX(), y_depart = position.getY(); // On mémorise les positions de départ du robot.
		if(y_depart == 2 && direction == 90 || y_depart == -2 && direction == 270) { // Si le robot est derrière un des deux camps et regarde le vide, on le retorune
			Sound.beep();
			tournerJusqua(Ligne.xToLongues.get(x_depart), true, 250);
			tournerJusqua(Ligne.xToLongues.get(x_depart), false, 50, 20);
			direction = (direction+180)%360; 
			robot.setDirection(direction); // On met à jour la direction
		}
		/*
		 * L'intersection (0,0) pose un problème puisque c'est une intersection Noire-Noire qui est difficile à detecter. Si on veut aller vers cette position,
		 * on contourne en allant d'abord quelque part avec une coordonnée y égale à 0, et ensuite on va à la position (x,y) en passant par la verticale
		 */
		if (x == 0 && y==0 && y_depart != y) { 
			allerVersPoint(x_depart==0? 1:x_depart, y);
			allerVersPoint(0, y);
			return;
		}
		/*
		 * L'intersection (0, -1) pose aussi problème à cause de l'intersection Bleue-Noire qui est complètement effacée.
		 * On contourne lors aussi en allant d'abord au bon y puis en se déplaçant à la verticale.
		 */
		if(x_depart == 0 &&  x == 0 && y == -1 && y_depart != -1) {
			System.out.println("Position au début : " + robot);
			allerVersPoint(x_depart==0? 1:x_depart, y);
			System.out.println("Position après changement de ligne : " + robot);
			allerVersPoint(0, y);
			System.out.println("Position à la fin : " + robot);
			return;
		}
		
		CouleurLigne ligne_arrivee = Ligne.xToLongues.get(x); // Ligne horizontale qu'on doit suivre
		CouleurLigne inters_arrivee = Ligne.yToLongues.get(y);  // Ligne verticale de laquelle on doit détecter l'intersection avec ligne_arrivee
		boolean det = direction ==270; // Permet de savoir si le robot avance ou recule dans le sens des y
		boolean inverse; // Décide si le reobot doit changer de direction en tournant ou pas (dépend de si le point cherché est devant le robot ou derrière lui)
		if(y> y_depart) {
			inverse= !det; 
		}
		else if(y< y_depart) {
			inverse = det;
		}
		else {
			inverse = true; // Si c'est le même y, on inverse la direction pa défaut.
		}
		
		if (x != x_depart) { // Si les x ne sont pas les mêmes, on change d'abord de ligne.
			int bonne_bifurquation = (det ? -1 : 1)*(x>x_depart ? -1 : 1);
			if (Math.abs(y_depart)==2 || y_depart ==0||true) {chassis.travel(30); chassis.waitComplete();}
			chassis.rotate(bonne_bifurquation*90); chassis.waitComplete();
			chercheLigne(ligne_arrivee, 20, 50, 180, (inverse ? !(bonne_bifurquation==1) : (bonne_bifurquation==1))); // On se gare dans la nouvelle ligne de manière à regarder direction le point cherché
			if ((true||Math.abs(y_depart)==2 || y_depart ==0)&&y==y_depart) { // On avance/recule pour regagner la distance perdue en tournant
				if(!inverse) {
					chassis.travel(15); chassis.waitComplete();
				}
				else {
					chassis.setLinearSpeed(10);
					chassis.travel(-30); chassis.waitComplete();
				}
			}
			Pilote.tournerJusqua(Ligne.xToLongues.get(x), true, 50, 20, 15);
			Pilote.tournerJusqua(Ligne.xToLongues.get(x), false, 50, 20, 30);
		}
		else { // Si on est sur la même ligne
			if(y>y_depart && det || y<y_depart && !det) { // Si on regarde dans la mauvais direction
				if(ligne_arrivee!=CouleurLigne.NOIRE || y_depart != 0) { // On se retourne. 
					Pilote.tournerJusqua(ligne_arrivee, true, 250); Pilote.tournerJusqua(ligne_arrivee, false, 50, 20);
				}
				else { // Si c'est l'intersection (0,0), on se retourne en deux étapes
					Sound.beep();
					chassis.rotate(20);chassis.waitComplete();
					Pilote.tournerJusqua(CouleurLigne.NOIRE, true, 250,0);
					Pilote.tournerJusqua(CouleurLigne.NOIRE, true, 250); Pilote.tournerJusqua(ligne_arrivee, false, 50, 20);
				}
			}
		}
		
		if(y!=y_depart) { // Si on est pas au même y 
			lancerSuivi(ligne_arrivee); // maintenant qu'on est dans le même x on peut suivre la ligne jusuqu'à tomber sur une des intersections
			Couleur.blacheTouchee(); 
			while(Couleur.getLastCouleur() != inters_arrivee) {
				if ((Couleur.blacheTouchee() && inters_arrivee!=CouleurLigne.BLANCHE)) {
					sens_change = !sens_change;
					arreterSuivi();
					Pilote.tournerJusqua(ligne_arrivee, true, 250); Pilote.tournerJusqua(ligne_arrivee, false, 50, 20);
					Couleur.blacheTouchee();
					lancerSuivi(ligne_arrivee);
				}
			};
			arreterSuivi();
		}
		
		/*
		 * à la toute fin, on change la position du robot pour la mettre à jour après avoir changé de point (et peu être de direction)
		 */
		robot.setPosition(x, y);
		if (y>y_depart) {
			robot.setDirection(!sens_change ? 90 : 270);
		}
		else if(y<y_depart) {
			robot.setDirection(!sens_change ? 270 : 90);
		}
		else {
			// Normalement la direction ne devrait pas changer ici
			//robot.setDirection(robot.getDirection() == 270 ? 90 : 270);
		}
	}
	
	/**
	 * Permet au robot d'aller vers le camp indiqué sans suivre de ligne 
	 * @param direction angle indiquant la direction du bon camp
	 */
	public static void rentrer(float direction) {
		System.out.println("[rentrer] direction : " + direction + "Robot actuellement : " + robot.getDirection() + "Ce que je dois faire : " + (direction  - robot.getDirection()));
		chassis.rotate(direction  - robot.getDirection()); chassis.waitComplete(); // On tourne de l'angle necessaire pour atteindre l'angle recherché.
		Couleur.blacheTouchee(); // On reset le détecteur de blanc
		chassis.travel(INF); // On fonce tout droit jusqu'à ce que l'on voit du blanc. 
		while(!Couleur.blacheTouchee());
		chassis.stop(); chassis.waitComplete();
	}
	
	/**
	 * Permet de faire aller le robot vers le camp indiqué en suivant une ligne, derrière la ligne blanche, puis se retourner
	 * <p> IMPORTANT : Le robot doit avoir une position calibrée pour pouvoir faire ça. Si il ne l'est pas quand cette méthode est appelée, 
	 * le robot va d'abord se calibrer avant de rentrer.
	 * @param direction le camp vers lequel le robot doit aller. "table", "fenetre" ou "" si le camp n'a pas d'importance
	 */
	public static void rentrer(String direction) {
		rentrer(direction, true);
	}
	
	/**
	 * Permet de faire aller le robot vers le camp indiqué en suivant une ligne, derrière la ligne blanche, puis se retourner
	 * <p> IMPORTANT : Le robot doit avoir une position calibrée pour pouvoir faire ça. Si il ne l'est pas quand cette méthode est appelée, 
	 * le robot va d'abord se calibrer avant de rentrer.
	 * @param direction le camp vers lequel le robot doit aller. "table", "fenetre" ou "" si le camp n'a pas d'importance
	 * @param se_retourner si à true, le robot se retroune après être rentré.
	 */
	public static void rentrer(String direction, boolean se_retourner) {
		assert (direction.equals("porte") || direction.equals("fenetre") || direction.equals("")) : "direction doit indiquer une direction, ou être une chaîne vide";
		float x = robot.getPosition().getX();
		float y = robot.getPosition().getY();
		if(robot.getPosition()==Point.INCONNU) {
			System.out.println("(RENTRER) position: "+robot.getPosition());
			carte.calibrerPosition();
		}
		
		if(y==2&&(direction.equals("porte")&&se_retourner || direction.equals("fenetre")&&!se_retourner)) {
			if(robot.getDirection() == 90) {
				tournerJusqua(Ligne.xToLongues.get(x), true, 250);
				tournerJusqua(Ligne.xToLongues.get(x), false, 50, 20);
			}
		}
		else if (y==-2&&(direction.equals("fenetre")&&se_retourner || direction.equals("porte")&&!se_retourner)) {
			if(robot.getDirection() == 270) {
				tournerJusqua(Ligne.xToLongues.get(x), true, 250);
				tournerJusqua(Ligne.xToLongues.get(x), false, 50, 20);
			}
		}
		else if(y==2) {
			if (robot.getDirection() == 90 && se_retourner) {
				tournerJusqua(Ligne.xToLongues.get(x), true, 250);
				tournerJusqua(Ligne.xToLongues.get(x), false, 50, 20);
			}
		}
		else if(y==-2) {
			if(robot.getDirection() == 270 && se_retourner) {
				tournerJusqua(Ligne.xToLongues.get(x), true, 250);
				tournerJusqua(Ligne.xToLongues.get(x), false, 50, 20);
			}
		}
		else {
			if(direction.equals("porte") && robot.getDirection()==270 || direction.equals("fenetre") && robot.getDirection() == 90) {
				tournerJusqua(Couleur.getLastCouleur(), true, 250);
				tournerJusqua(Couleur.getLastCouleur(), false, 50, 20);
			}
			if (true) {
				lancerSuivi(Ligne.xToLongues.get(x));
				Couleur.blacheTouchee();
				while(!Couleur.blacheTouchee());
				arreterSuivi();
				tournerJusqua(Ligne.xToLongues.get(x), true, 250);
			}
		}
		
		if (y==2 || y == -2) {
			robot.setDirection(y==2 ? 270 : 90);
		}
		else if(direction.equals("porte")) {
			robot.setDirection(270);
			robot.setPosition(x,2);
		}
		else if(direction.equals("fenetre")) {
			robot.setDirection(90);
			robot.setPosition(x, -2);
		}
	}
	
	/** Pour la fonction {@link #verifierPalet(int)} Indique qu'on doit vérifier si il y a un palet dans l'intersection en face*/
	public static int DEVANT = 0b100;
	/** Pour la fonction {@link #verifierPalet(int)} Indique qu'on doit vérifier si il y a un palet dans l'une des deux intersections à gauche/à droite*/
	public static int ACOTE = 0b010;
	/** Pour la fonction {@link #verifierPalet(int)} Indique qu'on doit vérifier si il y a un palet dans l'intersection derrière*/
	public static int DERRIERE = 0b001;
	
	
	/**
	 * Vérifie <b>à partir d'une intersection, et depuis une position calibrée</b>, si un palet se trouve dans l'intérsection d'aprés (sur les longues lignes),
	 * ou dans l'une des deux intersections à coté (sur les courtes lignes)
	 * 
	 * @return la position du premier palet trouvé, ou Point.INCONNU si aucun palet n'a été trouvé.
	 */
	public static Point verifierPalet() {
		return verifierPalet(DEVANT|ACOTE);
	}
	
	/**
	 * Vérifie <b>à partir d'une intersection, et depuis une position calibrée</b>, si un palet se trouve dans les intersections indiquées en paramètre
	 * parmi les intersections autour
	 * @param averifier indique les cotés à vérifier parmi {@link #DEVANT} , {@link #ACOTE} , et {@link #DERRIERE}
	 * @return la position du premier palet trouvé, ou Point.INCONNU si aucun palet n'a été trouvé.
	 */
	public static Point verifierPalet(int averifier){
		
		// On obtient les conditions initiales de la position du robot, ainsi que la ligne depuis laquelle on va vérifier et les intersections sur lesquelles on va tourner.
		float direction = robot.getDirection();
		float x = robot.getPosition().getX();
		float y = robot.getPosition().getY();
		CouleurLigne intersection = Ligne.yToLongues.get(y);
		CouleurLigne ligne = Ligne.xToLongues.get(x);
		
		// on n'accepte pas si le robot n'est pas claibré.
		if(robot.getPosition()==Point.INCONNU) {
			throw new PositionPasCalibreeException();
		}
		
		// Si le robot regarde verticalement et pas horizontalement, on le fait se tourner pour se mettre sur la ligne horizontale
		else if(direction==0 || direction==180) {
			tournerJusqua(ligne, true, 250, 250); tournerJusqua(ligne, false, 50, 20);
			robot.setDirection(direction=(direction+90));
		}
		
		
		float prochain_y = (direction == 90 ?  y+1 : y-1); // Le point que le robot regarde actuellement selon sa direction 
		boolean au_centre = (x==0); // pour savoir si on est dans la méchante ligne noire avec ses intersections indétectables.
		
		
		// On vérifie que l'on n'est pas dans la ligne blanche en direction du camp, auquel cas il n'y a rien à vérifier.
		if(Math.abs(prochain_y)<2 && (DEVANT&averifier)!=0) {
			System.out.println("[verifierPalet] Je vérifie devant ! ");
			// On essaie de vérifier si un palet se trouve dans la prochaine intersection
			Ultrason.setDistance();
			if(Ultrason.getDistance() <= .64f && Ultrason.getDistance() >= .40f) {
				return new Point(x,prochain_y);
			}
		}
		
		if((ACOTE&averifier)!=0) { // Si on doit aussi vérifier à coté, on avance de 8cm pour pouvoir bien tourner.
			chassis.travel(8); chassis.waitComplete();			
		}
		
		
		if(Math.abs(x)==2) { // Si on est sur la blanche.
			// on ne regarde pas sur les cotés
		}
		if(!au_centre && (averifier&ACOTE) != 0) { // Si nous sommes dans une ligne sur le coté et pas centrée (comme la ligne noire)
			boolean a_droite = (x==1 && direction == 90 || x==-1 && direction == 270); // On regarde selon notre position et notre direction de quel coté il faut regarder
			tournerJusqua(intersection, a_droite, 250, 300); tournerJusqua(intersection, !a_droite, 50, 20); // On tourne dans la direction calculée
			robot.setDirection(direction = (direction + (a_droite? 90 : -90))%360); // On met à jour la position du robot
			System.out.println("[verifierPalet] Direction après avoir tourné: " + robot.getDirection());
			// On vérifie si il y a un palet dans l'intersection suivante, ensuite si il y en a un dans celle d'après
			Ultrason.setDistance();
			if(Ultrason.getDistance()<= .56f && Ultrason.getDistance() >= .35f) {
				return new Point(0, y);
			}
			else if(Ultrason.getDistance()<= 1.05f && Ultrason.getDistance() >= .81f) {
				return new Point(-x, y);
			}
			else {
			}
			tournerJusqua(ligne, !a_droite, 250, 0); tournerJusqua(ligne, a_droite, 50, 0); // Si on a pas trouvé, on se remet sur la ligne horizontale
			robot.setDirection(direction = (direction + (a_droite? -90 : 90))%360);
		}
		else  if ((averifier&ACOTE) != 0){ // Si nous sommes sur la ligne noire, on fait la même chose mais en vérifiant à chaque fois des deux cotés
			float coef_a_droite = (direction==90 ? -1 : 1);
			tournerJusqua(intersection, true, 250, 300); tournerJusqua(intersection, false, 50, 20);
			robot.setDirection(direction = ((direction + 90)%360));
			Ultrason.setDistance();
			if(Ultrason.getDistance()<= .56f && Ultrason.getDistance() >= .35f) {
				return new Point(coef_a_droite, y);
			}
			tournerJusqua(intersection, true, 250, 250); tournerJusqua(intersection, false, 50, 20); 
			if(y==0) {
				tournerJusqua(intersection, true, 250, 250); tournerJusqua(intersection, false, 50, 20); 
			}
			robot.setDirection(direction = ((direction - 180)%360));
			Ultrason.setDistance();
			if(Ultrason.getDistance()<= .56f && Ultrason.getDistance() >= .35f) {
				return new Point(-coef_a_droite, y);
			}
			tournerJusqua(intersection, true, 250, 150); tournerJusqua(intersection, false, 50, 20);
			robot.setDirection(direction = (direction + 90));
		}
		
		// Si on n'a trouvé de palet dans aucun des endroits vérifiés, on retourne Point.INCONNU
		return Point.INCONNU;
	}

	
	
	/**
	 * Permet de chercher un palet dans une intersection de la table, en faisant parcourir une ligne au robot tout en vérifiant sur les cotés à chaque intersection.
	 * <p> Cette recherche ne marche que si tous les palets sont déposés sur une intersection.
	 * 
	 * @return Le point sur lequel on a détecté un palet, sinon Point.INCONNU
	 */
	public static Point trouverPalet() {
		
		// On prend les conditions de départ du robot
		float x = robot.getPosition().getX(), y = robot.getPosition().getY();
		Point point;
		
		// On accepte pas si le robot n'est pas claibré
		if(robot.getPosition() == Point.INCONNU) {
			throw new exceptions.PositionPasCalibreeException();
		}
		else if(robot.getDirection()%180 == 0) { // Si le robot est en position verticale, on le remet en position horizontale
			tournerJusqua(Ligne.xToLongues.get(x), true, 250, 250); tournerJusqua(Ligne.xToLongues.get(x), false, 50, 20);
			robot.setDirection(robot.getDirection() + 90);
		}
		else if(robot.getDirection() == 90 && robot.getPosition().getY() == 1 || robot.getDirection() == 270 && robot.getPosition().getY() == -1) { // Si le robot est dans  face à la blanche, on le fait se retourner
			Pilote.allerVersPoint(x, y*2);
			Pilote.allerVersPoint(x, y);
		}
		else if(robot.getDirection() == 90 && robot.getPosition().getY() == 2 || robot.getDirection() == 270 && robot.getPosition().getY() == -2) { // si le robot est face au vide, on le fait se retourner
			Pilote.tournerJusqua(Ligne.xToLongues.get(x), true, 250); Pilote.tournerJusqua(Ligne.xToLongues.get(x), false, 50, 20);
			chassis.travel(-5); chassis.waitComplete();
			robot.setDirection((robot.getDirection() + 180)%360);
		}
		
		if(x==0) { // Si on est sur la ligne noire, on contourne en allant vers la jaune.
			allerVersPoint(x=1, y);
		}
		
		CouleurLigne ligne = Ligne.xToLongues.get(x);
		
		float avancement = robot.getDirection() == 90 ? 1 : -1; // direction du robot 
		if(Math.abs(y) == 2 || y == 0) { // Si nous sommes dans le centre, ou que nous sommes dans une ligne blanche, on vérifie qu'aucun palet ne se trouve dans l'intersection qu'on regarde
			System.out.println("[trouverPalet] début de ligne, je vérifie la première intersection");
			point = verifierPalet(DEVANT);
			if (point != Point.INCONNU) {
				return point;
			}
			else { // Une fois qu'on est sûr que l'intersection est vide, on s'y place pour commencer une recherche sur les trois intersections
				System.out.println("[trouverPalet] rien trouvé dans la première intersection, j'avance");
				allerVersPoint(x, y=(y+avancement));
				if(y-avancement == 0) {
					System.out.println("[trouverPalet] y = 0, je dois me retourner");
					Pilote.allerVersPoint(x, y*2);
					Pilote.allerVersPoint(x, y);
					avancement = -avancement;
				}
			}
		}
		Sound.beep();
		/* à partir de ce point, on se trouve forcément dans la première intersection de notre direction 
		 * et on vérifie dans chacune des trois intersections si on voit un palet ou pas
		 */
		int lignes_verifiees = 0; y  = robot.getPosition().getY();
		while(lignes_verifiees<3) { // Pour chacune des trois intersections on vérifie sur les cotés.
			System.out.println("[trouverPalet] je verifie pour la ligne numero " + (lignes_verifiees+1));
			int verification = ACOTE | (lignes_verifiees < 2 ? DEVANT : 0);
			point = verifierPalet(verification);
			if(point!=Point.INCONNU) {
				return point;
			}
			if(lignes_verifiees < 2)
				allerVersPoint(x, y=(y+avancement));
			lignes_verifiees++;
		}
		
		
		// Si l'on a trouvé dans aucune intersection, on retourne INCONNU.
		return Point.INCONNU;
	}
	
}

/*
 * Runnable prenant un argument.
 */
abstract class ArgRunnable implements Runnable {
	Object truc;
	public ArgRunnable(Object truc) {
		this.truc = truc;
	}
}