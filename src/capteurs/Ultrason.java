package capteurs;

public class Ultrason {
	
	//Attributs 
	//float car utilis� par leJOS
	private static float[] distance;
	private static boolean bruit_detecte;
	//pour savoir si le capteur est effectivement actif
	private static boolean status;
	
	//M�thodes
	//Modifient status
	public static void startScan() {
		
	}
	public static void stopScan() {
		
	}
	//Donne la valuer de status
	public static boolean getStatus() {
		return status;
	}
	//Renvoie la distance mesur�e par le capteur
	public static float getDistance() {
		float[] distance = new float[Capteur.ULTRASON.sampleSize()];
		return distance[0];
	}
	//D�tecte la pr�sence d'un autre robot
	public static boolean autreRobot() {
		boolean autreRobot=false;
		return autreRobot;
	}
	
}
