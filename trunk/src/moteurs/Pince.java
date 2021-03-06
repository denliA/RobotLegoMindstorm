package moteurs;

public class Pince {
	
	//Attribut
	//les pinces s'ouvriront toujours de la m�me valeur par d�faut
	private static final float OUVERTURE;//TO DO 
	private static boolean ouvert;
	
	//M�thodes
	public static boolean getOuvert() {
		return ouvert;
	}
	
	//Ouverture et fermeture des pinces
	//m�thodes modifiant la valeur de ouvert simultan�ment
	public static void ouvrir() {
		if(ouvert==true) {
			//TO DO			
		}
		else {
			
			ouvert = true;
		}
	}
	public static void fermer() {
		if(ouvert==false) {
			//TO DO
		}
		else {
			Moteur.MOTEUR_PINCE.setSpeed();
			Moteur.MOTEUR_PINCE.backward();
			Delay.msDelay();
			ouvert = false;
		}
	}
}
