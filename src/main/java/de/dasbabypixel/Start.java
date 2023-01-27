package de.dasbabypixel;

import de.dasbabypixel.Tree.AVLBaum;
import de.dasbabypixel.Tree.IterationStrategy;
import de.dasbabypixel.Tree.TreeIterator;

import java.util.Comparator;
import java.util.Random;

public class Start {

	public static void main(String[] args) {
		AVLBaum<Integer> baum = new AVLBaum<>(Comparator.comparingInt(o -> o));
		Random r = new Random(2);
		System.out.println(baum.size());
		for (int i = 0; i < 15; i++) {
			int j = r.nextInt(10000);
			j = i;
			if (!baum.contains(j)) {
				baum.add(j);
			} else {
				i--;
			}
		}

		TreeIterator<Integer> it = baum.iterator(IterationStrategy.IN_ORDER);
		StringBuilder sb = new StringBuilder();
		while (it.hasNext()) {
			int i = it.next();
			sb.append(String.format(" %s%s", repeat(' ', 7 * it.depth()), i));
			sb.append('\n');
			System.out.printf(" %s%s%n", repeat(' ', 7 * it.depth()), i);
		}
		System.out.println(sb.toString().equals(baum.toString(IterationStrategy.IN_ORDER)));
		System.out.println(baum.size());
		it = baum.iterator();
				while (it.hasNext()) {
					int i = it.next();
					if (r.nextDouble() < 0.3) {
						System.out.println("remove " + i);
						it.remove();
					}
				}
		System.out.println(baum);
		System.out.println(baum.height());
		System.out.println(baum.size());
	}

	private static String repeat(char n, int times) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < times; i++)
			sb.append(n);
		return sb.toString();
	}

	private static BAUM einfuegen(BAUM baum, String... elements) {
		for (String element : elements) {
			baum.einfuegen(new NAME(element));
		}
		return baum;
	}
}
