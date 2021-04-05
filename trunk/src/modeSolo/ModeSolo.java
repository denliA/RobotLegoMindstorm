package modeSolo;
import moteurs.MouvementsBasiques;
import moteurs.Pince;
import moteurs.Pilote;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import capteurs.Capteur;
import capteurs.Couleur;
import capteurs.CouleurLigne;
import capteurs.Toucher;
import exceptions.OuvertureException;
import lejos.hardware.Sound;
import lejos.hardware.Button;
import capteurs.Capteur;
import exceptions.*;

public class ModeSolo {
	public static void ramasserPalet(int nbPalets,boolean rougeAgauche) throws EchecGarageException, InterruptedException, OuvertureException {
		new Capteur();
		Executor executor = Executors.newSingleThreadExecutor();
		double vitesse = MouvementsBasiques.getVitesseRobot();
		double acceleration = MouvementsBasiques.getAccelerationRobot()/5;
		int scoredPalets=0;
		int lignesParcourues=0;
		int palets_par_ligne = 3;
		int trio;
		boolean tient_palet=false;
		boolean droite=false;
		boolean gauche=false;
		boolean milieu=false;
		int rien_trouve;
		if(!Pince.getOuvert()){
			Pince.ouvrir();
		}
		CouleurLigne couleur = Couleur.getLastCouleur();
		System.out.println(couleur);
		if (rougeAgauche) { //robot demarre coté armoire
			if (couleur==CouleurLigne.ROUGE)
				droite=true; //je bifurque tjrs vers la ligne de droite
			else if(couleur==CouleurLigne.NOIRE) {
				milieu=true;
			}
			else if(couleur==CouleurLigne.JAUNE)
				gauche=true; //je bifurque tjrs vers la ligne de gauche
		}else { //robot demarre coté porte
			if (couleur==CouleurLigne.ROUGE)
				gauche=true; //je bifurque tjrs vers la ligne de gauche
			else if(couleur==CouleurLigne.NOIRE) {
				milieu=true;
			}
			else if(couleur==CouleurLigne.JAUNE)
				droite=true; //je bifurque tjrs vers la ligne de droite
		}
		while((scoredPalets<nbPalets)&&(lignesParcourues<3)) {
			System.out.println("Début de "+couleur);
			trio=0;
			rien_trouve = 0;
			while(trio<palets_par_ligne && rien_trouve<2) { //pour rammasser les 3 palets sur une ligne de couleur
				System.out.println("Itération "+trio+" de "+couleur);
				if (Toucher.getStatus()==false)
					Toucher.startScan();
				executor.execute(new Runnable() {
					public void run() {
						Pilote.suivreLigne();
					}
				} );

				while((tient_palet || Toucher.getTouche()==false)&&(Couleur.getLastCouleur()!=CouleurLigne.BLANCHE))
					; //on ne fait rien
					//System.out.print("Dans le while\t");
				Pilote.SetSeDeplace(false); //arrete le suivi de ligne
				if(Toucher.getTouche()) {
					System.out.println("Touché palet, on le prend");
					tient_palet=true;
					Pince.fermer();
					MouvementsBasiques.tourner(180); //demi-tour
				}else if (tient_palet){ //si le robot a atteint sa ligne blanche d'en but et qu'il a ramassé un palet
					System.out.println("tient palet et sur blanche, se retorune");
					trio++;
					scoredPalets++;
					MouvementsBasiques.avancerTravel((3-trio)*10);
					Pince.ouvrir();
					tient_palet = false;
					if(trio<palets_par_ligne) {
						MouvementsBasiques.avancerTravel(vitesse,acceleration,-8); //robot recule

					}else {
						MouvementsBasiques.avancerTravel(vitesse,acceleration,-8);
						lignesParcourues++;
					}
					MouvementsBasiques.tourner(180); //demi-tour
					MouvementsBasiques.avancerTravel((3-trio)*10);
				}else { //si le robot a atteint la ligne blanche de l'adversaire sans ramasser de palets
					if (rien_trouve==1) {
						lignesParcourues++;
						rien_trouve++;
					}
					else
						rien_trouve++;	
					MouvementsBasiques.tourner(180); //demi-tour
				}
				if (trio<palets_par_ligne) Pilote.seRedresserSurLigne(couleur, Couleur.aRecemmentVu(couleur, 10), 30, 400);
			}
			if (lignesParcourues>=3)
				break;
			if (gauche) {
				MouvementsBasiques.tourner(90); //tourne à gauche de 90 degres
				MouvementsBasiques.avancerTravel(vitesse,acceleration,50); //avance de 50 cm;
				MouvementsBasiques.tourner(-90); //tourne à droite de 90 degres
				//se redresser sur ligne noire
				if (lignesParcourues==1) {
					Pilote.seRedresserSurLigne(CouleurLigne.NOIRE,true,50,1500);
					couleur = CouleurLigne.NOIRE;
				}
				else if (lignesParcourues==2) {
					couleur = rougeAgauche? CouleurLigne.ROUGE : CouleurLigne.JAUNE;
					Pilote.seRedresserSurLigne(couleur,true,90,1500);
				}
			}
			if (droite) {
				MouvementsBasiques.tourner(-90); //tourne à droite de 90 degres
				MouvementsBasiques.avancerTravel(vitesse,acceleration,50); //avance de 50 cm;
				MouvementsBasiques.tourner(90); //tourne à gauche de 90 degres
				if (lignesParcourues==1) {
					Pilote.seRedresserSurLigne(CouleurLigne.NOIRE,true,90,1500);
					couleur = CouleurLigne.NOIRE;
				}
				else if (lignesParcourues==2) {
					couleur = rougeAgauche? CouleurLigne.JAUNE: CouleurLigne.ROUGE;
					Pilote.seRedresserSurLigne(couleur,true,90,1500);
				}		
			}
			if (milieu) {
				if (lignesParcourues==1) {
					MouvementsBasiques.tourner(90); //tourne à gauche de 90 degres
					MouvementsBasiques.avancerTravel(vitesse,acceleration,50); //avance de 50 cm;
					MouvementsBasiques.tourner(-90); //tourne à droite de 90 degres
					couleur = rougeAgauche? CouleurLigne.ROUGE : CouleurLigne.JAUNE;
					Pilote.seRedresserSurLigne(couleur,true,90,1500);
				}else if(lignesParcourues==2) {
					MouvementsBasiques.tourner(-90); //tourne à droite de 90 degres
					MouvementsBasiques.avancerTravel(vitesse,acceleration,100); //avance de 100 cm;
					MouvementsBasiques.tourner(90); //tourne à gauche de 90 degres
					couleur = rougeAgauche? CouleurLigne.JAUNE : CouleurLigne.ROUGE;
					Pilote.seRedresserSurLigne(couleur,true,90,1500);
				}
			}
		}	
	}
}
	

