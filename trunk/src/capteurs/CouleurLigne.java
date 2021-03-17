package capteurs;

// énumération des couleurs, avec les bornes de celles-ci
public enum CouleurLigne { 
	ROUGE ( null, new float[] {2.80f, 3.80f, 0.45f, 0.60f, 0.10f, 0.20f}), 
	VERTE (null, new float[] {0.30f, 0.40f, 0.20f, 0.25f, 0.58f, 0.70f}),
	BLEUE (null, new float[] {0.17f, 0.27f, 0.61f, 0.78f, 2.50f, 4.00f}),
	BLANCHEP (new float[] {40f, 255f, 60f, 255f, 30f, 255f}, new float[] {0.64f, 0.73f, 0.53f, 0.63f, 0.77f, 0.95f}),
	BLANCHEF (new float[] {40f, 255f, 60f, 255f, 30f, 255f}, new float[] {0.63f, 0.77f, 0.52f, 0.65f, 0.67f, 0.95f}), 
	NOIREH(new float[] {0,10,0,10,0,10 },null),
	NOIREV(new float[] {0,10,0,10,0,10 },null),
	NOIRE(new float[] {0,10,0,10,0,10 },null),
	JAUNE (null, new float[] {0.75f, 0.83f, 0.15f, 0.20f, 0.18f, 0.26f}),
	GRIS(new float[] {15f, 35f, 15f, 40f, 10f, 25f}, null), //TODO
	VIDE(new float[] {0,1,0,1,0,1 },null),
	INCONNU(null, null);
	
	Intervalle IRGB;
	Intervalle IRatios;
	
	
	// bc : bornesCouleurs : {rouge_min, rouge_max, vert_min, vert_max, bleu_min, bleu_max} 
	// br : bornesRatios : { R/G_min, R/G_max, B/G_min, B/G_max, B/R_min, B/R_max }
	// pour des valeurs à ne pas prendre en compte, le min et le max de celles-ci doivent tous les deux être à Float.NaN
	private CouleurLigne(float[] bc, float[] br) {
		if (bc != null)
			IRGB = new Intervalle(new float[] {bc[0], bc[2], bc[4]}, new float[] {bc[1], bc[3], bc[5]});
		else
			IRGB = null;
		if (br != null)
			IRatios = new Intervalle(new float[] {br[0], br[2], br[4]}, new float[] {br[1], br[3], br[5]});
		else 
			IRatios=null;
	}
	
}




class Intervalle {
	float[] min;
	float[] max;
	
	public Intervalle(float min, float max) {
		this(new float[] {min}, new float[] {max});
	}
	public Intervalle(float[] min, float[] max) {
		assert(min.length == max.length);
		this.min = min;
		this.max = max;
	}
	
	public boolean contains(float[] values) {
		return contains(values, 0, min.length);
	}
	
	public boolean contains(float[] values, int beg, int end) {
		assert(values.length == end-beg);
		for (int i=0; i<end-beg; i++)
			if (values[i] > max[i] || values[i] < min[i])
				return false;
		return true;
		
	}
	
}