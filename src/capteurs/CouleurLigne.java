package capteurs;
import java.util.HashMap;



/**
 * CouleurLigne est une énumération modélisant les différentes couleurs du terrain, et permettant de confronter une mesure donnée à chaque couleur par différents moyens.
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
 * <p>
 * De plus, à chaque intervalle d'une couleur, on peut associer deux valeurs indiquant la fiabilité de l'information obtenue si une mesure appartient ou pas à l'intervalle. 
 * Par exemple:
 * <ul>
 * <li> Si le couple (1,0) est associé, alors le fait qu'une mesure soit dans l'intervalle nous dit avec une forte assurance que l'on est sur cette couleur, mais si une mesure
 * est hors de l'intervalle, ce n'est pas un indicateur fort que l'on n'est pas dans cette couleur 
 * <li> Si le couple (0, -1) est associé, alors le fait qu'une mesure soit dans l'intervalle ne nous donne pas d'information forte sur notre présence ou pas sur la couleur, 
 * mais si la mesure n'y est pas, ça nous dit avec une forte assurance que l'on est pas sur cette couleur.
 * </ul>
 * Cette énumération fournit (notamment grâce à la classe locale Intervalle) plusieurs méthodes de comparaisons entre deux couleurs, ou entre une couleur et une mesure.
 * 
 * @see Intervalle
 * @see Couleur
 *
 */
public enum CouleurLigne { 
	
	
	/*
	 * énumération des couleurs de la table.
	 */ /**Couleur grise (fond du terrain) */
	GRIS(new float[] {19f, 29f, 27.5f, 37f, 15f, 22f}, new float[] {.66f, .825f, 0.5f, 0.62f, .67f, .85f},true), /**Couleur verte*/
	VERTE (new float[] {8f, 16f, 28.5f, 42f, 4.5f, 11f}, 1, -.75f,  new float[] {0.30f, 0.40f, 0.20f, 0.25f, 0.58f, 0.70f},1,-.75f), /**Couleur bleue*/
	BLEUE (new float[] {4.5f, 10f, 27f , 40f , 17.75f, 29.25f}, 1,-.75f, new float[] {0.17f, 0.28f, 0.61f, 0.78f, 2.50f, 4.00f},1,-.75f), /**Couleur blanche*/
	BLANCHE (new float[] {37f, 255f, 55f, 255f, 28f, 255f}, new float[] {0.63f, 0.77f, 0.52f, 0.65f, 0.67f, 0.95f}, true), /**Couleur noire*/
	NOIRE(new float[] {2,12,2,12,2,12 }, 1,-1, new float[] {0.55f, 1f, 0.40f, 0.67f, 0.40f, 0.98f}, 0, -.5f, new CouleurLigne[] {BLEUE, VERTE}, true), /**Couleur rouge*/
	ROUGE ( new float[] {22.5f, 36f, 5.75f, 13.5f, 2f, 11.25f}, 1,-.75f, new float[] {2.80f, 3.80f, 0.45f, 0.60f, 0.10f, 0.20f}, 1,-.75f, new CouleurLigne[] {BLEUE, VERTE, NOIRE, BLANCHE}, false), /**Couleur jaune*/ 
	JAUNE (new float[] {38f, 58f, 50f, 71.5f, 7.5f, 13f}, 1,-.75f, new float[] {0.75f, 0.83f, 0.15f, 0.20f, 0.18f, 0.26f},1,-.75f, new CouleurLigne[] {BLEUE, VERTE, NOIRE, BLANCHE}, false),
	/** Indique quand le robot voit le vide. Il est toujours détecté quand il n'y a aucune surface à mois de 5cm du capteur */
	VIDE(new float[] {0,1,0,1,0,1 },null),
	/** Constante représentant l'échec de reconnaissance de couleur. Apparaît notamment lorsque le capteur est entre deux couleurs, ou hors de la table*/
	INCONNU(null, null),
	/**Couleur noire horizontale*/NOIREH(null,null),
	/**Couleur noire verticale*/NOIREV(null,null),
	/**Couleur blanche au sud*/BLANCHE_BLEUE(null,null),
	/**Couleur blanche au nord*/BLANCHE_VERTE(null,null);
	
