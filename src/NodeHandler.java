import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Label;

public class NodeHandler {
	private enum NodeLabel implements Label {};
	
	public NodeHandler(enum NodeLabel) {
		this.NodeLabel = NodeLabel;
	}
	
	private void relateToRandomNode(Node node) {	
	}
	
	private ArrayList<Node> getNodes(GraphDatabaseService x, NodeLabel nL) {
		ArrayList<Node> nodes = new ArrayList<Node>();
		
		ResourceIterator<Node> resources = x.findNodes(nL);
		while(resources.hasNext()) {
			nodes.add(resources.next());
		}
		
		return nodes;
	}
	
	private Node getRandomNode(ArrayList<Node> nodes) {
		int randomInt = ThreadLocalRandom.current().nextInt(0, nodes.size()+1);
		return nodes.get(randomInt);
	}
}
