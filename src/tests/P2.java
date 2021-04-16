package tests;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import capteurs.Capteur;
import capteurs.Couleur;
import capteurs.CouleurLigne;
import capteurs.Toucher;
import capteurs.Ultrason;
import exceptions.OuvertureException;
import moteurs.MouvementsBasiques;
import moteurs.Pilote;
import moteurs.Pince;

/**
 * <p>Situation initiale :
 * 		<ol>
 * 			<li>Un palet est déposé au hasard sur une des 9 intersections de la table</li>
 * 			<li>Le robot est déposé au hasard sur une de ses trois positions de départ du camp Est ou Ouest</li>
 * 		</ol>
 * </p>
 * 
 * <p>Situation finale : Le robot franchit la ligne blanche du camp adverse avec le palet, s'arrête et ouvre ses pinces.</p>
 * @see capteurs#Ultrason
 */

public class P2 implements interfaceEmbarquee.Lancable{
	
	public void lancer() {
		new Capteur();
		Toucher.startScan();
		Ultrason.startScan();
		Couleur.startScanAtRate(0);
		if(!Pince.getOuvert()){
			try {
				Pince.ouvrir();
			} catch (OuvertureException e) {
				System.out.println("Prob pour ouvrir pince");
				e.printStackTrace();
			}
		}
		
		CouleurLigne couleur;
		boolean rougeAgauche;
		boolean touche=false;
		boolean goToBlack=false;
		couleur = Couleur.getLastCouleur();
		ExecutorService executor1 = Executors.newSingleThreadExecutor();
		Runnable r = new ArgRunnable(couleur) {
			public void run() {
				Pilote.suivreLigne((CouleurLigne) truc);
			}
		};
		executor1.submit(r);
		while(((Couleur.getLastCouleur())!=CouleurLigne.VERTE)) {
			if(Couleur.getLastCouleur()==CouleurLigne.NOIRE) {
				goToBlack=true;
			}
			if (Toucher.getTouche()==true) {
				touche=true;
				try {
					Pince.fermer();
				} catch (OuvertureException e) {
					System.out.println("Prob pour fermer pince");
					e.printStackTrace();
				}
			}
		}
		Pilote.SetSeDeplace(false); //arrete le suivi de ligne
		MouvementsBasiques.chassis.waitComplete();
		
		if (goToBlack) {
			rougeAgauche=true;
		}
		else {
			rougeAgauche=false;
		}
		if (touche) {
			MouvementsBasiques.chassis.travel(100); //robot avance
			while(!Couleur.blacheTouchee());
			MouvementsBasiques.chassis.stop();
			//MouvementsBasiques.chassis.waitComplete();
			try {
				Pince.ouvrir();
			} catch (OuvertureException e) {
				System.out.println("Prob pour ouvrir pince");
				e.printStackTrace();
			}
			Toucher.stopScan();
			Ultrason.stopScan();
			Couleur.stopScan();
			Pilote.stopVide();
		}
		else {
			//la ligne verte a été detectée a la première intersection
			if(!rougeAgauche) {
				try {
					modeCompetition.ModeCompetition.ramasserPalet(1, rougeAgauche);
				} catch (OuvertureException e) {
					System.out.println("Prob pour ouvrir pince");
					e.printStackTrace();
				}
			}
			//la ligne verte a été detectée a la troisième intersection
			else {
				MouvementsBasiques.chassis.travel(30);
				//faire demi-tour
				Pilote.tournerJusqua(couleur,true,250); //tourne jusqu'à la couleur
				Pilote.tournerJusqua(couleur, false, 50,50); //se remet bien sur la ligne si on la dépasse
				try {
					//appelle le ramasserPalet du modeSolo sachant qu'une ligne a été parcourue sans ramasser de palet
					modeSolo.ModeSolo.ramasserPalet(1,1,true,false,!rougeAgauche);
				} catch (OuvertureException e) {
					System.out.println("Prob pour ouvrir pince");
					e.printStackTrace();
				}
			}
		}
		executor1.shutdown();
	}
	
	public String getTitre() {
		return "P2";
	}
	
}

abstract class ArgRunnable implements Runnable {
	Object truc;
	public ArgRunnable(Object truc) {
		this.truc = truc;
	}
}