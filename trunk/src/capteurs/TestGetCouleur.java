package capteurs;

import java.text.SimpleDateFormat;
import java.util.Date;

import lejos.hardware.Button;
import moteurs.MouvementsBasiques;
public class TestGetCouleur {

	public static void main(String[] args) {
		int button = -1;
		boolean buffer = true;
		while(button != Button.ID_ESCAPE) {
			if (buffer)
				testGetLastCouleur();
			else 
				collectionnerDonnees("./Scans/20210415/NoirePorteFenetre_", false, 20);
			buffer=!buffer;
			button = Button.waitForAnyPress();
		}

	}
	
	
	public static void testGetCouleurLigne() {
		int button = -1;
		float[] res, res2;
		CouleurLigne couleur;
		Couleur.setScanMode(Couleur.RGBMODE);
		Couleur.startScanAtRate(10);
		while (button != Button.ID_LEFT && button != Button.ID_RIGHT) {
			button = Button.waitForAnyPress();
			if (button == Button.ID_ENTER) {
				couleur = Couleur.getLastCouleur();
				res = Couleur.getRGB();
				res2 =Couleur.getRatios();
				System.out.println(Float.toString(res[0]));
				System.out.println(Float.toString(res[1]));
				System.out.println(Float.toString(res[2]));
				System.out.println(Float.toString(res2[0]));
				System.out.println(Float.toString(res2[1]));
				System.out.println(Float.toString(res2[2]));
				System.out.println();
				System.out.println(couleur.toString()+"\n");
			}
			Button.waitForAnyEvent();
		}
	}
	
	public static void testGetLastCouleur() {
		int button = -1;
		Couleur.BufferContexte contexte;
		Couleur.setScanMode((byte) (Couleur.BUFFERING|Couleur.RGBMODE));
		Couleur.startScanAtRate(0);
		while (button != Button.ID_LEFT && button != Button.ID_RIGHT) {
			button = Button.waitForAnyPress();
			if (button == Button.ID_ENTER) {
				contexte = Couleur.buffer.getLast();
				System.out.println(contexte+ "\n");
				//System.out.println("Couleurs touchées: " + contexte.couleur_x + (contexte.intersection_x == null ? "" : contexte.intersection_x) + "\n");
				//System.out.println(Arrays.asList(Couleur.buffer.historique(10)));
			}
			Button.waitForAnyEvent();
		}
	}
	
	public static void collectionnerDonnees(String prefixeFichier, boolean tourner, int vitesse) {
		new Capteur();
		Couleur.setScanMode((byte) (Couleur.BUFFERING|Couleur.RGBMODE));
		Couleur.startScanAtRate(0);
		Date date = new Date() ;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss") ;
		//SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyymmdd");
		MouvementsBasiques.chassis.setLinearSpeed(vitesse);
		MouvementsBasiques.chassis.setAngularAcceleration(10);
		if(tourner)
			MouvementsBasiques.chassis.rotate(360);
		else
			MouvementsBasiques.chassis.travel(Float.POSITIVE_INFINITY);
		while(Button.ENTER.isUp())
			;
		MouvementsBasiques.chassis.stop(); MouvementsBasiques.chassis.waitComplete();
		Couleur.buffer.toCSV(prefixeFichier+dateFormat.format(date));
	}

}
