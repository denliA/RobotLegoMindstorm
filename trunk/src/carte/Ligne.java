package carte;

import java.util.concurrent.ConcurrentHashMap;

import capteurs.CouleurLigne;

public class Ligne{
	/**
	 * On considère la classe Ligne comme une droite parallèle à un axe du plan.
	 * Il suffit de connaître sa coordonnée fixée puis de ne pas prendre compte de l'autre.
	 * Puisqu'on peut simplement dire si elle est horizontale ou verticale.
	 * De plus on la définit par une couleur pour faciliter l'association aux données récupérée par le capteur de couleurs.
	 */
	private boolean horizontale;
	private float coord;
	private CouleurLigne couleur;
	
	/**
	 * Constructeur d'une ligne.
	 * @param coord
	 * @param horizontale
	 * @param couleur
	 */
	public Ligne(float coord, boolean horizontale, CouleurLigne couleur) {
		this.coord = coord;
		this.horizontale = horizontale;
		this.couleur = couleur;
	}
	
	/**
	 * Les getters.
	 * @return L'information d'interêt sur la Ligne.
	 */
	
	public boolean getDirection() {
		return(horizontale);
	}
	
	public float getPosition() {
		return(coord);
	}
	
	public CouleurLigne getCouleur() {
		return(couleur);
	}
	
	/**
	 * Calcul du point d'intersection entre la Ligne appelant cette méthode et la Ligne A passée en paramètre.
	 * @param A
	 * @return Le point en lequel s'intersectent les 2 Lignes
	 */
	public Point intersection(Ligne A) {
		if(this.horizontale && A.horizontale) {
			return(null);
		}
		return(this.getDirection()?new Point(A.getPosition(), this.coord):new Point(this.coord, A.getPosition()));
	}
	
	/**
	 * Utilisation d'une hashMap pour faciliter la compréhension de la Ligne d'interêt lors de la récupération d'une couleur par le capteur.
	 */
	static public ConcurrentHashMap<CouleurLigne, Ligne> hashLignes = new ConcurrentHashMap<>();
	static {
		hashLignes.put(CouleurLigne.VERTE, new Ligne(1f,true,CouleurLigne.VERTE));
		hashLignes.put(CouleurLigne.BLEUE, new Ligne(-1f,true,CouleurLigne.BLEUE));
		hashLignes.put(CouleurLigne.JAUNE, new Ligne(1f,false,CouleurLigne.JAUNE));
		hashLignes.put(CouleurLigne.ROUGE, new Ligne(-1f,false,CouleurLigne.ROUGE));
		hashLignes.put(CouleurLigne.BLANCHE_BLEUE, new Ligne(-2f,true,CouleurLigne.BLANCHE));
		hashLignes.put(CouleurLigne.BLANCHE_VERTE, new Ligne(2f,true,CouleurLigne.BLANCHE));
		hashLignes.put(CouleurLigne.NOIREH, new Ligne(0f,true,CouleurLigne.NOIRE));
		hashLignes.put(CouleurLigne.NOIREV, new Ligne(0f,false,CouleurLigne.NOIRE));
	}
	
	/**
	 * Création de 2 classes interne : 
	 * - LCC (Ligne, Couleur, Couleur) 
	 * - Etat (Position, direction) 
	 * pour pouvoir utiliser une hashMap où l'on fait se correspondre ces 2 informations.
	 * L'idée derrière cela est que la LCC représente la Ligne que le robot suit, et les couleurs ici sont celle de 2 intersections qu'il traverse.
	 * Cela permet de facilité l'identification de sa position et de sa direction au robot. En effet le terrain étant connu, il suffit de :
	 * - Connaître la Ligne que l'on suit pour avoir le x (si la Ligne est verticale) ou le y (si elle est horizontale)
	 * - Connaître l'intersection pour avoir la coordonnée manquante
	 * (ici c'est à la deuxième intersection que le robot connaît sa position car on n'a que faire de la première vu que l'on ne s'y arrête pas.)
	 * - Savoir dans quel ordre on a traversé 2 Lignes pour savoir dans quelle direction on va.
	 */
	public static class LCC{
		private Ligne ligne;
		private CouleurLigne cl1;
		private CouleurLigne cl2;
		public LCC(Ligne ligne, CouleurLigne cl1, CouleurLigne cl2) {
			this.ligne = ligne;
			this.cl1 = cl1;
			this.cl2 = cl2;
		}
		@Override
		public int hashCode() {
			return(ligne.hashCode()+cl1.hashCode()+cl2.hashCode());
		}
	}
	
