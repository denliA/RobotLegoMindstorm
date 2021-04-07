package capteurs;

import lejos.utility.Delay;
import lejos.utility.Timer;
import lejos.utility.TimerListener;
import java.util.concurrent.ConcurrentHashMap;



/**
 * Classe gérant toutes les mesures et les interprétations en rapport avec le capteur de couleurs.
 * <p>
 * Deux fonctions sont assurées ici:
 * <ul>
 * <li> La prise périodique de mesures.
 * <li> L'intérprétation de ces mesures.
 * </ul>
 * 
 * @see CouleurLigne
 * @see Capteur
 */
public class Couleur {
	
	public Couleur() {
		update();
		buffer.save(CouleurLigne.INCONNU);
	}
	
	//Attributs de la classe Couleur
	private static float rouge;
	private static float vert;
	private static float bleu;
	private static float lumiere;
	private static float IDCouleur;
	private static float intensiteRouge;
	private static boolean scanning = false;
	static BufferCouleurs buffer = new BufferCouleurs(50);
	
	
	// Constantes pour le modeFlag
	final static public byte REDMODE = 0b1;
	final static public byte IDMODE = 0b10;
	final static public byte RGBMODE = 0b100;
	final static public byte LIGHTMODE = 0b1000;
	final static public byte BUFFERING = 0b10000;
	
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
	private static byte modeFlag = (byte) RGBMODE|BUFFERING;
	
	
	
	static class BufferContexte {
		CouleurLigne couleur_x;
		long temps_x;
		float rouge_x, vert_x, bleu_x, rg_x, bg_x, br_x;
		CouleurLigne intersection_x=null;
		
		public BufferContexte(CouleurLigne couleur_x, long temps_x, float rouge_x, float vert_x, float bleu_x,
				float rg_x, float bg_x, float br_x) {
			this.couleur_x = couleur_x;
			this.temps_x = temps_x;
			this.rouge_x = rouge_x;
			this.vert_x = vert_x;
			this.bleu_x = bleu_x;
			this.rg_x = rg_x;
			this.bg_x = bg_x;
			this.br_x = br_x;
		}
		
		
		public BufferContexte(CouleurLigne couleur_x, long temps_x, float rouge_x, float vert_x, float bleu_x,
				float rg_x, float bg_x, float br_x, CouleurLigne intersection_x) {
			this(couleur_x, temps_x, rouge_x, vert_x, bleu_x, rg_x, bg_x, br_x);
			this.intersection_x = intersection_x;
			
		}
		
		public BufferContexte() {}
		
		public String toString() {
			return "Couleur: "+couleur_x+". "+"temps:"+temps_x+"RGB:"+rouge+"/"+vert+"/"+bleu+". R/G/B:"+"";
		}
		
	}
	static class BufferCouleurs {
		
		final static int COULEUR = 0;
		final static int TEMPS = 1;
		final static int ROUGE = 2;
		final static int VERT = 3;
		final static int BLEU = 4;
		final static int R_G = 5;
		final static int B_G = 6;
		final static int B_R = 7;
		
		private BufferContexte[] buffer;
		private int index;
		public int taille;
		
		public BufferCouleurs(int taille) {
			index = -1;
			this.taille = taille;
			buffer = new BufferContexte[taille];
			for (int i = 0; i<taille; i++)
				buffer[i] = new BufferContexte();
		}

		
		public void save(CouleurLigne c) {
			synchronized(buffer_lock) {
				BufferContexte x  = buffer[index=((index+1)%taille)];
				x.couleur_x = c;
				x.temps_x = System.currentTimeMillis();
				x.rouge_x = Couleur.rouge; x.vert_x = Couleur.vert; x.bleu_x=Couleur.bleu;
				x.rg_x = Couleur.rouge/Couleur.vert; x.bg_x = Couleur.bleu/Couleur.vert; x.br_x = Couleur.bleu/Couleur.rouge;
			}
		}
		
		public void save(CouleurLigne couleur, CouleurLigne intersection) {
			save(couleur);
			buffer[index].intersection_x = intersection;
		}

		
		public BufferContexte getLast() {
			return buffer[index];
		}
		
