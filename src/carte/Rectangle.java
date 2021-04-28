package carte;

/**
 * Classe qui permet d'engendrer les classes Robot et Ligne. 
 * Les intersections elles aussi sont des rectangles.
 * Les rectangles, du moins, a leur creation suivent les axes du plan.
 */
public class Rectangle {
	
	private Point bas_gauche;
	private Point haut_droite;
	
	/**
	 * Constructeur d'un rectangle quelconque, il suffit de 2 coins pour construire un rectangle suivant les axes du plan.
	 * @param infGauche
	 * @param supDroite
	 */
	public Rectangle(Point infGauche, Point supDroite) {
	this.bas_gauche = infGauche;
	this.haut_droite = supDroite;
	}
	
	/**
	 * Les getters.
	 * @return le coin, du rectangle, que l'on souhaite manipuler.
	 */
	public Point getBasGauche() {
		return(bas_gauche);
	}
	
	public Point getHautDroite() {
		return(haut_droite);
	}
	
	/**
	 * Le setter de la position du rectangle. Utilisée pour en déplacer un (@see Robot par exemple).
	 * @param x
	 * @param y
	 */
	void setPosition(float x, float y) {
		this.bas_gauche.setX(x-((this.haut_droite.getX()-this.bas_gauche.getX())/2));
		this.bas_gauche.setY(y-((this.haut_droite.getY()-this.bas_gauche.getY())/2));
		this.haut_droite.setX(x+((this.haut_droite.getX()-this.bas_gauche.getX())/2));
		this.haut_droite.setY(y+((this.haut_droite.getY()-this.bas_gauche.getY())/2));
	}
	
	/**
	 * Utilisée pour verifier si 2 rectangles s'intersectent ou non.
	 * Cette méthode est utile pour ensuite calculer dans le cas ou il y en a une, l'intersection.
	 * @param A
	 * @return Si oui ou non 2 rectangles s'intersectent.
	 * 
	 */
	boolean intersect(Rectangle A) {
		boolean inter = false;
		if(A.bas_gauche.getX()<=this.haut_droite.getX() && this.bas_gauche.getX()<=A.haut_droite.getX()) {
			if(A.bas_gauche.getY()<=this.haut_droite.getY() && this.bas_gauche.getY()<=A.haut_droite.getY()) {inter = true;}
		}
		return(inter);
	}
	
	/**
	 * Appelle la méthode @see Rectangle#intersect(Rectangle) puis si il y a en effet intersection, 
	 * trouve le coin inférieur gauche de l'intersection entre le rectangle appelant la méthode et le rectangle, A, passe en paramètre.
	 * @param A
	 * @return Le coin inférieur de l'intersection formée par 2 rectangles.
	 */
	private Point INFG(Rectangle A) {
		Point infg = null;
		if(this.intersect(A)) {
			if(this.bas_gauche.getX()<=A.bas_gauche.getX() && this.bas_gauche.getY()<=A.bas_gauche.getY()) {
				infg = A.bas_gauche;
			}
			else if(A.bas_gauche.getX()<=this.bas_gauche.getX() && A.bas_gauche.getY()<=this.bas_gauche.getY()) {
				infg = this.bas_gauche;
			}
			else if(A.bas_gauche.getX()<=this.bas_gauche.getX()){
				infg = new Point(this.bas_gauche.getX(),A.bas_gauche.getY());
			}
			else {
				infg = new Point(A.bas_gauche.getX(),this.bas_gauche.getY());
			}
		}
		return(infg);
	}
	
	/**
	 * Inférieur gauche devient supérieur droit dans la description de la méthode @see Rectangle#INFG(Rectangle) au dessus.
	 * @param A
	 * @return Le coin supérieur droit de l'intersection formée par 2 rectangles.
	 */
	private Point SUPD(Rectangle A) {
		Point supd = null;
		if(this.intersect(A)) {
			if(this.haut_droite.getX()<=A.haut_droite.getX() && this.haut_droite.getY()<=A.haut_droite.getY()) {
				supd = this.haut_droite;
			}
			else if(A.haut_droite.getX()<=this.haut_droite.getX() && A.haut_droite.getY()<=this.haut_droite.getY()) {
				supd = A.haut_droite;
			}
			else if(A.haut_droite.getX()<=this.haut_droite.getX()) {
				supd = new Point(A.haut_droite.getX(),this.haut_droite.getY());
			}
			else {
				supd = new Point(this.haut_droite.getX(),A.haut_droite.getY());
			}
		}
		return(supd);
	}
	
	/**
	 * Crée un rectangle a partir des coins reçus suite a l'appel des méthodes @see Rectangle#INFG(Rectangle) et @see Rectangle#SUPD(Rectangle) au dessus.
	 * @param A
	 * @return Le rectangle créé grace aux coins trouves par les méthodes INFG et SUPD.
	 */
	public Rectangle intersection(Rectangle A) {
		Rectangle inter = null;
		if(this.intersect(A)) {
			inter = new Rectangle(this.INFG(A), this.SUPD(A));
		}
		return(inter);
	}
	
}