package interfaceEmbarquee;

import java.util.Arrays;
import java.util.Vector;

public enum Visages implements Selection {
	content("sourire",new String[] {"sourire"}),
	triste("larmes",new String[] {"larmes"});
	
	private String val; //valeur selectionnée
	public Vector <String> s;
	
	private Visages(String val,String[] s) {
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
