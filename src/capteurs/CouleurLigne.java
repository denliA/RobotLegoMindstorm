package capteurs;
import java.util.HashMap;


/**
 * CouleurLigne est une énumération modélisant les différentes couleurs du terrain.
 * <p>
 * Chaque couleur énumérée est caractérisée par deux groupes d'intervalles :
 * <ul>
 * <li> <code>IRGB</code> qui contient les valeurs minimales et maximales pour chaque composante
 * (rouge, vert et bleu) normalisées à [0-255] 
 * <li> <code>IRatios</code> qui contient les valeurs minimales et maximales des rapports entre
 * les trois composantes deux à deux (R/G, B/G, B/R)
 * </ul>
 * Les deux groupes représentent la même information - dans le sens ou on peut calculer l'un à partir
 * de l'autre et réciproquement - mais selon le contexte, une des deux peut être plus pertinente. 
 * Par exemple, pour la couleur noire, les trois composantes ont des valeurs très faibles donc 
 * les rapports sont très variants et ne sont pas utiles. Tandis que pour la couleur jaune, utiliser
 * les intensités brutes n'est pas très robuste car les valeurs varient beaucoup en fonction de la 
 * luminosité, mais les valeurs des rapports restes quand à elles très proches.
 * <p>
 * Pour indiquer qu'un certain groupe d'intervalles n'est pas pertinent pour une certaine couleur, 
 * il est mis à null.
 * 
 * @see Intervalle
 * @see Couleur
 *
 */
public enum CouleurLigne { 
	
	
	GRIS(new float[] {15f, 35f, 15f, 40f, 10f, 25f}, new float[] {.69f, .78f, 0.5f, 0.6f, .67f, .82f},false), // {.69, .78, 0.5f, 0.6f, .67f, .82f} TOTEST
	VERTE (new float[] {8f, 16f, 28.5f, 42f, 4.5f, 11f}, 0, -1,  new float[] {0.30f, 0.40f, 0.20f, 0.25f, 0.58f, 0.70f},1,-1),
	BLEUE (new float[] {4.5f, 10f, 27f , 40f , 17.75f, 29.25f}, 0, -1, new float[] {0.17f, 0.27f, 0.61f, 0.78f, 2.50f, 4.00f},1,-1),
	NOIRE(new float[] {2,12,2,12,2,12 }, 1,-1, new float[] {0.60f, 1f, 0.40f, 0.62f, 0.40f, 0.90f}, 0, -.5f, new CouleurLigne[] {BLEUE, VERTE}, true),
	ROUGE ( new float[] {22.5f, 36f, 5.75f, 13.5f, 2f, 11.25f}, 0, -1, new float[] {2.80f, 3.80f, 0.45f, 0.60f, 0.10f, 0.20f}, 1,-1, new CouleurLigne[] {BLEUE, NOIRE, VERTE}, false), 
	BLANCHE (new float[] {40f, 255f, 60f, 255f, 30f, 255f}, new float[] {0.63f, 0.77f, 0.52f, 0.65f, 0.67f, 0.95f}), 
	JAUNE (new float[] {38f, 58f, 50f, 71.5f, 7.5f, 13f}, 0, -1, new float[] {0.75f, 0.83f, 0.15f, 0.20f, 0.18f, 0.26f},1,-1, new CouleurLigne[] {BLEUE, NOIRE, VERTE}, false),
	VIDE(new float[] {0,1,0,1,0,1 },null),
	INCONNU(null, null);
	
	static CouleurLigne[] principales = new CouleurLigne[] {ROUGE, VERTE, BLEUE, BLANCHE, NOIRE, JAUNE, GRIS};
	
	Intervalle IRGB;
	float pos_confiance_IRGB;
	float neg_confiance_IRGB;
	Intervalle IRatios;
	float pos_confiance_IRatios;
	float neg_confiance_IRatios;
	boolean forcerIRGB = false;
	float[] contexteGris;
	public HashMap<CouleurLigne, float[]> intersections = new HashMap<>(0);

	static {
		for (CouleurLigne c : principales) {
			c.contexteGris = c.construireContexte(CouleurLigne.GRIS);
		}
	}
	

	
	private CouleurLigne(float[] bc ,float[] br) {
		this(bc, 1f, -1f,  br, 1f, -1f);
	}
	
