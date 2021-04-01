package moteurs;
import lejos.utility.Timer;
import lejos.utility.TimerListener;

public abstract class ConditionArret {

	
	private String [] causes_arret;
	
	public abstract boolean eval();
	
	public String [] getCausesArret() {
		return causes_arret;
	}
	
}
