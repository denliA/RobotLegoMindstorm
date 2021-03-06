package capteurs;

public class Toucher implements Runnable{
	
	//Atrributs de la classe Toucher
	private static boolean status;
	private static boolean touche;
	
	//Detection de l'activation du capteur de contact
	public static boolean detecterPalet() {
		float[] touched = new float[Capteur.TOUCHER.sampleSize()];
		Capteur.TOUCHER.fetchSample(touched, 0);
		touche = (touched[0]==1);
		return(touche);
	}
	
	//Pour implementer Runnable
	public void run() {
		detecterPalet();
	}
	
	//Pour activer la lecture de la valeur envoy�e par le scanner
	public void startScan() {
		status = true;
	}
	
	//Pour d�sactiver la lecture de la valeur envoy�e par le scanner
	public void stopScan() {
		status = false;
	}
	
	//Retourne le status pour savoir l'�tat actuel
	public boolean getStatus() {
		return(status);
	}
}
