package tests;
import capteurs.Ultrason;
import capteurs.Toucher;
import carte.Carte;
import carte.Ligne;
import carte.Robot;
import interfaceEmbarquee.Configurations;
import interfaceEmbarquee.Picker;
import moteurs.MouvementsBasiques;
import moteurs.Pilote;
import moteurs.Pince;

/**
 * <p>Situation initiale : le robot est déposé sur n’importe quelle intersection de lignes de couleurs</p>
 * <p>Situation finale : le robot dépose le palet derrière la ligne blanche de l'adversaire et il ouvre ses pinces.</p>
 * <p>Ce test est réalisé avec le capteur d'ultrasons.</p>
 * @see Ultrason
 * @see MouvementsBasiques#chassis
 */

public class NFA6 implements interfaceEmbarquee.Lancable{
	
	Carte carte = Carte.carteUsuelle;
	Robot robot = carte.getRobot();
	
	public void lancer() {
		new Picker("Colonne départ?", Configurations.departX, true).lancer();
		new Picker("Ligne départ ?", Configurations.departY, true).lancer();
		new Picker("Direction ?", Configurations.departD, true).lancer();
		new Picker("Camp adverse ?", Configurations.campAdverse, true).lancer();
		
		robot.setPosition(Float.parseFloat(Configurations.departX.getVal()), Float.parseFloat(Configurations.departY.getVal()));
		robot.setDirection((Configurations.departD.getVal() == "porte" ? 90 : 270));
		
		Pilote.trouverPalet();
		Pilote.lancerSuivi((robot.getDirection()%180==0) ? Ligne.yToLongues.get(robot.getPosition().getY()) : Ligne.xToLongues.get(robot.getPosition().getX()));
		while(!Toucher.getTouche()) { /* rien */ }
		Pilote.arreterSuivi(); 
		Pince.fermer(500);
		Pilote.rentrer(Configurations.campAdverse.getVal() == "porte" ? 90 : 270);
		
	}
	
	public String getTitre() {
		return "NFA6 - Ramener palet ultrason";
	}
	
}