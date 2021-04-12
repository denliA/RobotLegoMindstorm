package carte;

import java.util.concurrent.ConcurrentHashMap;

import capteurs.CouleurLigne;

public class Carte {
	/**
	 * La classe principale du package carte. Cette classe sert � simuler le terrain de la mani�re la plus pr�cise possible.
	 * On calibre cette Carte � chaque fois que le robot travers une ligne, une intersection, ou re�oit une information comme le contact, ou encore l'abscence d'un palet.
	 * On a d�cid� de mettre de coder de mani�re dure les palets et les intersections pour s'�viter des algorithmes de calculs et de positionnement inutiles.
	 * De plus on utilise carteUsuelle pour cr�er une instance de Carte d�s le chargement de cette Classe et l'on l'utilisera comme la carte d'inter�t de notre partie.
	 * L'utilit� de cette classe est que lorsque le robot a besoin d'une information, il peut se baser sur la derni�re version de la carte qu'il a (la derni�re mise � jour qu'il a effectu�).
	 * Cela permet d'avoir une connaissance en temps r�el de la position approximative du robot et de sa direction.
	 * De plus cela permet d'avoir une connaissance de l'�tat du terrain pour �viter de rechercher une deuxi�me fois un palet ou autre.
	 */
	
	/**
	 * Ici sont les arguments de la classe :
	 * - robot qui r�f�rence l'entit� au nom �ponyme
	 * - lignes : notre hashMap de correspondance entre la couleur d'une Ligne et la Ligne en question
	 * - intersections : la liste, hardcoded, des intersections du terrain qu'on manipule
	 * - terrain : qui pose les limites du terrain, par son coin inf�rieur et son coin sup�rieur
	 * - palets : la liste, encore une fois hardcoded, des palets pr�sents sur une table au d�but d'un match ou d'une partie solo
	 */
	private Robot robot;
	private ConcurrentHashMap<CouleurLigne, Ligne> lignes;
	private Point[] intersections = { //Les intersections entre les lignes du terrain.
			new Point(-1,-2), new Point(-1,-1), new Point(-1,0), new Point(-1,1), new Point(-1,2),  // (Rouge, Blanche), (Rouge,Bleue), (Rouge, Noireh), (Rouge, Verte), (Rouge, Blanche)
			new Point(0,-2), new Point(0,-1), new Point(0,0), new Point(0,1), new Point(0,2),       // (Noirev, Blanche), (Noirev, Bleue), (Noirev, Noireh), (Noirev, Verte), (Noirev,Blanche)
			new Point(1,-2), new Point(1,-1), new Point(1,0), new Point(1,1), new Point(1,2)        // (Jaune, Blanche), (Jaune, Bleue), (Jaune, Noireh), (Jaune, Verte), (Jaune, Blanche)
	};
	private Rectangle terrain;
	Palet[] palets = { //Les palets pr�sent sur un terrain complet, il seront mis comme marqu�s si ils ne sont plus la.
			new Palet(new Point(-1,-1)), new Palet(new Point(-1,0)), new Palet(new Point(-1,1)),    // (Rouge,Bleue), (Rouge, Noireh), (Rouge, Verte),
			new Palet(new Point(0,-1)), new Palet(new Point(0,0)), new Palet(new Point(0,1)),       // (Noirev, Bleue), (Noirev, Noireh), (Noirev, Verte),
			new Palet(new Point(1,-1)), new Palet(new Point(1,0)), new Palet(new Point(1,1))        // (Jaune, Bleue), (Jaune, Noireh), (Jaune, Verte)
	};
	
	/**
	 * Le constrcteur.
	 * @param robot
	 * @param lignes
	 * @param terrain
	 */
	public Carte(Robot robot, ConcurrentHashMap<CouleurLigne, Ligne> lignes, Rectangle terrain) {
		this.robot = robot;
		this.lignes = lignes;
		this.terrain = terrain;
	}
	
	/**
	 * Une initialisation de la carte qui a lieu lors du chargement de cette classe.
	 * Elle sera utilis�e comme une carte classique que l'on modifiera, on esp�re gagner en temps d'initialisation de notre carte par ce proc�d�.
	 */
	static public Carte carteUsuelle = new Carte(Robot.robotUsuel, Ligne.hashLignes, new Rectangle(new Point(-2f,-2.5f), new Point(2f,2.5f)));	
	
	
	/**
	 * Les getters.
	 * @return La donn�es d'inter�t sur la carte appelante : le robot, la hashMap des Lignes (m�me si elle est hardcoded dans @see Ligne, pour la r�cuperer dans une variable au besoin), les intersections ou encore le terrain.
	 */
	public Robot getRobot() {
		return(robot);
	}
	
	public ConcurrentHashMap<CouleurLigne, Ligne> getHashLignes(){
		return(lignes);
	}
	
	public Point[] getIntersections() {
		return(intersections);
	}
	
	public Rectangle getTerrain() {
		return(terrain);
	}
	
	/**
	 * M�thode permettant de trouver le palet le plus proche du robot en calculant la distance entre le "centre" du robot et le centre du palet.
	 * Cela nous permet au robot d'avoir un d�but de "strat�gie" qui est d'aller au plus proche de lui.
	 * @return Le palet le plus proche du robot
	 */
	public Palet paletProche() {
		Palet res = palets[0];
		float dist = robot.getPosition().distance(palets[0].getCentre());
		for(int i = 1; i<palets.length;i++) {
			if(robot.getPosition().distance(palets[i].getCentre())<dist) {
				res = palets[i];
				dist = robot.getPosition().distance(palets[i].getCentre());
			}
		}
		return(res);
	}
	
}
