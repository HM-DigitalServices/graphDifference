import java.util.HashMap;
import java.util.Map;

public class Graph {
	private Map<Integer, String> nodeLabels = new HashMap<Integer, String>();
	
	private boolean[][] adjacency;
	private int[][] diff;
	
	public Graph(final boolean[][] adjacency, final Map<Integer, String> nodeLabels) {
		this.adjacency = adjacency;
		this.nodeLabels = nodeLabels;
	}
	
	public Graph(final int[][] diff, final Map<Integer, String> nodeLabels) {
		this.diff = diff;
		this.nodeLabels = nodeLabels;
	}
	
	public Map<Integer, String> getNodeLabels() {
		return nodeLabels;
	}
	
	// Columns and rows are symmetric!!! 
	// nxn matrix!!!
	public String getLabel(final int value) {
		if(!nodeLabels.containsKey(value)) {
			throw new IllegalArgumentException();
		}
		return nodeLabels.get(value);
	}
	
	public boolean[][] getAdjacency() {
		return adjacency;
	}
	
	public void printDiffGraph() {
		System.out.print("   ");
		for (int i = 0; i < nodeLabels.size(); i++) {
			System.out.printf("%2s ", nodeLabels.get(i));
		}
		System.out.println();
		for (int i = 0; i < diff.length; i++) {
			for (int j = 0; j < diff[i].length; j++) {
				if(j == 0) {
					System.out.printf("%2s ", nodeLabels.get(i));
				}
				System.out.printf("%2s ", diff[i][j]);
			}
			System.out.println();
		}
	}
}