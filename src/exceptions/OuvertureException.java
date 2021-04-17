package exceptions;

public class OuvertureException extends RuntimeException {
	/**
	 * Permet de pr�venir d'une erreur lors de l'execution d'une commande d'ouverture ou de fermeture des pinces. SERIAL IUD = 1L
	 */
	private static final long serialVersionUID = 1L;
	public OuvertureException(String s) {
		super(s);
	}
	public OuvertureException() {
		this("");
	}
}
