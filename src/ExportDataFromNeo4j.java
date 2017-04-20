import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseBuilder;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

/**
 * @author  administrator
 */
public class ExportDataFromNeo4j {
	private static String DB_PATH;
	private Graph anomaly;
	
	public ExportDataFromNeo4j(String dbPath) {
		this.DB_PATH = dbPath;
	}
	
	public ExportDataFromNeo4j() {
	}
	
	public void setDBPath(String path) {
		this.DB_PATH = path;
	}
	
	public Graph exportGraph() {
		GraphDatabaseBuilder d = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder(new File(DB_PATH));
		final GraphDatabaseService x = d.newGraphDatabase();
		
		boolean[][] adjacency = exportAdjacency(x);
		HashMap<Integer, String> nodeLabels = exportNodeLabels(x);
		
		return new Graph(adjacency, nodeLabels);
	}
	
	private boolean[][] exportAdjacency(GraphDatabaseService x) {
		ResourceIterable<Node> nodes = x.getAllNodes();
		ResourceIterator<Node> nodesIter = nodes.iterator();
		
		ArrayList<Node> nodesList = new ArrayList<Node>();
		
		ResourceIterable<Relationship> relations = x.getAllRelationships();
		ResourceIterator<Relationship> relationsIter = relations.iterator();
		
		int nodesAmount = 0;
		
		while(nodesIter.hasNext()) {
			Node n = nodesIter.next();
			nodesList.add(n);
			nodesAmount++;
		}
		
		boolean[][] adjacency = new boolean[nodesAmount][nodesAmount];
		
		for(int i = 0; i < adjacency.length; i++) {
			for(int j = 0; j < adjacency[i].length; j++) {
				for(Node k : nodesList) {
					for(Node l : nodesList) {
						while(relationsIter.hasNext()) {
							Relationship r = relationsIter.next();
							if(r.getStartNode().equals(k) && r.getEndNode().equals(l)) {
								adjacency[i][j] = true;
							}
							else {
								adjacency[i][j] = false;
							}
						}
					}
				}
			}
		}
		
		return adjacency;
	}
	
	private HashMap<Integer, String> exportNodeLabels(GraphDatabaseService x) {
		ResourceIterable<Node> nodes = x.getAllNodes();
		ResourceIterator<Node> nodesIter = nodes.iterator();
		
		HashMap<Integer, String> nodeLabels = new HashMap<Integer, String>();
		int labelCounter = 0;
		
		while(nodesIter.hasNext()) {
			Node n = nodesIter.next();
			nodeLabels.put(++labelCounter, n.getLabels().iterator().next().toString());
		}
		
		return nodeLabels;
	}
}
