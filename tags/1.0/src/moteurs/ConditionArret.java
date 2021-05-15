package moteurs;


/**
 * Modélise une condition d'arrêt qui doit être constamment évaluée.
 *
 */
public abstract class ConditionArret {

	
	protected String [] causes_arret;
	
	/**
	 * La fonction qui évalue si la condition est remplie ou pas
	 * @return true si la condition d'arrêt est remplie.
	 */
	public abstract boolean eval();
	
	/**
	 * Après l'arrêt de la condition, permet d'accèder à la cause exacte qui a mené à l'arrêt.
	 * @return Un tableau de chaînes avec chacune représentant une cause d'arrêt.
	 */
	public String [] getCausesArret() {
		return causes_arret;
	}
	
}
