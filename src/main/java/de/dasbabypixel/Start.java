package de.dasbabypixel;

import de.dasbabypixel.AVLBaum.IterationStrategy;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;

public class Start {

	public static void main(String[] args) {
		//		BAUM baum =
		//				einfuegen(new BAUM(), "c1", "g1", "l1", "a1", "h1", "i1", "k1", "g1", "g2", "z1");
		//		baum.ausgeben();
		//		System.out.println(baum.suchen(new NameSuchwort("g1")));
		//		System.out.println(baum.suchen(new NameSuchwort("z1")));
		//		System.out.println("remove: " + baum.entfernen(new NameSuchwort("g1")));
		//		baum.ausgeben();
		//		System.out.println(baum.anzahlKnoten());
		//		AVLBaum<String> baum = new AVLBaum<>(String::compareToIgnoreCase);
		AVLBaum<Integer> baum = new AVLBaum<>(Comparator.comparingInt(o -> o));
//		baum.add(0);
//		baum.add(1);
//		baum.add(2);
//		baum.add(3);
		for (int i = 0; i < 15; i++) {
			int j = new Random().nextInt(10000);
			if (!baum.contains(j)) {
				baum.add(j);
			} else {
				i--;
			}
		}

		Iterator<Integer> it = baum.iterator(IterationStrategy.POST_ORDER);
		while(it.hasNext()){
			System.out.println(it.next());
		}

		System.out.println(baum.toString(IterationStrategy.POST_ORDER));
		System.out.println(baum.height());
	}

	private static BAUM einfuegen(BAUM baum, String... elements) {
		for (String element : elements) {
			baum.einfuegen(new NAME(element));
		}
		return baum;
	}
}
