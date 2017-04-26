import org.neo4j.graphdb.Label;

public enum NodeLabel implements Label {
	SERVER(0), VM(1), RAM(2), OS(3), MANUFACTURER(4), HARDDISK(5), SOFTWARE(6), CPU(7), LICENCE(8), SERVICE(9);
	
    private int value;
    
    private NodeLabel(int value) {
        this.value = value;
    }
   
    public int getValue() {
        return value;
    }
}