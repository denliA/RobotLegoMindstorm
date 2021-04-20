package carte;

public class Palet extends Cercle {
	/**
	 * Classe heritant de la classe @see Cercle. L'ajout a cette derniere est l'etat du palet : marque ou non.
	 */

	private boolean marque = false;
	private final static float rayonPalet = 2.9f;
	/**
	 * Constructeur du palet qui fait en fait juste appel au constructeur @see Cercle#Cercle(Point, float).
	 * @param centre
	 * @param rayon
	 */
	public Palet(Point centre) {
		super(centre, rayonPalet);
	}
	
	/**
	 * Le getter de l'etat du palet (marque ou non).
	 * @return L'etat du palet
	 */
	public boolean getMarque() {
		return(marque);
	}
	
	/**
	 * Methode indiquant le deplacement. Appelle simplement la methode @see Cercle#setCentre(Point).
	 * @param x
	 * @param y
	 */
	public void deplacer(float x, float y) {
		this.setCentre(this.getCentre().getX()+x, this.getCentre().getY()+y);
	}
	
	/**
	 * Modifie, lorsqu'un palet est marque, son etat.
	 */
	public void but() {
		marque = true;
	}
	
}