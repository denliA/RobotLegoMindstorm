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
import lejos.utility.Delay;

/**
 * <p>ModeSolo est une classe qui contient les strategies du ModeSolo.</p>
 * 
 * <p>Pour le moment, nous avons codé une seule strategie qui ramasse les 9 palets. Le robot suit des mouvement prédéfinis vu que aucun autre robot ne poura déplacer des palets.</p>
 * 
 * <p>Dans le ModeSolo, le robot doit deposer les palets derrière sa ligne blanche.</p>
 * 
 * <p>L'utilisateur précise la camp avec le boolean rougeAgauche. Le robot peut demarrer sur l'un des trois points de departs qui sont la ligne rouge, noire ou jaune.</p>
 * 
 * 
 * 
 */

public class ModeSolo {
	
	/**
     * <p>Méthode classique du ModeSolo pour ramasser les 9 palets.</p>
     * 
     * @param nbPalets
     *            Le nombre de palets à ramasser.
     * @param rougeAgauche
     *            Indique le camp de depart du robot. Si la ligne rouge est à gauche, ce boolean est vrai. Sinon, il est faux.
     *  
     * @throws OuvertureException
     *            Si les pinces étaient dejà ouvertes quand on a demandé de les ouvrir. 
	 *
     */
	public static void ramasserPalet(int nbPalets,boolean rougeAgauche) throws OuvertureException{
		ramasserPalet(nbPalets,3,false,false,rougeAgauche);
	}
	
	/**
     * <p>Surcharge pour indiquer que le ModeCompetition a ramassé un palet.</p>
     * 
     * @param nbPalets
     *            Le nombre de palets à ramasser.
     * @param paletScored
     * 			  Est vrai si le robot avait ramassé un palet dans le ModeCompetition avant de lancer le ModeSolo
     * @param rougeAgauche
     *            Indique le camp de depart du robot. Si la ligne rouge est à gauche, ce boolean est vrai. Sinon, il est faux.
     *  
     * @throws OuvertureException
     *            Si les pinces étaient dejà ouvertes quand on a demandé de les ouvrir. 
	 *
     */
	public static void ramasserPalet(int nbPalets,boolean paletScored,boolean rougeAgauche) throws OuvertureException {
		ramasserPalet(nbPalets,3,false,paletScored,rougeAgauche);
	}
	