	/**
	 * Les couleurs principales, c'est à dire en excluant les cas particuliers de l'énumération (VIDE, INCONNU...)
	 */
	public static CouleurLigne[] principales = new CouleurLigne[] {ROUGE, VERTE, BLEUE, BLANCHE, NOIRE, JAUNE, GRIS};
	
	Intervalle IRGB;
	float pos_confiance_IRGB;
	float neg_confiance_IRGB;
	Intervalle IRatios;
	float pos_confiance_IRatios;
	float neg_confiance_IRatios;
	boolean forcerIRGB = false;
	/**
	 * Même chose que dans <code>intersections</code> mais pour la couelur spéciale qu'est le gris
	 * @see #intersections
	 */
	public ContextePID contexteGris;
	/**
	 * Dictionnaire associant à chaque intersection d'une couleur donnée un contextePID précisant quelle composante de quel intervalle est la plus différente
	 * entre les deux couleurs, et quelle est la moyenne des deux intensités pour cette composante
	 * <p>Par exemple, entre les lignes ROUGE et BLEUE, la composante avec le plus de différence entre les deux est la composante Rouge qui est très forte pour la ligne
	 * rouge et très faible pour la ligne bleue. 
	 */
	public HashMap<CouleurLigne, ContextePID> intersections = new HashMap<>(0);

	static {
		/*
		 * On construit le contexte avec le gris pour chaque autre couleur. Impossible de le faire dans le construceur car ça implique la CouleurLigne GRIS. On le fait donc en
		 * static.
		 */
		for (CouleurLigne c : principales) {
			c.contexteGris = c.construireContexte(CouleurLigne.GRIS);
		}
	}
	

	/**
	 * Constructeur sans préciser les valeurs de confiance pour chaque intervalle, elles sont mises à (1,-1) pour les deux intervalles
	 * @param bc intervalle des composantes RGB
	 * @param br intervalle des ratios
	 */
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
	
	/**
	 * Constructeur permettant d'associer en plus à une couleur un ensemble d'intersections (qui sont aussi des couleurs)
	 * @param intersections Liste des intersections possibles de cette couleur
	 * @param forcerIRGB booléen permettant de signaler si pour certaines opérations de comparaison (ex: {@link #distanceDe(float[], boolean)}) il vaut mieux utiliser l'intervalle d'IRGB
	 * @see #CouleurLigne(float[], float, float, float[], float, float)
	 */
	private CouleurLigne(float[] bc, float pos_confiance_bc, float neg_confiance_bc,  float[] br, float pos_confiance_br, float neg_confiance_br, CouleurLigne[] intersections, boolean forcerIRGB) {
		this(bc, pos_confiance_bc, neg_confiance_bc, br, pos_confiance_br, neg_confiance_br);
		this.forcerIRGB = forcerIRGB;
		for (CouleurLigne c : intersections) {
			ContextePID contexte = construireContexte(c); // On calcule le contexte pour chaque couleur intersectant this
			this.intersections.put(c, contexte); // On met le contexte dans le dictionnaire d'intersections
		}
	}


