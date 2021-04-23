package interfaceEmbarquee;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;

/**
 * <p>Menu est une classe qui affiche une liste de Lancables à lancer. Cela peut etre d'autres Menus ou des Pickers.</p>
 * 
 * <p>Elle implemente donc l'inteface Lancable.</p>
 * 
 * @see Lancable
 * @see Picker
 * 
 */
public class Menu implements Lancable{
	/**
	 * Titre du Menu qui sera affiché à l'écran.
	 * 
	 */
	String titre;
	
	/**
	 * Tableau de Lancables qui seront affichés à l'écran. Regroupe des objets selon leur comportement de Lancable.
	 * 
	 */
	Lancable[] tab;
	
	/**
	 * Constructeur du Menu.
	 * @param titre
	 * 					titre du Menu
	 * @param tab
	 * 					tableau de Lancables
	 * @see Lancable
	 * 
	 */
	public Menu(String titre,Lancable tab[]) {
		this.titre=titre;
		this.tab = tab;
	}
	
	public Menu(String titre) {
		this.titre=titre;
		this.tab = null;
	}
	
	/**
     * Le titre du Menu sera affiché sur l'écran LCD du robot.
     * @see Lancable
     * @return
     * 			Le titre du Menu
     */
	public String getTitre() {
		return titre;
	}
	
	/**
     * Affecte un tableau de Lancables préexistant à l'attribut tab du Menu
     * @see Lancable
     *
     */
	public void setTab(Lancable tab[]) {
		this.tab = tab;
	}

	public void lancer() {
		/**
		 * L'écran LCD du robot peut afficher 8 lignes et 18 caracteres par ligne.
		 */
		
		//nettoie l'écran du robot
		LCD.clear();
		//on initialise le bouton
		int button = -1;
		//indique le titre du Lancable sur lequel le curseur "->" pointe
		int choix = 1;
		//plusieurs pages permettent le defilement vertical entre les Lancables si leur nombre depasse la taille de l'écran du robot
		int page = 0;
		//tableau de caractères qui contiendra les 6 titres de Lancables qu'on peut afficher sur une page
		//on ne peut afficher que 6 Lancables à la fois car le titre du Menu prend une ligne et on saute une autre ligne pour aérer
		char[][] buffer = new char[6][];
		int i,j,k;
		//indice du 1er caractère a afficher. C'est un curseur qui se déplace dans le buffer et permet le defilement horizontal
		int debutColonne=0;
		//indice qui limite le defilement à droite
		long max=0;
		//tant que l'utilisateur n'appuie pas sur le bouton "ESCAPE", on reste dans la boucle
		while(button != Button.ID_ESCAPE) {
			//nettoie les 6 dernieres lignes de l'écran du robot.
			LCD.clear(1,2, 100);
			//affiche le curseur "->" qui pointe sur le Lancable actuellement choisi
			LCD.drawString("->", 0, choix+1);
			//affiche le titre du Menu
			LCD.drawString(titre, 3, 0);
			//i est le nombre de lignes
			for (i=0; i<6 && i+6*page<tab.length;i++) { 
				buffer[i]=tab[i+page*6].getTitre().toCharArray();
				max= (max<buffer[i].length ? buffer[i].length : max);
				for (j=debutColonne, k=0;j<buffer[i].length;j++,k++) {
					LCD.drawChar(buffer[i][j], 2+k, i+2);
				}
			}
			// on attend que l'utilisateur appuie sur un bouton du clavier du robot
			button = Button.waitForAnyPress();
			if (button == Button.ID_DOWN) {
				//si la flèche pointe sur le dernier Lancable de la page
				if (choix==Math.min(tab.length-6*page, 6)) {
					if (page==Math.floor(tab.length/ 6)) { //si on est à la dernière page
						//on revient à la première page
						page=0;
					}else { //sinon
						//page suivante
						page++;
						//on reinitialise le choix
						choix = 1;
					}
				}
				else
				//on passe au Lancable suivant
					choix = (choix%Math.min(tab.length-6*page, 6))+1;
			}
			else if (button == Button.ID_UP) {
				if(choix==1) {
					if(page>0)
						//page précédente
						page--;
					else {
						//dernière page
						page = (int) Math.floor(tab.length/6);
					}
				}
				//si la flèche pointe sur le 1er Lancable de la page, elle devra pointer sur le dernier Lancable de la page précédente
				//sinon, on selectionne le Lancable précédent
				choix = (choix == 1) ? Math.min(tab.length-6*page, 6) :(choix-1);
			}
			else if (button == Button.ID_ENTER) {
				//on lance le menu ou le picker sur lequel la flèche pointe
				tab[choix+6*page-1].lancer();
			}
			else if (button == Button.ID_RIGHT) {
				//le defilement horizontal est permis si le titre est trop grand pour l'écran. On n'a que 16 cases restantes pour afficher le titre
				if (tab[choix+6*page-1].getTitre().length()>15) {
					/* si debutColonne est l'indice du dernier caractère du titre de la configuration, on ne defile plus à droite
					 * sinon, on affiche le titre à partir du caractère suivant */
					debutColonne = (debutColonne == tab[choix-1].getTitre().length())?debutColonne:++debutColonne;
				}
			}
			else if (button == Button.ID_LEFT) {
				//le defilement horizontal est permis si le titre est trop grand pour l'écran. On n'a que 16 cases restantes pour afficher le titre
				if (tab[choix+6*page-1].getTitre().length()>15) {
					/* si debutColonne est l'indice du premier caractère du titre de la configuration, on ne defile plus à gauche
					 * sinon, on affiche le titre à partir du caractère précédent */
					debutColonne = (debutColonne == 0)?debutColonne:--debutColonne;
				}
			}
			// on attend que l'utilisateur appuie sur un bouton du robot ou le relache
			Button.waitForAnyEvent();
		}
	}
}
