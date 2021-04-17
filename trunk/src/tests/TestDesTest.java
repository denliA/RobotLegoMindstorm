package tests;

import java.util.Scanner;

import interfaceEmbarquee.Lancable;

public class TestDesTest {

	public static void main(String[] args) {
		Lancable [] imposes = {new P1(),new P2(),new P3(),new P4(),new P5(),new P6(),new P7()};
		Lancable [] basiques = {new NFBM1(),new NFBA1(),new NFBA2(),new NFBM2(),new NFBM3(),new NFBA3(),new NFBM4(),new NFBM5(),new NFBM6()};
		Lancable [] avancees = {new NFA0(),new NFA1(),new NFA2(),new NFA3(),new NFA4(),new NFA5(),new NFA6(),new NFA7()};
		Lancable [] optionnels = {new IN(),new OFA1()};
		int indice;
		int groupe;
		System.out.println("Quel test a lancer ? {1 : sotoTests, 2 : basiques, 3 : avancees, 4 : optionnels}");
		Scanner lectureClavier = new Scanner(System.in);
		groupe = lectureClavier.nextInt();
		System.out.println("Numero du test ? (Commence à 1)");
		indice = lectureClavier.nextInt();
		indice--;
		switch (groupe) {
        case 1:  imposes[indice].lancer();
                 break;
        case 2:  basiques[indice].lancer();
                 break;
        case 3:  avancees[indice].lancer();
                 break;
        case 4:  optionnels[indice].lancer();
        		 break; 		 
        default: System.out.println("Mauvais indice choisi");
                 break;
		}
	}
}
