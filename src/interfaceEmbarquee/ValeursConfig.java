package interfaceEmbarquee;

import java.util.HashMap;

public class ValeursConfig {
	public static HashMap<String, String> musiquesConfig = new HashMap<>();
	public static HashMap<String, String> bruitagesConfig = new HashMap<>();
	public static HashMap<String, String> visagesConfig = new HashMap<>();
	
	
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
