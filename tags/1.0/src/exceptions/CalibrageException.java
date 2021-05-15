package exceptions;

/**
 * Permet lors d'une tentative de calibrage de la carte de renvoyer une erreur en cas d'un problème quelconque rencontré.
 */
public class CalibrageException extends Exception {

	private static final long serialVersionUID = -1L;
	/**Construit l'erreur avec un message
	 * @param s message*/
	public CalibrageException(String s) {
		super(s);
	}
	/**Construit l'erreur*/
	public CalibrageException() {
		this("");
	}
}
