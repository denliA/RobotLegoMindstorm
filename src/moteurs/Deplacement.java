package moteurs;

public abstract class Deplacement extends Thread {
	
	public enum TypeDeplacement {EXCLUSIF, DEMON, AIDE}
	public enum StatusDeplacement { PRET, ENCOURS, ENATTENTE, FINI }
	
	protected ConditionArret condition;
	protected  StatusDeplacement status;
	protected TypeDeplacement type;
	Thread verificateur;
	
	public Deplacement(ConditionArret condition, TypeDeplacement type) {
		
	}
	
	public void lancer() {
		
	};
	
	public StatusDeplacement getStatus() {
		return status;
	}
	
	public String [] causesArret() {
		return condition.getCausesArret();
	}
	
	
	
}