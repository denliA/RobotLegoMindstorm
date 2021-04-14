package capteurs;

import lejos.hardware.Sound;
import lejos.utility.Delay;
import lejos.utility.Timer;
import lejos.utility.TimerListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;



/**
 * Couche d'abstraction pour la gestion du capteur de couleur. 
 * Trois fonctions sont assurées ici:
 * <ul>
 * <li> La prise périodique de mesures.
 * <li> La sauvegarde de ces mesures
 * <li> L'intérprétation.
 * </ul>
 * 
 * La prise de mesures est conditionnée par des flags indiquant si un certain mode du capteur doit être mesuré ou non. Une prise de mesure particulière est indivisible, et
 * aucune donnée partielle ne peut être accédée par un client avant la lecture et l'intérprétation de toutes les mesures demandées au capteur. Pour assurer cette indivisible,
 * une mesure donnée est modélisée par un objet BufferContexte qui contient toutes les informations mesurées et interprétées à un instant t.
 * <p>
 * Cette classe est intrinsèquement liée à l'énumération CouleurLigne qui modélise la couleur d'une ligne. Les opérations qui impliquent des calculs sur une couleur précise et
 * qui ne dépendent pas du temps (du changement de mesure) se trouveront plutôt dans CouleurLigne.
 * <p>
 * Cette classe fournit plusieurs méthodes d'intérprétation, dont certaines qui utilisent l'historique pour inférer comment le robot se déplace entre deux couleurs, mais ces 
 * méthodes ne sont pas assez robustes pour être utilisées, et . La seule méthode robuste et utilisable fournie est la méthode (privée) getCouleurLigne, qui retourne un objet
 * CouleurLigne qui représente la ligne sur laquelle se trouve le capteur, et qui marche sans faute tant que la surface mesurée est celle d'une seule couleur. De plus, le seul
 * mode exploité pour le moment est le mode RGB du capteur, nous n'avons pas trouvé au moment de l'écriture de ce commentaire d'utilisation pertinente des modes lumière ambiante 
 * et intensité du rouge.
 * 
 * @see CouleurLigne
 * @see Capteur
 */
public class Couleur {
	
	
	//Attributs de la classe Couleur
	private static float rouge;
	private static float vert;
	private static float bleu;
	private static float lumiere;
	private static float IDCouleur;
	private static float intensiteRouge;
	public static int scanRate=-1;
	// Les deux attributs last est previous sont en doublon avec les deux dernières valeurs du buffer, mais utiles pour avoir un accès rapide sans avoir besoin
	// d'assurer la synchronisation
	private static BufferContexte last;
	private static BufferContexte previous;
	public static BufferCouleurs buffer = new BufferCouleurs(5000);
	static CouleurLigne lastCouleur;
	static boolean blanche;
	
	
	// Constantes pour le modeFlag
	final static public byte REDMODE = 0b1;
	final static public byte IDMODE = 0b10;
	final static public byte RGBMODE = 0b100;
	final static public byte LIGHTMODE = 0b1000;
	final static public byte BUFFERING = 0b10000;
	final static public byte INTERPRETER = 0b10000;
	
	/**
	 * indicateur permettant de décider quelles mesures prendre et lesquelles ignorer.
	 * <p>
	 * Les 5 bits inférieurs e4e3e2e1e0 sont utilisés:
	 * <ul> 
	 * <li> e4 pour activer l'historique des valeurs
	 * <li> e3 pour la lumière ambiante
	 * <li> e2 pour le mode RGB
	 * <li> e1 pour le ColorID (algorithme par défaut de Lego)
	 * <li> e0 pour le mode intensité du rouge 
	 */
	private static byte modeFlag = (byte) RGBMODE|INTERPRETER|BUFFERING;
	
	
	/**
	 * Représente une mesure unitaire prise à un instant t. Continent toutes les composantes de la mesure.
	 * 
	 *
	 */
	public static class BufferContexte {
		public CouleurLigne couleur_x;
		public long temps_x;
		public float [] rgb_x, ratios_x;
		public CouleurLigne intersection_x=null;
		
		
		/**
		 * Constructeur d'une mesure
		 * 
		 * @param couleur_x   une couleurLigne trouvée par getCouleurLigne. Si l'intérprétation est désactivée, prend la valeur null. 
		 * @param temps_x  l'instant auquel la mesure a été prise
		 * @param rouge_x  composante rouge
		 * @param vert_x   composante bleu
		 * @param bleu_x   composante vert
		 * @param rg_x	   ratio entre le composante rouge et verte
		 * @param bg_x	   ratio entre la composante bleue et verte
		 * @param br_x     ratio entre la composante bleue et rouge
		 */
		public BufferContexte(CouleurLigne couleur_x, long temps_x, float rouge_x, float vert_x, float bleu_x,
				float rg_x, float bg_x, float br_x) {
			this.couleur_x = couleur_x;
			this.temps_x = temps_x;
			this.rgb_x = new float[] {rouge_x, vert_x, bleu_x};
			this.ratios_x = new float[] {rg_x, bg_x, br_x};
		}
		
