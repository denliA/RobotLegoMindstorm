package tests;
import interfaceEmbarquee.*;

public class IN implements interfaceEmbarquee.Lancable{
	
	public void lancer() {
		InterfaceTextuelle.main(null);
	}
	
	public String getTitre() {
		return "IN";
	}
	
}
