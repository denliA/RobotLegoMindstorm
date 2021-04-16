package tests;

public class IN implements interfaceEmbarquee.Lancable{
	
	public void lancer() {
		interfaceEmbarquee.InterfaceTextuelle.lancer();
	}
	
	public String getTitre() {
		return "IN";
	}
	
}
