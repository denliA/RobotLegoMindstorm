package capteurs;

public class Ultrason {
	
	//Attributs 
	//float car utilisé par leJOS
	private static float[] distance;
	private static boolean bruit_detecte;
	//pour savoir si le capteur est effectivement actif
	private static boolean status;
	
	//Méthodes
	//Modifient status
	public static void startScan() {
		
	}
	public static void stopScan() {
		
	}
	//Donne la valuer de status
	public static boolean getStatus() {
		return status;
	}
	//Renvoie la distance mesurée par le capteur
	public static float getDistance() {
		float[] distance = new float[Capteur.ULTRASON.sampleSize()];
		return distance[0];
	}
	//Détecte la présence d'un autre robot
	public static boolean autreRobot() {
		boolean autreRobot=false;
		return autreRobot;
	}
	
}
