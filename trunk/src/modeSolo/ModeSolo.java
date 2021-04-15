package modeSolo;
import moteurs.MouvementsBasiques;
import moteurs.Pince;
import moteurs.Pilote;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import capteurs.Capteur;
import capteurs.Couleur;
import capteurs.CouleurLigne;
import capteurs.Toucher;
import capteurs.Ultrason;
import exceptions.*;
import interfaceEmbarquee.Musique;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.Sounds;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

public class ModeSolo {
	//methode classique pour le ModeSolo
	public static void ramasserPalet(int nbPalets,boolean rougeAgauche) throws OuvertureException, InterruptedException {
		ramasserPalet(nbPalets,3,false,false,rougeAgauche);
	}
	
	//surcharge pour indiquer que le ModeCompetition a ramassé un palet
	public static void ramasserPalet(int nbPalets,boolean PaletScored,boolean rougeAgauche) throws OuvertureException, InterruptedException {
		ramasserPalet(nbPalets,3,false,PaletScored,rougeAgauche);
	}
	
	
	public static void ramasserPalet(int nbPalets,int palets_par_ligne, boolean ligneDuo, boolean PaletScored,boolean rougeAgauche) throws OuvertureException, InterruptedException {
		//Ne pas rouvrir les capteurs si ils ont deja ete ouverts dans le modeCompetition
		if ((ligneDuo==false)&&(PaletScored==false)) {
			new Capteur();
			Toucher.startScan();
			Ultrason.startScan();
			Couleur.startScanAtRate(0);
			//Pilote.startVideAtRate(0);
		}
		ExecutorService executor = Executors.newSingleThreadExecutor();
		final double vitesse = 25;
		final double acceleration = 30;
		final double vitesse_angulaire = 180;
		MouvementsBasiques.chassis.setAngularSpeed(vitesse_angulaire);
		MouvementsBasiques.chassis.setLinearSpeed(vitesse);
		MouvementsBasiques.chassis.setLinearAcceleration(acceleration);
		int scoredPalets=0;
		int lignesParcourues=0;
		boolean tient_palet=false;
		boolean droite=false;
		boolean gauche=false;
		boolean milieu=false;
		int rien_trouve;
		boolean touche=false;
		int trio;
		
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
			if (PaletScored) {
				trio=1;
				PaletScored=false; //la prochaine fois on ne rentre plus dans cette boucle
			}
			else {
				trio=0;
			}
			rien_trouve = 0;
			while(trio<palets_par_ligne && rien_trouve<2) { //pour rammasser les 3 palets sur une ligne de couleur
				//Ne pas parcourir la ligne de milieu si elle a deja été parcourue par le modeCompetition
				if (ligneDuo) {
					ligneDuo=false;
					lignesParcourues++;
					break;
				}

				executor.execute(new ArgRunnable(couleur) {
					public void run() {
						Pilote.suivreLigne((CouleurLigne) truc);
					}
				});
				
				Couleur.blacheTouchee();
				while((tient_palet || (touche=Toucher.getTouche())==false)&&!Couleur.blacheTouchee()){
					//on ne fait rien
				};
				
				
				
				Pilote.SetSeDeplace(false); //arrete le suivi de ligne
				MouvementsBasiques.chassis.waitComplete();

				if (tient_palet){ //si le robot a atteint sa ligne blanche d'en but et qu'il a ramassé un palet
					trio++;
					scoredPalets++;
					Pince.ouvrir(250);
					tient_palet = false;
					if(trio<palets_par_ligne) {
						MouvementsBasiques.chassis.travel(-8); MouvementsBasiques.chassis.waitComplete(); //robot recule

					}else {
						MouvementsBasiques.chassis.travel(-8); MouvementsBasiques.chassis.waitComplete();
						lignesParcourues++;
					}
					Pilote.tournerJusqua(couleur, true,250);
					Pilote.tournerJusqua(couleur, false, 50,50);
				}
				//si le robot vient de toucher un palet
				else if(touche) { 
					//lance le bruitage dans un thread
					Musique.startMusic("Wow.wav");
					tient_palet=true;
					Pince.fermer(500);
					if(couleur==CouleurLigne.NOIRE && trio == 1) {
						Pilote.tournerJusqua(couleur, true, 250, 850); //si adroite est vrai, la roue droite avance et la roue gauche recule. Donc le robot tourne a gauche
						Pilote.tournerJusqua(couleur, false, 50, 50); //le robot tourne a droite
					}
					else {
						Pilote.tournerJusqua(couleur, true,250);
						Pilote.tournerJusqua(couleur, false, 50,50);
					}
				}
				else { //si le robot a atteint la ligne blanche de l'adversaire sans ramasser de palets
					//lance le bruitage dans un thread
					Musique.startMusic("MissionFailed.wav");
					if (rien_trouve==1) {
						lignesParcourues++;
						rien_trouve++;
					}
					else
						rien_trouve++;	
					Pilote.tournerJusqua(couleur, true,250);
					Pilote.tournerJusqua(couleur, false, 50,50);
				}
			}
			if (lignesParcourues>=3||scoredPalets>=nbPalets)
				break;
			if (gauche) {
				//se redresser sur ligne noire
				if (lignesParcourues==1) {
					couleur = CouleurLigne.NOIRE;
				}
				else if (lignesParcourues==2) {
					couleur = rougeAgauche? CouleurLigne.ROUGE : CouleurLigne.JAUNE;
				}
				MouvementsBasiques.chassis.rotate(90); MouvementsBasiques.chassis.waitComplete(); //tourne à gauche de 90 degres
				Pilote.chercheLigne(couleur, vitesse, acceleration, vitesse_angulaire, false); //se gare sur la ligne de couleur
			}
			if (droite) {
				if (lignesParcourues==1) {
					couleur = CouleurLigne.NOIRE;
				}
				else if (lignesParcourues==2) {
					couleur = rougeAgauche? CouleurLigne.JAUNE: CouleurLigne.ROUGE;
				}		
				MouvementsBasiques.chassis.rotate(-90); MouvementsBasiques.chassis.waitComplete();  //tourne à droite de 90 degres
				Pilote.chercheLigne(couleur, vitesse, acceleration, vitesse_angulaire, true); //se gare sur la ligne de couleur
			}
			if (milieu) {
				if (lignesParcourues==1) {
					//D'abord, le robot va sur la ligne de gauche
					couleur = rougeAgauche? CouleurLigne.ROUGE : CouleurLigne.JAUNE;
					MouvementsBasiques.chassis.rotate(90); MouvementsBasiques.chassis.waitComplete();  //tourne à gauche de 90 degres
					Pilote.chercheLigne(couleur, vitesse, acceleration, vitesse_angulaire, false);
				}else if(lignesParcourues==2) {
					//Puis, le robot va sur la ligne de droite
					couleur = rougeAgauche? CouleurLigne.JAUNE : CouleurLigne.ROUGE;
					MouvementsBasiques.chassis.rotate(-90); MouvementsBasiques.chassis.waitComplete();  //tourne à droite de 90 degres
					Pilote.chercheLigne(couleur, vitesse, acceleration, vitesse_angulaire, true); //se gare sur la ligne de couleur
				}
			}
		}
		
