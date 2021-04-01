package moteurs;

import lejos.utility.Timer;
import lejos.utility.TimerListener;

public abstract class Deplacement extends Thread {
	
	public enum TypeDeplacement {EXCLUSIF, DEMON, AIDE}
	public enum StatusDeplacement { PRET, ENCOURS, ENATTENTE, INTERROMPU, FINI }
	
	protected ConditionArret condition;
	protected  StatusDeplacement status;
	protected TypeDeplacement type;
	Timer verificateur;
	
	public Deplacement(ConditionArret condition, TypeDeplacement type) {
		this.condition = condition;
		this.type =  type;
		this.status = StatusDeplacement.PRET;
		verificateur = new Timer(10, new TimerListener() {
			public void timedOut() {
				if(Deplacement.this.condition.eval()) {
					status = StatusDeplacement.FINI;
					interrupt();
				}
			}
		});
	}
	
	public void lancer() {
		this.status = StatusDeplacement.ENCOURS;
		run();
	};
	
	public StatusDeplacement getStatus() {
		return status;
	}
	
	public String [] causesArret() {
		return condition.getCausesArret();
	}
	
	public void Arreter() {
		this.status = StatusDeplacement.INTERROMPU;
		super.interrupt();
	}
	
	
}