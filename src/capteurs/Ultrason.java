package capteurs;

public class Ultrason {
	
	//Attributs 
	//float car utilisé par leJOS
	private static float distance;
	private static boolean bruitDetecte;
	//pour savoir si le capteur est effectivement actif
	private static boolean status;
	
	//Méthodes
	//Modifient status
	public static void startScan() {
		status = true;
	}
	public static void stopScan() {
		status = false;
	}
	//Donne la valuer de status
	public static boolean getStatus() {
		return status;
	}
	//Gestion de la distance mesurée par le capteur
	public static void setDistance() {
		float[] tabDistance = new float[Capteur.ULTRASON.sampleSize()];
		Capteur.ULTRASON.fetchSample(tabDistance, 0);
		distance = tabDistance[0];
	}
	public static float getDistance() {
		return distance;
	}
	//Détection de la présence d'un autre robot
	public static void setBruitDetecte() {
		bruitDetecte=false;
		
		float[] autreRobot = new float[Capteur.ECOUTE.sampleSize()];
		Capteur.ECOUTE.fetchSample(autreRobot, 0);
		
		//ECOUTE renvoie 1 ou 0
		if(autreRobot[0]==1) {bruitDetecte=true;}
	}
	public static boolean getBruitDetecte() {
		return bruitDetecte;
	}
	
}
