package moteurs;

public interface Deplacement {
	public boolean status = false;
	
	public void lancer(); //permet de lancer/d�marrer le d�placement
	
	public boolean getStatus(); //permet de retourner si le d�placement est "activ�" ou non
	
	public void interrompre(); //permet de mettre pause au milieu du d�placement, il pourra �tre repris, ou non, ensuite
	
	public void reprendre(); //permet de reprendre un mouvement interrompu
	
	public void arreter(); //permet d'arr�ter le d�placement, il ne pourra pas �tre repris apr�s
}