		/**
		 * Si on détecte que l'on est à une intersection donnée avec une autre couleur, <code>intersection_x</code> est initialisée à cette couleur
		 * en plus du reste des composantes de la mesure.
		 * @see #BufferContexte(CouleurLigne, long, float, float, float, float, float, float)
		 */
		public BufferContexte(CouleurLigne couleur_x, long temps_x, float rouge_x, float vert_x, float bleu_x,
				float rg_x, float bg_x, float br_x, CouleurLigne intersection_x) {
			this(couleur_x, temps_x, rouge_x, vert_x, bleu_x, rg_x, bg_x, br_x);
			this.intersection_x = intersection_x;
			
		}
		
		public BufferContexte() {}
		
		/**
		 * utile pour enregistrer la mesure en tant que ligne d'un tableau csv
		 * @return une chaîne de caractères représentant les colonnes de la ligne représentant la mesure au format R;G;B;ratioRG;ratioBG;ratioBR;temps;couleur
		 */
		public String formatCSV() {
			if (rgb_x==null)
				return "RIENG";
			String e =  rgb_x[0] + ";" + rgb_x[1] + ";" + rgb_x[2] + ";" + ratios_x[0] + ";" + ratios_x[1] + ";" + ratios_x[2] + ";" + temps_x + ";";
			if(couleur_x != null);
				e+=couleur_x.toString();
			return e;
		}
		
		
		public String toString() {
			return "Couleur: "+couleur_x+".\n"+"temps:"+temps_x+".\n "+"RGB:"+rgb_x[0]+"/"+rgb_x[1]+"/"+rgb_x[2]+". \nR/G/B:"+ratios_x[0]+"/"+ratios_x[1]+"/"+ratios_x[2]+(intersection_x !=null ? ("\nintersecte "+intersection_x):"");
		}
		
		
	}
	
	/**
	 * Représente l'historique des mesures. Est initialisé à une certaine taille n, et garde toujours les n dernières mesures effectuées.
	 * Peut être utile pour les méthodes d'intérprétation se servant de la variation entre deux ou plusieurs mesures
	 * <p>
	 * Est implémenté dans un tableau comme ceci en situation générale {n-i, n-i+1, ...., n-1, n, n-taille+1, n-taille+1, ..., } avec n le numéro de la mesure
	 * 
	 */
	public static class BufferCouleurs {
		
		private BufferContexte[] buffer;
		private int index;
		public int taille;
		public int mesures_effectuees;
		
		public BufferCouleurs(int taille) {
			index = -1;
			this.taille = taille;
			buffer = new BufferContexte[taille];
			mesures_effectuees = 0;
			for (int i = 0; i<taille; i++)
				buffer[i] = new BufferContexte();
		}

		/**
		 * Sauvegarde une nouvelle mesure dans le buffer
		 * @param b mesure à sauvegarder
		 */
		public void save(BufferContexte b) {
			synchronized(buffer_lock) {
				buffer[index=((index+1)%taille)] = b;
				mesures_effectuees++;
			}
		}
		
		
		/**
		 * @deprecated Moins adaptée que save(BufferContexte) car utilise les attributs de Couleur pour créer le contexte, et bloque tout le buffer pendant toute la durée de sauvegarde
		 * @param c couleur intérprétée de la mesure.
		 * @see #save(BufferContexte)
		 */
		public void save(CouleurLigne c) {
			synchronized(buffer_lock) {
				BufferContexte x  = buffer[index=((index+1)%taille)];
				x.couleur_x = c;
				x.temps_x = System.currentTimeMillis();
				x.rgb_x = getRGB();
				x.ratios_x = getRatios();
			}
		}
		
		/**
		 * @deprecated utiliser {@link #save(BufferContexte)}
		 * @param couleur
		 * @param intersection
		 */
		public void save(CouleurLigne couleur, CouleurLigne intersection) {
			save(couleur);
			buffer[index].intersection_x = intersection;
		}

		
		public BufferContexte getLast() {
			synchronized(buffer_lock) {
				return buffer[index];
			}
		}
		
