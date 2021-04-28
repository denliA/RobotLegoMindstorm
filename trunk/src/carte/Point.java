package carte;

/**
 * Une classe simple, servant a manipuler des points d'un plan.
 * Est utilisée pour implementer les classes @see Ligne, @see Rectangle, @see Cercle et toutes les classes qu'elles engendrent.
 */
public class Point {
	
	/**Représente un point indéterminé. Utile pour les fonctions qui retournent un point mais qui échouent*/
	public static final Point INCONNU = new Point(Float.NaN, Float.NaN);
	
	private float x;
	private float y;
	
	/**
	 * Le constructeur, un point sur un plan a des coordonnées (x,y).
	 * @param x coordonnée en x du point 
	 * @param y coordonnée en y du point
	 */
	public Point(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * getter.
	 * @return La coordonnée en x
	 */
	public float getX() {
		return(this.x);
	}
	/**
	 * getter.
	 * @return La coordonnée en y
	 */
	public float getY() {
		return(this.y);
	}
	
	/**
	 * setter 
	 * @param x la nouvelle valeur de la coordonnée en x 
	 */
	void setX(float x) {
		this.x = x;
	}
	
	/**
	 * setter 
	 * @param y la nouvelle valeur de la coordonnée en y 
	 */
	void setY(float y) {
		this.y = y;
	}
	
	/**
	 * Calcule la distance entre le point appelant cette méthode et le point A entre en paramètre.
	 * Utilise la formule classique consistant a faire la racine carrée de la somme des carres des difference entre les coordonnées des points :
	 * sqrt((x-x0)^2+(y-y0)^2).
	 * @param A point duquel on calcule la distance
	 * @return La distance entre 2 points.
	 */
	public float distance(Point A) {
		return((float) Math.sqrt(Math.pow(this.x-A.getX(), 2)+Math.pow(this.y-A.getY(),2)));
	}
	
	
	public String toString() {
		return "("+x+", "+y+")";
	}
	
}