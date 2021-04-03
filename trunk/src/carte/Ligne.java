package carte;

import capteurs.CouleurLigne;

public class Ligne extends Rectangle {
	private CouleurLigne couleur;
	public Ligne(Point infGauche, Point supDroite, CouleurLigne couleur) {
		super(infGauche, supDroite);
		this.couleur = couleur;
	}
	public CouleurLigne getCouleur() {
		return(couleur);
	}
}
