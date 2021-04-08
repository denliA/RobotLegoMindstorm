package interfaceEmbarquee;

import java.util.Arrays;
import java.util.Vector;

public enum Configurations implements Selection{
	strategieSolo("ramasserPalets",new String[] {"ramasserPalets","test"}),
	strategieDuo("ramasserPaletsDuo",new String[] {"ramasserPaletsDuo"});
	
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

