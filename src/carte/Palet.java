package carte;

public class Palet extends Cercle {
	/**
	 * Classe héritant de la classe @see Cercle. L'ajout à cette dernière est l'état du palet : marqué ou non.
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
	 * Le getter de l'état du palet (marqué ou non).
	 * @return L'état du palet
	 */
	public boolean getMarque() {
		return(marque);
	}
	
	/**
	 * Méthode indiquant le déplacement. Appelle simplement la méthode @see Cercle#setCentre(Point).
	 * @param x
	 * @param y
	 */
	public void deplacer(float x, float y) {
		this.setCentre(this.getCentre().getX()+x, this.getCentre().getY()+y);
	}
	
	/**
	 * Modifie, lorsqu'un palet est marqué, son état.
	 */
	public void but() {
		marque = true;
	}
	
}