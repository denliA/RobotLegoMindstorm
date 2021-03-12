package moteurs;

public interface Deplacement {
	public boolean status = false;
	
	public void lancer(); //permet de lancer/démarrer le déplacement
	
	public boolean getStatus(); //permet de retourner si le déplacement est "activé" ou non
	
	public void interrompre(); //permet de mettre pause au milieu du déplacement, il pourra être repris, ou non, ensuite
	
	public void reprendre(); //permet de reprendre un mouvement interrompu
	
	public void arreter(); //permet d'arrêter le déplacement, il ne pourra pas être repris après
}