	private CouleurLigne(float[] bc, float[] br, boolean forcerIRGB) {
		this(bc,br);
		this.forcerIRGB=forcerIRGB;
	}
	
	
	/**
	 * Calcule la composante avec le plus de différence entre les deux couleurs, celle-ci sera la plus pertinente pour 
	 * 
	 * @param c couleurLigne avec laquelle on veut construire le contexte
	 * @return le contexte entre les deux couleurs
	 * 
	 * @see #ContextePID
	 */
	private ContextePID construireContexte(CouleurLigne c) {
		int composante; // Indice de la composante la plus pertinente dans l'intervalle choisi
		boolean mode; // Lequel des deux intervalles est utilisé? à false pour IRatios, et true pour IRGB
		float target; // Valeur moyenne de l'intesité de la composante entre les deux couleurs
		float dists[];  // distance entre les deux intervalles pour chaque composante
		Intervalle intervalle_this, intervalle_c;
		if(!forcerIRGB && !c.forcerIRGB) { // Si aucune deux couleurs n'indique qu'il faut utiliser l'intervalle RGB, on calcule les distances pour les IRatios
			mode = false;
			dists = IRatios.distance(c.IRatios); //Distance entre les deux intervalles
			intervalle_this = IRatios; 
			intervalle_c = c.IRatios;
		}
		else { // Sinon, on fait la même chose mais pour les intervalles IRGB
			mode = true;
			dists = IRGB.distance(c.IRGB);
			intervalle_this = IRGB;
			intervalle_c = c.IRGB;
		}

		composante = dists[0] > dists[1] ? (dists[0]>dists[2] ? 0 : 2) : (dists[1] > dists[2] ? 1 : 2); // On prend l'indice pour lequel la distance est la plus grande
		// Pour target, on prend le milieu des deux centres de chaque intervalle pour la composante choisie
		target = (intervalle_this.max[composante]+intervalle_this.min[composante]+intervalle_c.max[composante]+intervalle_c.min[composante])/4;
		Intervalle all = intervalle_this.unionColoree(intervalle_c);
		float range = all.max[composante]-all.min[composante];
		
		return new ContextePID(mode, composante, target, range); // On construit le contexte calculé
	}
	
	
	/**
	 * 
	 * @param c couleur avec laquelle on veut savoir si on est entre deux
	 * @param pointRGB la mesure en RGB
	 * @param pointRatio la même mesure en ratios
	 * @return true ssi la mesure passée est entre this et la couleur c
	 */
	public boolean estEntreDeux(CouleurLigne c, float[] pointRGB, float[] pointRatio) {
		boolean forcer = forcerIRGB||c.forcerIRGB||true;
		Intervalle i_this = forcer? IRGB : IRatios;
		Intervalle i_c = forcer? c.IRGB : c.IRatios;
		float[] point = forcer? pointRGB : pointRatio;
		return i_this.estEntreDeux(i_c, point);
	}
	
	/**
	 * Vérifie si une mesure donnée est dans l'intersection de this
	 * @param pointRGB la mesure en version RGB
	 * @param pointRatio la mesure en version ratios
	 * @return true si on est sur une intersection de this
	 */
	public boolean intersecte(float [] pointRGB, float[] pointRatio) {
		for (CouleurLigne c : intersections.keySet())
			if (estEntreDeux(c, pointRGB, pointRatio))
				return true;
		return false;
	}
	
	/**
	 * 
	 * @param point mesure dont on veut calculer la distance
	 * @param irgb indique si la mesure est en RGB ou en ratios
	 * @return La distance euclidienne entre la mesure et l'intervalle
	 */
	public float distanceDe(float[] point, boolean irgb) {
		Intervalle I = irgb? IRGB : IRatios;
		float[] dists = I.distance(point);
		return (float) Math.pow(dists[0]*dists[0]+dists[1]*dists[1]+dists[2]*dists[2], .5f);
	}
	
