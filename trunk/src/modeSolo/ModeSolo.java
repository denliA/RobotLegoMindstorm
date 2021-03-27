package modeSolo;
import moteurs.MouvementsBasiques;
import moteurs.Pince;
import moteurs.Pilote;
import capteurs.Capteur;
import capteurs.Couleur;
import capteurs.CouleurLigne;
import capteurs.Toucher;
import exceptions.OuvertureException;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


class Task implements Runnable{
	public void run() {
		Pilote.suivreLigne();
	}
}

public class ModeSolo {
	private int positionDepart;
	public static void ramasserPalet(int nbPalets) throws Exception, InterruptedException {
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
		Pince.ouvrir();
		System.out.println("AVANTOuvert?" + Pince.getOuvert());
		if (Couleur.getCouleurLigne()==CouleurLigne.ROUGE)
			droite=true;
		else if(Couleur.getCouleurLigne()==CouleurLigne.NOIRE) {
			milieu=true;
		}
		else if(Couleur.getCouleurLigne()==CouleurLigne.JAUNE)
			gauche=true;
		CouleurLigne couleur = Couleur.getCouleurLigne();
		while((scoredPalets<nbPalets)||(lignesParcourues==3)) {
			tient_palet=false; //pour voir si le robot a touché le palet pendant son aller
			trio=0;
			while(trio<3) { //pour rammasser les 3 palets sur une ligne de couleur
				if (Toucher.getStatus()==false)
					Toucher.startScan();
				executor.execute(new Task());
				while((tient_palet || Toucher.getTouche()==false)&&(Couleur.getCouleurLigne()!=CouleurLigne.BLANCHEP)&&(Couleur.getCouleurLigne()!=CouleurLigne.BLANCHEF)); //on ne fait rien
				Pilote.SetSeDeplace(false); //arrete le suivi de ligne
				if(Toucher.getTouche()) {
					tient_palet=true;
					Pince.fermer();
					MouvementsBasiques.tourner(180); //demi-tour
					Pilote.seRedresserSurLigne(couleur, true, 45, 1000);
				}else if (tient_palet){ //si le robot a atteint sa ligne blanche d'en but et qu'il a ramassé un palet
					Pince.ouvrir();
					scoredPalets++;
					trio++;
					if(trio<3) {
						MouvementsBasiques.avancerTravel(vitesse,acceleration,-5); //robot recule
						MouvementsBasiques.tourner(180); //demi-tour
						Pilote.seRedresserSurLigne(couleur, true, 45, 1000);
					}else
						lignesParcourues++;
				}else { //si le robot a atteint la ligne blanche de l'adversaire sans ramasser de palets
					lignesParcourues++;
					MouvementsBasiques.tourner(180);
					Pilote.seRedresserSurLigne(couleur, true, 45, 1000);
				}
			}
			if (gauche) {
				MouvementsBasiques.tourner(vitesse,acceleration,90); //tourne à gauche de 90 degres
				MouvementsBasiques.avancerTravel(vitesse,acceleration,50); //avance de 50 cm;
				MouvementsBasiques.tourner(vitesse,acceleration,-90); //tourne à droite de 90 degres
				//se redresser sur ligne noire
				if (lignesParcourues==1) {
					try {
						Pilote.seRedresserSurLigne(CouleurLigne.NOIRE,true,90,1500);
					}
					catch (Exception e) {
						System.out.println("Prob pour seRedresserSurLigne");
					}
				}
				else if (lignesParcourues==2) {
					try {
						Pilote.seRedresserSurLigne(CouleurLigne.ROUGE,true,90,1500);
					}
					catch (Exception e) {
						System.out.println("Prob pour seRedresserSurLigne");
					}
				}
			}
			if (droite) {
				MouvementsBasiques.tourner(vitesse,acceleration,-90); //tourne à droite de 90 degres
				MouvementsBasiques.avancerTravel(vitesse,acceleration,50); //avance de 50 cm;
				MouvementsBasiques.tourner(vitesse,acceleration,90); //tourne à gauche de 90 degres
				if (lignesParcourues==1) {
					try {
						Pilote.seRedresserSurLigne(CouleurLigne.NOIRE,true,90,1500);
					}
					catch (Exception e) {
						System.out.println("Prob pour seRedresserSurLigne");
					}
				}
				else if (lignesParcourues==2) {
					try {
						Pilote.seRedresserSurLigne(CouleurLigne.JAUNE,true,90,1500);
					}
					catch (Exception e) {
						System.out.println("Prob pour seRedresserSurLigne");
					}
				}		
			}
			if (milieu) {
				if (lignesParcourues==1) {
					MouvementsBasiques.tourner(vitesse,acceleration,90); //tourne à gauche de 90 degres
					MouvementsBasiques.avancerTravel(vitesse,acceleration,50); //avance de 50 cm;
					MouvementsBasiques.tourner(vitesse,acceleration,-90); //tourne à droite de 90 degres
					try {
						Pilote.seRedresserSurLigne(CouleurLigne.ROUGE,true,90,1500);
					}
					catch (Exception e) {
						System.out.println("Prob pour seRedresserSurLigne");
					}
				}else if(lignesParcourues==2) {
					MouvementsBasiques.tourner(vitesse,acceleration,-90); //tourne à droite de 90 degres
					MouvementsBasiques.avancerTravel(vitesse,acceleration,100); //avance de 100 cm;
					MouvementsBasiques.tourner(vitesse,acceleration,90); //tourne à gauche de 90 degres
					try {
						Pilote.seRedresserSurLigne(CouleurLigne.JAUNE,true,90,1500);
					}
					catch (Exception e) {
						System.out.println("Prob pour seRedresserSurLigne");
					}
				}
			}
		}	
	}	
}

	

