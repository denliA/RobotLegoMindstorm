package carte;

public class Robot extends Rectangle {
	/**
	 * Classe representant le robot dans la simulation du terrain de @see Carte. 
	 * Utilisee pour avoir les informations telles que la position et la direction (toutes 2 relatives a un plan orthogonal
	 * qui serait trace par les 2 lignes noire et dirige de maniere a ce que l'axe des abscisse traverse les 2 lignes blanches)
	 * l'unite en abscisses est de 50cm et 60cm en ordonnees. 
	 * De plus ici sont certaines donnees en plus que nous n'avons malheureusement pas pu exploiter comme nous l'aurions voulu.
	 */
	private Point position;
	final float longueur = 29f;
	final float largeur = 16f;
	private float direction;
	private Rectangle roueDroite;  //A voir quoi faire des roues exactement mais je cherche comme faire tourner le robot (le rectangle) et quand je l'aurai 
	private Rectangle roueGauche;  //on pourra faire tourner les roue pour toujours savoir ou est quel element, mais en vrai elles ne me semble pas indispensable
	private Cercle capteurCouleur;
	
	/**
	 * Constructeur du robot. il suffit de donner le point au "centre" du robot et sa direction relative pour facilement construire notre robot virtuel.
	 * @param position
	 * @param direction
	 */
	public Robot(Point position, float direction) {
		super(new Point(position.getX()-8f, position.getY()-14.5f), new Point(position.getX()+8f, position.getY()+14.5f));
		this.position = position;
		this.direction = direction;
		this.roueDroite = new Rectangle(new Point(position.getX()+6.6f,position.getY()-10.3f),new Point(position.getX()+9.4f,position.getY()-4.7f));
		this.roueGauche = new Rectangle(new Point(position.getX()-9.4f,position.getY()-10.3f),new Point(position.getX()+6.6f,position.getY()-4.7f));
		this.capteurCouleur = new Cercle(new Point(position.getX(), position.getY()+10.5f), 0.5f);
	}
	
	/**
	 * Nous construisons un robot static,
	 * il permettra d'être simplement modifie pour simplifier notre programme et ameliorer l'encapsulation car nous n'accedons qu'a une instance static du robot.
	 */
	static Robot robotUsuel = new Robot(Point.INCONNU, Float.NaN);
	
	/**
	 * Les getters de la classe Robot.
	 * @return L'attribut d'interet du robot : sa position, sa direction ou encore la position de son capteur de couleurs.
	 */
	public Point getPosition() {
		return(this.position);
	}
	
	public float getDirection() {
		return(this.direction);
	}
	
	public Cercle getCapteurCouleur() {
		return(this.capteurCouleur);
	}
	
	/**
	 * Le setter de position.
	 * Permet de modifier la position du robot, lorsqu'il se deplace notamment.
	 * Permet de connaitre a chaque instant la position du robot et donc de le faire agir en consequence.
	 * @param x
	 * @param y
	 */
	public void setPosition(float x, float y) {
		this.position = new Point(x,y);
		this.getBasGauche().setX(x-8f);
		this.getBasGauche().setY(y-14.5f);
		this.roueGauche.setPosition(x-8f, y-7.5f);
		this.roueDroite.setPosition(x+8f, y-7.5f);
		this.capteurCouleur.setCentre(x, y+10.5f);
		this.getHautDroite().setX(x+8);
		this.getHautDroite().setY(y+14.5f);
	}
	
	/**
	 * Le setter de direction.
	 * Permet de modifier la direction du robot, lorsqu'il se tourne.
	 * Permet de connaitre a chaque instant la direction qu'a le robot et de le faire se redresser en consequence, lorsqu'il veut aller deposer un palet par exemple.
	 * @param angle
	 */
	public void setDirection(float angle) {
		this.direction = angle;
	}
	 /**
	  * Permet de calculer la direction qu'a le robot apres avoir tourner d'un certain angle entre en parametre.
	  * Est utilisee dans la methode @see Robot#deplacer(float, float).
	  * @param angle
	  */
	private void tourner(float angle) {
		this.direction = (direction + angle)%360;
	}
	
	/**
	 * Permet de calculer la position du robot apres un deplacement en ligne droite suivant sa direction.
	 * Est utilisee dans la methode @see Robot#deplacer(float, float).
	 * @param distance
	 */
	private void avancer(float distance) {
		this.setPosition((float) (this.position.getX()+(Math.cos(direction)*distance)),(float) (this.position.getY()+(Math.sin(direction)*distance)));
	}
	
	/**
	 * Permet de deplacer le robot de maniere "humaine" dans le sens ou on considere qu'on le tourne comme on veut puis qu'on l'amene a un point (x,y).
	 * Peut etre utilise pour se calibrer apres un mouvement complexe dont on aurait enregistre les informations durant le mouvement
	 * en une seule etape dans la carte plutot que de calculer en continue ce qui ne serait pas forcement utile.
	 * @param x
	 * @param y
	 * @param directionRelative
	 */
	public void deplacer(float x, float y, float directionRelative) {
		this.tourner(directionRelative);
		this.avancer(position.distance(new Point(x,y)));
	}
	
	/**
	 * Permet de deplacer le robot de maniere simple, il avance d'une distance entree en parametre et avant cela tourne de la directionRelative entree.
	 * Le mouvement est donc une simple ligne droite dans une direction relative au robot.
	 * @param distance
	 * @param directionRelative
	 */
	public void deplacer(float distance, float directionRelative) {
		this.tourner(directionRelative);
		this.avancer(distance);
	}
	
	/**
	 * Un simple toString, principalement utilise pour le debugging, mais peut servir si l'on a envie de faire une interface qui afficherai a chaque instant
	 * (ou a chaque mise a jour) la position et la direction du robot, ou pour toute autre raison.
	 * @return Une chaine de characteres contenant la position et la direction du robot (relativement a notre repere).
	 */
	public String toString() {
		return "Position : " + position + ". Direction : "+direction; 
	}
	
}