		/**
		 * Renvoie les <code>nombre</code> dernières mesures dans l'ordre
		 * @param nombre nombre de mesures à prendre dans l'historique
		 * @return tableau contenant les nombre dernières mesures
		 */
		public BufferContexte[] historique(int nombre) {
			int th = Math.min(nombre, mesures_effectuees);
			BufferContexte[] hist = new BufferContexte[th];
			int n = 0;
			synchronized(buffer_lock) {
				for (int i=index; i>=0&&n<th; i--, n++) {
					hist[n] = buffer[i];
				}
				for (int i=taille-1; i>index&&n<th; i--, n++) {
					hist[n] = buffer[i];
				}
			}
			return hist;
		}

		
		
		/**
		 * Pour sauvegarder l'historique entier des mesures dans un fichier au format CSV
		 * @param savePath chemin et nom du fichier contenant la sauvegarde
		 * @see {@link BufferContexte#formatCSV()}
		 */
		public void toCSV(String savePath) {
			BufferContexte[] h = historique(taille);
			try {
				File f=new File(savePath);
				FileWriter writer = new FileWriter(f);
				writer.write("Vitesse : " + moteurs.MouvementsBasiques.getVitesseRobot() + "\t Accélération :" + moteurs.MouvementsBasiques.pilot.getAngularSpeed() + "\n");
				writer.write("R\tG\tB\tR/G\tB/G\tB/R\tTemps\tCouleur\tIntersection\n");
				for (int i=h.length-1; i>=0 ; i--) {
					String s = h[i].formatCSV();
					if (s!="RIENG")
						writer.write(s+"\n");
				}
				writer.close();
			} catch (IOException e) {
				Sound.beep();
				e.printStackTrace();
			}
		}


		}
	
	
	
	// Assure la synchronisation des opérations : si une mesure est en train d'être faite, on ne veut pas accéder aux vairables modifées par celle-ci  entre temps.
	private final static Object lock = new Object();
	private final static Object buffer_lock = new Object();
	
	// Pour lancer périodiquement des mesures
	static TimerListener ecouteur = new TimerListener() {
		public void timedOut() {
			update(); // On prend toutes les mesures avec le capteur
			// On met le dernier bloc de mesure dans last, et on sauvegarde ce dernier dans buffer. La dernière valeur se trouve toujours en doublon, mais 
			// avoir la dernière mesure hors du buffer est un gain de temps autant pour le buffer que pour le client
			previous = last;
			last = new BufferContexte(((INTERPRETER&modeFlag)!=0?getCouleurLigne():null), System.currentTimeMillis(), rouge, vert, bleu, rouge/vert, bleu/vert, bleu/rouge);
			buffer.save(last);
		}
	};
	private static Timer lanceur = new Timer(100, ecouteur
			);
	
	

	
	//Avoir la valeur de la couleur suivant l'enumeration de leJOS
	public static float getColorID() {
		synchronized(lock) {
			return(IDCouleur);
		}
	}
	
	//Avoir la valeur de la couleur suivant un encodage RGB
	public static float[] getRGB() {
		return(new float[] {rouge,vert,bleu});
	}
	
	//Ratios R/G, B/G et B/R
	public static float[] getRatios() {
		synchronized(lock) {
			return (new float[] {rouge/vert, bleu/vert, bleu/rouge});
		}
	}
	
	//Recuperer une valeur definissant l'intensite de la lumiere ambiante
	public static float getAmbiantLight() {
		synchronized(lock) {
			return(lumiere);
		}
	}
	
	// Récupérer une valeur définissant l'intensité de la lumière rouge
	public static float getRedMode() {
		synchronized(lock) {
			return(intensiteRouge);
		}
	}
	
	
	/**
	 * Change le mode de prise de mesure.
	 * 
	 * @param flag indicateur des mesures à prendre
	 * @see #modeFlag
	 */
	public static void setScanMode(byte flag) {
		modeFlag = flag;
	}
	
