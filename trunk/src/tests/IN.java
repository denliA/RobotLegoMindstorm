package tests;
import interfaceEmbarquee.*;

/**
 * <p>Lance l'interface textuelle du robot.</p>
 * <p>Depuis le menu scenario, on peut lancer tous les tests du cahier de recettes</p>
 * @see interfaceEmbarquee
 */

public class IN implements interfaceEmbarquee.Lancable{
	
	public void lancer() {
		InterfaceTextuelle.main(null);
	}
	
	public String getTitre() {
		return "IN";
	}
	
}
