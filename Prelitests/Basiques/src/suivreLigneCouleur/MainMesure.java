package suivreLigneCouleur;
import lejos.hardware.Button;
import lejos.utility.Timer;
import lejos.utility.TimerListener;
import deplacer.Droit;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainMesure {
	public static void main(String[] args) {
		int accel=1500, vitesse=180, delai=50;
		Date date = new Date() ;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss") ;
		SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyymmdd");
		SuivreLigneCouleur.creerFichier("./Scans/20210403/LigneGriseGBleueD_"+dateFormat.format(date));
		SuivreLigneCouleur.metadataFichier(vitesse, accel, delai, 56);
		Droit.droitMoteur(accel, vitesse);
		Timer timer = new Timer(delai, new task_write());
		timer.start();
		while (Button.ENTER.isUp())
			;
		SuivreLigneCouleur.fermerFichier();
		timer.stop();
		

	}
}

class task_write implements TimerListener {
	public void timedOut() {
		SuivreLigneCouleur.mesurerCouleurFich();
	}
}