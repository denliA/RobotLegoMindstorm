package capteurs;

import lejos.hardware.Sound;
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
	
	//Attributs de la classe Couleur
	private static float rouge;
	private static float vert;
	private static float bleu;
	private static float lumiere;
	private static float IDCouleur;
	private static float intensiteRouge;
	private static CouleurLigne[] buffer = new CouleurLigne[15];
	private static int bufferIndex = 0;
	
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
	private static byte modeFlag;
	
	// Constantes pour le modeFlag
	final static public byte REDMODE = 0b1;
	final static public byte IDMODE = 0b10;
	final static public byte RGBMODE = 0b100;
	final static public byte LIGHTMODE = 0b1000;
	final static public byte BUFFERING = 0b10000;
	
	// Assure la synchronisation des opérations : si une mesure est en train d'être faite, on ne veut pas accéder aux vairables modifées par celle-ci  entre temps.
	private final static Object lock = new Object();
	private final static Object buffer_lock = new Object();
	
	// Pour lancer périodiquement des mesures
	private static Timer lanceur = new Timer(100, 
			new TimerListener() {
		public void timedOut() {
			update();
			if ((modeFlag&BUFFERING)==1) {
				CouleurLigne c = getCouleurLigne();
				synchronized(buffer_lock) {
					buffer[bufferIndex=((bufferIndex+1)%buffer.length)] = c;
				}
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
	public static CouleurLigne getCouleurLigne() {
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
						candidats.put(couleur, (val==null)? (val=couleur.neg_confiance_IRGB) : (val=val+couleur.neg_confiance_IRGB));
					//if (couleur==CouleurLigne.BLANCHEF) System.out.println(couleur+" IRGB "+couleur.pos_confiance_IRGB+" "+couleur.neg_confiance_IRGB + " " + candidats.get(couleur));
				}
				if (couleur.IRatios!=null) {
					if (couleur.IRatios.contains(ratios))
						candidats.put(couleur, (val == null ? (val=couleur.pos_confiance_IRatios) : (val=val+couleur.pos_confiance_IRatios)));
					else 
						candidats.put(couleur, (val==null)? (val=couleur.neg_confiance_IRatios) : (val=val+couleur.neg_confiance_IRatios));
					//System.out.println(couleur+" IRatios "+couleur.pos_confiance_IRatios+" "+couleur.neg_confiance_IRatios);
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
			//System.out.println(candidats);
			//return cand==null?  CouleurLigne.INCONNU : cand;
			if (cand ==null)
				return cand;
			candidats.clear();
			for(CouleurLigne c : CouleurLigne.values()) {
				for(CouleurLigne intersect : c.intersetions) {
					Float val = candidats.get(intersect);
					if (intersect.IRGB!=null) {
						if (intersect.IRGB.contains(RGB))
							candidats.put(intersect, (val == null ? (val=intersect.pos_confiance_IRGB) : (val=val+intersect.pos_confiance_IRGB)));
						else 
							candidats.put(intersect, (val==null)? (val=intersect.neg_confiance_IRGB) : (val=val+intersect.neg_confiance_IRGB));
						//if (intersect==intersectLigne.BLANCHEF) System.out.println(intersect+" IRGB "+intersect.pos_confiance_IRGB+" "+intersect.neg_confiance_IRGB + " " + candidats.get(intersect));
					}
					if (intersect.IRatios!=null) {
						if (intersect.IRatios.contains(ratios))
							candidats.put(intersect, (val == null ? (val=intersect.pos_confiance_IRatios) : (val=val+intersect.pos_confiance_IRatios)));
						else 
							candidats.put(intersect, (val==null)? (val=intersect.neg_confiance_IRatios) : (val=val+intersect.neg_confiance_IRatios));
						//System.out.println(intersect+" IRatios "+intersect.pos_confiance_IRatios+" "+intersect.neg_confiance_IRatios);
					}
				}
			}
			cand=null;
			max = 0;
			for (CouleurLigne c : CouleurLigne.values()) {
				if (candidats.get(c)!= null &&  candidats.get(c)>max) {
					cand = c;
					max = candidats.get(c);
				}
			}
			return cand==null?  CouleurLigne.INCONNU : cand;
		
	}
	
	
	public static CouleurLigne getLastCouleur() {
		synchronized(buffer_lock) {
			return buffer[bufferIndex];
		}
	}
	
	public static CouleurLigne[] historiqueCouleurs(int nb) {
		CouleurLigne[] hist = new CouleurLigne[nb];
		int n = 0;
		synchronized(buffer) {
			for (int i=bufferIndex; i>=0&&n<nb; i--, n++) {
				hist[n] = buffer[i];
			}
			for (int i=14; i>bufferIndex&&n<nb; i--, n++) {
				hist[n] = buffer[i];
			}
		}
		return hist;
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
		Delay.msDelay(delai);
		if((modeFlag & RGBMODE) != 0) {
			float[] couleurRGB = new float[Capteur.RGB.sampleSize()];
			Capteur.RGB.fetchSample(couleurRGB, 0);
			synchronized(lock) {
				rouge = 255*couleurRGB[0];
				vert = 255*couleurRGB[1];
				bleu = 255*couleurRGB[2];
			}
		}
		Delay.msDelay(delai);
		if((modeFlag & IDMODE)!=0) {
			float[] couleur_id = new float[Capteur.ID_COULEUR.sampleSize()];
			Capteur.ID_COULEUR.fetchSample(couleur_id, 0);
			synchronized(lock) {
				IDCouleur = couleur_id[0];
			}
		}
		Delay.msDelay(delai);
		if((modeFlag & REDMODE)!=0) {
			float[] rouge = new float[1];
			Capteur.ROUGE.fetchSample(rouge, 0);
			synchronized(lock) {intensiteRouge = rouge[0];}
		}
	}
}

