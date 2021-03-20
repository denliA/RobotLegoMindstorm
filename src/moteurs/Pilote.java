package moteurs;
import capteurs.*;



public class Pilote {
	private boolean seDeplace=true;
	
	//Le robot doit etre posé et suivre une ligne de couleur donnée par l'utilisateur
	public void suivreLigne(CouleurLigne c) { //je mets en void car l'interface Deplacement n'est pas encore realisée. Je ne sais pas quoi retourner
		float defaultSpeed;
		long debut;
		long dureeRotation = 250; //millisecondes
		Couleur.startScanAtRate(0); //Lance immediatement le timer qui execute update() toutes les 0.1 secondes. C'est une méthode qui scanne la couleur et met à jour les attrubuts statiques de la classe Couleur 
		MouvementsBasiques.avancer(); // Le robot commence à avancer tout droit sans arret
		while(seDeplace) {
			defaultSpeed = Moteur.MOTEUR_DROIT.getSpeed();
			if (Couleur.getCouleurLigne()!=c) { //Retourne sur quelle couleur le robot est posé en fonction des attributs statiques de Couleur. C'est une aapproximation en fonction de probabilités.
				//arreter le timer lanceur
				Couleur.stopScan();
				//restreindre l'acces a la ressource citique du moteur grace à la semaphore. Il ne faut pas que MovePilot et LargeRegulatedMotor accèdent au meme moteur en meme temps
				try {
					MouvementsBasiques.s1.acquire();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//tourner à gauche pendant dureeRotation
				debut = System.currentTimeMillis();
				Moteur.MOTEUR_GAUCHE.setSpeed(defaultSpeed*1.2f);
				while((Couleur.getCouleurLigne()!=c)&&((System.currentTimeMillis() - debut) < dureeRotation)) {
					Couleur.update();
				}
				Moteur.MOTEUR_GAUCHE.setSpeed(defaultSpeed);
				Couleur.update();
				if (Couleur.getCouleurLigne()!=c) {
					//tourner à droite pendant dureeRotation*2
					debut = System.currentTimeMillis();
					Moteur.MOTEUR_DROIT.setSpeed(defaultSpeed*1.2f);
					while((Couleur.getCouleurLigne()!=c)&&((System.currentTimeMillis() - debut) < (dureeRotation*2))) {
						Couleur.update();
					}
					Moteur.MOTEUR_DROIT.setSpeed(defaultSpeed);
				}
				//liberer la ressource critique
				MouvementsBasiques.s1.release();
				//On relance le timer lanceur immediatement
				Couleur.startScanAtRate(0);
				if(Couleur.getCouleurLigne()!=c) {
					//gestion d'erreur le robot n'a pas pu se redresser sur une ligne de couleur et il est perdu. Il faut arreter le mouvement
					seDeplace = false;
				}
			}
		}
		MouvementsBasiques.arreter(); //Le robot s'arrete
	}
	
	//Le robot doit etre posé sur une ligne à suivre
	public void suivreLigne() {
		suivreLigne(Couleur.getCouleurLigne());
	}
	
	/*
	public Deplacement versIntersection(Ligne ligne1, Ligne ligne2) {
		//TO DO
	}
	
	public Deplacement seRedresserSurLigne() {
		//TO DO
	}
	
	public Deplacement diagonale(Point arrivee) {
		//TO DO
	}
	*/
}
