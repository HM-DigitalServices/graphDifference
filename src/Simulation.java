import java.io.File;

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
}