import org.neo4j.graphdb.RelationshipType;

public enum RelationLabel implements RelationshipType {
	RUNS, IN, PROCUDES, REQUIRES, DEPENDS;
}