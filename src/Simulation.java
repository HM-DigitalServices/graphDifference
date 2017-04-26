import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.neo4j.cypher.internal.compiler.v2_3.planner.logical.cardinality.triplet.calculateOverlapCardinality;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseBuilder;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class Simulation {
	public static void main(String[] args) {
		ImportDataInNeo4j importer = new ImportDataInNeo4j("/var/lib/neo4j/data/databases/graph.db");

		GraphDatabaseBuilder d = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder(new File("/var/lib/neo4j/data/databases/graph.db"));
		final GraphDatabaseService x = d.newGraphDatabase();

		try (Transaction tx = x.beginTx()) {
			importer.initGraph(x, importer);
			System.out.println("Successful...");
			importer.removeRandomNode(x, NodeLabel.SERVER);
			tx.success();
		}
	}
	
	public void simulate(double pAddServer, double pRemoveServer) {
		double pAddElse = 1 - pAddServer;
		double pRemoveElse = 1- pRemoveServer;
		
		NormalDistribution d = new NormalDistribution(0, 1);
		double quantile = d.density(pAddServer);
		
		ArrayList<Double> normalDistributedValues = calcNormalDistributedValues(0, 1, 50);
	}
	
	private void simulateAdding(GraphDatabaseService x, ImportDataInNeo4j importer, double quantile, ArrayList<double> normalDistributedValues) {
		for(double normalDistributedValue : normalDistributedValues) {
			if(normaldistributedValue < quantile) {
				importer.addNode(x, new NodeHandler().getNodes(x, NodeLabel.SERVER).size(), NodeLabel.SERVER);
			}
			
			else {
				
				NodeLabel.valueOf(0);
			}
		}
	}
	
	private ArrayList<Double> calcNormalDistributedValues(double mean, double std, int size) {
		ArrayList<Double> normalDistributedValues = new ArrayList<Double>();
		Random r = new Random();
		
		for(int i = 0; i < size; i++) {
			normalDistributedValues.add(r.nextGaussian()*1);
		}
		
		return normalDistributedValues;
	}
}