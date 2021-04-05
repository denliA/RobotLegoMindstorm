package capteurs;

import java.util.Arrays;

import lejos.hardware.Button;
public class TestGetCouleur {

	public static void main(String[] args) {
		int button = -1;
		boolean buffer = true;
		while(button != Button.ID_ESCAPE) {
			if (buffer)
				testGetLastCouleur();
			else 
				testGetCouleurLigne();
			buffer=!buffer;
			button = Button.waitForAnyPress();
		}

	}
	
	
	public static void testGetCouleurLigne() {
		int button = -1;
		float[] res, res2;
		CouleurLigne couleur;
		Couleur.setScanMode(Couleur.RGBMODE);
		Couleur.startScanAtRate(10);
		while (button != Button.ID_LEFT && button != Button.ID_RIGHT) {
			button = Button.waitForAnyPress();
			if (button == Button.ID_ENTER) {
				couleur = Couleur.getLastCouleur();
				res = Couleur.getRGB();
				res2 =Couleur.getRatios();
				System.out.println(Float.toString(res[0]));
				System.out.println(Float.toString(res[1]));
				System.out.println(Float.toString(res[2]));
				System.out.println(Float.toString(res2[0]));
				System.out.println(Float.toString(res2[1]));
				System.out.println(Float.toString(res2[2]));
				System.out.println();
				System.out.println(couleur.toString()+"\n");
			}
			Button.waitForAnyEvent();
		}
	}
	
	public static void testGetLastCouleur() {
		int button = -1;
		CouleurLigne couleur;
		Couleur.setScanMode((byte) (Couleur.BUFFERING|Couleur.RGBMODE));
		Couleur.startScanAtRate(10);
		while (button != Button.ID_LEFT && button != Button.ID_RIGHT) {
			button = Button.waitForAnyPress();
			if (button == Button.ID_ENTER) {
				couleur = Couleur.getLastCouleur();
				System.out.println("\n\\nn"+couleur);
				System.out.println(Arrays.asList(Couleur.buffer.historique(10)));
			}
			Button.waitForAnyEvent();
		}
		
	}

}
