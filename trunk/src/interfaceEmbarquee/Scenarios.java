package interfaceEmbarquee;

import java.util.Arrays;
import java.util.Vector;

public enum Scenarios implements Selection{

	basiques("NFBM1 - Reconnaitre couleur",new String[] {"NFBM1 - Reconnaitre couleur","NFBA1 - Avancer tout droit","NFBA2 - Faire angle droit",
			 "NFBM2 - Reconnaître intersections","NFBM3 - Détecter palet","NFBA3 - Ramener palet","NFBM4 - Detecter vide",
			"NFBM5 - Arret apres 5 min","NFBM6 - Capteur ultrason"}),
	avances("NFA0 - Ligne blanche adverse",new String[] {"NFA0 - Ligne blanche adverse","NFA1 - Rectangle","NFA2 - Creneau",
			"NFA3 - Intersection ligne","NFA4 - Intersection partout","NFA5 - Ramener palet position connue","NFA6 - Ramener palet ultrason",
			"NFA7 - Chemin predefini 9 palets"}),
	optionnels("OFBA1 - Angles et distances",new String[] {"OFBA1 - Angles et distances","OFA1 - Carte virtuelle","OFA2 - Diagonales",
			"IF1 - Strategies victoire","IF2 - Sabotage"});
	
	private String val; //valeur selectionnée
	public Vector <String> s;
	
	private Scenarios(String val,String[] s) {
		this.val = val;
		this.s = new Vector<>(Arrays.asList(s));
	}
	
	public void setVal(String val) {
		if (s.contains(val))
			this.val = val;
		else
			System.err.println(val+ " n'est pas dans le tableau");
	}
	
	public String getVal() {
		return val;
	}
}