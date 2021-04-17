package carte;

public class Point {
	/**
	 * Une classe simple, servant � manipuler des points d'un plan.
	 * Est utilis�e pour impl�menter les classes @see Rectangle, @see Cercle et toutes les classes qu'elles engendrent.
	 */
	
	public static final Point INCONNU = new Point(Float.NaN, Float.NaN);
	
	private float x;
	private float y;
	
	/**
	 * Le constructeur, un point sur un plan a des coordonn�es (x,y).
	 * @param x
	 * @param y
	 */
	public Point(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Les getters.
	 * @return La coordonn�e que l'on souhaite conna�tre.
	 */
	public float getX() {
		return(this.x);
	}
	
	public float getY() {
		return(this.y);
	}
	
	/**
	 * Les setters, ils permettent de modifier la coordonn�e qui leur est associ�e.
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
	 * Calcule la distance entre le point appelant cette m�thode et le point A entr� en param�tre.
	 * Utilise la formule classique consistant � faire la racine carr� de la somme des carr�es des diff�rence entre les coordonn�es des points :
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