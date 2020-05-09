package com.mfg.interfaces;

import com.mfg.interfaces.probabilities.ElementsPatterns;
import com.mfg.interfaces.probabilities.SwingCalculator;
import com.mfg.interfaces.trading.RefType;
import com.mfg.utils.StepDefinition;

public class ProbabilityRecord {
	private double positiveTargetPrice;
	private double negativeTargetPrice;
	private long time;
	private int level;
	private boolean direction;
	private long positiveTHProbabilitiesPrice;
	private long negativeTHProbabilitiesPrice;
	private long positiveCurrentProbabilitiesPrice;
	private long negativeCurrentProbabilitiesPrice;
	private double firstTarget;
	private double firstNegativeTarget;
	private double prevSwing;
	private long pivot0;
	private long TH0;
	private long HHLL;
	private long currentPrice;
	private int sign;
	private double target;
	private StepDefinition targetStep;

	public ProbabilityRecord(int aLevel, double aPositiveTargetPrice,
			double aNegativeTargetPrice, long aTime, double firstTarget1,
			StepDefinition targetStep1) {
		super();
		positiveTargetPrice = aPositiveTargetPrice;
		negativeTargetPrice = aNegativeTargetPrice;
		time = aTime;
		level = aLevel;
		this.firstTarget = firstTarget1;
		this.targetStep = targetStep1;
	}

	public ProbabilityRecord() {
		super();
	}

	public double getPositiveTargetPrice() {
		return positiveTargetPrice;
	}

	public void setPositiveTargetPrice(double aPositiveTargetPrice) {
		positiveTargetPrice = aPositiveTargetPrice;
	}

	public double getNegativeTargetPrice() {
		return negativeTargetPrice;
	}

	public void setNegativeTargetPrice(double aNegativeTargetPrice) {
		negativeTargetPrice = aNegativeTargetPrice;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long aTime) {
		time = aTime;
	}

	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @param level1
	 *            the level to set
	 */
	public void setLevel(int level1) {
		this.level = level1;
	}

	public void setPositiveTradeDirection(boolean direction1) {
		this.direction = direction1;
	}

	public boolean isPositiveTradeDirection() {
		return direction;
	}

	public double getTarget(int index) {
		double res = firstTarget + (index - 1) * targetStep.getStepDouble();
		return targetStep.round(res);
	}

	public double getFirstTarget() {
		return firstTarget;
	}

	public void setFirstTarget(double aFirstTarget) {
		firstTarget = aFirstTarget;
	}

	public StepDefinition getTargetStep() {
		return targetStep;
	}

	public void setTargetStep(StepDefinition aTargetStep) {
		targetStep = aTargetStep;
	}

	public double getPrevSwing() {
		return prevSwing;
	}

	public void setPrevSwing(double aPrevSwing) {
		prevSwing = aPrevSwing;
	}

	public long getPivot0() {
		return pivot0;
	}

	public void setPivot0(long aPivot0) {
		pivot0 = aPivot0;
	}

	public long getHHLL() {
		return HHLL;
	}

	public void setHHLL(long aHHLL) {
		HHLL = aHHLL;
	}

	public int getSign() {
		return sign;
	}

	public void setSign(int aSign) {
		sign = aSign;
	}

	public double getTarget() {
		return target;
	}

	public void setTarget(double aTarget) {
		target = aTarget;
	}

	public long getPriceFromTID(int TID, RefType type) {
		double targetPoints = ElementsPatterns.getTargetPoints(TID, targetStep,
				firstTarget);
		if (type != RefType.Swing0_00)
			return (long) SwingCalculator.getPriceFromTargetPoints(pivot0, sign,
					targetPoints, prevSwing);
		return (long) SwingCalculator.getPriceFromTargetPoints00(pivot0, TH0,
				sign, targetPoints);
	}

	public long getPriceFromNTID(int TID, RefType type) {
		double targetPoints = ElementsPatterns.getTargetPoints(TID, targetStep,
				firstNegativeTarget);
		if (type != RefType.Swing0_00)
			return (long) SwingCalculator.getPriceFromNTID(target, HHLL,
					sign, targetPoints);
		return (long) SwingCalculator.getPriceFromNTID00(currentPrice,
				HHLL, sign, targetPoints);
	}

	public long getPositiveTHProbabilitiesPrice() {
		return positiveTHProbabilitiesPrice;
	}

	public void setPositiveTHProbabilitiesPrice(
			long aPositiveTHProbabilitiesPrice) {
		positiveTHProbabilitiesPrice = aPositiveTHProbabilitiesPrice;
	}

	public long getNegativeTHProbabilitiesPrice() {
		return negativeTHProbabilitiesPrice;
	}

	public void setNegativeTHProbabilitiesPrice(
			long aNegativeTHProbabilitiesPrice) {
		negativeTHProbabilitiesPrice = aNegativeTHProbabilitiesPrice;
	}

	public long getPositiveCurrentProbabilitiesPrice() {
		return positiveCurrentProbabilitiesPrice;
	}

	public void setPositiveCurrentProbabilitiesPrice(
			long aPositiveCurrentProbabilitiesPrice) {
		positiveCurrentProbabilitiesPrice = aPositiveCurrentProbabilitiesPrice;
	}

	public long getNegativeCurrentProbabilitiesPrice() {
		return negativeCurrentProbabilitiesPrice;
	}

	public void setNegativeCurrentProbabilitiesPrice(
			long aNegativeCurrentProbabilitiesPrice) {
		negativeCurrentProbabilitiesPrice = aNegativeCurrentProbabilitiesPrice;
	}

	public double getFirstNegativeTarget() {
		return firstNegativeTarget;
	}

	public void setFirstNegativeTarget(double aFirstNegativeTarget) {
		firstNegativeTarget = aFirstNegativeTarget;
	}

	public long getTH0() {
		return TH0;
	}

	public void setTH0(long aTH0) {
		TH0 = aTH0;
	}

	public long getCurrentPrice() {
		return currentPrice;
	}

	public void setCurrentPrice(long aCurrentPrice) {
		currentPrice = aCurrentPrice;
	}

}