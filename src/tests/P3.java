package tests;

import capteurs.Capteur;
import capteurs.Couleur;
import capteurs.CouleurLigne;
import carte.Ligne;
import carte.Point;
import interfaceEmbarquee.Lancable;
import lejos.hardware.Button;
import lejos.robotics.chassis.Chassis;

import java.util.Arrays;
import java.util.Vector;

import capteurs.*;
import carte.Robot;
import carte.Carte;
import moteurs.*;

/**
 * <p> Le robot est posé sur la table mais pas sur une ligne, doit ramener le palet pose a une intersection dans le camp adverse</p>
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
		
		float campRentree = choixDirection();
		new Capteur(); Couleur.startScanAtRate(0); Toucher.startScan();
		if(!Pince.getOuvert()) Pince.ouvrir();
		MouvementsBasiques.chassis.setLinearAcceleration(10);
		MouvementsBasiques.chassis.setLinearSpeed(20);
		
		
		Ultrason.setDistance();
		if(Ultrason.getDistance() < .6f) {
			chassis.rotate(180); chassis.waitComplete();
		}
		Toucher.aToucheAUnMoment();
		carte.calibrerPosition();
		if (Toucher.aToucheAUnMoment()) {
			Pince.fermer();
		}
		else {
		
			
			if(!Pince.getOuvert()) Pince.ouvrir();
			Pilote.trouverPalet();
			Pilote.lancerSuivi((robot.getDirection()%180==0) ? Ligne.yToLongues.get(robot.getPosition().getY()) : Ligne.xToLongues.get(robot.getPosition().getX()));
			while(!Toucher.getTouche()) { /* rien */ }
			Pilote.arreterSuivi(); 
			Pince.fermer(); 
		}
		if(Float.isNaN(campRentree)) {
			System.out.println("[P3] Direction avant de rentrer : " + robot.getDirection());
			Pilote.rentrer(robot.getPosition().getY() < 0 ? 270 : 90); Pince.ouvrir();
		}
		else {
			Pilote.rentrer(campRentree);
		}
		
		Pince.ouvrir();
		
	}
	
	
	
	public float choixDirection() {
		return Float.NaN;
	}

	
	
	public static void main(String[] args) {
		new P4().lancer();
//		new P3().P2_Ultrason();
		
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
