import org.neo4j.graphdb.Label;

public enum NodeLabel implements Label {
	VM, SERVER, RAM, OS, MANUFACTURER, HARDDISK, SOFTWARE, CPU, LICENCE, SERVICE;
}