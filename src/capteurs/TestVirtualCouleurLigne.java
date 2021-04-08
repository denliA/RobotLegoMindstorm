package capteurs;

import java.util.Map;

import capteurs.CouleurLigne.ContextePID;


public class TestVirtualCouleurLigne {
	public static void main(String[] args) {
		testsIntervallesCouleurs(CouleurLigne.principales);
	}
	

	public static void testIntervalles() {
		Intervalle i1 = new Intervalle(new float[] {1,5}, new float[] {3,6});
		Intervalle i2 = new Intervalle(new float[] {2,7}, new float[] {4,8});
		System.out.println("i1:"+i1);
		System.out.println("i2:"+i2);
		
		System.out.println("intersection: " + i1.intersection(i2));
		System.out.println("entreDeux: " + i1.entreDeux(i2));
	}
	
	
	public static void testsIntervallesCouleurs(CouleurLigne [] couleurs) {
		for (CouleurLigne c : couleurs) {
			System.out.println("Couleur: "+c);
			if(c.IRatios!=null) 
				System.out.println("IRatios."+c.IRatios);
			if (c.IRGB!=null) {
				System.out.println("IRGB : "+c.IRGB);
			}
			System.out.println("	Gris: "+CouleurLigne.GRIS.IRatios);
			System.out.println("	contexte IRatios : "+c.contexteGris.indice+"  "+c.contexteGris.target);
			for (Map.Entry<CouleurLigne, CouleurLigne.ContextePID> e : c.intersections.entrySet()) {
				CouleurLigne cc = e.getKey();
				ContextePID contexte = e.getValue();
				if (c==cc) continue;
				System.out.println("	"+cc+" : ");
				if(c.IRatios!=null && cc.IRatios != null && contexte.mode_rgb)  {
					System.out.println("	IRatios : ");
					System.out.println("	entreDeux IRatios : "+c.IRatios.entreDeux(cc.IRatios));
					System.out.println("	Contexte: "+contexte.indice+" target:"+contexte.target);
				}
				if(c.IRGB!=null && cc.IRGB != null && !contexte.mode_rgb) {
					System.out.println("	IRGB.");
					System.out.println("	entreDeux IRGB : "+c.IRGB.entreDeux(cc.IRatios));
					System.out.println("	Contexte: "+contexte.indice+" target:"+contexte.target);
				}
			}
			System.out.println("\n");
		}
	}
}
