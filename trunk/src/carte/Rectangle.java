package carte;

public class Rectangle {
	
	private Point bas_gauche;
	private Point haut_droite;
	
	public Rectangle(Point infGauche, Point supDroite) {
	this.bas_gauche = infGauche;
	this.haut_droite = supDroite;
	}
	
	public Point getBasGauche() {
		return(bas_gauche);
	}
	
	public Point getHautDroite() {
		return(haut_droite);
	}
	
}