	/**
     * <p>La vraie méthode qui permet toutes les surcharges pour le ModeSolo et le ModeCompetition.</p>
     * 
     * @param nbPalets
     *            Le nombre de palets à ramasser.
     * @param palets_par_ligne
     * 			  Le nombre de palets présents sur chaque ligne de couleur
     * @param ligneDuo
     * 			  Est vrai si le robot a parcourue toute une ligne de couleur sans ramasser de palet dans le ModeCompetition avant de lancer le ModeSolo
     * @param paletScored
     * 			  Est vrai si le robot avait ramassé un palet dans le ModeCompetition avant de lancer le ModeSolo
     * @param rougeAgauche
     *            Indique le camp de depart du robot. Si la ligne rouge est à gauche, ce boolean est vrai. Sinon, il est faux.
     *  
     * @throws OuvertureException
     *            Si les pinces étaient dejà ouvertes quand on a demandé de les ouvrir. 
	 *
     */
	public static void ramasserPalet(int nbPalets,int palets_par_ligne, boolean ligneDuo, boolean paletScored,boolean rougeAgauche) throws OuvertureException {
		//Ne pas rouvrir les capteurs si ils ont deja ete ouverts dans le modeCompetition
		if ((ligneDuo==false)&&(paletScored==false)) {
			//Charger la classe capteur pour pouvoir l'utiliser
			new Capteur();
			//Debut des prises de mesures par les capteurs
			Toucher.startScan();
			Ultrason.startScan();
			Couleur.startScanAtRate(0);
			Delay.msDelay(500);
		}
		ExecutorService executor = Executors.newSingleThreadExecutor();
		
		//Calibrer la vitesse et l'acceleration réelles du robot et celles des moteurs
		final double vitesse = 25;
		final double acceleration = 30;
		final double vitesse_angulaire = 180;
		MouvementsBasiques.chassis.setAngularSpeed(vitesse_angulaire);
		MouvementsBasiques.chassis.setLinearSpeed(vitesse);
		MouvementsBasiques.chassis.setLinearAcceleration(acceleration);
		
		//Nombre de palets ramassés
		int scoredPalets=0;
		//Nombre de lignes parcourues
		int lignesParcourues=0;
		//Si le robot tient un palet
		boolean tient_palet=false;
		//Pour indiquer dans quel sens le robot va tourner selon son point de départ
		boolean droite=false;
		boolean gauche=false;
		boolean milieu=false;
		//Utilisé pour faire un aller retour dans une ligne de couleur
		int rien_trouve;
		//Si le capteur de toucher détecte un palet
		boolean touche=false;
		//Pour ramasser trois palets par ligne
		int trio;
		
		//Ouvrir les pinces si ce n'est pas deja fait
		if(!Pince.getOuvert()){
			Pince.ouvrir();
		}
		//On sauvegarde la couleur de la ligne de depart du robot
		CouleurLigne couleur = Couleur.getLastCouleur();
		System.out.println(couleur);
		
		//robot demarre coté armoire
		if (rougeAgauche) { 
			if (couleur==CouleurLigne.ROUGE) {
				//je bifurque tjrs vers la ligne de droite
				droite=true; 
			}else if(couleur==CouleurLigne.NOIRE) {
				milieu=true;
			}
			else if(couleur==CouleurLigne.JAUNE) {
				//je bifurque tjrs vers la ligne de gauche
				gauche=true;
			}
		}
		//robot demarre coté porte
		else { 
			if (couleur==CouleurLigne.ROUGE) {
				//je bifurque tjrs vers la ligne de gauche
				gauche=true; 
			}else if(couleur==CouleurLigne.NOIRE) {
				milieu=true;
			}
			else if(couleur==CouleurLigne.JAUNE) {
				//je bifurque tjrs vers la ligne de droite
				droite=true; 
			}
		}
		
		//conditions de sortie de la boucle
		while((scoredPalets<nbPalets)&&(lignesParcourues<3)) {
			if (paletScored) {
				//le robot a déjà ramassé un palet
				trio=1;
				//la prochaine fois on ne rentre plus dans cette boucle
				paletScored=false; 
			}
			else {
				//on initialise trio à zero
				trio=0;
			}
			rien_trouve = 0;
			//pour rammasser les 3 palets sur une ligne de couleur
			while(trio<palets_par_ligne && rien_trouve<2) { 
				//Ne pas parcourir la ligne de milieu si elle a deja été parcourue par le modeCompetition
				if (ligneDuo) {
					ligneDuo=false;
					lignesParcourues++;
					break;
				}
				//demande à l'executor d'executer le suivi de ligne dans un thread
				executor.execute(new ArgRunnable(couleur) {
					public void run() {
						Pilote.suivreLigne((CouleurLigne) truc);
					}
				});
				//si le robot avait déjà détecté du blanc, on met le boolean à faux pour ignorer cette information
				Couleur.blacheTouchee();
				while((tient_palet || (touche=Toucher.getTouche())==false)&&!Couleur.blacheTouchee()){
					//on ne fait rien, le robot continue d'avancer
				};
				
				
				//arrete le suivi de ligne
				Pilote.SetSeDeplace(false); 
				MouvementsBasiques.chassis.waitComplete();
				//si le robot a atteint sa ligne blanche d'en but et qu'il a ramassé un palet
				if (tient_palet){ 
					trio++;
					scoredPalets++;
					Pince.ouvrir(250);
					tient_palet = false;
					if(trio<palets_par_ligne) {
						//robot recule
						MouvementsBasiques.chassis.travel(-8); MouvementsBasiques.chassis.waitComplete(); 

					}else {
						MouvementsBasiques.chassis.travel(-8); MouvementsBasiques.chassis.waitComplete();
						lignesParcourues++;
					}
					//robot fait demi-tour
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
						//si adroite est vrai, la roue droite avance et la roue gauche recule. Donc le robot tourne a gauche
						Pilote.tournerJusqua(couleur, true, 250, 850); 
						//le robot tourne a droite
						Pilote.tournerJusqua(couleur, false, 50, 50); 
					}
					else {
						//robot fait demi-tour
						Pilote.tournerJusqua(couleur, true,250, 350, 200);
						Pilote.tournerJusqua(couleur, false, 50,50);
					}
				}
				//si le robot a atteint la ligne blanche de l'adversaire sans ramasser de palets
				else { 
					//lance le bruitage dans un thread
					Musique.startMusic("MissionFailed.wav");
					if (rien_trouve==1) {
						lignesParcourues++;
						rien_trouve++;
					}
					else {
						rien_trouve++;
					}
					//robot fait demi-tour
					Pilote.tournerJusqua(couleur, true,250);
					Pilote.tournerJusqua(couleur, false, 50,50);
				}
			}
			//on sort de la boucles si 3 lignes longues ont été parcourues ou si tous les palets demandés ont été ramassés
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
				//tourne à gauche de 90 degres
				MouvementsBasiques.chassis.rotate(90); MouvementsBasiques.chassis.waitComplete(); 
				//se gare sur la ligne de couleur
				Pilote.chercheLigne(couleur, vitesse, acceleration, vitesse_angulaire, false); 
			}
			if (droite) {
				if (lignesParcourues==1) {
					couleur = CouleurLigne.NOIRE;
				}
				else if (lignesParcourues==2) {
					couleur = rougeAgauche? CouleurLigne.JAUNE: CouleurLigne.ROUGE;
				}		
				//tourne à droite de 90 degres
				MouvementsBasiques.chassis.rotate(-90); MouvementsBasiques.chassis.waitComplete();  
				//se gare sur la ligne de couleur
				Pilote.chercheLigne(couleur, vitesse, acceleration, vitesse_angulaire, true); 
			}
			if (milieu) {
				if (lignesParcourues==1) {
					//D'abord, le robot va sur la ligne de gauche
					couleur = rougeAgauche? CouleurLigne.ROUGE : CouleurLigne.JAUNE;
					//tourne à gauche de 90 degres
					MouvementsBasiques.chassis.rotate(90); MouvementsBasiques.chassis.waitComplete();  
					Pilote.chercheLigne(couleur, vitesse, acceleration, vitesse_angulaire, false);
				}else if(lignesParcourues==2) {
					//Puis, le robot va sur la ligne de droite
					couleur = rougeAgauche? CouleurLigne.JAUNE : CouleurLigne.ROUGE;
					//tourne à droite de 90 degres
					MouvementsBasiques.chassis.rotate(-90); MouvementsBasiques.chassis.waitComplete();  
					//se gare sur la ligne de couleur
					Pilote.chercheLigne(couleur, vitesse, acceleration, vitesse_angulaire, true); 
				}
			}
		}
		
		//limite arbitraire pour evaluer notre niveau de satisfaction
		if (scoredPalets>=(nbPalets)/2) {
			//partie gagnée
			Musique.startMusic("VictorySong.wav"); //lance la musique dans un thread
		}
		else {
			//partie perdue
			Musique.startMusic("LosingSong.wav"); //lance la musique dans un thread
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
		executor.shutdown();
	}
}

/**
 * <p>Dans un thread, on n'a pas acces a des variables d'instances d'autres classes. Or ici, on a besoin de passer la couleur en parametre de suivreLigne() qui sera executée dans la methode run() du thread.</p>
 * <p>Pour contourner ça, voici une classe qui permet de passer un parametre à un Runnable</p>
 * 
 */
abstract class ArgRunnable implements Runnable {
	Object truc;
	public ArgRunnable(Object truc) {
		this.truc = truc;
	}
}