	/**
	 * 
	 * @return retourne le mode de prise de mesure
	 * @see #modeFlag
	 */
	public static byte getScanMode() {
		return(modeFlag);
	}
	
	
	/**
	 * Lance la prise de mesure périodique.
	 * @param delay délai entre chaque mesure.
	 */
	public static void startScanAtRate(int delay) {
		// Dans le cas ou on n'a pas fait de scan avant, on fait au moins un scan et une sauvegarde dans le buffer avant de rendre la main, pour
		// que l'appelant ait au moins deux valeurs à exploiter juste après.
		if (! (scanRate<0)) {
			ecouteur.timedOut(); ecouteur.timedOut();
		}
		lanceur.setDelay(delay);
		lanceur.start();
	}
	
	/**
	 * Arrête la prise de mesure périodique.
	 */
	public static void stopScan() {
		lanceur.stop();
		scanRate = -1;
	}
	
	/**
	 * Fonction principale de la classe Couleur. Fait la décision de quelle couleur
	 * il s'agit. Idéalement, pour un code extérieur qui aurait besoin de la couleur, cette méthode est la seule necessaire pour ce faire.
	 * 
	 * @return la couleur trouvée par l'algorithme, ou CouleurLigne.INCONNUE sinon.
	 */
	private static CouleurLigne getCouleurLigne() {
		float[] RGB, ratios;
		setScanMode((byte) (getScanMode() | RGBMODE)); // On s'assure que l'on scanne les valeurs RGB 
		Delay.msDelay(10);
		synchronized(lock) {
			RGB = getRGB();
			ratios = getRatios();
		}
			// On associe pour chaque couleur rencontrée une valeur flottante, représentant grossièrement la probabilité que cette couleur soit la bonne.
			ConcurrentHashMap <CouleurLigne, Float> candidats = new ConcurrentHashMap<>();
			
			/*
			 * Pour chaque couleur, on vérifie si elle définit un intervalle de valeurs directes (resp de rapports),
			 * et si c'est le cas on regarde si notre mesure appartient à cet intervalle.
			 * Selon le résultat, on ajoute la valeur de probabilité associée au couple (intervalle, résultat) à la probabilité totale de cette couleur
			 */
			for (CouleurLigne couleur : CouleurLigne.principales) {
				Float val = candidats.get(couleur);
				if (couleur.IRGB!=null) {
					if (couleur.IRGB.contains(RGB))
						candidats.put(couleur, (val == null ? (val=couleur.pos_confiance_IRGB) : (val=val+couleur.pos_confiance_IRGB)));
					else 
						candidats.put(couleur, (val==null)? (val=couleur.neg_confiance_IRGB) : (val=val+couleur.neg_confiance_IRGB));				}
				if (couleur.IRatios!=null) {
					if (couleur.IRatios.contains(ratios))
						candidats.put(couleur, (val == null ? (val=couleur.pos_confiance_IRatios) : (val=val+couleur.pos_confiance_IRatios)));
					else 
						candidats.put(couleur, (val==null)? (val=couleur.neg_confiance_IRatios) : (val=val+couleur.neg_confiance_IRatios));
				}
			}
			
			/*
			 * On trouve le candidat avec la probabilité la plus élevée d'être la bonne couleur
			 */
			CouleurLigne cand=null;
			float max = 0;
			for (CouleurLigne c : CouleurLigne.values()) {
				if (candidats.get(c)!= null &&  candidats.get(c)>max) {
					cand = c;
					max = candidats.get(c);
				}
			}
			
			if(cand==CouleurLigne.BLANCHE) {
				blanche = true;
			}
			
			// On retourne la couleur trouvée, ou CouleurLigne.INCONNU si il n'y a aucun candidat.
			if (cand !=null) {
				lastCouleur = cand;
				return cand;
			}
			
			// Si aucune couleur n'a une probabilité >0, on dit qu'on ne sait pas quelle couleur c'est
			lastCouleur = CouleurLigne.INCONNU;
			return CouleurLigne.INCONNU;
		
	}
	

	
	/**
	 * Renvoie la dernière couleur sur laquelle on est passé
	 * @return couleurLigne représentant 
	 */
	public static CouleurLigne getLastCouleur() {
		return lastCouleur;
	}
	
	

	
	/**
	 * @see #update(int)
	 */
	private static void update() {
		update(0);
	}
	
	
	/**
	 * Fait une prise de mesure pour chaque mesure activée dans <code>modeFlag</code>
	 * 
	 * @param delai temps d'attente entre deux mesures successives
	 * @see lejos.robotics.SampleProvider
	 */
	private static void update(int delai) {
		
		/*
		 * Chaque mode possède un flag qui détermine si on effectue la mesure pour ce dernier. 
		 * Entre chaque mode mesuré, on attend <code>delai</code> secondes pour éviter d'éventuelles erreurs.
		 */
		
		if((modeFlag & LIGHTMODE)!=0) {
			float[] ambiantLight = new float [Capteur.LUMIERE_AMBIANTE.sampleSize()];
			Capteur.LUMIERE_AMBIANTE.fetchSample(ambiantLight, 0);
			synchronized(lock) { lumiere = ambiantLight[0]; }
		}
		if((modeFlag & RGBMODE) != 0) {
			Delay.msDelay(delai);
			float[] couleurRGB = new float[Capteur.RGB.sampleSize()];
			Capteur.RGB.fetchSample(couleurRGB, 0);
			synchronized(lock) {
				rouge = 255*couleurRGB[0];
				vert = 255*couleurRGB[1];
				bleu = 255*couleurRGB[2];
			}
		}
		if((modeFlag & IDMODE)!=0) {
			Delay.msDelay(delai);
			float[] couleur_id = new float[Capteur.ID_COULEUR.sampleSize()];
			Capteur.ID_COULEUR.fetchSample(couleur_id, 0);
			synchronized(lock) {
				IDCouleur = couleur_id[0];
			}
		}
		if((modeFlag & REDMODE)!=0) {
			Delay.msDelay(delai);
			float[] rouge = new float[1];
			Capteur.ROUGE.fetchSample(rouge, 0);
			synchronized(lock) {intensiteRouge = rouge[0];}
		}
	}
	
	

	
	
