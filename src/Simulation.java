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
	private final static int k = 600;
	private final static int daysPerMonth = 30;

	public static void main(String[] args) {
		ImportDataInNeo4j importer = new ImportDataInNeo4j("/var/lib/neo4j/data/databases/graph.db");

		GraphDatabaseBuilder d = new GraphDatabaseFactory()
				.newEmbeddedDatabaseBuilder(new File("/var/lib/neo4j/data/databases/graph.db"));
		final GraphDatabaseService x = d.newGraphDatabase();

		try (Transaction tx = x.beginTx()) {
			importer.initGraph(x, importer);
			new Simulation().simulate(x, importer, 0.25);
			System.out.println("Successful...");
			tx.success();
		}
	}

	public void simulate(GraphDatabaseService x, ImportDataInNeo4j importer, double pAddServer) {
		NormalDistribution d = new NormalDistribution(0, 1);
		double quantileAdd = d.density(pAddServer);
		ArrayList<Double> normalDistributedValues = calcNormalDistributedValues(0, 1, 50);
		
		int numberOfOperationsPerDay = getNumberOfOperationsPerDay(calcLambda());
		
		for(int i = 0; i < numberOfOperationsPerDay; i++) {
			Random r = new Random();
			int operationType = r.nextInt(2);
			
			if(operationType == 1) {
				simulateAdding(x, importer, quantileAdd, normalDistributedValues);
			}
			
			else {
				simulateRemoving(x, importer, 1 - quantileAdd, normalDistributedValues);
			}
		}
	}

	private void simulateAdding(GraphDatabaseService x, ImportDataInNeo4j importer, double quantile,
			ArrayList<Double> normalDistributedValues) {
		for (double normalDistributedValue : normalDistributedValues) {
			if (normalDistributedValue < quantile) {
				importer.addNode(x, new NodeHandler().getNodes(x, NodeLabel.SERVER).size(), NodeLabel.SERVER);
			}

			else {
				Random r = new Random();
				int randomNum = r.nextInt(10) + 1;
				importer.addNode(x, new NodeHandler().getNodes(x, NodeLabel.values()[randomNum]).size(),
						NodeLabel.values()[randomNum]);
			}
		}
	}

	private void simulateRemoving(GraphDatabaseService x, ImportDataInNeo4j importer, double quantile,
			ArrayList<Double> normalDistributedValues) {
		for (double normalDistributedValue : normalDistributedValues) {
			if (normalDistributedValue < quantile) {
				importer.removeRandomNode(x, NodeLabel.SERVER);
			}

			else {
				Random r = new Random();
				int randomNum = r.nextInt(10) + 1;
				importer.removeRandomNode(x, NodeLabel.values()[randomNum]);
			}
		}
	}

	private ArrayList<Double> calcNormalDistributedValues(double mean, double std, int size) {
		ArrayList<Double> normalDistributedValues = new ArrayList<Double>();
		Random r = new Random();

		for (int i = 0; i < size; i++) {
			normalDistributedValues.add(r.nextGaussian() * 1);
		}

		return normalDistributedValues;
	}

	private int getNumberOfOperationsPerDay(double lambda) {
		double L = Math.exp(-lambda);
		double p = 1.0;
		int k = 0;

		do {
			k++;
			p *= Math.random();
		} while (p > L);

		return k - 1;
	}
	
	private double calcLambda() {
		return this.k/this.daysPerMonth;
	}
}