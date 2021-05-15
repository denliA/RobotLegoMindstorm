package tests;
import interfaceEmbarquee.InterfaceTextuelle;

/**
 * <p>Lance le menu scenarios de l'interface textuelle du robot</p>
 * <p>Permet de lancer tous les tests du cahier de recette.</p>
 * @see InterfaceTextuelle
 */

public class TestDesTest {

	public static void main(String[] args) {
		new interfaceEmbarquee.InterfaceTextuelle().scenarios.lancer();
	}
}
