package tests;

import java.util.Arrays;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import capteurs.Capteur;
import capteurs.Couleur;
import capteurs.CouleurLigne;
import capteurs.Toucher;
import capteurs.Ultrason;
import carte.Carte;
import carte.Ligne;
import carte.Point;
import carte.Robot;
import exceptions.OuvertureException;
import lejos.hardware.lcd.LCD;
import lejos.robotics.chassis.Chassis;
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
 * @see Couleur;
 * @see Toucher;
 * @see Ultrason;
 */

public class P2 implements interfaceEmbarquee.Lancable{
	
	Chassis chassis = MouvementsBasiques.chassis;
	Carte carte = Carte.carteUsuelle;
	Robot robot = carte.getRobot();
	boolean saisirPalet = true;
	
	public void lancer() {
		new Capteur();
		Toucher.startScan();
		chassis.setLinearSpeed(20);
		Couleur.startScanAtRate(0);
		if(!Pince.getOuvert()) {
			Pince.ouvrir();
		}
		float avancement, bonne_direction=Float.NaN;
		boolean debut_noir;
		CouleurLigne ligne = Couleur.getLastCouleur();
		if(debut_noir = (ligne == CouleurLigne.NOIRE)) {
			chassis.travel(20); chassis.waitComplete();
			chassis.rotate(90); chassis.waitComplete();
			ligne = Pilote.chercheLigne(new Vector<CouleurLigne>(Arrays.asList(new CouleurLigne[] {CouleurLigne.ROUGE, CouleurLigne.JAUNE})), 20, 20, 20, false);
			chassis.travel(-15); chassis.waitComplete(); Pilote.tournerJusqua(ligne, true, 50, 0, 15); Pilote.tournerJusqua(ligne, false, 50, 0, 15);
			carte.getRobot().setPosition(ligne == CouleurLigne.ROUGE ? -1 : 1, ligne == CouleurLigne.ROUGE ? -2 : 2);
			carte.getRobot().setDirection(ligne == CouleurLigne.ROUGE ? 90 : 270);
			bonne_direction = robot.getDirection();
		}
		
		
		Ultrason.setDistance();
		if(Ultrason.getDistance()<= .64f && Ultrason.getDistance()>= .40f) {
			new ArgSuivi(ligne).start();
			while(!Toucher.getTouche()) {
				//rien
			}
			Pince.fermer(200);
			Pilote.SetSeDeplace(false); chassis.waitComplete();
			chassis.travel(Float.POSITIVE_INFINITY);
			Couleur.blacheTouchee();
			while(!Couleur.blacheTouchee());
			chassis.stop();
			Pince.ouvrir();
			return;
		}
		
		
		if(!debut_noir) {
			Pilote.lancerSuivi(ligne);
			CouleurLigne inters;
			while((inters=Couleur.getLastCouleur())!=CouleurLigne.VERTE && inters != CouleurLigne.BLEUE);
			Pilote.arreterSuivi();
			carte.calibrerPosition(ligne, CouleurLigne.BLANCHE, inters);
			avancement = robot.getDirection() == 90 ? 1 : -1; 
			bonne_direction = robot.getDirection();
		}
		
		else {
			avancement = robot.getDirection() == 90 ? 1 : -1; 
			Pilote.allerVersPoint(robot.getPosition().getX(), robot.getPosition().getY()+avancement);
		}
		Point point = Pilote.trouverPalet();
		if(point == Point.INCONNU) {
			LCD.drawString("Pas trouve!", 0, 3);
		}
		else {
			if(!Pince.getOuvert()) Pince.ouvrir();
			Pilote.lancerSuivi((robot.getDirection()%180==0) ? Ligne.yToLongues.get(robot.getPosition().getY()) : Ligne.xToLongues.get(robot.getPosition().getX()));
			while(!Toucher.getTouche()) { /* rien */ }
			Pilote.arreterSuivi(); 
			Pince.fermer(); 
			Pilote.rentrer(avancement == 1 ? 90 : 270);
			Pince.ouvrir();
		}
//		System.out.println("[Ultrason P2] Position : " + robot);
//		
//		Point palet_trouve;
//		palet_trouve = Pilote.verifierPalet();
//		int essais = 1;
//		while(palet_trouve == Point.INCONNU&&essais<3) {
//			Pilote.allerVersPoint(robot.getPosition().getX(), robot.getPosition().getY()+1);
//			palet_trouve = Pilote.verifierPalet();
//			essais++;
//		}
//		new ArgSuivi((robot.getDirection()%180==0) ? Ligne.yToLongues.get(robot.getPosition().getY()) : Ligne.xToLongues.get(robot.getPosition().getX())).start();
//		while(!Toucher.getTouche());
//		Pilote.arreterSuivi();
//		Pince.fermer();
//		System.out.println("[P2Ultrason] pour rentrer : " + bonne_direction + " Mais on est à " + robot.getDirection());
//		chassis.rotate((bonne_direction-robot.getDirection())%360);
//		chassis.waitComplete();
//		chassis.travel(Float.POSITIVE_INFINITY);
//		Couleur.blacheTouchee();
//		while(!Couleur.blacheTouchee());
//		chassis.stop();
//		Pince.ouvrir();
//		return;
		
		
	}
	
	public String getTitre() {
		return "P2";
	}
	
}

/**
 * <p>Dans un thread, on n'a pas acces a des variables d'instances d'autres classes or ici, on a besoin de passer la couleur en parametre
 *  de suivreLigne() qui sera executée dans la methode run() du thread</p>
 * 
 * <p>Pour contourner ça, voici une classe qui permet de passer un parametre à un Runnable</p>
 */
abstract class ArgRunnable implements Runnable {
	Object truc;
	public ArgRunnable(Object truc) {
		this.truc = truc;
	}
}