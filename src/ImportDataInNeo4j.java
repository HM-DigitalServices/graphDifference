import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
//	private final static String DB_PATH = "C:/Users/moest/OneDrive/Dokumente/Neo4j/default.graphdb";
	private final static String DB_PATH = "C:/Users/4D6F6573746C/Documents/Neo4j/default.graphdb";

	@SuppressWarnings("unused")
	private final static String DELETE_ALL = "MATCH (n) OPTIONAL MATCH (n)-[r]-() DELETE n,r";
	private Map<Integer, String> nodeLabels = new HashMap<Integer, String>();
	private static boolean[][] adjacency;

	public ImportDataInNeo4j() {
	}

	public static void main(String[] args) {
		ImportDataInNeo4j obj = new ImportDataInNeo4j();

		GraphDatabaseBuilder d = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder(new File(DB_PATH));
		final GraphDatabaseService x = d.newGraphDatabase();

		try (Transaction tx = x.beginTx()) {
			obj.initGraph(x, obj);
			System.out.println("Successful...");
			obj.removeRandomServer(x);
			tx.success();
		}
	}
	
	private void initGraph(GraphDatabaseService x, ImportDataInNeo4j obj) {
		int nodeSize = 100;
		int serverSize = 10;
		List<Node> nodes = new ArrayList<>();
		for (int i = 0; i < nodeSize; i++) {
			nodes.add(obj.createNode(x, i, NodeLabel.LICENCE));
			nodes.add(obj.createNode(x, i, NodeLabel.VM));
			nodes.add(obj.createNode(x, i, NodeLabel.SERVICE));
			nodes.add(obj.createNode(x, i, NodeLabel.RAM));
			nodes.add(obj.createNode(x, i, NodeLabel.CPU));
			nodes.add(obj.createNode(x, i, NodeLabel.SOFTWARE));
			nodes.add(obj.createNode(x, i, NodeLabel.OS));
			nodes.add(obj.createNode(x, i, NodeLabel.HARDDISK));
			nodes.add(obj.createNode(x, i, NodeLabel.MANUFACTURER));
		}
		
		for (int i = 0; i < serverSize; i++) {
			nodes.add(obj.createNode(x, i, NodeLabel.SERVER));
		}
		for(int i = 0; i < nodeSize * 9; i++) {
			createRelationship(nodes.get(i), x);
		}
		for(int i = nodeSize * 9; i < nodeSize * 9 + serverSize; i++) {
			createRelationship(nodes.get(i), x);
		}
	}
	
	public void addNode(GraphDatabaseService x, int counter, NodeLabel nL) {
		Node node = createNode(x, counter, nL);
		createRelationship(node, x);
	}

	public void removeRandomServer(GraphDatabaseService x) {
		NodeHandler handler = new NodeHandler();
		
		Node serverNode = handler.getSpecificRandomNode(x, NodeLabel.SERVER);
		Iterable<Relationship> relations = serverNode.getRelationships();
		Iterator<Relationship> iter = relations.iterator();
		
		ArrayList<Node> nodesToOldServerNode = new ArrayList<>();
		Relationship relationOld;
		while(iter.hasNext()) {
			relationOld = iter.next();
			nodesToOldServerNode.add(relationOld.getNodes()[0]);
			System.out.println("nodeToOldServer : " + relationOld.getNodes()[0].getId() + " " + relationOld.getNodes()[0].getLabels().iterator().next().name());
			relationOld.delete();
		}
		long id = serverNode.getId();
		
		serverNode.delete();
		assert validateDeleteNode(x, handler, id) : "Server-Node konnte nicht gelöscht werden...";
		
		createRelationshipsToServer(x, handler, nodesToOldServerNode);
	}

	private void createRelationshipsToServer(GraphDatabaseService x, NodeHandler handler,
			ArrayList<Node> nodesToOldServerNode) {
		Node newServerNode = handler.getSpecificRandomNode(x, NodeLabel.SERVER);
		for(int i = 0; i < nodesToOldServerNode.size(); i++) {
			nodesToOldServerNode.get(i).createRelationshipTo(newServerNode, getRelationshipType(nodesToOldServerNode.get(i)));
		}
	}
	
	private RelationshipType getRelationshipType(Node node) {
		String label = node.getLabels().iterator().next().name();
		if(label.equals(NodeLabel.CPU)) {
			return RelationLabel.IN;
		}
		else if(label.equals(NodeLabel.SOFTWARE)) {
			return RelationLabel.RUNS;
		}
		else if(label.equals(NodeLabel.OS)) {
			return RelationLabel.RUNS;
		}
		else if(label.equals(NodeLabel.MANUFACTURER)) {
			return RelationLabel.IN;
		}
		else if(label.equals(NodeLabel.RAM)) {
			return RelationLabel.PROCUDES;
		}
		else if(label.equals(NodeLabel.VM)) {
			return RelationLabel.RUNS;
		}
		else if(label.equals(NodeLabel.HARDDISK)) {
			return RelationLabel.IN;
		}
		return null;
	}

	private boolean validateDeleteNode(GraphDatabaseService x, NodeHandler handler, long id) {
		ArrayList<Node> nodes = handler.getNodes(x, NodeLabel.SERVER);
		for(int i = 0; i < nodes.size(); i++) {
			if(nodes.get(i).getId() == id) {
				return false;
			}
		}
		return true;
	}
	
	private Node createNode(GraphDatabaseService x, int counter, NodeLabel nL) {
		Node node = x.createNode(nL);
		StringBuffer sB = new StringBuffer();

		sB.append(nL.toString());
		node.setProperty("Name", sB.append(counter).toString());
	
		return node;
	}
	
	private void createRelationship(Node startNode, GraphDatabaseService x) {
		
		Iterable<Label> iter = startNode.getLabels();
		Iterator<Label> it = iter.iterator();
		Label label = it.next();

		NodeHandler nodeHandler = new NodeHandler();
		
		if(NodeLabel.SERVER.toString().equals(label.toString())) {
			nodeHandler.getSpecificRandomNode(x, NodeLabel.CPU).createRelationshipTo(startNode, RelationLabel.IN);
			nodeHandler.getSpecificRandomNode(x, NodeLabel.SOFTWARE).createRelationshipTo(startNode, RelationLabel.RUNS);
			nodeHandler.getSpecificRandomNode(x, NodeLabel.OS).createRelationshipTo(startNode, RelationLabel.RUNS);
			nodeHandler.getSpecificRandomNode(x, NodeLabel.MANUFACTURER).createRelationshipTo(startNode, RelationLabel.IN);
			nodeHandler.getSpecificRandomNode(x, NodeLabel.RAM).createRelationshipTo(startNode, RelationLabel.PROCUDES);
			nodeHandler.getSpecificRandomNode(x, NodeLabel.VM).createRelationshipTo(startNode, RelationLabel.RUNS);
			nodeHandler.getSpecificRandomNode(x, NodeLabel.HARDDISK).createRelationshipTo(startNode, RelationLabel.IN);
		}
		else if(NodeLabel.LICENCE.toString().equals(label.toString())) {
			nodeHandler.getSpecificRandomNode(x, NodeLabel.SOFTWARE).createRelationshipTo(startNode, RelationLabel.REQUIRES);
		}
		else if(NodeLabel.VM.toString().equals(label.toString())) {
			startNode.createRelationshipTo(nodeHandler.getSpecificRandomNode(x, NodeLabel.SERVER), RelationLabel.RUNS);
		}
		else if(NodeLabel.SERVICE.toString().equals(label.toString())) {
			startNode.createRelationshipTo(nodeHandler.getSpecificRandomNode(x, NodeLabel.SOFTWARE), RelationLabel.DEPENDS);
		}
		else if(NodeLabel.RAM.toString().equals(label.toString())) {
			startNode.createRelationshipTo(nodeHandler.getSpecificRandomNode(x, NodeLabel.SERVER), RelationLabel.IN);
			nodeHandler.getSpecificRandomNode(x, NodeLabel.MANUFACTURER).createRelationshipTo(startNode, RelationLabel.PROCUDES);
		}
		else if(NodeLabel.CPU.toString().equals(label.toString())) {
			startNode.createRelationshipTo(nodeHandler.getSpecificRandomNode(x, NodeLabel.SERVER), RelationLabel.IN);
			nodeHandler.getSpecificRandomNode(x, NodeLabel.MANUFACTURER).createRelationshipTo(startNode, RelationLabel.PROCUDES);
		}
		else if(NodeLabel.SOFTWARE.toString().equals(label.toString())) {
			startNode.createRelationshipTo(nodeHandler.getSpecificRandomNode(x, NodeLabel.OS), RelationLabel.RUNS);
			startNode.createRelationshipTo(nodeHandler.getSpecificRandomNode(x, NodeLabel.SOFTWARE), RelationLabel.REQUIRES);
			startNode.createRelationshipTo(nodeHandler.getSpecificRandomNode(x, NodeLabel.LICENCE), RelationLabel.REQUIRES);
			
			nodeHandler.getSpecificRandomNode(x, NodeLabel.SERVICE).createRelationshipTo(startNode, RelationLabel.DEPENDS);
			nodeHandler.getSpecificRandomNode(x, NodeLabel.MANUFACTURER).createRelationshipTo(startNode, RelationLabel.PROCUDES);
		}	
		else if(NodeLabel.OS.toString().equals(label.toString())) {
			startNode.createRelationshipTo(nodeHandler.getSpecificRandomNode(x, NodeLabel.SERVER), RelationLabel.RUNS);
			nodeHandler.getSpecificRandomNode(x, NodeLabel.SOFTWARE).createRelationshipTo(startNode, RelationLabel.RUNS);
			nodeHandler.getSpecificRandomNode(x, NodeLabel.MANUFACTURER).createRelationshipTo(startNode, RelationLabel.PROCUDES);
		}
		else if(NodeLabel.HARDDISK.toString().equals(label.toString())) {
			startNode.createRelationshipTo(nodeHandler.getSpecificRandomNode(x, NodeLabel.SERVER), RelationLabel.IN);
			nodeHandler.getSpecificRandomNode(x, NodeLabel.MANUFACTURER).createRelationshipTo(startNode, RelationLabel.PROCUDES);
		}
		else { //MANUFACTURER
			startNode.createRelationshipTo(nodeHandler.getSpecificRandomNode(x, NodeLabel.SERVER), RelationLabel.PROCUDES);
			startNode.createRelationshipTo(nodeHandler.getSpecificRandomNode(x, NodeLabel.OS), RelationLabel.PROCUDES);
			startNode.createRelationshipTo(nodeHandler.getSpecificRandomNode(x, NodeLabel.SOFTWARE), RelationLabel.PROCUDES);
			startNode.createRelationshipTo(nodeHandler.getSpecificRandomNode(x, NodeLabel.CPU), RelationLabel.PROCUDES);
			startNode.createRelationshipTo(nodeHandler.getSpecificRandomNode(x, NodeLabel.HARDDISK), RelationLabel.PROCUDES);
			startNode.createRelationshipTo(nodeHandler.getSpecificRandomNode(x, NodeLabel.RAM), RelationLabel.PROCUDES);
		}
	}
	

	@SuppressWarnings("unused")
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
	@SuppressWarnings("unused")
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

	@SuppressWarnings("unused")
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