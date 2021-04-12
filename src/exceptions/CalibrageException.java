package exceptions;

public class CalibrageException extends Exception {

	/**
	 * Permet lors d'une tentative de calibrage de la carte de renvoyer une erreur en cas d'un probl�me quelconque rencontr�.
	 */
	private static final long serialVersionUID = -1L;

	public CalibrageException(String s) {
		super(s);
	}
	public CalibrageException() {
		this("");
	}
}
