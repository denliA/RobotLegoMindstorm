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
		ExecutorService executor2 = Executors.newSingleThreadExecutor();
		final double vitesse = 25;
		final double acceleration = 30;
		final double vitesse_angulaire = 180;
		double acceleration_angulaire = MouvementsBasiques.chassis.getAngularAcceleration();
		MouvementsBasiques.chassis.setAngularSpeed(vitesse_angulaire);
		MouvementsBasiques.chassis.setLinearSpeed(vitesse);
		MouvementsBasiques.chassis.setLinearAcceleration(acceleration);
		int scoredPalets=0;
		boolean droite=false;
		boolean gauche=false;
		boolean milieu=false;
		boolean touche=false;
		boolean couleurRepassee=false;
		double angle;
		
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
		
		executor2.execute(new ArgRunnableDuo(couleur) {
			public void run() {
				Pilote.suivreLigne((CouleurLigne) truc);
			}
		} );
		
		while(((touche=Toucher.getTouche())==false) && (Couleur.getLastCouleur()!=CouleurLigne.BLANCHE)); //on ne fait rien
		
		Pilote.SetSeDeplace(false); //arrete le suivi de ligne
		MouvementsBasiques.chassis.waitComplete();
		executor2.shutdown(); //on detruit le pool de l'executor2 pour liberer des ressources

	
		if(touche) { //si le robot vient de toucher un palet
			Pince.fermer();
			if(gauche){
				MouvementsBasiques.chassis.arc(); MouvementsBasiques.chassis.waitComplete();  //se decaler vers la gauche de la ligne
				angle = 90;
			}
			if(droite){
				MouvementsBasiques.chassis.arc(); MouvementsBasiques.chassis.waitComplete();  //se decaler vers la droite de la ligne
				angle = -90;
			}
			if(milieu){
				MouvementsBasiques.chassis.arc(); MouvementsBasiques.chassis.waitComplete();  //se decaler vers la droite de la ligne
				angle = -90;
			}
			MouvementsBasiques.chassis.travel(Double.POSITIVE_INFINITY); //comme un forward(), le robot avance tout droit tant qu'il n'est pas arrete
			while(Couleur.getLastCouleur()!=CouleurLigne.BLANCHE) { //peut etre vide a ajouter?
				couleurRepassee=(Couleur.getLastCouleur()==couleur?true:false); //la couleur de depart a ete detectee?
			}
			scoredPalets++;
			Pince.ouvrir();
			MouvementsBasiques.chassis.travel(-5); MouvementsBasiques.chassis.waitComplete(); //robot recule
			if (!couleurRepassee) {
				//tourner dans la direction contraire au decalage
				angle= -angle;
			}
			MouvementsBasiques.chassis.arc(0,angle); MouvementsBasiques.chassis.waitComplete(); //tourner de 90 degres vers la gauche ou la droite selon la position de depart
			Pilote.seRedresserSurLigne(couleur,true,40,80);	
			modeSolo.ModeSolo.ramasserPalet(nbPalets-scoredPalets, 3, 0, rougeAgauche);
			
		}
		else { //si le robot a atteint la ligne blanche de l'adversaire sans ramasser de palets
			if (gauche) {
				MouvementsBasiques.chassis.arc(0,90); MouvementsBasiques.chassis.waitComplete(); //tourne à gauche de 90 degres
				MouvementsBasiques.chassis.travel(50); MouvementsBasiques.chassis.waitComplete();  //avance de 50 cm;
				MouvementsBasiques.chassis.arc(0,90); MouvementsBasiques.chassis.waitComplete();  //tourne à gauche de 90 degres
				//se redresser sur ligne noire
				Pilote.seRedresserSurLigne(CouleurLigne.NOIRE,true,40,80);	
			}
			if (droite) {
				MouvementsBasiques.chassis.arc(0,-90); MouvementsBasiques.chassis.waitComplete();  //tourne à droite de 90 degres
				MouvementsBasiques.chassis.travel(50);  MouvementsBasiques.chassis.waitComplete(); //avance de 50 cm;
				MouvementsBasiques.chassis.arc(0,-90); MouvementsBasiques.chassis.waitComplete();  //tourne à droite de 90 degres
				//se redresser sur ligne noire
				Pilote.seRedresserSurLigne(CouleurLigne.NOIRE,true,40,80);		
			}
			if (milieu) {
				//robot va sur la ligne de gauche
				MouvementsBasiques.chassis.arc(0,90); MouvementsBasiques.chassis.waitComplete();  //tourne à gauche de 90 degres
				MouvementsBasiques.chassis.travel(50);  MouvementsBasiques.chassis.waitComplete();//avance de 50 cm;
				MouvementsBasiques.chassis.arc(0,90); MouvementsBasiques.chassis.waitComplete();  //tourne à gauche de 90 degres
				couleur = rougeAgauche? CouleurLigne.ROUGE : CouleurLigne.JAUNE;
				Pilote.seRedresserSurLigne(couleur,true,90,80);
			}
			modeSolo.ModeSolo.ramasserPalet(nbPalets, 3, 1, rougeAgauche); //une ligne a deja ete parcourue
		}
	}
}
	
	abstract class ArgRunnableDuo implements Runnable {
		Object truc;
		public ArgRunnableDuo(Object truc) {
			this.truc = truc;
		}
	}



