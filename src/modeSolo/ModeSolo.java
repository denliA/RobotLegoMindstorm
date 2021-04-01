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
		int trio;
		boolean tient_palet=false;
		boolean droite=false;
		boolean gauche=false;
		boolean milieu=false;
		int rien_trouve;
		Pince.ouvrir();
		System.out.println("AVANTOuvert?" + Pince.getOuvert());
		if (rougeAgauche) { //robot demarre coté armoire
			if (Couleur.getCouleurLigne()==CouleurLigne.ROUGE)
				droite=true; //je bifurque tjrs vers la ligne de droite
			else if(Couleur.getCouleurLigne()==CouleurLigne.NOIRE) {
				milieu=true;
			}
			else if(Couleur.getCouleurLigne()==CouleurLigne.JAUNE)
				gauche=true; //je bifurque tjrs vers la ligne de gauche
		}else { //robot demarre coté porte
			if (Couleur.getCouleurLigne()==CouleurLigne.ROUGE)
				gauche=true; //je bifurque tjrs vers la ligne de gauche
			else if(Couleur.getCouleurLigne()==CouleurLigne.NOIRE) {
				milieu=true;
			}
			else if(Couleur.getCouleurLigne()==CouleurLigne.JAUNE)
				droite=true; //je bifurque tjrs vers la ligne de droite
		}
		CouleurLigne couleur = Couleur.getCouleurLigne();
		while((scoredPalets<nbPalets)||(lignesParcourues<3)) {
			trio=0;
			rien_trouve = 0;
			while(trio<3 && rien_trouve<2) { //pour rammasser les 3 palets sur une ligne de couleur
				if (Toucher.getStatus()==false)
					Toucher.startScan();
				executor.execute(new Runnable() {
					public void run() {
						Pilote.suivreLigne();
					}
				} );

				while((tient_palet || Toucher.getTouche()==false)&&(Couleur.getCouleurLigne()!=CouleurLigne.BLANCHE))
					//System.out.print(milieu); //on ne fait rien
					System.out.print("Dans le while\t");
				Pilote.SetSeDeplace(false); //arrete le suivi de ligne
				if(Toucher.getTouche()) {
					System.out.println("Touché palet, on le prend");
					tient_palet=true;
					Pince.fermer();
				}else if (tient_palet){ //si le robot a atteint sa ligne blanche d'en but et qu'il a ramassé un palet
					System.out.println("tient palet et sur blanche, se retorune");
					Pince.ouvrir();
					scoredPalets++;
					trio++;
					tient_palet = false;
					if(trio<3) {
						MouvementsBasiques.avancerTravel(vitesse,acceleration,-5); //robot recule

					}else
						lignesParcourues++;
				}else { //si le robot a atteint la ligne blanche de l'adversaire sans ramasser de palets
					if (rien_trouve==1) {
						lignesParcourues++;
						rien_trouve++;
					}
					else
						rien_trouve++;	
				}
				MouvementsBasiques.tourner(180); //demi-tour
				Pilote.seRedresserSurLigne(couleur, true, 45, 750);
			}
			if (gauche) {
				MouvementsBasiques.tourner(90); //tourne à gauche de 90 degres
				MouvementsBasiques.avancerTravel(vitesse,acceleration,50); //avance de 50 cm;
				MouvementsBasiques.tourner(-90); //tourne à droite de 90 degres
				//se redresser sur ligne noire
				if (lignesParcourues==1) {
					Pilote.seRedresserSurLigne(CouleurLigne.NOIRE,true,90,1500);
					couleur = CouleurLigne.NOIRE;
				}
				else if (lignesParcourues==2) {
					Pilote.seRedresserSurLigne(CouleurLigne.ROUGE,true,90,1500);
					couleur = CouleurLigne.ROUGE;
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
					Pilote.seRedresserSurLigne(CouleurLigne.JAUNE,true,90,1500);
					couleur = CouleurLigne.JAUNE;
				}		
			}
			if (milieu) {
				if (lignesParcourues==1) {
					MouvementsBasiques.tourner(90); //tourne à gauche de 90 degres
					MouvementsBasiques.avancerTravel(vitesse,acceleration,50); //avance de 50 cm;
					MouvementsBasiques.tourner(-90); //tourne à droite de 90 degres
					Pilote.seRedresserSurLigne(CouleurLigne.ROUGE,true,90,1500);
				}else if(lignesParcourues==2) {
					MouvementsBasiques.tourner(-90); //tourne à droite de 90 degres
					MouvementsBasiques.avancerTravel(vitesse,acceleration,100); //avance de 100 cm;
					MouvementsBasiques.tourner(90); //tourne à gauche de 90 degres
					Pilote.seRedresserSurLigne(CouleurLigne.JAUNE,true,90,1500);
				}
			}
		}	
	}
}
	

