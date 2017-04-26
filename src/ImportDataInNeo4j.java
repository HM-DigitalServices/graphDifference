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
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseBuilder;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

/**
 * @author  administrator
 */
public class ImportDataInNeo4j {
	private static String DB_PATH;
	
	/**
	 * @uml.property  name="nodeLabels"
	 * @uml.associationEnd  multiplicity="(0 -1)" ordering="true" elementType="java.lang.Object" qualifier="valueOf:java.lang.Integer java.lang.Object"
	 */
	private Map<Integer, String> nodeLabels = new HashMap<Integer, String>();
	
	private static boolean[][] adjacency;
	
	public ImportDataInNeo4j(String dbPath) {
		this.DB_PATH = dbPath;
	}
	
	public void initGraph(GraphDatabaseService x, ImportDataInNeo4j obj) {
		int nodeSize = 10;
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

	public void removeRandomNode(GraphDatabaseService x, NodeLabel label) {
		NodeHandler handler = new NodeHandler();
		
		Node node = handler.getSpecificRandomNode(x, label);
		Iterable<Relationship> relations = node.getRelationships();
		Iterator<Relationship> iter = relations.iterator();
		
		ArrayList<Node> nodesToOldNode = new ArrayList<>();
		Relationship relationOld;
		while(iter.hasNext()) {
			relationOld = iter.next();
			nodesToOldNode.add(relationOld.getNodes()[0]);
			relationOld.delete();
		}
		long id = node.getId();
		
		node.delete();
		assert validateDeleteNode(x, handler, id) : "Server-Node konnte nicht gelöscht werden...";
		
		createRelationshipsToNode(x, handler, nodesToOldNode, label);
	}

	private void createRelationshipsToNode(GraphDatabaseService x, NodeHandler handler, ArrayList<Node> nodesToOldServerNode, NodeLabel label) {
		Node newServerNode = handler.getSpecificRandomNode(x, label);
		for(int i = 0; i < nodesToOldServerNode.size(); i++) {
			nodesToOldServerNode.get(i).createRelationshipTo(newServerNode, getRelationshipType(nodesToOldServerNode.get(i)));
		}
	}
	
	private RelationshipType getRelationshipType(Node node) {
		String label = node.getLabels().iterator().next().name();
		
		if(label.equals(NodeLabel.CPU.name())) {
			return RelationLabel.IN;
		}
		else if(label.equals(NodeLabel.SOFTWARE.name())) {
			return RelationLabel.RUNS;
		}
		else if(label.equals(NodeLabel.OS.name())) {
			return RelationLabel.RUNS;
		}
		else if(label.equals(NodeLabel.MANUFACTURER.name())) {
			return RelationLabel.IN;
		}
		else if(label.equals(NodeLabel.RAM.name())) {
			return RelationLabel.PROCUDES;
		}
		else if(label.equals(NodeLabel.VM.name())) {
			return RelationLabel.RUNS;
		}
		else if(label.equals(NodeLabel.HARDDISK.name())) {
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
}