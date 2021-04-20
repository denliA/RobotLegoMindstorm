package carte;

public class Point {
	/**
	 * Une classe simple, servant a manipuler des points d'un plan.
	 * Est utilisee pour implementer les classes @see Ligne, @see Rectangle, @see Cercle et toutes les classes qu'elles engendrent.
	 */
	
	public static final Point INCONNU = new Point(Float.NaN, Float.NaN);
	
	private float x;
	private float y;
	
	/**
	 * Le constructeur, un point sur un plan a des coordonnees (x,y).
	 * @param x
	 * @param y
	 */
	public Point(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Les getters.
	 * @return La coordonnee que l'on souhaite connaetre.
	 */
	public float getX() {
		return(this.x);
	}
	
	public float getY() {
		return(this.y);
	}
	
	/**
	 * Les setters, ils permettent de modifier la coordonnee qui leur est associee.
	 * @param x
	 * ou bien (pour le setY()
	 * @param y
	 */
	void setX(float x) {
		this.x = x;
	}
	
	void setY(float y) {
		this.y = y;
	}
	
	/**
	 * Calcule la distance entre le point appelant cette methode et le point A entre en parametre.
	 * Utilise la formule classique consistant a faire la racine carree de la somme des carres des difference entre les coordonnees des points :
	 * sqrt((x-x0)^2+(y-y0)^2).
	 * @param A
	 * @return La distance entre 2 points.
	 */
	public float distance(Point A) {
		return((float) Math.sqrt(Math.pow(this.x-A.getX(), 2)+Math.pow(this.y-A.getY(),2)));
	}
	
	
	public String toString() {
		return "("+x+", "+y+")";
	}
	
}