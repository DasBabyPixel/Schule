package de.dasbabypixel;

import de.dasbabypixel.Graph.Algorithm;
import de.dasbabypixel.Graph.Algorithm.DijkstraData;
import de.dasbabypixel.Graph.Node;
import de.dasbabypixel.Graph.Path;
import de.dasbabypixel.Graph.PathWriter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Start {

	public static void main(String[] args) throws InterruptedException {

		//		int nodesCount = 1_000_00;
		//		int connectionsCount = 2_000_00;
		//		int paths = 100;
		//		int reachableCheckCount = 1000;
		//
		//		long time = System.currentTimeMillis();
		//		Random r = new Random();
		//
		//		Graph<Integer, Integer> graph = Graph.linkedGraph();
		//
		//		Map<Integer, Node<Integer, Integer>> nodes = new HashMap<>();
		//		for (int i = 0; i < nodesCount; i++) {
		//			nodes.put(i, graph.newNode(i));
		//		}
		//		for (int i = 0; i < connectionsCount; i++) {
		//			int id1 = r.nextInt(graph.nodes().size());
		//			int id2 = r.nextInt(graph.nodes().size());
		//			if (id1 == id2) {
		//				i--;
		//				continue;
		//			}
		//			nodes.get(id1).newConnection(nodes.get(id2), r.nextInt(1000));
		//		}
		//		ExecutorService service = Executors.newWorkStealingPool();
		//		CountDownLatch latch = new CountDownLatch(paths);
		//		for (int i = 0; i < paths; i++) {
		//			service.submit(() -> {
		//				Random random = new Random();
		//				int id1;
		//				int id2;
		//				do {
		//					id1 = random.nextInt(graph.nodes().size());
		//					id2 = random.nextInt(graph.nodes().size());
		//				} while (id1 == id2);
		//				Path<Integer, Integer> path = Algorithm.<Integer, Integer>dijkstra().search(graph,
		//						new DijkstraData<>(nodes.get(id1), nodes.get(id2),
		//								n -> n.way().longValue()));
		//
		//				try {
		//					PathWriter.simple.write(path, System.out);
		//				} catch (IOException e) {
		//					throw new RuntimeException(e);
		//				}
		//				latch.countDown();
		//			});
		//		}
		//		latch.await();
		//		CountDownLatch latch2 = new CountDownLatch(reachableCheckCount);
		//		for (int i = 0; i < reachableCheckCount; i++) {
		//			service.submit(() -> {
		//				Random random = new Random();
		//				int id = random.nextInt(graph.nodes().size());
		//				System.out.println(nodes.get(id).reachableNodes().size());
		//				latch2.countDown();
		//			});
		//		}
		//		latch2.await();
		//		System.out.println((System.currentTimeMillis() - time) + "ms");

		Graph<String, Integer> graph = Graph.linkedGraph();
		Node<String, Integer> berlin = graph.newNode("Berlin");
		Node<String, Integer> dresden = graph.newNode("Dresden");
		Node<String, Integer> erfurt = graph.newNode("Erfurt");
		Node<String, Integer> frankfurt = graph.newNode("Frankfurt");
		Node<String, Integer> hannover = graph.newNode("Hannover");
		Node<String, Integer> bremen = graph.newNode("Bremen");
		Node<String, Integer> hamburg = graph.newNode("Hamburg");
		Node<String, Integer> koeln = graph.newNode("Köln");
		Node<String, Integer> kassel = graph.newNode("Kassel");
		Node<String, Integer> leipzig = graph.newNode("Leipzig");
		Node<String, Integer> muenchen = graph.newNode("München");
		Node<String, Integer> nuernberg = graph.newNode("Nürnberg");
		Node<String, Integer> stuttgart = graph.newNode("Stuttgart");
		Node<String, Integer> wuerzburg = graph.newNode("Würzburg");

		connection(berlin, hannover, 260);
		connection(berlin, hamburg, 280);
		connection(berlin, leipzig, 180);
		connection(dresden, leipzig, 140);
		connection(erfurt, frankfurt, 270);
		connection(erfurt, kassel, 140);
		connection(erfurt, leipzig, 170);
		connection(erfurt, nuernberg, 260);
		connection(erfurt, wuerzburg, 300);
		connection(frankfurt, koeln, 190);
		connection(frankfurt, kassel, 190);
		connection(frankfurt, stuttgart, 200);
		connection(frankfurt, wuerzburg, 130);
		connection(hannover, bremen, 120);
		connection(hannover, hamburg, 150);
		connection(hannover, kassel, 240);
		connection(muenchen, nuernberg, 160);
		connection(muenchen, stuttgart, 210);
		connection(nuernberg, wuerzburg, 110);

		System.out.println(graph);

		graph.writeAdjacencyMatrix(System.out);
	}

	private static void connection(Node<String, Integer> n1, Node<String, Integer> n2, int way) {
		n1.newConnection(n2, way);
		n2.newConnection(n1, way);
	}
}