		public BufferContexte[] historique(int nombre) {
			BufferContexte[] hist = new BufferContexte[nombre];
			int n = 0;
			synchronized(buffer_lock) {
				for (int i=index; i>=0&&n<nombre; i--, n++) {
					hist[n] = buffer[i];
				}
				for (int i=taille-1; i>index&&n<nombre; i--, n++) {
					hist[n] = buffer[i];
				}
			}
			return hist;
		}



		}
	
	// Assure la synchronisation des opérations : si une mesure est en train d'être faite, on ne veut pas accéder aux vairables modifées par celle-ci  entre temps.
	private final static Object lock = new Object();
	private final static Object buffer_lock = new Object();
	
	// Pour lancer périodiquement des mesures
	private static Timer lanceur = new Timer(100, 
			new TimerListener() {
		public void timedOut() {
			update();
			if ((modeFlag&BUFFERING)!=0) {
				CouleurLigne c = getCouleurLigne();
				if (c==CouleurLigne.INCONNU) {
					CouleurLigne[] cs = getCouleurMoitie();
					if (cs==null)
						buffer.save(CouleurLigne.INCONNU);
					else
						buffer.save(cs[0], cs[1]);
				}
				else
					buffer.save(c);
			}
		}
	});
	
	

	
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
		// que l'appelant ait au moins une valeur à exploiter juste après.
		if (!scanning) {
			update();
			buffer.save(getCouleurLigne());
			scanning = true;
		}
		lanceur.setDelay(delay);
		lanceur.start();
	}
	
	/**
	 * Arrête la prise de mesure périodique.
	 */
	public static void stopScan() {
		lanceur.stop();
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
			
			// On retourne la couleur trouvée, ou CouleurLigne.INCONNU si il n'y a aucun candidat.
			if (cand !=null)
				return cand;
			return CouleurLigne.INCONNU;
		
	}
	
	private static CouleurLigne[] getCouleurMoitie() {
		BufferContexte contexte = buffer.getLast();
		float[] RGB = getRGB(), ratios = getRatios();
		if (contexte.intersection_x != null && contexte.couleur_x.estEntreDeux(contexte.intersection_x, RGB, ratios)) {
			return new CouleurLigne[] {contexte.couleur_x, contexte.intersection_x};
		}
		if (contexte.couleur_x!=CouleurLigne.INCONNU) {
			if (contexte.couleur_x.estEntreDeux(CouleurLigne.GRIS, RGB, ratios))
				return new CouleurLigne[] {contexte.couleur_x, CouleurLigne.GRIS};
			for (CouleurLigne intersection : contexte.couleur_x.intersections.keySet()) {
				if(contexte.couleur_x.estEntreDeux(CouleurLigne.GRIS, RGB, ratios))
					return new CouleurLigne[] {contexte.couleur_x, intersection };
			}
		}
		for (CouleurLigne c: CouleurLigne.principales) {
			if (c==CouleurLigne.GRIS) continue;
			for(CouleurLigne cc : CouleurLigne.principales) {
				if (cc==c) continue;
				if (c.estEntreDeux(cc, RGB, ratios))
					return new CouleurLigne[] {c, cc};
			}
		}
		return null;
		
	}
	
	
	public static CouleurLigne getLastCouleur() {
		synchronized(buffer_lock) {
			return buffer.getLast().couleur_x;
		}
	}

	
	
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
	
	public static boolean estSurLigne(CouleurLigne c, char strict) {
		BufferContexte cont = buffer.getLast();
		if(strict == 'i')
			return cont.couleur_x==c || cont.intersection_x==c;
		if(strict == 'm')
			return cont.couleur_x==c && (cont.intersection_x==null || cont.intersection_x==CouleurLigne.GRIS);
		else
			return cont.couleur_x==c&&cont.intersection_x==null;
	}
	
	public static boolean aRecemmentVu(CouleurLigne c, int n) {
		BufferContexte[] historique = buffer.historique(n);
		for (int i=0; i<n; i++) {
			if (historique[i].couleur_x==c)
				return true;
		}
		return false;
	}
	
}



