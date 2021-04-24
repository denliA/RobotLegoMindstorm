package tests;
import capteurs.Couleur;
import capteurs.Ultrason;
import carte.Carte;
import carte.Point;
import carte.Robot;
import interfaceEmbarquee.Configurations;
import interfaceEmbarquee.Picker;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import moteurs.Pilote;

/**
 * <p>Situation initiale :
 * 		<ul>
 * 			<li>le robot est déposé au début d'une ligne de couleur quelconque</li>
 * 			<li>le palet est déposé sur une des 9 intersections des lignes de couleur</li>
 * 		</ul>
 * </p>
 * <p>Situation finale : le robot avance d’une certaine distance puis s’arrête lorsqu’il est à moins d’un mètre du palet. Il affiche sur l’écran quelle intersection contient le palet.</p>
 * <p>Si le palet n’est pas sur une intersection mais sur une partie quelconque de la ligne, le robot affiche un message adéquat.</p>
 * @see Couleur
 * @see Ultrason
 * @see carte
 */

public class NFBM6 implements interfaceEmbarquee.Lancable{
	Carte carte = Carte.carteUsuelle;
	Robot robot = carte.getRobot();
	public void lancer() {
		LCD.clear();
		new Picker("Colonne départ?", Configurations.departX, true).lancer();
		new Picker("Ligne départ ?", Configurations.departY, true).lancer();
		new Picker("Direction ?", Configurations.departD, true).lancer();
		
		robot.setPosition(Float.parseFloat(Configurations.departX.getVal()), Float.parseFloat(Configurations.departY.getVal()));
		robot.setDirection((Configurations.departD.getVal() == "porte" ? 90 : 270));
		float avancement = robot.getDirection() == 90 ? 1 : -1;
		int verifies=0; Point point;
		do {
			point = Pilote.verifierPalet(Pilote.DEVANT);
			if(point == Point.INCONNU) {
				Pilote.allerVersPoint(robot.getPosition().getX(), robot.getPosition().getY() + avancement);
			}
		} while(point == Point.INCONNU && verifies<3);
		
		LCD.drawString("Palet dans "+point, 3, 0);
		Button.waitForAnyPress();
		
	}
	
	public String getTitre() {
		return "NFBM6 - Capteur ultrason";
	}
	
}