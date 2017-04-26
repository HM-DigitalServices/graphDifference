import java.io.File;
import java.util.Random;

import org.apache.commons.math3.distribution.NormalDistribution;
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
	
	public static void calc() {
		double pAddServer = 0.15;
		double pRemoveServer = 1 - pAddServer;
		
		Random r = new Random();
		double val = r.nextGaussian(); //var = 2, x=10
		int millisDelay = (int) Math.round(val);
		NormalDistribution d = new NormalDistribution(10, 1);
		
		for(int i = 0; i < 100; i++) {
			if(Math.random()*pAddServer > 2) {
				
			}
		}
	}
}