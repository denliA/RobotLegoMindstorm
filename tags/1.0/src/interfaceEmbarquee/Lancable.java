package interfaceEmbarquee;

/**
 * <p>Interface pour les Menus et les Pickers qui doivent implementer les fonctions ci dessous.</p>
 * <p>Permet de regrouper les objets de l'InterfaceTextuelle selon leur comportement de Lancable.</p>
 * 
 * @see Picker
 * @see Menu
 * @see InterfaceTextuelle
 * 
 */

public interface Lancable {
	
	/**
     * Lance le lancable suivant.
     */
	public void lancer();
	
	/**
     * Le titre du Lancable sera affiché sur l'écran du robot dans le Menu ou le Picker.
     * @return
     * 			Le titre du Lancable
     */
	public String getTitre();
}
