package modeCompetition;

import capteurs.Capteur;
import capteurs.Couleur;
import capteurs.CouleurLigne;
import capteurs.Toucher;
import capteurs.Ultrason;
import exceptions.OuvertureException;
import interfaceEmbarquee.Musique;
import lejos.utility.Delay;
import moteurs.MouvementsBasiques;
import moteurs.Pilote;
import moteurs.Pince;
import modeSolo.ModeSolo;

/**
 * <p>ModeCompetition est une classe qui contient les strategies du ModeCompetition et qui s'appuie sur le ModeSolo.</p>
 * 
 * <p>Pour le moment, nous avons codé une seule stratégie qui ramasse les 9 palets. Ce n'est pas un "vrai" mode competition vu que les déplacements 
 * du robot adverse et les palets qu'il a éventuellement ramassés ne sont pas pris en compte. Dans une vrai competition, nous devons utiliser le 
 * capteur à ultrason pour connaître les positions réelles des palets et du robot adverse.</p>
 * 
 * <p>Dans le ModeSolo, le robot doit déposer les palets derrière sa ligne blanche. Dans le ModeCompetition, il doit les deposer derrière 
 * la ligne blanche de l'adversaire. Cette stratégie imite cette difference de zone d'en but.</p>
 * 
 * <p>Le robot peut démarrer sur l'un des trois points de departs qui sont la ligne rouge, noire ou jaune. Deux possibilités :
 *     <ul>
 *         <li>Le robot ramasse le premier palet qui se trouve devant lui, sort de la ligne et se décale pour ne pas pousser les autres palets. 
 *         Puis, il avance tout droit et depose son palet derrière la ligne blanche de l'adversaire. Il se retourne et appelle le ramasserPalet()
 *         du modeSolo pour ramener les 8 palets restants. Ensuite, il les depose dans la zone d'en but derrière lui qui est celle de l'adversaire.
 *         </li>
 *         <li>Le robot a atteint la ligne blanche de l'adversaire sans ramener de palet. Il se décale sur une autre ligne et appelle le ramasserPalet() du modeSolo.</li>
 *     </ul>
 * 
 * @see ModeSolo#ramasserPalet(int, int, boolean, boolean, boolean)
 * 
 */

public class ModeCompetition {
	
	/**
     * Unique stratégie du ModeCompetition pour ramasser les 9 palets
     * 
     * @param nbPalets
     *            Le nombre de palets à ramasser.
     * @param rougeAgauche
     *            Indique le camp de depart du robot. Si la ligne rouge est à gauche, ce boolean est vrai. Sinon, il est faux.
     *  
     * @throws OuvertureException      Si les pinces étaient dejà ouvertes quand on a demandé de les ouvrir. 
	 *
     */
	
