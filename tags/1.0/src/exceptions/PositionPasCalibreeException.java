package exceptions;

/**
 * Exception indiquant que la position du robot n'est pas calibrée. Est throw par les méthodes n'opérant que si 
 * le robot est calibré.
 *
 */
public class PositionPasCalibreeException extends RuntimeException{

	private static final long serialVersionUID = 1500397695670937114L;

}
