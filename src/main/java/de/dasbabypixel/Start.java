package de.dasbabypixel;

import de.dasbabypixel.Graph.Algorithm;
import de.dasbabypixel.Graph.Algorithm.DijkstraData;
import de.dasbabypixel.Graph.Node;
import de.dasbabypixel.Graph.Path;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Start {

	public static void main(String[] args) throws InterruptedException {

		int nodesCount = 1_000_00;
		int connectionsCount = 2_000_00;
		int paths = 100;

		long time = System.currentTimeMillis();
		Random r = new Random();

		Graph<Integer, Integer> graph = Graph.graph();

		Map<Integer, Node<Integer, Integer>> nodes = new HashMap<>();
		for (int i = 0; i < nodesCount; i++) {
			nodes.put(i, graph.newNode(i));
		}
		for (int i = 0; i < connectionsCount; i++) {
			int id1 = r.nextInt(graph.nodes().size());
			int id2 = r.nextInt(graph.nodes().size());
			if (id1 == id2) {
				i--;
				continue;
			}
			nodes.get(id1).newConnection(nodes.get(id2), r.nextInt(1000));
		}
		ExecutorService service = Executors.newWorkStealingPool();
		CountDownLatch latch = new CountDownLatch(paths);
		for (int i = 0; i < paths; i++) {
			service.submit(() -> {
				Random random = new Random();
				int id1;
				int id2;
				do {
					id1= random.nextInt(graph.nodes().size());
					id2= random.nextInt(graph.nodes().size());
				} while(id1==id2);
				Path<Integer, Integer> path = Algorithm.<Integer, Integer>dijkstra().search(graph,
						new DijkstraData<>(nodes.get(id1), nodes.get(id2),
								n -> n.way().longValue()));

				System.out.println(path);
				latch.countDown();
			});
		}
		latch.await();
		System.out.println((System.currentTimeMillis() - time) + "ms");
	}
}
