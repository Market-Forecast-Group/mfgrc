package com.mfg.interfaces.probabilities;

import com.mfg.interfaces.trading.Configuration;
import com.mfg.utils.StepDefinition;

public class SwingCalculator {
	private Configuration configuration;
	private StepDefinition tickSize;

	public SwingCalculator(Configuration aConfiguration, StepDefinition aTickSize) {
		super();
		this.configuration = aConfiguration;
		this.tickSize = aTickSize;
	}
	
	public SwingCalculator(Configuration aConfiguration) {
		this(aConfiguration, new StepDefinition(25));
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration aConfiguration) {
		this.configuration = aConfiguration;
	}

	public StepDefinition getTickSize() {
		return tickSize;
	}

	public void setTickSize(StepDefinition aTickSize) {
		this.tickSize = aTickSize;
	}

	public double getTargetTickPoints(int tID, int aScale) {
		return tickSize.getTimes(tID*configuration.getScaleMultiplier(aScale));
	}

	public int getTargetIDTicks(int scale, double targetPoints) {
		int times = getConfiguration().getScaleMultiplier(scale);
		return com.mfg.utils.MathUtils.getStepDiffAbs(targetPoints, 0, 
				getTickSize().getStepInteger(), 
				getTickSize().getStep10Scale())/times;
	}

	public double normalize(double target2) {
		return configuration.getTargetStep().roundMore(target2, 1);
	}

	public static double getPriceFromTargetPoints00(long pivotPrice, long thPrice,
			int sign, double targetPoints) {
		return thPrice + sign * targetPoints * Math.abs(thPrice - pivotPrice);
	}

	public static double getPriceFromTargetPoints(long pivotPrice, int sign,
			double targetPoints, double prevSwing) {
		return pivotPrice + sign * targetPoints * prevSwing;
	}

	public static double getPriceFromNTID00(double currentPrice, double hhll,
			int sign, double targetPoints) {
		return currentPrice - sign * targetPoints
				* Math.abs(currentPrice - hhll);
	}

	public static double getPriceFromNTID(double targetDelta, double hhll,
			int sign, double targetPoints) {
		return hhll - sign * targetPoints * targetDelta;
	}

}
