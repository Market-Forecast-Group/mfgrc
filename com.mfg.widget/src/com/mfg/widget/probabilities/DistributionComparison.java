package com.mfg.widget.probabilities;

public class DistributionComparison {
	private double[] distribution1;
	private double[] distribution2;
	private double MSE;
	private double SSE;
	public DistributionComparison(double[] aDistribution1,
			double[] aDistribution2) {
		super();
		distribution1 = aDistribution1;
		distribution2 = aDistribution2;
		compute();
	}
	private void compute() {
		for (int i = 0; i < distribution1.length; i++) {
			SSE += Math.pow(distribution1[i] - distribution2[i], 2);
		}
		MSE = SSE / distribution1.length;
	}
	public double[] getDistribution1() {
		return distribution1;
	}
	public double[] getDistribution2() {
		return distribution2;
	}
	public double getMSE() {
		return MSE;
	}
	/**
	 * the sum of squared errors
	 * 
	 * @return
	 */
	public double getSSE() {
		return SSE;
	}

}
