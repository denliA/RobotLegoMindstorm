package carte;

public class Rectangle {
	/**
	 * Classe qui permet d'engendrer les classes Robot et Ligne. 
	 * Les intersections elles aussi sont des rectangles.
	 * Les rectangles, du moins, � leur cr�ation suivent les axes du plan.
	 */
	
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
	 * Le setter de la position du rectangle. Utilis�e pour en d�placer un (@see Robot par exemple).
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
	 * Utilis�e pour v�rifier si 2 rectangles s'intersectent ou non.
	 * Cette m�thode est utile pour ensuite calculer dans le cas o� il y en a une, l'intersection.
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
	 * Appelle la m�thode @see Rectangle#intersect(Rectangle) puis si il y a en effet intersection, 
	 * trouve le coin inf�rieur gauche de l'intersection entre le rectangle appelant la m�thode et le rectangle, A, pass� en param�tre.
	 * @param A
	 * @return Le coin inf�rieur de l'intersection form�e par 2 rectangles.
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
	 * Inf�rieur gauche devient sup�rieur droit dans la description de la m�thode @see Rectangle#INFG(Rectangle) au dessus.
	 * @param A
	 * @return Le coin sup�rieur droit de l'intersection form�e par 2 rectangles.
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
	 * Cr�e un rectangle � partir des coins re�u suite � l'appel des m�thodes @see Rectangle#INFG(Rectangle) et @see Rectangle#SUPD(Rectangle) au dessus.
	 * @param A
	 * @return Le rectangle cr�e gr�ce aux coins trouv�s par les methodes INFG et SUPD.
	 */
	public Rectangle intersection(Rectangle A) {
		Rectangle inter = null;
		if(this.intersect(A)) {
			inter = new Rectangle(this.INFG(A), this.SUPD(A));
		}
		return(inter);
	}
	
}