	/**
	 * Indique pour deux couleurs et un intervalle donné, quelle couleur est plus forte pour chauqe composante
	 * @param c couleurLigne contre laquelle on teste this
	 * @param irgb indique si le test se fait sur les intervalles RGB ou ratio
	 * @return un tableau de 3 floats valant 1 en i si la composante i de this est plus élevée, et -1 sinon
	 */
	public float[] dominations(CouleurLigne c, boolean irgb) {
		float[] diffs;
		if (irgb)
			diffs = IRGB.distance(c.IRGB, true);
		else
			diffs = IRatios.distance(c.IRGB, true);
		return new float [] {Math.signum(diffs[0]),Math.signum(diffs[1]),Math.signum(diffs[2]) };
	}
	
	
	/**
	 * Contient toutes les informations nécessaires pour effectuer un suivi de ligne en PID. Est associé à un couple (couleurLigne, couleurLigne)
	 */
	public class ContextePID {
		/**
		 * Vaut false si ces informations concernent les ratios, et true si elles concernent le rgb
		 */
		public boolean mode_rgb;
		/**
		 * Indice de la composante la plus appropriée pour faire le suivi 
		 */
		public int indice;
		/**
		 * Valeur target pour cette composante
		 */
		public float target;
		/**
		 * Erreur maximale possiblement détectée. Si on dépasse cette erreur, c'est qu'on est vraisemblablement pas entre les deux couleurs
		 */
		public float range;
		protected ContextePID(boolean mode_rgb, int indice, float target, float range) {
			this.mode_rgb = mode_rgb; 
			this.indice = indice;
			this.target = target;
			this.range = range;
		}
		
		public String toString() {
			return "PID en mode " + (mode_rgb? "RGB. " : "Ratios. ") + "Indice " + indice + ". Target : "+ target;
		}
	}
	
	
	
}