		//limite arbitraire pour evaluer notre niveau de satisfaction
		if (scoredPalets>(nbPalets)/2) {
			//partie gagnée
			Musique.startMusic("VictorySong.wav"); //lance le bruitage dans un thread
		}
		else {
			//partie perdue
			Musique.startMusic("LosingSong.wav"); //lance le bruitage dans un thread
		}
		
		//possibilite d'arreter la musique qui ne dure pas plus de 30 secondes
//		int button = -1;
//		Delay.msDelay(2000);
//		LCD.clear();
//		LCD.drawString("Arreter?", 3, 3);
//		LCD.drawString("Pressez sur Entree", 3, 4);
//		while((button!=Button.ID_ENTER)&&(button!=Button.ID_ESCAPE)) {
//			button = Button.waitForAnyPress();
//		}
//		if (button!=Button.ID_ENTER) {
//			Musique.stopMusic();	
//		}
		
		//arreter les mesures des capteurs et fermer le pool de threads
		Toucher.stopScan();
		Ultrason.stopScan();
		Couleur.stopScan();
		Pilote.stopVide();
		executor.shutdown();
	}
}
	
/* Dans un thread, on n'a pas acces a des variables d'instances d'autres classes
 * or ici, on a besoin de passer la couleur en parametre de suivreLigne() qui sera executée dans la methode run() du thread */
 
//Pour contourner ça, voici une classe qui permet de passer un parametre à un Runnable
abstract class ArgRunnable implements Runnable {
	Object truc;
	public ArgRunnable(Object truc) {
		this.truc = truc;
	}
}
