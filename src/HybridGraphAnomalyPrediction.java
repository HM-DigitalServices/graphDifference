import org.apache.commons.math3.stat.regression.SimpleRegression;

public class HybridGraphAnomalyPrediction {
	private SimpleRegression simpleRegression;
	private double intercept, slope;
	private double rsquare, significance;
	
	public void HybridGraphAnomalyPrediction() {
		this.simpleRegression = new SimpleRegression();
		this.intercept = 0.0;
		this.slope = 0.0;
		this.rsquare = 0.0;
	}
	
	public void addTimeSeries(double[][] timeSeries) {
		this.simpleRegression.addData(timeSeries);
	}
	
	public void DetermineRegressionFunction() {
		this.intercept = this.simpleRegression.getIntercept();
		this.slope = this.simpleRegression.getSlope();
	}
	
	public void DetermineQualityOfRegression() {
		this.rsquare = this.simpleRegression.getRSquare();
		this.significance = this.simpleRegression.getSignificance();
	}
	
	public double predict(double timeStamp) {
		return this.simpleRegression.predict(timeStamp);
	}
	
	public double getIntercept() {
		return this.intercept;
	}
	
	public double getSlope() {
		return this.slope;
	}
	
	public double getRSquare() {
		return this.rsquare;
	}
	
	public double getSignificance() {
		return this.significance;
	}
}