/**
 * Modélise des intervalles à plusieurs dimensions et une relation d'inclusion "(x1, ..., xn) appartient à (I1, ..., In)
 * ssi pour tout i dans {1,...,n}, xi appartient à Ii".
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
	float[] longueurs;
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
		longueurs = new float[taille];
		for (int i=0; i<taille; i++)
			longueurs[i] = max[i]-min[i];
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
	
	
	/**
	 * Permet de savoir si une certaine composante est dans l'intervalle associé. Parfois utile quand on veut regarder une seule couleur au lieu de la globalité.
	 * @param point la mesure
	 * @param indice l'indice de la composante à vérifier
	 * @return true si la i-ème composante du point est contenue dans le i-ème intervalle
	 */
	public boolean containsTerme(float point, int indice) {
		assert (indice <= taille);
		return (point<=max[indice] && point>=min[indice]);
	}
	
	
	
	/**
	 * Calcule l'intersection entre deux intervalles, définie par l'intersection de chaque intervalle composant.
	 * Si l'intersection d'une composante est vide, elle est représentée par l'intervalle [-1;1]
	 * @param I deuxième intervalle
	 * @return intersection entre this et I
	 */
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
	
	/**
	 * Pour deux intervalles se s'intersectant pas, retourne l'intervalle qui est entre les deux. 
	 * <p> Si les deux intervalles s'intersectent, le résultat de l'entre deux est vide ([-1,1])
	 * @param I deuxième intervalle
	 * @return intervalle se trouvant entre this et I
	 */
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
	
	
	/**
	 * Calcule l'union entre deux couleurs dans ce sens : pour chaque composante, l'union des deux intervalles A et B est [min(inf(A), inf(B)); max(sup(A), sup(B))]
	 * @param I deuxième intervalle 
	 * @return union de I avec this
	 */
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
	
	
	/**
	 * Retourne la distance en valeur absolue entre deux intervalles (terme à terme)
	 * @param I deuxième intervalle
	 * @return distance entre I et this
	 * @see #distance(Intervalle, boolean)
	 */
	float[] distance(Intervalle I) {
		return distance(I, false);
	}
	
	
	/**
	 * Retourne la distance entre deux intervalles au sens de la distance entre chaque intervalle composant 
	 * @param I deuxième intervalle
	 * @param signee si à true, la distance est signée et est négative si l'intervalle de I domine l'intervalle de this
	 * @return distance (signée ou non) entre les deux intervalles
	 */
	float[] distance(Intervalle I, boolean signee) {
		assert I.taille == taille : "Intervalles incompatibles";
		Intervalle difference = entreDeux(I);
		float [] res = new float[taille];
		for (int i=0; i<taille;i++) {
			int coef = (signee && max[i] < I.min[i]) ? -1 : 1;
			if (difference.min[i]==-1)
				res[i]=0;
			else
				res[i] = coef*(difference.max[i]-difference.min[i]);
		}
		return res;
	}
	
	/**
	 * Distance (non signée) entre un point et un intervalle, au sens des distances composante par composante
	 * @param point mesure
	 * @return distance entre this et la mesure
	 */
	float[] distance(float[] point) {
		assert point.length==taille : "Point non compatible avec l'intervalle";
		float [] res = new float[taille];
		for(int i=0; i<taille; i++) {
			if (point[i]>max[i])
				res[i] = point[i]-max[i];
			else if (point[i]<min[i])
				res[i] = min[i] - point[i];
			else
				res[i]=0;
		}
		return res;
	}
	
	/**
	 * Distance entre une certaine composante et l'intervalle associé à cette composante
	 * @param scalaire 
	 * @param indice indice de la composante pour laquelle on doit calculer la distance
	 * @return
	 */
	float distanceAt(float scalaire, int indice) {
		if(scalaire > max[indice])
			return scalaire - max[indice];
		if(scalaire < min[indice])
			return min[indice] - scalaire;
		else
			return 0;
	}
	
	
	/**
	 * Vérifie si une certaine mesure est dans l'entre deux de deux intervalles avec une précision par défaut de .25
	 * @param I deuxième intervalle 
	 * @param p mesure
	 * @return true si elle est entre les deux
	 * @see #entreDeux(Intervalle)
	 * @see #estEntreDeux(Intervalle, float[], float[])
	 */
	public boolean estEntreDeux(Intervalle I, float[] p) {
		float precision[] = new float[taille];
		for (int i=0;i<taille;i++) 
			precision[i] = Math.min(longueurs[i], I.longueurs[i])/4;
		return estEntreDeux(I, p, precision);
	}
	
	/**
	 * Vérifie si une certaine mesure est dans l'entre deux de deux intervalles
	 * @param I deuxième intervalle 
	 * @param p mesure
	 * @param precision erreur acceptée
	 * @return true si elle est entre les deux
	 * @see #entreDeux(Intervalle)
	 */
	public boolean estEntreDeux(Intervalle I, float[] p, float[] precision ) {
		
		Intervalle entre_deux = entreDeux(I);
		float[] distances = distance(I);
		int meilleur_indice = (distances[0] > distances[1] ? (distances[0]>distances[2] ? 0 : 2) : (distances[1]>distances[2]? 1 : 2));
		for(int i=0;i<taille; i++) {
			if(entre_deux.max[i]==-1) {
				if ( !(containsTerme(p[i], i) || I.containsTerme(p[i], i)))
					return false;
			}
			else if (distances[i] < precision[i] || i!=meilleur_indice){
				if(  !(containsTerme(p[i], i) || I.containsTerme(p[i], i) || entre_deux.containsTerme(p[i], i)) )
					return false;
			}
			else if (!entre_deux.containsTerme(p[i],i)) {
				return false;
			}
		}
		return true;
	}
	
	
	/**
	 * Calcule le centre de chaque intervalle
	 * @return tableau contenant le centre pour chaque composante
	 */
	public float[] centre() {
		float[] res = new float[taille];
		for (int i=0; i<taille; i++) res[i] = (min[i]+max[i])/2;
		return res;
	}
	
	
	/**
	 * Calcule les distances géométriques des composantes d'une certaine mesure par rapport aux centres des intervalles
	 * @param point mesure 
	 * @return distances géométriques du centre
	 */
	public float[] rapportsAuCentre(float[] point) {
		float[] res = centre();
		for (int i=0; i<taille; i++) {
			res[i] = point[i]/res[i];
		}
		return res;
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
