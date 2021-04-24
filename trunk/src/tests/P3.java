package tests;

import capteurs.Capteur;
import capteurs.Couleur;
import capteurs.CouleurLigne;
import carte.Ligne;
import carte.Point;
import interfaceEmbarquee.Lancable;
import lejos.hardware.Sound;
import lejos.robotics.chassis.Chassis;
import modeSolo.ModeSolo;

import java.util.Arrays;
import java.util.Vector;

import capteurs.*;
import carte.Robot;
import carte.Carte;
import moteurs.*;

/**
 * <p>Le robot est posé sur la table mais pas sur une ligne, doit ramener le palet pose a une intersection dans le camp adverse</p>
 * 
 */

public class P3 implements Lancable {



	@Override
	public String getTitre() {
		return ("P3");
	}
	Carte carte  = Carte.carteUsuelle;
	Robot robot = carte.getRobot();
	Chassis chassis = MouvementsBasiques.chassis;

	
	/**
	 * Contient le code du test :
	 * <ul>
	 * <li> Le robot repere le palet et l'attrape @see PaletUltrason
	 * <li> Le robot repere une ligne de couleur et s'oriente avec @see Couleur
	 * <li> Le robot se dirige vers le camp adverse
	 * </ul>
	 */
	@Override	
	public void lancer() {
		
		new Capteur(); Couleur.startScanAtRate(0); Toucher.startScan();
		if(Pince.getOuvert()) Pince.fermer();
		
		
		carte.calibrerPosition();
//		Pilote.rentrer(""); (Pas besoin pour trouver palet??)
		Pilote.trouverPalet();
		Pilote.lancerSuivi((robot.getDirection()%180==0) ? Ligne.yToLongues.get(robot.getPosition().getY()) : Ligne.xToLongues.get(robot.getPosition().getX()));
		while(!Toucher.getTouche()) { /* rien */ }
		Pilote.arreterSuivi(); 
		Pince.fermer(500);
		Pilote.rentrer(robot.getPosition().getY() < 0 ? 270 : 90);
//		ModeSolo.ramasserPalet(1, carte.getRobot().getDirection()==90); Valeur sûre mais met trop de temps.
		
	}
	
//TODO à remplacer dans P2	
	public void P2_Ultrason() {
		new Capteur();
		Toucher.startScan();
		chassis.setLinearSpeed(20);
		Couleur.startScanAtRate(0);
		if(!Pince.getOuvert()) {
			Pince.ouvrir();
		}
		float avancement;
		boolean debut_noir;
		CouleurLigne ligne = Couleur.getLastCouleur();
		if(debut_noir = (ligne == CouleurLigne.NOIRE)) {
			chassis.travel(20); chassis.waitComplete();
			chassis.rotate(90); chassis.waitComplete();
			ligne = Pilote.chercheLigne(new Vector<CouleurLigne>(Arrays.asList(new CouleurLigne[] {CouleurLigne.ROUGE, CouleurLigne.JAUNE})), 20, 20, 20, false);
			chassis.travel(-15); chassis.waitComplete(); Pilote.tournerJusqua(ligne, true, 50, 0, 15); Pilote.tournerJusqua(ligne, false, 50, 0, 15);
			carte.getRobot().setPosition(ligne == CouleurLigne.ROUGE ? -1 : 1, ligne == CouleurLigne.ROUGE ? -2 : 2);
			carte.getRobot().setDirection(ligne == CouleurLigne.ROUGE ? 90 : 270);
		}
		
		float bonne_direction = robot.getDirection();
		
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
			new ArgSuivi(ligne).start();
			CouleurLigne inters;
			while((inters=Couleur.getLastCouleur())!=CouleurLigne.VERTE && inters != CouleurLigne.BLEUE);
			Pilote.SetSeDeplace(false); chassis.waitComplete();
			carte.calibrerPosition(ligne, CouleurLigne.BLANCHE, inters);
			avancement = robot.getDirection() == 90 ? 1 : -1; 
		}
		else {
			avancement = robot.getDirection() == 90 ? 1 : -1; 
			Pilote.allerVersPoint(robot.getPosition().getX(), robot.getPosition().getY()+avancement);
		}
		
		System.out.println("[Ultrason P2] Position : " + robot);
		
		Point palet_trouve;
		palet_trouve = Pilote.verifierPalet();
		int essais = 1;
		while(palet_trouve == Point.INCONNU&&essais<3) {
			Pilote.allerVersPoint(robot.getPosition().getX(), robot.getPosition().getY()+1);
			palet_trouve = Pilote.verifierPalet();
			essais++;
		}
		new ArgSuivi((robot.getDirection()%180==0) ? Ligne.yToLongues.get(robot.getPosition().getY()) : Ligne.xToLongues.get(robot.getPosition().getX())).start();
		while(!Toucher.getTouche());
		Pilote.SetSeDeplace(false); chassis.waitComplete();
		Pince.fermer();
		System.out.println("[P2Ultrason] pour rentrer : " + bonne_direction + " Mais on est à " + robot.getDirection());
		chassis.rotate(90);
		chassis.waitComplete();
//		chassis.rotate((robot.getDirection()-bonne_direction)%360);
//		chassis.waitComplete();
		chassis.travel(Float.POSITIVE_INFINITY);
		Couleur.blacheTouchee();
		while(!Couleur.blacheTouchee());
		chassis.stop();
		Pince.ouvrir();
		return;
		
		
		
			
		
		
	}
	
	
	public static void main(String[] args) {
//		new P3().lancer();
		new P3().P2_Ultrason();
		
	}

}

/**
 * <p>Dans un thread, on n'a pas acces a des variables d'instances d'autres classes or ici, on a besoin de passer la couleur en parametre
 *  de suivreLigne() qui sera executée dans le thread</p>
 * 
 * <p>Pour contourner ça, voici une classe qui permet de passer un parametre à un Thread</p>
 */

class ArgSuivi extends Thread{
	CouleurLigne ligne;
	public ArgSuivi(CouleurLigne ligne) {
		this.ligne = ligne;
	}
	
	@Override
	public void run() {
		Pilote.suivreLigne(ligne);
	}
}