	/**
	 * Constructeur d'une couleur
	 * 
	 * @param bc bornes des intensités : {min, max, vert_min, vert_max, bleu_min, bleu_max} 
	 * @param br bornes des rapports : { R/G_min, R/G_max, B/G_min, B/G_max, B/R_min, B/R_max }
	 * @param pos_confiance_bc Précise la certitude de cet intervalle : par exemple, pour <code>confiance_bc==1</code>,
	 * le fait qu'une mesure soit dans cet intervalle nous indique avec certitude si on est dans cette ligne ou 
	 * pas
	 * @param confiance_br même chose que confiance_bc mais pour les rapports
	 */
	private CouleurLigne(float[] bc, float pos_confiance_bc, float neg_confiance_bc,  float[] br, float pos_confiance_br, float neg_confiance_br) {
		this.pos_confiance_IRatios = pos_confiance_br;
		this.neg_confiance_IRatios = neg_confiance_br;
		this.pos_confiance_IRGB = pos_confiance_bc;
		this.neg_confiance_IRGB = neg_confiance_bc;
		if (bc != null)
			IRGB = new Intervalle(new float[] {bc[0], bc[2], bc[4]}, new float[] {bc[1], bc[3], bc[5]});
		else
			IRGB = null; // Une fonction extérieure considérera qu'un test pour les valeurs directes n'est pas pertinent dans ce cas
		if (br != null)
			IRatios = new Intervalle(new float[] {br[0], br[2], br[4]}, new float[] {br[1], br[3], br[5]});
		else 
			IRatios=null;
		
		
		
	}
	
	private CouleurLigne(float[] bc, float pos_confiance_bc, float neg_confiance_bc,  float[] br, float pos_confiance_br, float neg_confiance_br, CouleurLigne[] intersections, boolean forcerIRGB) {
		this(bc, pos_confiance_bc, neg_confiance_bc, br, pos_confiance_br, neg_confiance_br);
		this.forcerIRGB = forcerIRGB;
		for (CouleurLigne c : intersections) {
			float [] contexte = construireContexte(c);
			this.intersections.put(c, contexte);
		}
	}

	
	private CouleurLigne(float[] bc, float[] br, boolean forcerIRGB) {
		this(bc,br);
		this.forcerIRGB=forcerIRGB;
	}
	
	private float[] construireContexte(CouleurLigne c) {
		int mode, composante;
		float target;
		float dists[];
		Intervalle intervalle_this, intervalle_c;
		if(!forcerIRGB && !c.forcerIRGB) {
			mode = 1;
			dists = IRatios.distance(c.IRatios);
			intervalle_this = IRatios;
			intervalle_c = c.IRatios;
		}
		else {
			mode = 0;
			dists = IRGB.distance(c.IRGB);
			intervalle_this = IRGB;
			intervalle_c = c.IRGB;
		}
		composante = dists[0] > dists[1] ? (dists[0]>dists[2] ? 0 : 2) : (dists[1] > dists[2] ? 1 : 2);
		target = (intervalle_this.max[composante]+intervalle_this.min[composante]+intervalle_c.max[composante]+intervalle_c.min[composante])/4;
		return new float[] {mode, composante, target};
	}
	
	public boolean estEntreDeux(CouleurLigne c, float[] pointRGB, float[] pointRatio) {
		boolean forcer = forcerIRGB||c.forcerIRGB;
		Intervalle i_this = forcer? IRGB : IRatios;
		Intervalle i_c = forcer? c.IRGB : c.IRatios;
		float[] point = forcer? pointRGB : pointRatio;
		return i_this.estEntreDeux(i_c, point);
	}
	
	

	
}



/**
 * Modélise des intervalles à plusieurs dimensions et une relation d'inclusion "(x1, ..., xn) appartient à (I1, ..., In)
 * ssi pour tout i dans {1,...,n}, xi appartient à Ii.
 * <p>
 * Sert à tester de manière compacte l'appartenace d'une certaine mesure (par exemple RGB) à des limites définies pour chaque partie
 * de la mesure.
 * 
 * 
 *
 */
class Intervalle {
	float[] min;
	float[] max;
	int taille;
	
	/**
	 * Constructeur d'un intervalle simple (à une seule dimention).
	 * 
	 * @param min borne inférieure de l'intervalle
	 * @param max borne supérieure de l'intervalle
	 */
	public Intervalle(float min, float max) {
		this(new float[] {min}, new float[] {max});
	}
	
	/**
	 * Constructeur.
	 * 
	 * @param min tableau des bornes inférieures de chaque intervalle
	 * @param max tableau -de même taille que min- des bornes supérieures de chaque intervalle. 
	 */
	public Intervalle(float[] min, float[] max) {
		assert(min.length == max.length);
		taille = min.length;
		this.min = min;
		this.max = max;
	}
	
