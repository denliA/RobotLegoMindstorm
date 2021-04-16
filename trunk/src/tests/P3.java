package tests;

import interfaceEmbarquee.Lancable;
import capteurs.*;
import carte.Ligne;
import exceptions.*;
import moteurs.*;

/**
 * <p>Le robot est pose sur la table mais pas sur une ligne, doit ramener le palet pose a une intersection dans le camp adverse</p>
 * 
 */
public class P3 implements Lancable {
	
	/**
	 * campAdverse vaut 1 pour Est et 0 pour Ouest. On supose la ligne verte du cote ouest
	 */
	private int campAdverse;

	@Override
	public String getTitre() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return campAdverse
	 */
	public int getCampAdverse() {
		return campAdverse;
	}

	/**
	 * @param campAdverse le camp souhaite (1 pour Est et 0 pour Ouest)
	 */
	public void setCampAdverse(int campAdverse) {
		this.campAdverse = campAdverse;
	}
	
	/**
	 * Contient le code du test :
	 * <ul>
	 * <li> Le robot repere le palet et l'attrape @see PaletUltrason
	 * <li> Le robot repere une ligne de couleur et s'oriente avec @see Couleur
	 * <li> Le robot se dirige vers le camp adverse
	 * </ul>
	 */
	@Override
	public void lancer(){
		//le vert est a l'ouest et le bleu a l'est
		CouleurLigne ouest = CouleurLigne.VERTE;
		CouleurLigne est = CouleurLigne.BLEUE;
		//repere et attrape le palet
		try {
			PaletUltrason.dichotomique();
		}
		catch(OuvertureException e) {
			System.out.println("nsm les pinces de ");
		}
		//repere une couleur en avancant
		/**
		 * <p>
		 * Le robot avance jusqu'a une ligne de couleur puis adapte son comportement.
		 * Les lignes noires sont ignorees car elles n'apportent pas d'information.
		 * </p>
		 */
		MouvementsBasiques.avancer();
		while(Couleur.getLastCouleur()==CouleurLigne.GRIS||Couleur.getLastCouleur()==CouleurLigne.NOIRE);
		MouvementsBasiques.arreter();
		
		switch(Couleur.getLastCouleur()) {
		//Cas 1 : on capte du vert
		case VERTE: 
			//si l'adversaire est a l'ouest : bingo
			if(campAdverse==0) {
				MouvementsBasiques.avancer();
				while(Couleur.getLastCouleur()!=CouleurLigne.BLANCHE);
				MouvementsBasiques.arreter();
				try{
					Pince.ouvrir();
				}
				catch(OuvertureException e) {
					//osef en vrai
				}
			}
			//si l'adversaire est a l'est : demi-tour
			else {
				MouvementsBasiques.tourner(180);
				MouvementsBasiques.avancer();
				while(Couleur.getLastCouleur()!=CouleurLigne.BLANCHE);
				MouvementsBasiques.arreter();
				try {
					Pince.ouvrir();
				}
				catch(OuvertureException e) {
					//belek
				}
			}
			break;
		//Cas 2 : on capte du bleu
		case BLEUE:
			//si l'aversaire est a l'est : bingo
			if(campAdverse==1) {
				MouvementsBasiques.avancer();
				while(Couleur.getLastCouleur()!=CouleurLigne.BLANCHE);
				MouvementsBasiques.arreter();
				try{
					Pince.ouvrir();
				}
				catch(OuvertureException e) {
					//osef en vrai
				}
			}
			//si l'adversaire est a l'ouest : demi-tour
			else {
				MouvementsBasiques.tourner(180);
				MouvementsBasiques.avancer();
				while(Couleur.getLastCouleur()!=CouleurLigne.BLANCHE);
				MouvementsBasiques.arreter();
				try {
					Pince.ouvrir();
				}
				catch(OuvertureException e) {
					//belek
				}
			}
			break;
		}
		
		
	}
	
	public void lancer2() {
		new Capteur();
		Couleur.startScanAtRate(0);
		carte.Carte.carteUsuelle.calibrerPosition();
		boolean rougeAGauche = carte.Carte.carteUsuelle.getRobot().getDirection()==90;
		moteurs.Pilote.rentrer("");
		CouleurLigne c = Ligne.xToLongues.get(carte.Carte.carteUsuelle.getRobot().getPosition().getX());
		Pilote.tournerJusqua(c, true, 250); Pilote.tournerJusqua(c, false, 50,20);
		modeSolo.ModeSolo.ramasserPalet(1, 1, false, false, rougeAGauche);
		
	}

}
