import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class Difference {

	private final static String DELIMITER = "-->";

	private Hashtable<String, Comparision> nodes = new Hashtable<String, Comparision>();
	private Hashtable<String, Comparision> edges = new Hashtable<String, Comparision>();

	private Graph graph1;
	private Graph graph2;

	public Difference(final Graph graph1, final Graph graph2) {
		this.graph1 = graph1;
		this.graph2 = graph2;
	}

	public void calcDifference() {
		putInMapNodes(graph1, true);
		putInMapNodes(graph2, false);

		putInMapEdges(graph1, true);
		putInMapEdges(graph2, false);
	}

	public static void main(String[] args) {
		HashMap<Integer, String> h1 = new HashMap<>();
		h1.put(0, "A");
		h1.put(1, "B");
		h1.put(2, "C");
		h1.put(3, "D");
		Graph g1 = new Graph(new boolean[][] { { false, false, false, true }, { true, false, false, false },
				{ true, false, false, false }, { false, true, false, false } }, h1);

		HashMap<Integer, String> h2 = new HashMap<>();
		h2.put(0, "A");
		h2.put(1, "D");
		h2.put(2, "E");
		h2.put(3, "F");
		h2.put(4, "G");
		Graph g2 = new Graph(new boolean[][] { { false, true, false, true, true }, { false, false, true, false, false },
				{ false, false, false, false, true }, { false, false, false, false, false },
				{ false, false, false, false, false } }, h2);

		Difference diff = new Difference(g1, g2);
		diff.calcDifference();

		Hashtable<String, Comparision> nodes = new Hashtable<String, Comparision>(diff.getNodes());
		Hashtable<String, Comparision> edges = new Hashtable<String, Comparision>(diff.getEdges());

		for (String key : nodes.keySet()) {
			System.out.println("Node " + key + " in Graph1: " + nodes.get(key).isInG1() + "; in Graph2: "
					+ nodes.get(key).isInG2());
		}
		System.out.println("----------------");
		for (String key : edges.keySet()) {
			System.out.println("Edge " + key + " in Graph1: " + edges.get(key).isInG1() + "; in Graph2: "
					+ edges.get(key).isInG2());
		}
		Graph difference = diff.calcAdjacency();
		difference.printDiffGraph();
	}

	// Part 1 of algorithm
	private void putInMapNodes(final Graph graph, final boolean graph1) {
		if (graph1) {
			for (int i = 0; i < graph.getNodeLabels().size(); i++) {
				nodes.put(graph.getLabel(i), new Comparision(true, false));
			}
		} else {
			for (int i = 0; i < graph.getNodeLabels().size(); i++) {
				if (!nodes.containsKey(graph.getLabel(i))) {
					nodes.put(graph.getLabel(i), new Comparision(false, true));
				} else {
					nodes.get(graph.getLabel(i)).setG2(true);
				}
			}
		}
	}

	private void putInMapEdges(final Graph graph, final boolean graph1) {
		boolean[][] adjacency = graph.getAdjacency();

		if (graph1) {
			for (int row = 0; row < adjacency.length; row++) {
				for (int col = 0; col < adjacency.length; col++) {
					if (adjacency[row][col]) {
						edges.put(graph.getLabel(row).concat(DELIMITER).concat(graph.getLabel(col)),
								new Comparision(true, false));
					}
				}
			}
		} else {
			for (int row = 0; row < adjacency.length; row++) {
				for (int col = 0; col < adjacency.length; col++) {
					if (adjacency[row][col]) {
						String tmp = graph.getLabel(row).concat(DELIMITER).concat(graph.getLabel(col));
						if (!edges.containsKey(tmp)) {
							edges.put(tmp, new Comparision(false, true));
						} else {
							edges.get(tmp).setG2(true);
						}
					}
				}
			}
		}
	}

	public Graph calcAdjacency() {
		int size = nodes.size();
		int[][] adjacency = new int[size][size];

		ArrayList<String> nodes = new ArrayList<>(this.nodes.keySet());
		Collections.sort(nodes);

		Map<String, Integer> map = new HashMap<>();
		for (int i = 0; i < nodes.size(); i++) {
			map.put(nodes.get(i), i);
			// System.out.println(nodes.get(i) + " " + map.get(nodes.get(i)));
		}

		Map<Integer, String> map2 = new HashMap<>();
		for (int i = 0; i < nodes.size(); i++) {
			map2.put(i, nodes.get(i));
		}

		ArrayList<String> e = new ArrayList<>(this.edges.keySet());
		Collections.sort(e);
		Comparision c;
		String[] startAndEndNode = new String[2];

		for (int i = 0; i < e.size(); i++) {
			c = this.edges.get(e.get(i));
			startAndEndNode = e.get(i).split("\\-->");
			int fst = map.get(startAndEndNode[0]);
			int snd = map.get(startAndEndNode[1]);
			if (!c.isInG1() && c.isInG2()) {
				adjacency[fst][snd] = 1;
			} else if (c.isInG1() && !c.isInG2()) {
				adjacency[fst][snd] = -1;
			}
		}
		return new Graph(adjacency, map2);
	}

	public Hashtable<String, Comparision> getNodes() {
		return nodes;
	}

	public Hashtable<String, Comparision> getEdges() {
		return edges;
	}
}