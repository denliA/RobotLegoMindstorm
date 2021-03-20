package moteurs;

public interface Deplacement {
	
	public void lancer(); //permet de lancer/demarrer le deplacement
	
	public boolean getStatus(); //permet de retourner si le deplacement est "active" ou non
	
	public void interrompre(); //permet de mettre pause au milieu du deplacement, il pourra etre repris, ou non, ensuite
	
	
	public void arreter(); //permet d'arreter le deplacement, il ne pourra pas etre repris apres
}