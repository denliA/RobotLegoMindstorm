package capteurs;

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
	
	ROUGE ( null, new float[] {2.80f, 3.80f, 0.45f, 0.60f, 0.10f, 0.20f}), 
	VERTE (null, new float[] {0.30f, 0.40f, 0.20f, 0.25f, 0.58f, 0.70f}),
	BLEUE (null, new float[] {0.17f, 0.27f, 0.61f, 0.78f, 2.50f, 4.00f}),
	BLANCHEP (new float[] {40f, 255f, 60f, 255f, 30f, 255f}, new float[] {0.64f, 0.73f, 0.53f, 0.63f, 0.77f, 0.95f}),
	BLANCHEF (new float[] {40f, 255f, 60f, 255f, 30f, 255f}, new float[] {0.63f, 0.77f, 0.52f, 0.65f, 0.67f, 0.95f}), 
	NOIREH(new float[] {2,12,2,12,2,12 },null),
	NOIREV(new float[] {2,12,2,12,2,12 },null),
	NOIRE(new float[] {0,10,0,10,0,10 },null),
	JAUNE (null, new float[] {0.75f, 0.83f, 0.15f, 0.20f, 0.18f, 0.26f}),
	GRIS(new float[] {15f, 35f, 15f, 40f, 10f, 25f}, null), // {.69, .78, 0.5f, 0.6f, .67f, .82f} TOTEST
	VIDE(new float[] {0,1,0,1,0,1 },null),
	INCONNU(null, null);
	
	Intervalle IRGB;
	Intervalle IRatios;
	

	/**
	 * Constructeur d'une couleur
	 * 
	 * @param bc bornes des intensités : {rouge_min, rouge_max, vert_min, vert_max, bleu_min, bleu_max} 
	 * @param br bornes des rapports : { R/G_min, R/G_max, B/G_min, B/G_max, B/R_min, B/R_max }
	 */
	private CouleurLigne(float[] bc, float[] br) {
		if (bc != null)
			IRGB = new Intervalle(new float[] {bc[0], bc[2], bc[4]}, new float[] {bc[1], bc[3], bc[5]});
		else
			IRGB = null; // Une fonction extérieure considérera qu'un test pour les valeurs directes n'est pas pertinent dans ce cas
		if (br != null)
			IRatios = new Intervalle(new float[] {br[0], br[2], br[4]}, new float[] {br[1], br[3], br[5]});
		else 
			IRatios=null;
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
	
}