	/**
	 * 
	 * @param values représente la mesure 
	 * @return retourne vrai ssi le tuple values est contenu dans l'intervalle
	 * @see #contains(float, int, int)
	 */
	public boolean contains(float[] values) {
		return contains(values, 0, min.length);
	}
	
	/**
	 * Si une certaine mesure <code>values</code>  est contenue
	 * dans la sous partie (Ibeg, Ibeg+1, ..., Iend) de l'intervalle
	 * 
	 * @param values tuple à tester (de taille <code>end-beg</code>)
	 * @param beg 
	 * @param end
	 * @return renvoie vrai ssi pour chaque i dans {beg, ..., end}, values[i] est contenu dans Ii.
	 */
	public boolean contains(float[] values, int beg, int end) {
		assert(values.length == end-beg);
		for (int i=0; i<end-beg; i++)
			if (values[i] > max[i] || values[i] < min[i])
				return false;
		return true;
	}
	
	public boolean containsTerme(float point, int indice) {
		assert (indice <= taille);
		return (point<=max[indice] && point>=min[indice]);
	}
		
	public Intervalle intersection(Intervalle I) {
		assert I.taille == this.taille : "Intervalles incompatibles";
		float [] minimum = new float[taille];
		float [] maximum = new float[taille];
		for (int i=0; i<taille ; i++) {
			minimum[i] = this.min[i] < I.min[i] ? I.min[i] : this.min[i];
			maximum[i] = this.max[i] > I.max[i] ? I.max[i] : this.max[i];
			if (minimum[i] > maximum[i])
				minimum[i]=maximum[i]=-1;
		}
		return new Intervalle(minimum, maximum);
	}
	
	public Intervalle entreDeux(Intervalle I) {
		assert I.taille == this.taille : "Intervalles incompatibles";
		float [] minimum = new float[taille];
		float [] maximum = new float[taille];
		for (int i=0; i<taille;i++) {
			maximum[i] = this.min[i] < I.min[i] ? I.min[i] : this.min[i];
			minimum[i] = this.max[i] > I.max[i] ? I.max[i] : this.max[i];
			if(minimum[i] > maximum[i])
				minimum[i] = maximum[i] = -1;
		}
		return new Intervalle(minimum, maximum);
	}
	
	public Intervalle unionColoree(Intervalle I) {
		assert I.taille == taille : "Intevalles incompatibles";
		float minimum[] = new float[taille];
		float maximum[] = new float[taille];
		
		for (int i=0; i<taille;i++) {
			maximum[i] = this.max[i] < I.max[i] ? I.max[i] : this.max[i];
			minimum[i] = this.min[i] > I.min[i] ? I.min[i] : this.min[i];
		}
		
		return new Intervalle(minimum, maximum);
	}
	
	float[] distance(Intervalle I) {
		assert I.taille == taille : "Intervalles incompatibles";
		Intervalle difference = entreDeux(I);
		float [] res = new float[taille];
		for (int i=0; i<taille;i++) {
			if (difference.min[i]==-1)
				res[i]=0;
			else
				res[i] = difference.max[i]-difference.min[i];
		}
		return res;
	}
	
	float[] distance(float[] point) {
		assert point.length==taille : "Point non compatible avec l'intervalle";
		float [] res = new float[taille];
		for(int i=0; i<taille; i++) {
			if (point[i]>max[i])
				res[i] = point[i]-max[i];
			else if (point[i]<min[i])
				res[i] = min[i] - max[i];
			else
				res[i]=0;
		}
		return res;
	}
	
	boolean estEntreDeux(Intervalle I, float[] p) {
		
		Intervalle entre_deux = entreDeux(I);
		Intervalle intersection = intersection(I);
		for(int i=0;i<taille; i++) {
			if(entre_deux.max[i]==-1) {
				if (!intersection.containsTerme(p[i],i))
					return false;
			}
			else if (!entre_deux.containsTerme(p[i],i))
				return false;
		}
		return true;
	}
	
	public String toString() {
		StringBuffer res = new StringBuffer("( ");
		for (int i=0; i<taille; i++) {
			if(min[i] != -1) {
				res.append('[');
				res.append(min[i]);
				res.append(", ");
				res.append(max[i]);
				res.append("]; ");
			}
			else
				res.append("vide; ");
		}
		res.append(')');
		return res.toString();
	}
		
}
