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
	
	private boolean intersect(Rectangle A) {
		boolean inter = false;
		if(A.bas_gauche.getX()<=this.haut_droite.getX() && this.bas_gauche.getX()<=A.haut_droite.getX()) {
			if(A.bas_gauche.getY()<=this.haut_droite.getY() && this.bas_gauche.getY()<=A.haut_droite.getY()) {inter = true;}
		}
		return(inter);
	}
	
	private Point INFG(Rectangle A) {
		Point infg = null;
		if(this.intersect(A)) {
			if(this.bas_gauche.getX()<=A.bas_gauche.getX() && this.bas_gauche.getY()<=A.bas_gauche.getY()) {
				infg = A.bas_gauche;
			}
			else if(A.bas_gauche.getX()<=this.bas_gauche.getX() && A.bas_gauche.getY()<=this.bas_gauche.getY()) {
				infg = this.bas_gauche;
			}
			else if(A.bas_gauche.getX()<=this.bas_gauche.getX()){
				infg = new Point(this.bas_gauche.getX(),A.bas_gauche.getY());
			}
			else {
				infg = new Point(A.bas_gauche.getX(),this.bas_gauche.getY());
			}
		}
		return(infg);
	}
	
	private Point SUPD(Rectangle A) {
		Point supd = null;
		if(this.intersect(A)) {
			if(this.haut_droite.getX()<=A.haut_droite.getX() && this.haut_droite.getY()<=A.haut_droite.getY()) {
				supd = this.haut_droite;
			}
			else if(A.haut_droite.getX()<=this.haut_droite.getX() && A.haut_droite.getY()<=this.haut_droite.getY()) {
				supd = A.haut_droite;
			}
			else if(A.haut_droite.getX()<=this.haut_droite.getX()) {
				supd = new Point(A.haut_droite.getX(),this.haut_droite.getY());
			}
			else {
				supd = new Point(this.haut_droite.getX(),A.haut_droite.getY());
			}
		}
		return(supd);
	}
	
	public Rectangle intersection(Rectangle A, Rectangle B) {
		Rectangle inter = null;
		if(this.intersect(A)) {
			inter = new Rectangle(this.INFG(A), this.SUPD(A));
		}
		return(inter);
	}
	
	
}