package exceptions;

/**
 * Permet de prévenir d'une erreur lors de l'execution d'une commande d'ouverture ou de fermeture des pinces. SERIAL IUD = 1L
 */
public class OuvertureException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public OuvertureException(String s) {
		super(s);
	}
	public OuvertureException() {
		this("");
	}
}
