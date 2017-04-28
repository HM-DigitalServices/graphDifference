import java.util.HashMap;
import java.util.Map;

public class AdjacencyTimeSeries {
	private Map<Integer, boolean[][]> timeSeries = new HashMap<Integer, boolean[][]>();
	private Difference graphDifference;
	
	public void AdjacencyTimeSeries(Difference graphDifference, Map<Integer, boolean[][]> timeSeries) {
		this.graphDifference = graphDifference;
		this.timeSeries = timeSeries;
	}
}
