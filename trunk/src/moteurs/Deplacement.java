package moteurs;

import java.util.Stack;

import lejos.utility.Timer;
import lejos.utility.TimerListener;

/*
 * Note pour le mainteneur :
 * Cette classe ainsi que la classe ConditionArret n'a pas pu être utilisée dans la première version du produit final rendu (à la date du 28 Avril) faute de temps 
 * et par nécessité de corriger des problèmes plus techniques avec les moteurs. Tous les déplacements ont été finalement codés avec des fonctions statiques de 
 * la classe Pilote et la gestion de la concurrence et des conditions d'arrêt faite manuellement.
 * Mais elles gardent un grand potentiel et pour un potentiel mainteneur de ce code, l'amélioration la plus importante et la plus prioritaire serait sûrement celle là: 
 * intégrer les Déplacements dans la classe Pilote. Cette intégration ne change pas les capacités fonctionnelles du programme au début, mais elles représentent une interface
 * plus naturelle et plus puissante qui présente de plus en plus d'avantages à mesure que les déplamements deviennent compliqués.
 */
/**
 * Représente l'abstraction d'un déplacement, qui peut être lancé, arrêté et qui dont l'arrêt peut être conditionné par une condition d'arrêt
 */
public abstract class Deplacement extends Thread {
	
	/**
	 * Enumeration listant les types de déplacements : <ul>
	 *  	<li> Un déplacement <code>EXCLUSIF</code> représente le mouvement principal du robot, qui ne peut s'éxécuter en concurrence avec un autre déplacement exclusif. 
	 *  	<li> Un déplacement <code>DEMON</code> est un déplacement qui peut s'exécuter en permanence en concurrence avec d'autres déplacements. Par exemple, la gestion du vide
	 *  	     peut être modélisée par un déplacement de ce type.
	 *  	<li> 
	 *
	 */
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