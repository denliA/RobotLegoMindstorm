package carte;

/**
 * Classe représentant le robot dans la simulation du terrain de @see Carte. 
 * Utilisée pour avoir les informations telles que la position et la direction (toutes 2 relatives a un plan orthogonal
 * qui serait trace par les 2 lignes noire et dirige de manière a ce que l'axe des abscisse traverse les 2 lignes blanches)
 * l'unite en abscisses est de 50cm et 60cm en ordonnées. 
 * De plus ici sont certaines données en plus que nous n'avons malheureusement pas pu exploiter comme nous l'aurions voulu.
 */
public class Robot extends Rectangle {
	private Point position;
	final float longueur = 29f;
	final float largeur = 16f;
	private float direction;
	private Rectangle roueDroite;  //A voir quoi faire des roues exactement mais je cherche comme faire tourner le robot (le rectangle) et quand je l'aurai 
	private Rectangle roueGauche;  //on pourra faire tourner les roue pour toujours savoir ou est quel element, mais en vrai elles ne me semble pas indispensable
	private Cercle capteurCouleur;
	
	/**
	 * Constructeur du robot. il suffit de donner le point au "centre" du robot et sa direction relative pour facilement construire notre robot virtuel.
	 * @param position position du centre/capteur couleur du robot
	 * @param direction direction du robot
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
	 * il permettra d'être simplement modifie pour simplifier notre programme et améliorer l'encapsulation car nous n'accedons qu'a une instance static du robot.
	 */
	static Robot robotUsuel = new Robot(Point.INCONNU, Float.NaN);
	
	/**
	 * getter
	 * @return la position du robot
	 */
	public Point getPosition() {
		return(this.position);
	}
	/**
	 * getter
	 * @return la direction du robot
	 */
	public float getDirection() {
		return(this.direction);
	}
	/**
	 * getter
	 * @return le cercle représentant le capteur
	 */
	public Cercle getCapteurCouleur() {
		return(this.capteurCouleur);
	}
	
	/**
	 * Le setter de position.
	 * Permet de modifier la position du robot, lorsqu'il se déplace notamment.
	 * Permet de connaître a chaque instant la position du robot et donc de le faire agir en consequence.
	 * @param x nouvelle coordonnée en x
	 * @param y nouvelle coordonnée en y
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
	 * Permet de connaître a chaque instant la direction qu'a le robot et de le faire se redresser en consequence, lorsqu'il veut aller déposer un palet par exemple.
	 * @param angle nouvelle direction
	 */
	public void setDirection(float angle) {
		this.direction = angle;
	}
	 /**
	  * Permet de calculer la direction qu'a le robot après avoir tourner d'un certain angle entre en paramètre.
	  * Est utilisée dans la méthode @see Robot#deplacer(float, float).
	  * @param angle
	  */
	private void tourner(float angle) {
		this.direction = (direction + angle)%360;
	}
	
	/**
	 * Permet de calculer la position du robot après un deplacement en ligne droite suivant sa direction.
	 * Est utilisée dans la méthode @see Robot#deplacer(float, float).
	 * @param distance
	 */
	private void avancer(float distance) {
		this.setPosition((float) (this.position.getX()+(Math.cos(direction)*distance)),(float) (this.position.getY()+(Math.sin(direction)*distance)));
	}
	
	/**
	 * Permet de déplacer le robot de manière "humaine" dans le sens ou on considère qu'on le tourne comme on veut puis qu'on l'amene a un point (x,y).
	 * Peut être utilise pour se calibrer après un mouvement complexe dont on aurait enregistre les informations durant le mouvement
	 * en une seule étape dans la carte plutôt que de calculer en continue ce qui ne serait pas forcement utile.
	 * @param x delta en x
	 * @param y delta en y
	 * @param directionRelative delta en angle
	 */
	public void deplacer(float x, float y, float directionRelative) {
		this.tourner(directionRelative);
		this.avancer(position.distance(new Point(x,y)));
	}
	
	/**
	 * Permet de déplacer le robot de manière simple, il avance d'une distance entrée en paramètre et avant cela tourne de la directionRelative entrée.
	 * Le mouvement est donc une simple ligne droite dans une direction relative au robot.
	 * @param distance distance de déplacement
	 * @param directionRelative angle de rotation avant d'avancer
	 */
	public void deplacer(float distance, float directionRelative) {
		this.tourner(directionRelative);
		this.avancer(distance);
	}
	
	/**
	 * Un simple toString, principalement utilise pour le debugging, mais peut servir si l'on a envie de faire une interface qui afficherai a chaque instant
	 * (ou a chaque mise a jour) la position et la direction du robot, ou pour toute autre raison.
	 * @return Une chaîne de caractères contenant la position et la direction du robot (relativement a notre repère).
	 */
	public String toString() {
		return "Position : " + position + ". Direction : "+direction; 
	}
	
}