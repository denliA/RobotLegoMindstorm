package tests;

public class NFBA2 implements interfaceEmbarquee.Lancable{
	
	public void lancer() {
		moteurs.MouvementsBasiques.chassis.rotate(90);
	}
	
	public String getTitre() {
		return "NFBA2 - Faire angle droit";
	}
	
}