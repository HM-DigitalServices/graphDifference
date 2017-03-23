import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseBuilder;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class ImportDataInNeo4j {
	private final static String DB_PATH = "C:/Users/moest/OneDrive/Dokumente/Neo4j/default.graphdb";

	@SuppressWarnings("unused")
	private final static String DELETE_ALL = "MATCH (n) OPTIONAL MATCH (n)-[r]-() DELETE n,r";
	private Map<Integer, String> nodeLabels = new HashMap<Integer, String>();
	private static boolean[][] adjacency;

	public ImportDataInNeo4j() {
	}

	public static void main(String[] args) {
		ImportDataInNeo4j obj = new ImportDataInNeo4j();

		GraphDatabaseBuilder d = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder(new File(DB_PATH));
		GraphDatabaseService x = d.newGraphDatabase();

		try (Transaction tx = x.beginTx()) {
			obj.createGraph(x);
			obj.createAdjacency(x);
			obj.printAdjacency();
			obj.createNodes(x, obj);
			
			tx.success();
		}
	}
	
	private void createNodes(GraphDatabaseService x, ImportDataInNeo4j obj) {
		int nodeSize = 1000;
		for (int i = 0; i < nodeSize; i++) {
			obj.createNode(x, i, NodeLabel.LICENCE);
			obj.createNode(x, i, NodeLabel.VM);
			obj.createNode(x, i, NodeLabel.SERVICE);
			obj.createNode(x, i, NodeLabel.RAM);
			obj.createNode(x, i, NodeLabel.CPU);
			obj.createNode(x, i, NodeLabel.SOFTWARE);
			obj.createNode(x, i, NodeLabel.OS);
			obj.createNode(x, i, NodeLabel.HARDDISK);
			obj.createNode(x, i, NodeLabel.MANUFACTURER);
		}
		for (int i = 0; i < 100; i++) {
			obj.createNode(x, i, NodeLabel.SERVER);
		}
	}

	private void createNode(GraphDatabaseService x, int counter, NodeLabel nL) {
		Node node = x.createNode(nL);
		StringBuffer sB = new StringBuffer();

		sB.append(nL.toString());
		node.setProperty("Name", sB.append(counter).toString());
	}

	private void createGraph(GraphDatabaseService x) {
		Node a = x.createNode(NodeLabel.VM);
		Node b = x.createNode(NodeLabel.SERVER);
		Node c = x.createNode(NodeLabel.OS);
		Node dd = x.createNode(NodeLabel.RAM);

		a.setProperty("Track", "one");
		a.setProperty("Kind of", "Competition");
		b.setProperty("Track", "two");
		b.setProperty("Kind of", "training");
		c.setProperty("Track", "four");
		c.setProperty("Kind of", "training");
		dd.setProperty("Track", "five");
		dd.setProperty("Kind of", "intervall");

		a.createRelationshipTo(b, RelationLabel.IN);
		b.createRelationshipTo(c, RelationLabel.REQUIRES);
		c.createRelationshipTo(a, RelationLabel.DEPENDS);
	}

	// Annahme n.getId() startet immer bei "index"=0
	private void createAdjacency(GraphDatabaseService x) {
		ArrayList<Node> nodes = new ArrayList<Node>();
		ResourceIterator<Node> iter = x.getAllNodes().iterator();
		while (iter.hasNext()) {
			Node n = iter.next();
			nodes.add(n);
			// System.out.println(n.getProperty("Track") + " " + n.getId());
		}
		adjacency = new boolean[nodes.size()][nodes.size()];
		for (int i = 0; i < nodes.size(); i++) {
			Node n = nodes.get(i);
			nodeLabels.put((int) n.getId(), (String) n.getProperty("Track"));
			Iterator<Relationship> iter2 = n.getRelationships(Direction.OUTGOING).iterator();
			while (iter2.hasNext()) {
				int fst = (int) n.getId();
				int snd = (int) iter2.next().getEndNode().getId();
				adjacency[fst][snd] = true;
			}
		}
	}

	public Graph getAdjacency() {
		return new Graph(adjacency, nodeLabels);
	}

	private void printAdjacency() {
		int fieldWidth = 10;
		String formatS = "|%" + fieldWidth + "s";
		String formatB = "|%" + fieldWidth + "b";
		System.out.print("Adjacency:\n----------\n ");
		for (int i = 0; i < fieldWidth; i++) {
			System.out.print(" ");
		}

		for (int i = 0; i < nodeLabels.size(); i++) {
			System.out.printf(formatS, nodeLabels.get(i));
		}
		System.out.println("|");

		for (int i = 0; i < adjacency.length; i++) {
			System.out.printf(formatS, nodeLabels.get(i));
			for (int k = 0; k < adjacency[i].length; k++) {
				if (adjacency[i][k])
					System.out.printf(formatB, adjacency[i][k]);
				else
					System.out.printf(formatB, adjacency[i][k]);
			}
			System.out.println("|");
		}
	}
}