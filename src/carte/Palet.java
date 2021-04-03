package carte;

public class Palet extends Cercle {

	private boolean marque;
	
	public Palet(Point centre, float rayon) {
		super(centre, rayon);
	}
	
	public boolean getMarque() {
		return(marque);
	}
	
	public void deplacer(float x, float y) {
		this.setCentre(this.getCentre().getX()+x, this.getCentre().getY()+y);
	}
	
	public void but() {
		marque = true;
	}
	
}
