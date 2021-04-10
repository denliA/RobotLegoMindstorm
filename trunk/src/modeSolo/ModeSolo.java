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
import exceptions.*;

public class ModeSolo {
	public static void ramasserPalet(int nbPalets,boolean rougeAgauche) throws EchecGarageException, InterruptedException, OuvertureException {
		new Capteur();
		Executor executor = Executors.newSingleThreadExecutor();
		final double vitesse = 25;
		final double acceleration = 30;
		final double vitesse_angulaire = 180;
		double acceleration_angulaire = MouvementsBasiques.chassis.getAngularAcceleration();
		MouvementsBasiques.chassis.setAngularSpeed(vitesse_angulaire);
		MouvementsBasiques.chassis.setLinearSpeed(vitesse);
		MouvementsBasiques.chassis.setLinearAcceleration(acceleration);
		int scoredPalets=0;
		int lignesParcourues=0;
		int palets_par_ligne = 3;
		int trio;
		boolean tient_palet=false;
		boolean droite=false;
		boolean gauche=false;
		boolean milieu=false;
		int rien_trouve;
		boolean touche=false;
		
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
			//System.out.println("Début de "+couleur);
			trio=0;
			rien_trouve = 0;
			while(trio<palets_par_ligne && rien_trouve<2) { //pour rammasser les 3 palets sur une ligne de couleur
				System.out.println("Itération "+trio+" de "+couleur);

				
				executor.execute(new ArgRunnable(couleur) {
					public void run() {
						Pilote.suivreLigne((CouleurLigne) truc);
					}
				} );

				while((tient_palet || (touche=Toucher.getTouche())==false)&&(Couleur.getLastCouleur()!=CouleurLigne.BLANCHE))
					; //on ne fait rien
					//System.out.print("Dans le while\t");
				
				Pilote.SetSeDeplace(false); //arrete le suivi de ligne
				MouvementsBasiques.chassis.waitComplete();

				if (tient_palet){ //si le robot a atteint sa ligne blanche d'en but et qu'il a ramassé un palet
					trio++;
					scoredPalets++;
					MouvementsBasiques.chassis.travel((3-trio)*4); MouvementsBasiques.chassis.waitComplete();
					Pince.ouvrir();
					tient_palet = false;
					if(trio<palets_par_ligne) {
						MouvementsBasiques.chassis.travel(-8-(3-trio)*4); MouvementsBasiques.chassis.waitComplete(); //robot recule

					}else {
						MouvementsBasiques.chassis.travel(-8); MouvementsBasiques.chassis.waitComplete();
						lignesParcourues++;
					}
					MouvementsBasiques.chassis.arc(0,180); MouvementsBasiques.chassis.waitComplete(); //demi-tour
				}
				else if(touche) {
					tient_palet=true;
					Pince.fermer();
					MouvementsBasiques.chassis.arc(0,185); MouvementsBasiques.chassis.waitComplete();  //demi-tour
				}
				else { //si le robot a atteint la ligne blanche de l'adversaire sans ramasser de palets
					if (rien_trouve==1) {
						lignesParcourues++;
						rien_trouve++;
					}
					else
						rien_trouve++;	
					MouvementsBasiques.chassis.arc(0,180); MouvementsBasiques.chassis.waitComplete();  //demi-tour
				}
				Pilote.seRedresserSurLigne(couleur, Couleur.aRecemmentVu(couleur, 40), 30*(1+trio), 60);
			}
			if (lignesParcourues>=3)
				break;
			if (gauche) {
				MouvementsBasiques.chassis.arc(0,90); MouvementsBasiques.chassis.waitComplete(); //tourne à gauche de 90 degres
				MouvementsBasiques.chassis.travel(50); MouvementsBasiques.chassis.waitComplete();  //avance de 50 cm;
				MouvementsBasiques.chassis.arc(0,-90); MouvementsBasiques.chassis.waitComplete();  //tourne à droite de 90 degres
				//se redresser sur ligne noire
				if (lignesParcourues==1) {
					Pilote.seRedresserSurLigne(CouleurLigne.NOIRE,true,40,80);
					couleur = CouleurLigne.NOIRE;
				}
				else if (lignesParcourues==2) {
					couleur = rougeAgauche? CouleurLigne.ROUGE : CouleurLigne.JAUNE;
					Pilote.seRedresserSurLigne(couleur,true,40,40);
				}
			}
			if (droite) {
				MouvementsBasiques.chassis.arc(0,-90); MouvementsBasiques.chassis.waitComplete();  //tourne à droite de 90 degres
				MouvementsBasiques.chassis.travel(50);  MouvementsBasiques.chassis.waitComplete(); //avance de 50 cm;
				MouvementsBasiques.chassis.arc(0,90); MouvementsBasiques.chassis.waitComplete();  //tourne à gauche de 90 degres
				if (lignesParcourues==1) {
					couleur = CouleurLigne.NOIRE;
					Pilote.seRedresserSurLigne(CouleurLigne.NOIRE,Couleur.aRecemmentVu(couleur, 10),90,90);
				}
				else if (lignesParcourues==2) {
					couleur = rougeAgauche? CouleurLigne.JAUNE: CouleurLigne.ROUGE;
					Pilote.seRedresserSurLigne(couleur,Couleur.aRecemmentVu(couleur, 10),90,90);
				}		
			}
			if (milieu) {
				if (lignesParcourues==1) {
					MouvementsBasiques.chassis.arc(0,90); MouvementsBasiques.chassis.waitComplete();  //tourne à gauche de 90 degres
					MouvementsBasiques.chassis.travel(50);  MouvementsBasiques.chassis.waitComplete();//avance de 50 cm;
					MouvementsBasiques.chassis.arc(0,-90); MouvementsBasiques.chassis.waitComplete();  //tourne à droite de 90 degres
					couleur = rougeAgauche? CouleurLigne.ROUGE : CouleurLigne.JAUNE;
					Pilote.seRedresserSurLigne(couleur,true,90,80);
				}else if(lignesParcourues==2) {
					MouvementsBasiques.chassis.arc(0,-90); MouvementsBasiques.chassis.waitComplete();  //tourne à droite de 90 degres
					MouvementsBasiques.chassis.travel(100);  MouvementsBasiques.chassis.waitComplete(); //avance de 100 cm;
					MouvementsBasiques.chassis.arc(0,90); MouvementsBasiques.chassis.waitComplete();  //tourne à gauche de 90 degres
					couleur = rougeAgauche? CouleurLigne.JAUNE : CouleurLigne.ROUGE;
					Pilote.seRedresserSurLigne(couleur,true,90,80);
				}
			}
		}	
	}
}
	

abstract class ArgRunnable implements Runnable {
	Object truc;
	public ArgRunnable(Object truc) {
		this.truc = truc;
	}
}
