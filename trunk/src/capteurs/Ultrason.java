package capteurs;

public class Ultrason {
	
	//Attributs 
	//float car utilis� par leJOS
	private static float distance;
	private static boolean bruitDetecte;
	//pour savoir si le capteur est effectivement actif
	private static boolean status;
	
	//M�thodes
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
	//Renvoie la distance mesur�e par le capteur
	public static float getDistance() {
		float[] tabDistance = new float[Capteur.ULTRASON.sampleSize()];
		Capteur.ULTRASON.fetchSample(tabDistance, 0);
		distance = tabDistance[0];
		return distance;
	}
	//D�tecte la pr�sence d'un autre robot
	public static boolean autreRobot() {
		bruitDetecte=false;
		
		float[] autreRobot = new float[Capteur.ECOUTE.sampleSize()];
		Capteur.ECOUTE.fetchSample(autreRobot, 0);
		
		//ECOUTE renvoie 1 ou 0
		if(autreRobot[0]==1) {bruitDetecte=true;}
		
		return bruitDetecte;
	}
	
}
