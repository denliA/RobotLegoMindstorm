package capteurs;

public class Toucher implements Runnable{
	static float[] touched = new float[Capteur.TOUCHER.sampleSize()];
	private static boolean status;
	private static boolean touche;
	
	public static boolean detecterPalet() {
		Capteur.TOUCHER.fetchSample(touched, 0);
		if (touched[0]==1) {
			return true;
		}
		return false;	
	}
	
	public void run() {
		touche = detecterPalet();
	}
	
	public void startScan() {
		status = true;
	}
	
	public void stopScan() {
		status = false;
	}
	
	public boolean getStatus() {
		return(status);
	}
}
