package de.dasbabypixel;

import de.dasbabypixel.Graph.Algorithm;
import de.dasbabypixel.Graph.Algorithm.DijkstraData;
import de.dasbabypixel.Graph.Node;
import de.dasbabypixel.Graph.Path;
import de.dasbabypixel.Graph.PathWriter;
import gamelauncher.engine.util.GameException;
import gamelauncher.lwjgl.LWJGLGameLauncher;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Start {

	public static Graph<String, Integer> graph = Graph.linkedGraph();

	public static void main(String[] args)
			throws InterruptedException, GameException, URISyntaxException {

		System.setProperty("file.encoding", "UTF-8");

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

		LWJGLGameLauncher launcher = new LWJGLGameLauncher();
		launcher.pluginManager().loadPlugin(
				Paths.get(Start.class.getProtectionDomain().getCodeSource().getLocation().toURI()));
		launcher.start(args);

	}

	private static void connection(Node<String, Integer> n1, Node<String, Integer> n2, int way) {
		n1.newConnection(n2, way);
		n2.newConnection(n1, way);
	}
}
