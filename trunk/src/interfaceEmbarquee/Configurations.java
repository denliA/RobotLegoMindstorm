package interfaceEmbarquee;

import java.util.Arrays;
import java.util.Vector;

/**
 * <p>Classe qui contient toutes les configurations possibles pour un Picker.</p>
 * 
 * @see Picker
 * 
 */

public enum Configurations{
	/**
     * enumeration des configurations choisies par l'utilisateur. Elles seront lancées depuis l'interface textuelle
     * 
     * @see InterfaceTextuelle
     */
	strategieSolo("ramasserPalets",new String[] {"ramasserPalets"}),
	strategieDuo("ramasserPaletsDuo",new String[] {"ramasserPaletsDuo"}),
	
	musique("megalovania",new String[] {"megalovania","victory","losing"}),
	bruitage("wow",new String[] {"wow","easy","ohNo","nani","missionFailed","whilhelmScream","goatScream","nope","ennemySpotted","targetAcquired","targetLocked"}),
	danse("victoire",new String[] {"victoire","defaite"}),
	
	arriveeX("0", new String[] {"-2", "-1", "0", "1", "2", "3"}),
	arriveeY("0", new String[] {"-2", "-1", "0", "1", "2", "3"}),
	departX("0", new String[] {"-2", "-1", "0", "1", "2", "3"}),
	departY("0", new String[] {"-2", "-1", "0", "1", "2", "3"}), 
	departD("porte", new String[] {"porte", "fenetre"}),
	campAdverse("porte", new String[] {"porte", "fenetre"});
	
	
	 /**
     * valeur choisie par défaut. Peut etre modifiée par l'utilisateur dans le Picker. 
     * @see Picker
     */
	private String val; //valeur selectionnée
	
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