	public static class Etat{
		private Point position;
		private float direction;
		public Etat(Point position, float direction) {
			this.position = position;
			this.direction = direction;
		}
	}
	
	/**
	 * Dans cette hashMap on compte utiliser le robot de manière à ce qu'il aille sur une ligne verticale pour se repérer, car cela nous facilite la tâche.
	 */
	static public ConcurrentHashMap<LCC, Etat> hashPerdu = new ConcurrentHashMap<>();
	static {
		hashPerdu.put(new LCC(hashLignes.get(CouleurLigne.JAUNE),CouleurLigne.BLEUE, CouleurLigne.NOIRE), new Etat(new Point(1,0), 90f));
		hashPerdu.put(new LCC(hashLignes.get(CouleurLigne.JAUNE), CouleurLigne.NOIRE, CouleurLigne.VERTE), new Etat(new Point(1,1),90f));
		hashPerdu.put(new LCC(hashLignes.get(CouleurLigne.JAUNE), CouleurLigne.VERTE, CouleurLigne.BLANCHE), new Etat(new Point(1,2),90f));
		hashPerdu.put(new LCC(hashLignes.get(CouleurLigne.JAUNE), CouleurLigne.VERTE, CouleurLigne.NOIRE), new Etat(new Point(1,0),270f));
		hashPerdu.put(new LCC(hashLignes.get(CouleurLigne.JAUNE), CouleurLigne.NOIRE, CouleurLigne.BLEUE), new Etat(new Point(1,-1),270f));
		hashPerdu.put(new LCC(hashLignes.get(CouleurLigne.JAUNE), CouleurLigne.BLEUE, CouleurLigne.BLANCHE), new Etat(new Point(1,-2),270f));
		
		hashPerdu.put(new LCC(hashLignes.get(CouleurLigne.NOIRE), CouleurLigne.BLEUE, CouleurLigne.NOIRE), new Etat(new Point(0,0),90f));
		hashPerdu.put(new LCC(hashLignes.get(CouleurLigne.NOIRE), CouleurLigne.NOIRE, CouleurLigne.VERTE), new Etat(new Point(0,1),90f));
		hashPerdu.put(new LCC(hashLignes.get(CouleurLigne.NOIRE), CouleurLigne.VERTE, CouleurLigne.BLANCHE), new Etat(new Point(0,2),90f));
		hashPerdu.put(new LCC(hashLignes.get(CouleurLigne.NOIRE), CouleurLigne.VERTE, CouleurLigne.NOIRE), new Etat(new Point(0,0),270f));
		hashPerdu.put(new LCC(hashLignes.get(CouleurLigne.NOIRE), CouleurLigne.NOIRE, CouleurLigne.BLEUE), new Etat(new Point(0,-1),270f));
		hashPerdu.put(new LCC(hashLignes.get(CouleurLigne.NOIRE), CouleurLigne.BLEUE, CouleurLigne.BLANCHE), new Etat(new Point(0,-2),270f));
		
		hashPerdu.put(new LCC(hashLignes.get(CouleurLigne.ROUGE), CouleurLigne.BLEUE, CouleurLigne.NOIRE), new Etat(new Point(-1,0),90f));
		hashPerdu.put(new LCC(hashLignes.get(CouleurLigne.ROUGE), CouleurLigne.NOIRE, CouleurLigne.VERTE), new Etat(new Point(-1,1),90f));
		hashPerdu.put(new LCC(hashLignes.get(CouleurLigne.ROUGE), CouleurLigne.VERTE, CouleurLigne.BLANCHE), new Etat(new Point(-1,2),90f));
		hashPerdu.put(new LCC(hashLignes.get(CouleurLigne.ROUGE), CouleurLigne.VERTE, CouleurLigne.NOIRE), new Etat(new Point(-1,0),270f));
		hashPerdu.put(new LCC(hashLignes.get(CouleurLigne.ROUGE), CouleurLigne.NOIRE, CouleurLigne.BLEUE), new Etat(new Point(-1,-1),270f));
		hashPerdu.put(new LCC(hashLignes.get(CouleurLigne.ROUGE), CouleurLigne.BLEUE, CouleurLigne.BLANCHE), new Etat(new Point(-1,-2),270f));
	}
	
}