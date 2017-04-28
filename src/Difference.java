import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

/**
 * @author  administrator
 */
public class Difference {

	private final static String DELIMITER = "-->";

	/**
	 * @uml.property  name="nodes"
	 * @uml.associationEnd  qualifier="getLabel:java.lang.String Comparision"
	 */
	private Hashtable<String, Comparision> nodes = new Hashtable<String, Comparision>();
	/**
	 * @uml.property  name="edges"
	 * @uml.associationEnd  qualifier="get:java.lang.String Comparision"
	 */
	private Hashtable<String, Comparision> edges = new Hashtable<String, Comparision>();

	/**
	 * @uml.property  name="nodes_common"
	 */
	private Hashtable<String, Comparision> nodes_common = new Hashtable<String, Comparision>();
	/**
	 * @uml.property  name="edges_common"
	 */
	private Hashtable<String, Comparision> edges_common = new Hashtable<String, Comparision>();
	
	private Hashtable<String, Comparision> nodes_different = new Hashtable<String, Comparision>();
	
	private Hashtable<String, Comparision> edges_different = new Hashtable<String, Comparision>();

	/**
	 * @uml.property  name="graph1"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private Graph cmdb;
	/**
	 * @uml.property  name="graph2"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private Graph anomaly;

	public Difference(final Graph graph1, final Graph graph2) {
		this.cmdb = graph1;
		this.anomaly = graph2;
	}

	public void calcDifference() {
		putInMapNodesG1(cmdb);
		putInMapNodesG2(anomaly);

		putInMapEdgesG1(cmdb);
		putInMapEdgesG2(anomaly);
	}

	public void identifyCommonNodes() {
		Iterator<Map.Entry<String, Comparision>> it = nodes.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry<String, Comparision> node = it.next();
			if (node.getValue().isInG1() && node.getValue().isInG2()) {
				nodes_common.put(node.getKey(), node.getValue());
			}

			if (node.getKey() == null || node.getKey() == "") {
				it.remove();
			}
		}
	}
	
	public void identifyDifferentNodes() {
		Iterator<Map.Entry<String, Comparision>> it = nodes.entrySet().iterator();
		
		while(it.hasNext()) {
			Map.Entry<String, Comparision> node = it.next();
			if((node.getValue().isInG1() && !node.getValue().isInG2()) || (node.getValue().isInG2() && !node.getValue().isInG1())) {
				nodes_different.put(node.getKey(), node.getValue());
			}
			
			if(node.getKey() == null || node.getKey() == "") {
				it.remove();
			}
		}
	}

	public void identifyCommonEdges() {
		Iterator<Map.Entry<String, Comparision>> it = edges.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry<String, Comparision> edge = it.next();
			if (edge.getValue().isInG1() && edge.getValue().isInG2()) {
				edges_common.put(edge.getKey(), edge.getValue());
			}

			if (edge.getKey() == null || edge.getKey() == "") {
				it.remove();
			}
		}
	}
	
	public void identifyDifferentEdges() {
		Iterator<Map.Entry<String, Comparision>> it = edges.entrySet().iterator();
		
		while(it.hasNext()) {
			Map.Entry<String, Comparision> edge = it.next();
			if((edge.getValue().isInG1() && !edge.getValue().isInG2()) || (edge.getValue().isInG2() && !edge.getValue().isInG1())) {
				edges_different.put(edge.getKey(), edge.getValue());
			}
			
			if(edge.getKey() == null || edge.getKey() == "") {
				it.remove();
			}
		}
	}

	public double calcSimilarity() {
		double similarityNodes = nodes_common.size() / (1 + nodes.size() - nodes_common.size());
		double similarityEdges = edges_common.size() / (1 + edges.size() - edges_common.size());

		return (similarityNodes + similarityEdges) / (nodes.size() + edges.size());
	}

	public boolean isAnomalyLikeSubgraph(double similarity, double minA_prob) {
		if (similarity >= minA_prob) {
			return true;
		} else {
			return false;
		}
	}

	private void putInMapNodesG1(final Graph graph) {
		for (int i = 0; i < graph.getNodeLabels().size(); i++) {
			nodes.put(graph.getLabel(i), new Comparision(true, false));
		}
	}

	private void putInMapNodesG2(final Graph graph) {
		for (int i = 0; i < graph.getNodeLabels().size(); i++) {
			if (!nodes.containsKey(graph.getLabel(i))) {
				nodes.put(graph.getLabel(i), new Comparision(false, true));
			} else {
				nodes.get(graph.getLabel(i)).setG2(true);
			}
		}
	}

	private void putInMapEdgesG1(final Graph graph) {
		boolean[][] adjacency = graph.getAdjacency();

		for (int row = 0; row < adjacency.length; row++) {
			for (int col = 0; col < adjacency.length; col++) {
				if (adjacency[row][col]) {
					edges.put(graph.getLabel(row).concat(DELIMITER).concat(graph.getLabel(col)),
							new Comparision(true, false));
				}
			}
		}
	}

	private void putInMapEdgesG2(final Graph graph) {
		boolean[][] adjacency = graph.getAdjacency();
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

	public Hashtable<String, Comparision> getNodes() {
		return nodes;
	}

	public Hashtable<String, Comparision> getEdges() {
		return edges;
	}
	
	public Hashtable<String, Comparision> getCommonNodes() {
		return nodes_common;
	}
	
	public Hashtable<String, Comparision> getCommonEdges() {
		return edges_common;
	}
	
	public Hashtable<String, Comparision> getDifferentNodes() {
		return nodes_different;
	}
	
	public Hashtable<String, Comparision> getDifferentEdges() {
		return edges_different;
	}
}