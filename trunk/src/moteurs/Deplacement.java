package moteurs;

import java.util.Stack;

import lejos.utility.Timer;
import lejos.utility.TimerListener;

public abstract class Deplacement extends Thread {
	
	public enum TypeDeplacement {EXCLUSIF, DEMON, AIDE}
	public enum StatusDeplacement { PRET, ENCOURS, INTERROMPU, FINI }
	
	protected ConditionArret condition;
	protected  StatusDeplacement status;
	protected TypeDeplacement type;
	private boolean _sorti = false;
	
	Timer verificateur;
	Stack<Deplacement> pile_aide = new Stack<>();
	
	public Deplacement(ConditionArret condition, TypeDeplacement type) {
		this.condition = condition;
		this.type =  type;
		this.status = StatusDeplacement.PRET;
		verificateur = new Timer(10, new TimerListener() {
			public void timedOut() {
				if(Deplacement.this.condition.eval()) {
					interrupt();
					while(!_sorti)
						Thread.yield();
					status = StatusDeplacement.FINI;
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
	
	public void arreter(boolean attendreArret) {
		while(!pile_aide.isEmpty()) {
			Deplacement d = pile_aide.pop();
			d.arreter(attendreArret);
		}
		interrupt();
		while(!_sorti && attendreArret)
			Thread.yield();
		this.status = StatusDeplacement.INTERROMPU;
	}
	
	
}