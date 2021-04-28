package interfaceEmbarquee;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;

/**
 * <p>Picker est une classe qui permet à l'utilisateur de choisir une configuration à lancer.</p>
 * 
 * <p>Elle implemente donc l'inteface Lancable.</p>
 * 
 * @see Configurations
 * @see Lancable
 * 
 */

public class Picker implements Lancable{
	/**
	 * Titre du Picker qui sera affiché à l'écran.
	 * 
	 */
	String titre;
	
	/**
	 * Configuration choisie par defaut. Elle peut être modifiée par l'utilisateur.
	 * 
	 */
	Configurations configuration;
	boolean une_seule_fois;
	
	/**
	 * Constructeur du Picker.
	 * @param titre
	 * 					titre du Picker
	 * @param c
	 * 					enumeration de Configurations
	 * @see Configurations
	 * 
	 */
	public Picker(String titre, Configurations c) {
		this(titre, c , false);
	}
	
	public Picker(String titre, Configurations c, boolean une_seule_fois) {
		this.titre=titre;
		this.configuration=c;
		this.une_seule_fois = une_seule_fois;
	}
	
	//affiche 1 seule configuration à la fois
	//defilement vertical entre les configurations
	//defilement horizontal pour afficher l'integralité de la chaine de caractères
	
	/**
	 * <p>Lance le Picker qui permet de choisir une Configuration.</p>
	 * <p>Affiche 1 seule configuration à la fois au milieu de l'écran du robot.</p>
	 * <p>Defilement vertical possible entre les configurations en appuyant sur les boutons "UP" et "DOWN".</p>
	 * <p>Defilement horizontal possible pour afficher l'integralité du titre des Configurations.</p>
	 * @see Lancable
	 * @see Configurations
	 * 
	 */
	public void lancer() {
		//nettoie l'écran du robot
		LCD.clear();
		//affiche le titre du Picker
		LCD.drawString(titre, 7, 1);
		//on initialise le bouton
		int button = -1; 
		//indique la configuration à afficher sur l'écran du robot. Ce n'est pas forcément celle qui est choisie dans la variable val de Configurations
		int choix = 1;
		//le buffer permettra de defiler à l'horizontale entre les caractères pour afficher l'integralité du titre
		char[] buffer;
		int j,k;
		//indice du 1er caractère a afficher. C'est un curseur qui se déplace dans le buffer et permet le defilement horizontal
		int debutColonne=0;
		//tant que l'utilisateur n'appuie pas sur le bouton "ESCAPE", on reste dans la boucle
		while(button != Button.ID_ESCAPE && !(une_seule_fois && button == Button.ID_ENTER)) {
			LCD.clear(1,2, 100); //nettoie l'écran	
			LCD.drawString(titre, 0, 0);
			/** affiche une croix sur la configuration actuellement choisie. Elle est stockée dans la variable val de Configurations
			 * @see Configurations#val
			 */
			if (configuration.getVal().equals(configuration.s.get(choix-1))) {
				LCD.drawString("X", 0, 4);
			}
			//sauvegarde dans le tableau de caractères, le titre de la configuration qui doit être affichée sur l'écran
			buffer=configuration.s.get(choix-1).toCharArray(); //transforme la chaîne de caractère en un tableau de caractères
			
			/* La variable k permet de defiler entre les caractères du buffer. Elle indique à quelle case le caractère sera affiché.
			 * On initialise k à deux car on laisse deux cases pour afficher la croix qui montre si la configuration affichée est choisie ou pas */
			//l'écran a une largeur de 18 cases
			for (j=debutColonne, k=2;(j<buffer.length)&&(k<18);j++,k++) {
				//affiche un caractère sur l'écran LCD du robot
				LCD.drawChar(buffer[j], k, 4); 
			}
			// attend que l'utilisateur appuie sur un bouton du clavier du robot
			button = Button.waitForAnyPress();
			if (button == Button.ID_UP) {
				//configuration précédente à afficher
				choix = ((choix == 1) ? configuration.s.size() :(choix-1)); 
				//on réinitialise la variable pour effacer le défilement horizontal
				debutColonne=0; 
			}
			else if (button == Button.ID_DOWN) {
				//configuration suivante à afficher
				choix = (choix%configuration.s.size())+1; 
				//on réinitialise la variable pour effacer le défilement horizontal
				debutColonne=0; 
			}
			else if (button == Button.ID_RIGHT) {
				//le défilement horizontal est permis si le titre est trop grand pour l'écran. On n'a que 16 cases restantes pour afficher le titre
				if (configuration.s.get(choix-1).length()>16) { 	
					/* si debutColonne est l'indice du dernier caractère du titre de la configuration, on ne defile plus à droite
					 * sinon, on affiche le titre à partir du caractère suivant */
					debutColonne = (15 == (configuration.s.get(choix-1).length()-1)-debutColonne)?debutColonne:++debutColonne; 
				}
			}
			else if (button == Button.ID_LEFT) {
				//le défilement horizontal est permis si le titre est trop grand pour l'écran
				if (configuration.s.get(choix-1).length()>16) {
					/* si debutColonne est l'indice du premier caractère du titre de la configuration, on ne defile plus à gauche
					 * sinon, on affiche le titre à partir du caractère précédent */
					debutColonne = (debutColonne == 0)?debutColonne:--debutColonne; 
				}
			}
			else if (button == Button.ID_ENTER) {
				//on sélectionne la configuration à lancer
				configuration.setVal(configuration.s.get(choix-1)); 
			}
			// on attend que l'utilisateur appuie sur un bouton du robot ou le relache
			Button.waitForAnyEvent();
		}
	}
	
	/**
     * Le titre du Picker sera affiché sur l'écran LCD du robot.
     * @see Lancable
     * @return
     * 			Le titre du Picker
     */
	public String getTitre() {
		return titre;
	}
}
