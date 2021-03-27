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
import capteurs.Capteur;

public class ModeSolo {
	private int positionDepart;
	public static void ramasserPalet(int nbPalets) throws OuvertureException, InterruptedException {
		new Capteur();
		boolean tient_palet = false;
		double vitesse = MouvementsBasiques.getVitesseRobot()/*/1.5*/;
		double acceleration = MouvementsBasiques.getAccelerationRobot();
		int scoredPalets = 0;
		boolean droite=false;
		boolean gauche=false;
		Pince.ouvrir();
		System.out.println("AVANTOuvert?" + Pince.getOuvert());
		if (Couleur.getCouleurLigne()==CouleurLigne.ROUGE)
			droite=true;
		else if(Couleur.getCouleurLigne()==CouleurLigne.NOIRE) {
			droite=true;
			gauche=true;
		}
		else if(Couleur.getCouleurLigne()==CouleurLigne.JAUNE)
			gauche=true;
		
		while(scoredPalets<nbPalets) {
			if (Toucher.getStatus()==false)
				Toucher.startScan();
			Thread t = new Thread() {
				public void run() {
					Pilote.suivreLigne();
				}
			};
			try{  
				t.start();  
			}catch(Exception e){
				System.out.println("Le thread de suivreLigne n'est pas lancé");
				System.out.println("Exception = "+e);
				return; //on sort de la fonction
			}
			while((tient_palet||Toucher.getTouche()==false)&&(Couleur.getCouleurLigne()!=CouleurLigne.BLANCHEP)&&(Couleur.getCouleurLigne()!=CouleurLigne.BLANCHEF)); //on ne fait rien
			Pilote.SetSeDeplace(false); //arrete le suivi de ligne
			//Toucher.stopScan();
//			try{  
//				t.interrupt();  
//			}catch(Exception e){
//				System.out.println("Le thread de suivreLigne n'est pas interrompu");
//				System.out.println("Exception = "+e);
//				return; //on sort de la fonction
//			}
			if(Toucher.getTouche()==true) {
				//MouvementsBasiques.arreter();
				Pince.fermer();
				//MouvementsBasiques.tourner(vitesse,acceleration,90);
				MouvementsBasiques.pilot.rotate(180);
				tient_palet = true;
			}
			else { //si le robot a atteint la ligne blanche d'en but
				//MouvementsBasiques.arreter();
				System.out.println("Pas de palet" + Couleur.getCouleurLigne());
				Pince.ouvrir();
				MouvementsBasiques.avancerTravel(vitesse,acceleration,-5); //robot recule
				MouvementsBasiques.tourner(vitesse,acceleration,90);
				scoredPalets++;
				tient_palet=false;
			}
		}
	}
	
}
