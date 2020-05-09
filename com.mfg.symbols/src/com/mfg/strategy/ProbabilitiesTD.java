package com.mfg.strategy;

import com.mfg.interfaces.probabilities.WidgetSwingsElement;

public class ProbabilitiesTD {
	private int bestNPTarget, bestPPTarget, bestNNTarget, bestPNTarget;
	private double bestNPPrice, bestPPPrice, bestNNPrice, bestPNPrice;
	private double bestNTD, bestPTD;
	private boolean positiveTradeDirection;
	private boolean changingDir = false;

	public ProbabilitiesTD(int aBestPPTarget, int aBestPNTarget,
			int aBestNPTarget, int aBestNNTarget, double aBestPTD,
			double aBestNTD, WidgetSwingsElement aElement, int aWidgetScale,
			long cPrice) {
		super();
		bestNPTarget = aBestNPTarget;
		bestPPTarget = aBestPPTarget;
		bestNNTarget = aBestNNTarget;
		bestPNTarget = aBestPNTarget;
		bestNTD = aBestNTD;
		bestPTD = aBestPTD;
		compute(aElement, aWidgetScale, cPrice);
	}

	public ProbabilitiesTD() {
		super();
	}

	public void update(int aBestPPTarget, int aBestPNTarget, int aBestNPTarget,
			int aBestNNTarget, double aBestPTD, double aBestNTD,
			WidgetSwingsElement aElement, int aWidgetScale, long cPrice) {
		bestNPTarget = aBestNPTarget;
		bestPPTarget = aBestPPTarget;
		bestNNTarget = aBestNNTarget;
		bestPNTarget = aBestPNTarget;
		bestNTD = aBestNTD;
		bestPTD = aBestPTD;
		compute(aElement, aWidgetScale, cPrice);
	}

	public void compute(WidgetSwingsElement aElement, int aWidgetScale,
			long cPrice) {
		bestNPPrice = bestNPTarget == 0 ? cPrice : aElement.getTargetPrice(
				bestNPTarget, aWidgetScale);
		bestPPPrice = bestPPTarget == 0 ? cPrice : aElement.getTargetPrice(
				bestPPTarget, aWidgetScale);
		bestNNPrice = bestNNTarget == 0 ? cPrice : aElement
				.getNegativeTargetPrice(bestNNTarget, aWidgetScale);
		bestPNPrice = bestPNTarget == 0 ? cPrice : aElement
				.getNegativeTargetPrice(bestPNTarget, aWidgetScale);
		boolean newDir = bestPTD >= bestNTD;
		changingDir = positiveTradeDirection != newDir;
		if (changingDir) {
			positiveTradeDirection = newDir;
		}
	}

	public int getBestNPTarget() {
		return bestNPTarget;
	}

	public void setBestNPTarget(int aBestNPTarget) {
		bestNPTarget = aBestNPTarget;
	}

	public int getBestPPTarget() {
		return bestPPTarget;
	}

	public void setBestPPTarget(int aBestPPTarget) {
		bestPPTarget = aBestPPTarget;
	}

	public int getBestNNTarget() {
		return bestNNTarget;
	}

	public void setBestNNTarget(int aBestNNTarget) {
		bestNNTarget = aBestNNTarget;
	}

	public int getBestPNTarget() {
		return bestPNTarget;
	}

	public void setBestPNTarget(int aBestPNTarget) {
		bestPNTarget = aBestPNTarget;
	}

	public double getBestNTD() {
		return bestNTD;
	}

	public void setBestNTD(double aBestNTD) {
		bestNTD = aBestNTD;
	}

	public double getBestPTD() {
		return bestPTD;
	}

	public void setBestPTD(double aBestPTD) {
		bestPTD = aBestPTD;
	}

	public double getBestNPPrice() {
		return bestNPPrice;
	}

	public void setBestNPPrice(double aBestNPPrice) {
		bestNPPrice = aBestNPPrice;
	}

	public double getBestPPPrice() {
		return bestPPPrice;
	}

	public void setBestPPPrice(double aBestPPPrice) {
		bestPPPrice = aBestPPPrice;
	}

	public double getBestNNPrice() {
		return bestNNPrice;
	}

	public void setBestNNPrice(double aBestNNPrice) {
		bestNNPrice = aBestNNPrice;
	}

	public double getBestPNPrice() {
		return bestPNPrice;
	}

	public double getBestPPrice() {
		return isPositiveTradeDirection() ? bestPPPrice : bestNPPrice;
	}

	public double getBestNPrice() {
		return isPositiveTradeDirection() ? bestPNPrice : bestNNPrice;
	}

	public void setBestPNPrice(double aBestPNPrice) {
		bestPNPrice = aBestPNPrice;
	}

	public boolean isPositiveTradeDirection() {
		return positiveTradeDirection;
	}

	public void setPositiveTradeDirection(boolean aPositiveTradeDirection) {
		positiveTradeDirection = aPositiveTradeDirection;
	}

	public boolean isChangingDir() {
		return changingDir;
	}

}
