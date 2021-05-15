package interfaceEmbarquee;

import java.util.Arrays;
import java.util.Vector;

/**
 * <p> Enumeration  qui contient toutes les configurations possibles pour un Picker.</p>
 * 
 * @see Picker
 * 
 */

public enum Configurations{
	/** Permet de choisir quelle stratégie utiliser pour le mode solo */
	strategieSolo("ramasserPalets",new String[] {"ramasserPalets"}),
	/** Permet de choisir quelle stratégie utiliser pour le mode compétition*/
	strategieDuo("ramasserPaletsDuo",new String[] {"ramasserPaletsDuo"}),
	
	/** Permet de choisir la musique */
	musique("megalovania",new String[] {"megalovania","victory","losing"}),
	/** Permet de choisir les bruitages */
	bruitage("wow",new String[] {"wow","easy","ohNo","nani","missionFailed","whilhelmScream","goatScream","nope","ennemySpotted","targetAcquired","targetLocked"}),
	/** Permet de choisir la danse */
	danse("victoire",new String[] {"victoire","defaite"}),
	
	/** Permet de choisir la coordonnée x du point d'arrivée*/
	arriveeX("0", new String[] {"-2", "-1", "0", "1", "2"}),
	/** Permet de choisir la coordonnée y du point d'arrivée */
	arriveeY("0", new String[] {"-2", "-1", "0", "1", "2"}),
	/** Permet de choisir la coordonnée x du point de départ */
	departX("0", new String[] {"-2", "-1", "0", "1", "2"}),
	/** Permet de choisir la coordonnée y du point de départ */
	departY("0", new String[] {"-2", "-1", "0", "1", "2"}), 
	/** Permet de choisir la direction au départ */
	departD("porte", new String[] {"porte", "fenetre"}),
	/** Permet de choisir le camp adverse */
	campAdverse("porte", new String[] {"porte", "fenetre"});
	
	
	 /**
     * valeur choisie par défaut. Peut être modifiée par l'utilisateur dans le Picker. 
     * @see Picker
     */
	private String val; //valeur sélectionnée
	
	/**
     * vecteur qui contient toutes les valeurs possibles de l'enumeration.
     */
	public Vector <String> s;
	
	/**
	 * Constructeur d'une valeur de l'enumeration
	 * 
	 * @param val
	 * 				valeur choisie par l'utilisateur dans le Picker.
	 * @param s
	 * 				vecteur qui contient toutes les valeurs possibles de l'enumeration.
	 */
	
	private Configurations(String val,String[] s) {
		this.val = val;
		this.s = new Vector<>(Arrays.asList(s));
	}
	
	
	/**
     * Modifie la valeur de la Configuration qui sera lancée
     * 
     * @param val
     * 				La valeur choisie dans les Configurations.
     * 
     */
	
	public void setVal(String val) {
		if (s.contains(val))
			this.val = val;
		else
			System.err.println(val+ " n'est pas dans le tableau");
	}
	
	/**
     * Affiche la valeur de la Configuration qui sera lancée
     * @return
     * 				La valeur de la Configuration choisie
     * 
     */
	
	public String getVal() {
		return val;
	}
}


