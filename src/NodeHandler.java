import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;

public class NodeHandler {
	
	private ArrayList<Node> getNodes(GraphDatabaseService x, NodeLabel nL) {
		ArrayList<Node> nodes = new ArrayList<Node>();
		
		ResourceIterator<Node> resources = x.findNodes(nL);
		while(resources.hasNext()) {
			nodes.add(resources.next());
		}
		
		return nodes;
	}
	
	private Node getRandomNode(ArrayList<Node> nodes) {
		int randomInt = ThreadLocalRandom.current().nextInt(0, nodes.size());
		try {
			nodes.get(randomInt);
		}
		catch (Exception e) {
			System.out.println("Test... " + randomInt);
		}
		return nodes.get(randomInt);
	}
	
	public Node getSpecificRandomNode(GraphDatabaseService x, NodeLabel nL) {
		ArrayList<Node> nodes = getNodes(x, nL);
		
		return getRandomNode(nodes);
	}
}
