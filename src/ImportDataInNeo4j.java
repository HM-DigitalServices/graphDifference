import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
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

	private enum Sports implements Label {
		RUNNING, SWIMMING, BIKING;
	}

	private enum RelationSport implements RelationshipType {
		LOW, MIDDLE, HIGH, VERY_HIGH;
	}
	
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

			tx.success();
		}
	}

	private void createGraph(GraphDatabaseService x) {
		Node a = x.createNode(Sports.RUNNING);
		Node b = x.createNode(Sports.BIKING);
		Node c = x.createNode(Sports.SWIMMING);
		Node dd = x.createNode(Sports.RUNNING);

		a.setProperty("Track", "one");
		a.setProperty("Kind of", "Competition");
		b.setProperty("Track", "two");
		b.setProperty("Kind of", "training");
		c.setProperty("Track", "four");
		c.setProperty("Kind of", "training");
		dd.setProperty("Track", "five");
		dd.setProperty("Kind of", "intervall");

		a.createRelationshipTo(b, RelationSport.MIDDLE);
		b.createRelationshipTo(c, RelationSport.HIGH);
		c.createRelationshipTo(a, RelationSport.VERY_HIGH);
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
				if(adjacency[i][k])
					System.out.printf(formatB, adjacency[i][k]);
				else
					System.out.printf(formatB, adjacency[i][k]);
			}
			System.out.println("|");
		}
	}	
}