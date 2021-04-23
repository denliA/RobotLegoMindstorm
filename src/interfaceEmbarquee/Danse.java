package interfaceEmbarquee;

import capteurs.Couleur;
import exceptions.OuvertureException;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;
import moteurs.MouvementsBasiques;
import moteurs.Pince;
import modeSolo.ModeSolo;
import modeCompetition.ModeCompetition;

/**
 * <p>Classe qui contient les danses.</p>
 * <p>Elles sont lancées en fin de partie dans le ModeSolo et le ModeCompetition. On peut aussi les lancer dans le menu "Regalages" de l'interface textuelle.</p>
 * 
 * @see ModeSolo
 * @see ModeCompetition
 * @see InterfaceTextuelle
 * 
 */
public class Danse {
	
	/**
	 * <p>Lance le bruitage choisi dans le picker.</p>
	 * 
	 * @see Picker
	 */
	public static void startDance(){
		startDance(Configurations.bruitage.getVal());
	}
	
	/**
	 * <p>Lance la danse passée en paramètre. Seuls deux dances ont été codées : victoire et défaite.</p>
	 * 
	 *	@param name
	 *				nom de la danse
	 */
	public static void startDance(final String name) {
		LCD.clear();
		LCD.drawString("Dance time", 3, 3);
		if (name=="victoire") {
			victoire();
		}
		else if(name=="defaite") {
			defaite();
		}		
	}
	
	/**
	 * <p>Danse en cas de victoire. Lancée si le robot a ramassé plus de la moitié des palets demandés (9 en général).</p>
	 * 
	 */
	public static void victoire() {
		long debut;
		//durée de la musique de victoire
		long fin = 23*1000;
		double speed = MouvementsBasiques.chassis.getLinearSpeed();
		double acceleration = MouvementsBasiques.chassis.getLinearAcceleration();
		MouvementsBasiques.chassis.setLinearSpeed(speed);
		//on l'eloigne de la ligne blanche
		MouvementsBasiques.chassis.travel(100);
		//Pilote.allerVersPoint(0, 0);
		
		//lance la musique dans un thread
		Musique.startMusic("VictorySong.wav");
		//sauvegarde le temps actuel en millisecondes sur l'horloge de l'ordinateur
		debut = System.currentTimeMillis();
		//on attend le debut de la musique
		Delay.msDelay(2000);
		//le robot danse tant que la musique n'a pas fini de jouer en intégralité
		while(System.currentTimeMillis()-debut<fin) {
			/* Sens normal */
			//mouvements brusques
			MouvementsBasiques.chassis.setLinearSpeed(speed*2);
			MouvementsBasiques.chassis.setLinearAcceleration(acceleration*2);
			MouvementsBasiques.chassis.travel(5); MouvementsBasiques.chassis.waitComplete();
			MouvementsBasiques.chassis.travel(-5); MouvementsBasiques.chassis.waitComplete();
			//mouvements lents
			MouvementsBasiques.chassis.setLinearSpeed(speed/10);
			MouvementsBasiques.chassis.setLinearAcceleration(acceleration/10);
			//on ouvre les pinces si elles ne sont pas déjà ouvertes
			try {
				Pince.ouvrir();
			}
			catch(OuvertureException e) {
				; //on ignore l'exception levée par l'ouverture des pinces
			}
			MouvementsBasiques.chassis.rotate(360); MouvementsBasiques.chassis.waitComplete();
			
			/* Sens inverse */
			//mouvements brusques
			MouvementsBasiques.chassis.setLinearSpeed(speed*2);
			MouvementsBasiques.chassis.setLinearAcceleration(acceleration*2);
			MouvementsBasiques.chassis.travel(-5); MouvementsBasiques.chassis.waitComplete();
			MouvementsBasiques.chassis.travel(5); MouvementsBasiques.chassis.waitComplete();
			//mouvements lents
			MouvementsBasiques.chassis.setLinearSpeed(speed/10);
			MouvementsBasiques.chassis.setLinearAcceleration(acceleration/10);
			//on ferme les pinces si elles ne sont pas déjà fermées
			try {
				Pince.fermer();
			}
			catch(OuvertureException e) {
				; //on ignore l'exception levée par la fermeture des pinces
			}
			MouvementsBasiques.chassis.rotate(-360); MouvementsBasiques.chassis.waitComplete();
		}
		//on remet les vitesses normales
		MouvementsBasiques.chassis.setLinearSpeed(speed);
		MouvementsBasiques.chassis.setLinearAcceleration(acceleration);
	}
	
	/**
	 * <p>Danse en cas de défaite. Lancée si le robot a ramassé moins de la moitié des palets demandés (9 en général).</p>
	 * 
	 */
	public static void defaite() {
		//instancier les capteurs. Sinon vide pas détecté à temps
		new capteurs.Capteur();
		//lance la musique dans un thread
		Musique.startMusic("LosingSong.wav");
		//début des mesures effectuées par le capteur de couleur
		Couleur.startScanAtRate(10);
		double speed = MouvementsBasiques.chassis.getLinearSpeed();
		//tourne à droite
		MouvementsBasiques.chassis.rotate(-90);
		MouvementsBasiques.chassis.setLinearSpeed(speed/4);
		//on reinitialise le boolean qui indique si le vide a été détecté
		Couleur.videTouche();
		//avance jusqu'à l'appel de la méthode stop du chassis
		MouvementsBasiques.chassis.travel(Double.POSITIVE_INFINITY);
		while(!Couleur.videTouche()) {
			//on continue d'avancer tout droit tant que le vide n'est pas détécté
		}
		//forte accéleration pour freiner rapidement
		MouvementsBasiques.chassis.setLinearAcceleration(200);
		//robot d'arrete
		MouvementsBasiques.chassis.stop(); MouvementsBasiques.chassis.waitComplete();
		//il recule de 10 cm
		MouvementsBasiques.chassis.travel(-10); MouvementsBasiques.chassis.waitComplete();
		//il fait demi-tour
		MouvementsBasiques.chassis.rotate(180); MouvementsBasiques.chassis.waitComplete();
		//fin des mesures effectuées par le capteur de couleur
		Couleur.stopScan();
	}
}
