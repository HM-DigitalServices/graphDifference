import org.neo4j.graphdb.Label;

public enum NodeLabel implements Label {
	SERVER, VM, RAM, OS, MANUFACTURER, HARDDISK, SOFTWARE, CPU, LICENCE, SERVICE;
}