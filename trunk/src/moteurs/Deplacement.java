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
	 * Enumeration listant les types de déplacements :
	 */
	public enum TypeDeplacement {/**représente le mouvement principal du robot, qui ne peut s'éxécuter en concurrence avec un autre déplacement exclusif. */EXCLUSIF, 
		/**est un déplacement qui peut s'exécuter en permanence en concurrence avec d'autres déplacements. Par exemple, la gestion du vide peut être modélisée par un déplacement de ce type.*/DEMON, 
		/**est lancé par un autre déplacement */AIDE}
	
	/**
	 * Enumeration listant le l'état du déplacement.
	 */
	public enum StatusDeplacement { /**Avant le début du déplacement*/PRET, /**Pendant le déplacement*/ENCOURS, /**Après l'interruption, mais avant la fin */INTERROMPU, /**après la fin*/FINI }
	
	protected ConditionArret condition;
	protected  StatusDeplacement status;
	protected TypeDeplacement type;
	private boolean _sorti = false;
	
	Timer verificateur;
	Stack<Deplacement> pile_aide = new Stack<>();
	
	/**
	 * 
	 * @param condition condition d'arrêt du déplacement. Va être constamment évaluée pendant le dit déplacement
	 * @param type type du déplacement (si c'est un déplacement d'aide, exclusif etc...
	 */
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
	
	/**
	 * Fonction contenant le code à executer pour faire le déplacement.
	 */
	public void lancer() {
		this.status = StatusDeplacement.ENCOURS;
		run();
	};
	
	/**
	 * Retourne le status du déplacement (Prêt, en cours, interrompu etc...)
	 * @return status du déplacement
	 * @see StatusDeplacement
	 */
	public StatusDeplacement getStatus() {
		return status;
	}
	
	/**
	 * Appelée <b>après</b> l'arrêt d'un déplacement. Indique les cause d'arrêt du déplacement
	 * @return un tableau de chaînes de caractères, chacune représentant une cause d'arrêt
	 */
	public String [] causesArret() {
		return condition.getCausesArret();
	}
	
	/**
	 * Permet d'arrêter manuellement le déplacement
	 * @param attendreArret si à true, la fonction ne retourne pas la main à l'appelant avant la fin du déplacement.
	 */
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