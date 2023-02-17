package de.dasbabypixel;

import java.util.Iterator;

public class Start {

	public static void main(String[] args) {

		Graph<String, Integer> graph = Graph.graph();
		Graph.Node<String, Integer> n1 = graph.newNode("n1");
		Graph.Node<String, Integer> n2 = graph.newNode("n2");
		Graph.Node<String, Integer> n3 = graph.newNode("n3");
		Graph.Node<String, Integer> n4 = graph.newNode("n4");
		Graph.Node<String, Integer> n5 = graph.newNode("n5");
		n1.newConnection(n2, 1);
		n1.newConnection(n3, 4);
		n1.newConnection(n5, 4);
		n2.newConnection(n1, 1);
		n2.newConnection(n4, 1);
		n4.newConnection(n3, 1);
		n4.newConnection(n1, 1);
		n5.newConnection(n4, 4);

		System.out.println(graph.connections());

		Iterator<Graph.Node<String, Integer>> it = graph.nodes().iterator();
		System.out.println(0);
		it.next();
		System.out.println(1);
		it.next();
		System.out.println(1);
		it.next();
		System.out.println(1);
		it.remove();
		System.out.println(1);

		System.out.println(graph);

		System.out.println(graph.search(Graph.Algorithm.<String, Integer>dijkstra()
				.withData(new Graph.Algorithm.DijkstraData<>(n1, n3, c -> c.way().longValue()))));

		System.out.println(graph);
	}
}
