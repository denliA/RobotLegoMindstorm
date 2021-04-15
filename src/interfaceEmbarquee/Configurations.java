package interfaceEmbarquee;

import java.util.Arrays;
import java.util.Vector;

public enum Configurations{
	strategieSolo("ramasserPalets",new String[] {"ramasserPalets"}),
	strategieDuo("ramasserPaletsDuo",new String[] {"ramasserPaletsDuo"}),
	
	musique("megalovania",new String[] {"megalovania","victory","losing"}),
	bruitage("wow",new String[] {"wow","easy","ohNo","nani","missionFailed","whilhelmScream","goatScream","nope","ennemySpotted","targetAcquired","targetLocked"}),
	expression("sourire",new String[] {"sourire","larmes"});
	
	private String val; //valeur selectionnée
	public Vector <String> s;
	
	private Configurations(String val,String[] s) {
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


