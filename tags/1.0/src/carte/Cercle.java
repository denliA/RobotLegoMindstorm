package carte;

/**
 * Classe permettant de simuler un cercle dans le plan.
 * Elle engendre la classe @see Palet et permet egalement de simuler la zone d'action du capteur de couleur dans la classe @see Robot.
 */
public class Cercle {
	
	
	private Point centre;
	private float rayon;
	
	/**
	 * Le constructeur du cercle, il suffit d'avoir le centre et le rayon pour connaître toutes les informations du cercle.
	 * @param centre centre du cerle 
	 * @param rayon rayon du cercle
	 */
	public Cercle(Point centre, float rayon) {
		this.centre = centre;
		this.rayon = rayon;
	}
	
	/**
	 * Les setter de la position.
	 * Les cercles engendrant les palets il faut pouvoir les déplacer. Pour cela il suffit de déplacer leur centre.
	 * Ici on peut soit passer un point p, soit simplement des coordonnées (x,y) du plan, qui feront office de point.
	 * @param p
	 * ou bien
	 * @param x
	 * @param y
	 */
	protected void setCentre(Point p) {
		this.centre = p;
	}
	
	protected void setCentre(float x, float y) {
		this.centre.setX(x);
		this.centre.setY(y);
	}
	
	/**
	 * getter
	 * @return Le centre du cercle
	 */
	public Point getCentre() {
		return(this.centre);
	}
	/**
	 * getter
	 * @return Le rayon du cercle
	 */
	public float getRayon() {
		return(rayon);
	}
	
	/**
	 * Permet de savoir si un point, A, est contenu dans le cercle appelant cette méthode.
	 * @param A point pour lequel on vérifie
	 * @return Le fait que le point A soit ou non contenu dans le cercle appelant cette méthode.
	 */
	public boolean contient(Point A) {
		boolean in = false;
		if(centre.distance(A)<=this.rayon) {
			in = true;
		}
		return(in);
	}
	
	/**
	 * Indique si le cercle appelant cette méthode et le cercle passe en paramètre s'intersectent.
	 * @param c deuxième cercle
	 * @return S'il y a ou non intersection entre le cercle appelant cette méthode et le cercle c.
	 */
	public boolean intersect(Cercle c) {
		boolean inter = false;
		if(this.centre.distance(c.getCentre())<=(this.rayon+c.getRayon())) {
			inter = true;
		}
		return(inter);
	}
	
}