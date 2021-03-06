package capteurs;

public class Couleur {
	
	//Attributs de la classe Couleur
	private static float rouge;
	private static float vert;
	private static float bleu;
	private static float lumiere;
	private static float IDCouleur;
	private static byte modeFlag;  //4bits 0000e1e2e3e4 avec e1 : light, e2 : RGB, e3 : ID, e4 : undefined
	enum CouleurLigne { ROUGE, VERTE, BLEUE, BLANCHE, NOIREH, NOIREV, JAUNE, GRIS };
	
	//Avoir la valeur de la couleur suivant l'énumération de leJOS
	public static float getColorID() {
		return(IDCouleur);
	}
	
	//Avoir la valeur de la couleur suivant un encoge RGB
	public static float[] getRGB() {
		return(new float[] {rouge,vert,bleu});
	}
	
	//Récuperer une valeur définissant l'intensité de la lumière ambiante
	public static float getAmbiantLight() {
		return(lumiere);
	}
	
	//Définir le mode de scanner de couleur (quelles couleurs capter et quelle couleur ignorer)
	public static void setScanMode(byte flag) {
		modeFlag = flag;
	}
	
	//Retourn le mode de scanner de couleur
	public static byte getScanMode() {
		return(modeFlag);
	}
	
	//Fait des choix d'approximation en fonction des valeurs des autres méthodes pour retourner la couleur analysée
	public static CouleurLigne getCouleurLigne() {
		//TODO
	}
	
	private static void update() {
		if((modeFlag & 0b00001000)!=0) {
			float[] ambiantLight = new float [Capteur.LUMIERE_AMBIANTE.sampleSize()];
			Capteur.LUMIERE_AMBIANTE.fetchSample(ambiantLight, 0);
			lumiere = ambiantLight[0];
		}
		if((modeFlag & 0b00000100) != 0) {
			float[] couleurRGB = new float[Capteur.RGB.sampleSize()];
			Capteur.RGB.fetchSample(couleurRGB, 0);
			rouge = couleurRGB[0];
			vert = couleurRGB[1];
			bleu = couleurRGB[2];
		}
		if((modeFlag & 0b00000010)!=0) {
			float[] couleur_id = new float[Capteur.ID_COULEUR.sampleSize()];
			Capteur.ID_COULEUR.fetchSample(couleur_id, 0);
			IDCouleur = couleur_id[0];
		}
	}
}
