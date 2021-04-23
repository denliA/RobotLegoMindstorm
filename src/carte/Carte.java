package carte;

import java.util.concurrent.ConcurrentHashMap;

import capteurs.CouleurLigne;
import carte.Ligne.Etat;
import exceptions.CalibrageException;
import moteurs.Pilote;

public class Carte {
	/**
	 * La classe principale du package carte. Cette classe sert a simuler le terrain de la maniere la plus precise possible.
	 * On calibre cette Carte a chaque fois que le robot travers une ligne, une intersection, ou revoit une information comme le contact, ou encore l'abscence d'un palet.
	 * On a decide de hardcoded les palets et les intersections pour s'eviter des algorithmes de calculs et de positionnement inutiles.
	 * De plus on utilise carteUsuelle pour creer une instance de Carte des le chargement de cette Classe et l'on l'utilisera comme la carte d'interet de notre partie.
	 * L'utilite de cette classe est que lorsque le robot a besoin d'une information, il peut se baser sur la derniere version de la carte qu'il a (la derniere mise a jour qu'il a effectuee).
	 * Cela permet d'avoir une connaissance en temps reel de la position approximative du robot et de sa direction.
	 * De plus cela permet d'avoir une connaissance de l'etat du terrain pour eviter de rechercher une deuxieme fois un palet ou autre.
	 */
	
	/**
	 * Ici sont les arguments de la classe :
	 * - robot qui reference l'entite au nom eponyme
	 * - lignes : notre hashMap de correspondance entre la couleur d'une Ligne et la Ligne en question
	 * - intersections : la liste, hardcoded, des intersections du terrain qu'on manipule
	 * - terrain : qui pose les limites du terrain, par son coin inferieur et son coin superieur
	 * - palets : la liste, encore une fois hardcoded, des palets presents sur une table au debut d'un match ou d'une partie solo
	 */
	private Robot robot;
	private ConcurrentHashMap<CouleurLigne, Ligne> lignes;
	private Point[][] intersections = { //Les intersections entre les lignes du terrain.
			{new Point(-1,-2), new Point(-1,-1), new Point(-1,0), new Point(-1,1), new Point(-1,2)},  // (Rouge, Blanche), (Rouge,Bleue), (Rouge, Noireh), (Rouge, Verte), (Rouge, Blanche)
			{new Point(0,-2), new Point(0,-1), new Point(0,0), new Point(0,1), new Point(0,2)},       // (Noirev, Blanche), (Noirev, Bleue), (Noirev, Noireh), (Noirev, Verte), (Noirev,Blanche)
			{new Point(1,-2), new Point(1,-1), new Point(1,0), new Point(1,1), new Point(1,2)}       // (Jaune, Blanche), (Jaune, Bleue), (Jaune, Noireh), (Jaune, Verte), (Jaune, Blanche)
	};
	private Rectangle terrain;
	Palet[] palets = { //Les palets présent sur un terrain complet, il seront mis comme marqués si ils ne sont plus la.
			new Palet(new Point(-1,-1)), new Palet(new Point(-1,0)), new Palet(new Point(-1,1)),    // (Rouge,Bleue), (Rouge, Noireh), (Rouge, Verte),
			new Palet(new Point(0,-1)), new Palet(new Point(0,0)), new Palet(new Point(0,1)),       // (Noirev, Bleue), (Noirev, Noireh), (Noirev, Verte),
			new Palet(new Point(1,-1)), new Palet(new Point(1,0)), new Palet(new Point(1,1))        // (Jaune, Bleue), (Jaune, Noireh), (Jaune, Verte)
	};
	
	/**
	 * Le constructeur.
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
	 * Elle sera utilisee comme une carte classique que l'on modifiera, on espere gagner en temps d'initialisation de notre carte par ce procede.
	 */
	static public Carte carteUsuelle = new Carte(Robot.robotUsuel, Ligne.hashLignes, new Rectangle(new Point(-2f,-2.5f), new Point(2f,2.5f)));	
	
	
	/**
	 * Les getters.
	 * @return La donnees d'interet sur la carte appelante : le robot, la hashMap des Lignes (meme si elle est hardcoded dans @see Ligne, pour la recuperer dans une variable au besoin), les intersections ou encore le terrain.
	 */
	public Robot getRobot() {
		return(robot);
	}
	
	public ConcurrentHashMap<CouleurLigne, Ligne> getHashLignes(){
		return(lignes);
	}
	
	public Point[][] getIntersections() {
		return(intersections);
	}
	
	public Rectangle getTerrain() {
		return(terrain);
	}
	
	/**
	 * Methode permettant de trouver le palet le plus proche du robot en calculant la distance entre le "centre" du robot et le centre du palet.
	 * Cela nous permet au robot d'avoir un debut de "strategie" qui est d'aller au plus proche de lui.
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
	
	/**
	 * Cette methode permet d'utiliser la hashMap {@link Ligne#hashPerdu} pour trouver la position exacte du robot.
	 * En effet on appel la methode @see Pilote#chercherPosition() de maniere � recuperer un objet du type @see LCC.
	 * Ensuite en utilisant la hashMap {@link Ligne#hashPerdu} on fait la correspondance entre ce LCC et un objet du type @see Etat qui est juste la position et la direction du robot.
	 */
	public void calibrerPosition() {
		Etat etat = Ligne.hashPerdu.get(Pilote.chercherPosition());
		robot.setPosition(etat.position.getX(), etat.position.getY());
		robot.setDirection(etat.direction);
	}
	
	public void calibrerPosition(CouleurLigne l, CouleurLigne i1, CouleurLigne i2 ) {
		Etat etat = Ligne.hashPerdu.get(new Ligne.LCC(Ligne.hashLignes.get(l), i1,i2));
		robot.setPosition(etat.position.getX(), etat.position.getY());
		robot.setDirection(etat.direction);
	}
	
	/**
	 * Cette methode permet de calibrer la position du robot sur la carte lorsque ce dernier traverse une ligne.
	 * Cela permet d'eviter les imprecision qui s'accumulent duent a des approximations.
	 * Le parametre entre est la ligne en question. 
	 * @param l
	 */
	public void traverseLigne(Ligne l) {
		if(l.getDirection()) {
			robot.setPosition(robot.getPosition().getX(), l.getPosition());
		}
		else {
			robot.setPosition(l.getPosition(), robot.getPosition().getY());
		}
	}
	
	/**
	 * Cette methode permet de calibrer la position du robot lorsqu'il traverse une intersection.
	 * Elle renvoie une erreur lorsque les 2 lignes sont parall�les.
	 * @param l1
	 * @param l2
	 */
	public void traverseIntersection(Ligne l1, Ligne l2) throws CalibrageException{
		if(!(l1.getDirection()^l2.getDirection())) {
			throw new CalibrageException("Les deux lignes sont parall�les, elles n'ont pas d'interseciton.");
		}
		else {
			if(l1.getDirection()) {
				robot.setPosition(l2.getPosition(), l1.getPosition());
			}
			else {
				robot.setPosition(l1.getPosition(), l2.getPosition());
			}
		}
	}
}
