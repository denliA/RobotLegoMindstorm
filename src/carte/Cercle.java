package carte;

public class Cercle {
	
	private Point centre;
	private float rayon;
	
	public Cercle(Point centre, float rayon) {
		this.centre = centre;
		this.rayon = rayon;
	}
	
	protected void setCentre(Point p) {
		this.centre = p;
	}
	
	protected void setCentre(float x, float y) {
		this.centre.setX(x);
		this.centre.setY(y);
	}
	
	public Point getCentre() {
		return(this.centre);
	}
	
	public boolean contient(Point A) {
		boolean in = false;
		if(centre.distance(A)<=this.rayon) {
			in = true;
		}
		return(in);
	}
	
}