	/**
	 * Utilise l'historique pour savoir si l'on est passé par une ligne dans les n dernières mesures
	 * @param c couleur par laquelle on veut savoir si l'on est passé
	 * @param n nombre de mesures consécutives sur lesquelles on vérifie
	 * @return true ssi on est passé par la couleur dans les dernières n mesures.
	 */
	public static boolean aRecemmentVu(CouleurLigne c, int n) {
		BufferContexte[] historique = buffer.historique(n); // On prend les n dernières mesures
		for (int i=0; i<n; i++) {
			if (historique[i].couleur_x==c) // dès qu'on a trouvé la couleur, on retourne
				return true;
		}
		// Si on n'a pas encore retourné, c'est qu'on a pas trouvé la couleur
		return false;
	}
	
	/**
	 * Détecte quand le robot avancer de la ligne origine vers la ligne arrivee (ou l'opposé)
	 * <p>Est assez précise pour vérifier pour une seule couleur si l'on sait ou on est, mais 
	 * donne beaucoup de faux positifs lorsqu'on vérifie pour toutes les couleurs.
	 * @param origine couleurLigne depuis laquelle on se repère
	 * @param arrivee couleurLigne vers laquelle on souhaite voir si on avance 
	 * @return -1.0 si on va de <code>arrivee</code> à <code>origine</code>, 1.0 si c'est l'opposé, et 0 sinon.
	 */
	public static float transitionneEntre(CouleurLigne origine, CouleurLigne arrivee) {
		BufferContexte passe = previous, present = last;
		if(passe==present) { passe = previous; present = last; }
		
		float [] variation_origine = new float[3], variation_arrivee=new float[3];
		float[] dists_origine_present = origine.IRGB.distance(present.rgb_x);
		float[] dists_origine_passe = origine.IRGB.distance(passe.rgb_x);
		float[] dists_arrivee_present = arrivee.IRGB.distance(present.rgb_x);
		float[] dists_arrivee_passe = arrivee.IRGB.distance(passe.rgb_x);
		
		for (int i=0;i<3;i++) {
			variation_origine[i] = Math.signum(dists_origine_present[i] - dists_origine_passe[i]);
			variation_arrivee[i] = Math.signum(dists_arrivee_present[i] - dists_arrivee_passe[i]);
		}
		
		float direction_presumee = variation_origine[0];
		for (int i=0; i<3;i++) {
			if(variation_origine[i]*direction_presumee<0)
				return 0;
			if(variation_arrivee[i]*direction_presumee > 0)
				return 0;
		}
		return direction_presumee;
		
	}
	
	public static float variationDistanceDe(CouleurLigne repere) {
		BufferContexte passe = previous, present = last;
		float distance_passe = repere.distanceDe(passe.rgb_x, true);
		float distance_present = repere.distanceDe(present.rgb_x, true);
		return Math.signum(distance_present-distance_passe);
	}
	
	public static boolean blacheTouchee() {
		if (blanche) {
			blanche=false;
			return true;
		}
		else return false;
	}
	
}



