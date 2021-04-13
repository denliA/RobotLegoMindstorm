package modeCompetition;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import capteurs.Capteur;
import capteurs.Couleur;
import capteurs.CouleurLigne;
import capteurs.Toucher;
import capteurs.Ultrason;
import exceptions.EchecGarageException;
import exceptions.OuvertureException;
import modeSolo.ModeSolo;
import moteurs.MouvementsBasiques;
import moteurs.Pilote;
import moteurs.Pince;

public class ModeCompetition {
	public static void ramasserPalet(int nbPalets,boolean rougeAgauche) throws EchecGarageException, InterruptedException, OuvertureException {
		new Capteur();
		Toucher.startScan();
		Ultrason.startScan();
		Couleur.startScanAtRate(0);
		
		double angle;
		boolean droite=false;
		boolean gauche=false;
		boolean milieu=false;
		boolean touche=false;
		boolean couleurRepassee=false;
		CouleurLigne couleur2;
		Thread t1;
		
		final double vitesse = 25;
		final double acceleration = 30;
		final double vitesse_angulaire = 180;
		double acceleration_angulaire = MouvementsBasiques.chassis.getAngularAcceleration();
		
		MouvementsBasiques.chassis.setAngularSpeed(vitesse_angulaire);
		MouvementsBasiques.chassis.setLinearSpeed(vitesse);
		MouvementsBasiques.chassis.setLinearAcceleration(acceleration);
		
		if(!Pince.getOuvert()){
			Pince.ouvrir();
		}
		CouleurLigne couleur = Couleur.getLastCouleur();
		System.out.println(couleur);
		
		t1 = new Thread(new ArgRunnableDuo(couleur) {
			public void run() {
				Pilote.suivreLigne((CouleurLigne) truc);
			}
		} );
		
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
		
		t1.start();
		
		while(((touche=Toucher.getTouche())==false) && (Couleur.getLastCouleur()!=CouleurLigne.BLANCHE)); //on ne fait rien
		
		Pilote.SetSeDeplace(false); //arrete le suivi de ligne
		MouvementsBasiques.chassis.waitComplete();
		
	
		if(touche) { //si le robot vient de toucher un palet
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
				MouvementsBasiques.chassis.rotate(angle); MouvementsBasiques.chassis.waitComplete();  
				MouvementsBasiques.chassis.travel(25); MouvementsBasiques.chassis.waitComplete();  
				MouvementsBasiques.chassis.rotate(-angle);	MouvementsBasiques.chassis.waitComplete(); 
			}
			MouvementsBasiques.chassis.travel(Double.POSITIVE_INFINITY); //comme un forward(), le robot avance tout droit tant qu'il n'est pas arrete
			while(Couleur.getLastCouleur()!=CouleurLigne.BLANCHE) { //peut etre vide a ajouter?
				couleurRepassee=(Couleur.getLastCouleur()==couleur?true:false); //la couleur de depart a ete detectee?
			}
			MouvementsBasiques.chassis.stop();
			Pince.ouvrir();
			MouvementsBasiques.chassis.travel(-5); MouvementsBasiques.chassis.waitComplete(); //robot recule
			//si le robot n'est pas repassé sur la ligne de couleur de depart
			if (!couleurRepassee) {
				//tourner dans la direction contraire au decalage
				angle=-angle;
			}
			MouvementsBasiques.chassis.rotate(angle); MouvementsBasiques.chassis.waitComplete();
			Pilote.chercheLigne(couleur,vitesse,acceleration,vitesse_angulaire,true); //se gare sur la couleur initiale
			modeSolo.ModeSolo.ramasserPalet(nbPalets-1, 3, 0, 1, !rougeAgauche);
			
		}
		else { //si le robot a atteint la ligne blanche de l'adversaire sans ramasser de palets
			if (gauche) {
				angle = 100;
				MouvementsBasiques.chassis.rotate(angle); MouvementsBasiques.chassis.waitComplete();
				couleur2 = rougeAgauche? CouleurLigne.ROUGE : CouleurLigne.JAUNE;
				Pilote.chercheLigne(couleur2,vitesse,acceleration,vitesse_angulaire,true);
			}
			if (droite) {
				angle = -100;
				MouvementsBasiques.chassis.rotate(angle); MouvementsBasiques.chassis.waitComplete();
				couleur2 = rougeAgauche? CouleurLigne.JAUNE : CouleurLigne.ROUGE;
				Pilote.chercheLigne(couleur2,vitesse,acceleration,vitesse_angulaire,false);
			}
			if (milieu) {
				//se decaler vers la gauche
				angle = (rougeAgauche? 100 : -100);
				MouvementsBasiques.chassis.rotate(angle); MouvementsBasiques.chassis.waitComplete(); 
				couleur2 = rougeAgauche? CouleurLigne.ROUGE : CouleurLigne.JAUNE;
				Pilote.chercheLigne(couleur2,vitesse,acceleration,vitesse_angulaire,true); 
			}
			modeSolo.ModeSolo.ramasserPalet(nbPalets, 3, 0, 0, !rougeAgauche); //une ligne a deja ete parcourue mais c pas grave
		}
	}
}
	
	abstract class ArgRunnableDuo implements Runnable {
		Object truc;
		public ArgRunnableDuo(Object truc) {
			this.truc = truc;
		}
	}