	public static void ramasserPalet(int nbPalets,boolean rougeAgauche) throws OuvertureException {
		//Charger la classe capteur pour pouvoir l'utiliser
		new Capteur(); 
		//Debut des prises de mesures par les capteurs
		Toucher.startScan();
		Ultrason.startScan();
		Couleur.startScanAtRate(0);
		Delay.msDelay(500);
		
		double angle;
		boolean droite=false;
		boolean gauche=false;
		boolean milieu=false;
		boolean touche=false;
		boolean couleurRepassee=false;
		
		//Calibrer la vitesse et l'acceleration réelles du robot et celles des moteurs
		final double vitesse = 25;
		final double acceleration = 30;
		final double vitesse_angulaire = 180;
		MouvementsBasiques.chassis.setAngularSpeed(vitesse_angulaire);
		MouvementsBasiques.chassis.setLinearSpeed(vitesse);
		MouvementsBasiques.chassis.setLinearAcceleration(acceleration);
		
		//Ouvrir les pinces si ce n'est pas deja fait
		if(!Pince.getOuvert()){
			Pince.ouvrir();
		}
		
		//On sauvegarde la couleur de la ligne de depart du robot
		CouleurLigne couleur = Couleur.getLastCouleur(); 
		
		//Le suivi de ligne va se lancer une seule fois dans un thread à l'appel de la methode start()
		Thread t1 = new Thread(new ArgRunnableDuo(couleur) {
			public void run() {
				Pilote.suivreLigne((CouleurLigne) truc);
			}
		});
		
		//robot demarre coté armoire
		if (rougeAgauche) { 
			if (couleur==CouleurLigne.ROUGE) {
				//je bifurque tjrs vers la ligne de droite
				droite=true; 
			}
			else if(couleur==CouleurLigne.NOIRE) {
				milieu=true;
			}
			else if(couleur==CouleurLigne.JAUNE) {
				//je bifurque tjrs vers la ligne de gauche
				gauche=true; 
			}
		//robot demarre coté porte
		}else { 
			if (couleur==CouleurLigne.ROUGE) {
				//je bifurque tjrs vers la ligne de gauche
				gauche=true; 
			}
			else if(couleur==CouleurLigne.NOIRE) {
				milieu=true;
			}
			else if(couleur==CouleurLigne.JAUNE) {
				//je bifurque tjrs vers la ligne de droite
				droite=true; 
			}
		}
		t1.start(); //debut suivreLigne()
		
		//on ne fait rien, le robot continue d'avancer
		while(((touche=Toucher.getTouche())==false) && (Couleur.getLastCouleur()!=CouleurLigne.BLANCHE));
		
		//le robot a touche un palet ou detecte la ligne blanche
		//arrete le suivi de ligne et le robot s'immobilise
		Pilote.SetSeDeplace(false); 
		MouvementsBasiques.chassis.waitComplete();
		
		/* Si le robot vient de toucher un palet */
		if(touche) {
			//lance le bruitage dans un thread
			Musique.startMusic("Wow.wav");
			Pince.fermer();
			angle=90; //angle pour tourner a gauche
			if(gauche){
				//se decaler vers la gauche de la ligne
				MouvementsBasiques.chassis.rotate(angle); MouvementsBasiques.chassis.waitComplete();  
				MouvementsBasiques.chassis.travel(25); MouvementsBasiques.chassis.waitComplete();  
				MouvementsBasiques.chassis.rotate(-angle);	MouvementsBasiques.chassis.waitComplete(); 	
			}
			if(droite){
				//se decaler vers la droite de la ligne
				angle=-90;
				MouvementsBasiques.chassis.rotate(angle); MouvementsBasiques.chassis.waitComplete(); 
				MouvementsBasiques.chassis.travel(25); MouvementsBasiques.chassis.waitComplete();  
				MouvementsBasiques.chassis.rotate(-angle); MouvementsBasiques.chassis.waitComplete();  
				
			}
			if(milieu){
				//se decaler vers la gauche de la ligne
				angle=-90;
				MouvementsBasiques.chassis.rotate(angle); MouvementsBasiques.chassis.waitComplete();  
				MouvementsBasiques.chassis.travel(25); MouvementsBasiques.chassis.waitComplete();  
				MouvementsBasiques.chassis.rotate(-angle);	MouvementsBasiques.chassis.waitComplete(); 
			}
			MouvementsBasiques.chassis.travel(Double.POSITIVE_INFINITY); //le robot avance tout droit tant qu'il n'est pas arrete
			while(Couleur.getLastCouleur()!=CouleurLigne.BLANCHE) { //peut etre vide a ajouter?
				//la couleur de depart a ete detectee
				if (Couleur.getLastCouleur()==couleur) {
					couleurRepassee=true; //flag mis a vrai si se test est vrai au moins une fois
				}
			}
			//lance le bruitage dans un thread
			Musique.startMusic("Easy.wav");
			MouvementsBasiques.chassis.stop();
			Pince.ouvrir();
			MouvementsBasiques.chassis.travel(-5); MouvementsBasiques.chassis.waitComplete(); //robot recule
			/* si le robot n'est pas repassé sur la ligne de couleur de depart */
			if (!couleurRepassee) {
				//tourner dans la direction contraire au decalage pour revenir sur la ligne de depart
				angle=-angle;
			}
			/* si le robot a franchi la ligne de couleur de depart, on tourne dans la meme direction que le decalage
			 * pour revenir sur la ligne de depart */
			MouvementsBasiques.chassis.rotate(angle); MouvementsBasiques.chassis.waitComplete();
			Pilote.chercheLigne(couleur,vitesse,acceleration,vitesse_angulaire,true); //se gare sur la ligne de couleur initiale
			//appelle le ramasserPalet du modeSolo sachant qu'un palet a été ramassé
			modeSolo.ModeSolo.ramasserPalet(nbPalets-1,true,!rougeAgauche); 
			
		}
		/* Si le robot a atteint la ligne blanche de l'adversaire sans ramasser de palets */
		else {
			//lance le bruitage dans un thread
			Musique.startMusic("MissionFailed.wav");
			//robot recule
			MouvementsBasiques.chassis.travel(-5); MouvementsBasiques.chassis.waitComplete();
			//faire demi-tour
			Pilote.tournerJusqua(couleur,true,250); //tourne jusqu'à la couleur
			Pilote.tournerJusqua(couleur, false, 50,50); //se remet bien sur la ligne si on la dépasse
			//appelle le ramasserPalet du modeSolo sachant qu'une ligne a été parcourue sans ramasser de palet
			modeSolo.ModeSolo.ramasserPalet(nbPalets,3,true,false,!rougeAgauche);
		}
	}
}
	
	/**
	 * <p>Dans un thread, on n'a pas acces a des variables d'instances d'autres classes or ici, on a besoin de passer la couleur en parametre
	 *  de suivreLigne() qui sera executée dans la methode run() du thread</p>
	 * 
	 * <p>Pour contourner ça, voici une classe qui permet de passer un parametre à un Runnable</p>
	 */
	abstract class ArgRunnableDuo implements Runnable {
		Object truc;
		public ArgRunnableDuo(Object truc) {
			this.truc = truc;
		}
	}



