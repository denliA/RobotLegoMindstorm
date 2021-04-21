package tests;

import moteurs.MouvementsBasiques;

/**
 * <p>Situation initiale : le robot est déposé sur n’importe quelle intersection de lignes de couleurs</p>
 * <p>Situation finale : le robot dépose le palet derrière la ligne blanche de l'adversaire et il ouvre ses pinces.</p>
 * <p>Ce test est realisé avec le capteur d'ultrasons.</p>
 * @see capteurs#Ultrason
 * @see MouvementsBasiques#chassis
 */

public class NFA6 implements interfaceEmbarquee.Lancable{
	
	public void lancer() {
	}
	
	public String getTitre() {
		return "NFA6 - Ramener palet ultrason";
	}
	
}