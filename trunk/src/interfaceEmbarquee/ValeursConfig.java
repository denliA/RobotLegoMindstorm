package interfaceEmbarquee;
import java.util.HashMap;

/**
 * <p>Classe qui lie un nom de fichier à sa configuration. Permet de lancer une musique ou un bruitage qui correspond à un fichier.wav selon la cofiguration choisie par l'utilisateur.</p>
 * 
 * @see Picker
 * 
 */

public class ValeursConfig {
	/**Dictionnaire qui associe un nom de fichier à chaque nom de musique.*/
	public static HashMap<String, String> musiquesConfig = new HashMap<>();
	/**Dictionnaire qui associe un nom de fichier à chaque nom de bruitage.*/
	public static HashMap<String, String> bruitagesConfig = new HashMap<>();
	
	/**
	 * <p>Bloc statique exécuté une seule fois quand la classe est chargée en mémoire. Permet de remplir le dictionnaire ci dessus.</p>
	 * 
	 */
	static {
		musiquesConfig.put("megalovania", "MEGALOVANIA.wav");
		musiquesConfig.put("victory", "VictorySong.wav");
		musiquesConfig.put("losing", "LosingSong.wav");
		
		bruitagesConfig.put("wow", "Wow.wav");
		bruitagesConfig.put("easy", "Easy.wav");
		bruitagesConfig.put("ohNo", "OhNo.wav");
		bruitagesConfig.put("nani", "Nani.wav");
		bruitagesConfig.put("missionFailed", "MissionFailed.wav");
		bruitagesConfig.put("whilhelmScream", "WhilhelmScream.wav");
		bruitagesConfig.put("goatScream", "GoatScream.wav");
		bruitagesConfig.put("nope", "Nope.wav");
		bruitagesConfig.put("ennemySpotted", "EnemySpotted.wav");
		bruitagesConfig.put("targetAcquired", "TargetAcquired.wav");
		bruitagesConfig.put("targetLocked", "TargetLocked.wav");
		
	}
}
