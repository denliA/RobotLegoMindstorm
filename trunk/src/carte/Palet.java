package carte;

public class Palet extends Cercle {
	/**
	 * Classe h�ritant de la classe @see Cercle. L'ajout � cette derni�re est l'�tat du palet : marqu� ou non.
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
	 * Le getter de l'�tat du palet (marqu� ou non).
	 * @return L'�tat du palet
	 */
	public boolean getMarque() {
		return(marque);
	}
	
	/**
	 * M�thode indiquant le d�placement. Appelle simplement la m�thode @see Cercle#setCentre(Point).
	 * @param x
	 * @param y
	 */
	public void deplacer(float x, float y) {
		this.setCentre(this.getCentre().getX()+x, this.getCentre().getY()+y);
	}
	
	/**
	 * Modifie, lorsqu'un palet est marqu�, son �tat.
	 */
	public void but() {
		marque = true;
	}
	
}