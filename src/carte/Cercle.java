package carte;

public class Cercle {
	/**
	 * Classe permettant de simuler un cercle dans le plan.
	 * Elle engendre la classe @see Palet et permet egalement de simuler la zone d'action du capteur de couleur dans la classe @see Robot.
	 */
	
	
	private Point centre;
	private float rayon;
	
	/**
	 * Le constructeur du cercle, il suffit d'avoir le centre et le rayon pour connaitre toutes les informations du cercle.
	 * @param centre
	 * @param rayon
	 */
	public Cercle(Point centre, float rayon) {
		this.centre = centre;
		this.rayon = rayon;
	}
	
	/**
	 * Les setter de la position.
	 * Les cercles engendrant les palets il faut pouvoir les deplacer. Pour cela il suffit de deplacer leur centre.
	 * Ici on peut soit passer un point p, soit simplement des coordonnees (x,y) du plan, qui feront office de point.
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
	 * Les getters.
	 * @return Le centre ou le rayon en fonction de celui appele.
	 */
	public Point getCentre() {
		return(this.centre);
	}
	
	public float getRayon() {
		return(rayon);
	}
	
	/**
	 * Permet de savoir si un point, A, est contenu dans le cercle appelant cette methode.
	 * @param A
	 * @return Le fait que le point A soit ou non contenu dans le cercle appelant cette methode.
	 */
	public boolean contient(Point A) {
		boolean in = false;
		if(centre.distance(A)<=this.rayon) {
			in = true;
		}
		return(in);
	}
	
	/**
	 * Indique si le cercle appelant cette methode et le cercle passe en parametre s'intersectent.
	 * @param c
	 * @return S'il y a ou non intersection entre le cercle appelant cette methode et le cercle c.
	 */
	public boolean intersect(Cercle c) {
		boolean inter = false;
		if(this.centre.distance(c.getCentre())<=(this.rayon+c.getRayon())) {
			inter = true;
		}
		return(inter);
	}
	
}