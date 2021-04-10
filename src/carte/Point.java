package carte;

public class Point {
	/**
	 * Une classe simple, servant à manipuler des points d'un plan.
	 * Est utilisée pour implémenter les classes @see Rectangle, @see Cercle et toutes les classes qu'elles engendrent.
	 */
	
	private float x;
	private float y;
	
	/**
	 * Le constructeur, un point sur un plan a des coordonnées (x,y).
	 * @param x
	 * @param y
	 */
	public Point(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Les getters.
	 * @return La coordonnée que l'on souhaite connaître.
	 */
	public float getX() {
		return(this.x);
	}
	
	public float getY() {
		return(this.y);
	}
	
	/**
	 * Les setters, ils permettent de modifier la coordonnée qui leur est associée.
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
	 * Calcule la distance entre le point appelant cette méthode et le point A entré en paramètre.
	 * Utilise la formule classique consistant à faire la racine carré de la somme des carrées des différence entre les coordonnées des points :
	 * sqrt((x-x0)^2+(y-y0)^2).
	 * @param A
	 * @return La distance entre 2 points.
	 */
	public float distance(Point A) {
		return((float) Math.sqrt(Math.pow(this.x-A.getX(), 2)+Math.pow(this.y-A.getY(),2)));
	}
	
}