package com.mfg.interfaces.probabilities;

import com.mfg.interfaces.indicator.IIndicator;
import com.mfg.interfaces.indicator.Pivot;
import com.mfg.interfaces.probabilities.IElement.IDetailedElement;
import com.mfg.utils.StepDefinition;

/**
 * is an implementation of {@link IDetailedElement}
 * 
 * @author gardero
 * 
 */
public class WidgetSwingsElement implements IDetailedElement {

	private IIndicator widget;
	private ElementsPatterns[] pattern;
	private ElementsPatterns root;
	private int[] savedthcount;
	private double[] target;
	private double[] backTarget, lastTargetPrice;
	private double[] backPTarget;
	private long[] backPTime, lastTargetTime;
	private long[] hhTime;
	private Pivot[] savedPivots;
	private boolean[] thdir, saveddir;
	/**
	 * the scale that we are analyzing can ignore some pivots if we are
	 * computing targets instead of swing0.
	 */
	private int offSetScale;
	private SwingReference[] lastReferences;
	private boolean ms;
	private SwingCalculator calculator;

	public WidgetSwingsElement(IIndicator aWidget, int scale,
			ElementsPatterns aRoot, boolean multiscale,
			SwingCalculator aCalculator) {
		super();
		this.calculator = aCalculator;
		widget = aWidget;
		int dim = widget.getChscalelevels();
		savedthcount = new int[dim + 1];
		target = new double[dim + 1];
		backTarget = new double[dim + 1];
		lastTargetPrice = new double[dim + 1];
		backPTarget = new double[dim + 1];
		backPTime = new long[dim + 1];
		lastTargetTime = new long[dim + 1];
		hhTime = new long[dim + 1];
		pattern = new ElementsPatterns[dim + 1];
		// prevHSTI = new HSTargetInfo[dim+1];
		for (int i = scale; i < savedthcount.length; i++) {
			if (hasScale(i))
				savedthcount[i] = aWidget.getCurrentPivotsCount(i);
		}
		savedPivots = new Pivot[dim + 1];
		for (int i = scale; i < savedthcount.length; i++) {
			if (hasScale(i))
				savedPivots[i] = aWidget.getLastPivot(0, i);
		}
		saveddir = new boolean[dim + 1];
		for (int i = widget.getStartScaleLevelWidget(); i < saveddir.length; i++) {
			if (hasScale(i))
				saveddir[i] = aWidget.isSwingDown(i);
		}
		thdir = new boolean[dim + 1];
		for (int i = scale; i < thdir.length; i++) {
			if (hasScale(i))
				thdir[i] = aWidget.isSwingDown(i);
		}
		long currentPrice = aWidget.getCurrentPrice();
		long currentTime = aWidget.getCurrentTime();
		for (int i = scale; i < thdir.length; i++) {
			if (hasScale(i)) {
				lastTargetPrice[i] = currentPrice;
				lastTargetTime[i] = currentTime;
			}
		}
		offSetScale = scale;
		this.root = aRoot;
		for (int i = scale; i < thdir.length; i++) {
			if (hasScale(i) && (multiscale || i == scale))
				pattern[i] = aRoot.getPatternLeaf(this, i);
		}
		this.ms = multiscale;
		aCalculator.getConfiguration().getType();
		considerPrice(widget.getCurrentPrice());
	}

	/**
	 * gets the offset that we will apply to the scale. it will be different of
	 * 0 only when we are in the targets version and not in the
	 * swing<sub>0</sub> one.
	 * 
	 * @param scale
	 * @return
	 */
	private int getOffset(int scale) {
		return widget.getCurrentPivotsCount(scale) - savedthcount[scale];
	}

	@Override
	public double getRatio(int ratioIndex, int scale) {
		return getRatio(ratioIndex, scale, 0);
	}

	@SuppressWarnings("boxing")
	@Override
	public double getRatio(int ratioIndex, int scale, int aOffset) {
		int offset = getOffset(scale) + aOffset;
		int idx = ratioIndex + offset;
		if (aOffset < 0 && ratioIndex <= 1) {
			double t = target[scale];
			Pivot p = widget.getLastPivot(Math.min(-offset, 0), scale);
			if (idx == 0) {
				return t / new Double(p.fLinearSwing);
			} else if (idx == -1) {
				// double hhll = Math.abs(p.getPivotPrice() + getSign(scale) * t
				// - widget.getCurrentPrice());
				return backTarget[scale] / t;
			}
		}
		if (ratioIndex == 0) {
			Pivot p = widget.getLastPivot(0 - offset, scale);
			return Math.abs(p.fConfirmPrice - p.fPivotPrice)
					/ new Double(p.fLinearSwing);
		} else if (ratioIndex < 0) {
			return 0;
		}
		Pivot p = widget.getLastPivot(-ratioIndex + 1 - offset, scale);
		Pivot p1 = widget.getLastPivot(-ratioIndex - offset, scale);
		return new Double(p.fLinearSwing) / new Double(p1.fLinearSwing);
	}

	@SuppressWarnings("boxing")
	@Override
	public double getTarget(int scale) {
		double t = target[scale];
		Pivot p;
		long sw0P;
		switch (calculator.getConfiguration().getComputationType()) {
		case S2ndTicks:
			p = savedPivots[scale];
			sw0P = Math.abs(p.fConfirmPrice - p.fPivotPrice);
			return (t - sw0P);
		case S1stRatio:
			p = savedPivots[scale];
			sw0P = Math.abs(p.fConfirmPrice - p.fPivotPrice);
			return (t - sw0P) / new Double(sw0P);
		case Sm1Ratio:
			return t / savedPivots[scale].fLinearSwing;
		default:
			return 0;
		}
	}

	public int getPriceCluster(int scale) {
		if (!calculator.getConfiguration().isUsingPriceClusters())
			return 0;
		int clustersNumber = calculator.getConfiguration()
				.getPriceClustersInSw0();
		Pivot p = savedPivots[scale];
		int a = (int) Math.abs(p.getPivotPrice() - p.getConfirmPrice());
		int z = (int) backPTarget[scale];
		if (z > 0)
			return (z * clustersNumber) / a + 1;
		return 0;
	}

	public int getTimeCluster(int scale) {
		if (!calculator.getConfiguration().isUsingTimeClusters())
			return 0;
		int clustersNumber = calculator.getConfiguration()
				.getTimeClustersInSw0st();
		Pivot p = savedPivots[scale];
		long a = Math.abs(p.getConfirmTime() - p.getPivotTime());
		long z = backPTime[scale];
		return (int) ((z * clustersNumber) / a + 1);
	}

	/**
	 * equal to n if targetPoints >= T<sub>n</sub> and targetPoints <
	 * T<sub>(n+1)</sub>
	 * 
	 * @param scale
	 * @param targetPoints
	 * @return
	 */
	public int getTargetID(int scale, double targetPoints) {
		switch (calculator.getConfiguration().getComputationType()) {
		case S2ndTicks:
			return calculator.getTargetIDTicks(scale, targetPoints);
			// $CASES-OMITTED$
		default:
			double t1 = root.getPatternLeaf(this, scale).getFirstTarget();
			StepDefinition s = calculator.getConfiguration().getTargetStep();
			if (targetPoints <= t1)
				return 0;
			int steps = com.mfg.utils.MathUtils.getStepDiffAbs(targetPoints,
					t1, s.getStepInteger(), s.getStep10Scale()) + 1;
			// if (com.mfg.utils.MathUtils.isRoundStepDiffAbs(targetPoints, t1,
			// s.getStepInteger(), s.getStep10Scale()))
			// return steps - 1;
			return steps;
		}
	}

	public int getTargetID(int scale) {
		return getTargetID(scale, getTarget(scale));
	}

	public double getTargetDelta(int scale) {
		return target[scale];
	}

	@Override
	public double getTargetFromPrice(double aPrice, int scale) {
		Pivot p = savedPivots[scale];
		double delta;
		switch (calculator.getConfiguration().getComputationType()) {
		case S2ndTicks:
			delta = Math
					.max(0, getSign(scale) * (aPrice - p.getConfirmPrice()));
			// $CASES-OMITTED$
			//$FALL-THROUGH$
		default:
			delta = Math.max(0, getSign(scale) * (aPrice - p.getPivotPrice()));
		}
		return getTargetFromDelta(delta, scale);
	}

	public int getTargetIDFromPrice(double aPrice, int scale) {
		double t = getTargetFromPrice(aPrice, scale);
		return getTargetID(scale, t);
	}

	@Override
	public double getTargetFromDelta(double delta, int scale) {
		switch (calculator.getConfiguration().getComputationType()) {
		case S2ndTicks:
			return delta;
			// $CASES-OMITTED$
		default:
			Pivot p = savedPivots[scale];
			return Math.max(delta / p.fLinearSwing, getTarget(scale));
		}
	}

	public int getTargetIDFromDelta(double delta, int scale) {
		double t = getTargetFromDelta(delta, scale);
		return getTargetID(scale, t);
	}

	@Override
	public double getSwing(int scale) {
		return getTarget(scale);
	}

	@Override
	public boolean hasScale(int scale) {
		return widget.isLevelInformationPresent(scale);
	}

	@Override
	public boolean isGoingUP(int scale) {
		return !saveddir[scale];
	}

	@Override
	public long getPivotPrice(int aIndex, int aScale) {
		Pivot p = getPivotInfo(aIndex, aScale);
		return p.fPivotPrice;
	}

	public Pivot getPivotInfo(int aIndex, int aScale) {
		int offset = getOffset(aScale);
		return widget.getLastPivot(-aIndex - offset, aScale);
	}

	@Override
	public long getPivotTime(int aIndex, int aScale) {
		Pivot p = null;
		try {
			p = getPivotInfo(aIndex, aScale);
		} catch (Exception ex) {
			ex.printStackTrace();
			return -1;
		}
		return p.fPivotTime;
	}

	public double getPriceFromTID(int TID, int aScale) {
		double targetPoints = getTargetPoints(TID, aScale);
		return getPriceFromTargetPoints(targetPoints, aScale);
	}

	private double getTargetPoints(int TID, int aScale) {
		switch (calculator.getConfiguration().getComputationType()) {
		case S2ndTicks:
			return calculator.getTargetTickPoints(TID, aScale);
			// $CASES-OMITTED$
		default:
			double targetPoints = getPattern(aScale).getTarget(TID);
			return targetPoints;
		}

	}

	public double getPriceFromTargetPoints(double targetPoints, int aScale) {
		Pivot pivotInfo = getPivotInfo(0, aScale);
		long pivotPrice = pivotInfo.getPivotPrice();
		int sign = getSign(aScale);
		switch (calculator.getConfiguration().getComputationType()) {
		case S1stRatio:
			long thPrice = (long) pivotInfo.getConfirmPrice();
			return SwingCalculator.getPriceFromTargetPoints00(pivotPrice,
					thPrice, sign, targetPoints);
		case Sm1Ratio:
			double prevSwing = getPrevSwing(aScale);
			return SwingCalculator.getPriceFromTargetPoints(pivotPrice, sign,
					targetPoints, prevSwing);
		case S2ndTicks:
			return pivotInfo.getConfirmPrice() + sign * targetPoints;
		default:
			return 0;
		}
	}

	public double getPriceFromNTID(int TID, int aScale) {
		double targetDelta = target[aScale];
		double hhll = getHHLL(aScale);
		root.getNextPatternLeaf(this, aScale);
		int sign = getSign(aScale);
		double targetPoints = getTargetPoints(TID, aScale);
		switch (calculator.getConfiguration().getComputationType()) {
		case S1stRatio:
			long currentPrice = widget.getCurrentPrice();
			return SwingCalculator.getPriceFromNTID00(currentPrice, hhll, sign,
					targetPoints);
		case Sm1Ratio:
			return SwingCalculator.getPriceFromNTID(targetDelta, hhll, sign,
					targetPoints);
		case S2ndTicks:
			return hhll - sign * targetPoints;
		default:
			return 0;
		}

	}

	public long getHHLL(int aScale) {
		double t = target[aScale];
		Pivot p = widget.getLastPivot(0, aScale);
		double hhll = p.getPivotPrice() + getSign(aScale) * t;
		return (long) hhll;
	}

	public ElementsPatterns getPattern(int aScale) {
		if (aScale != offSetScale)
			return root.getPatternLeaf(this, aScale);
		return pattern[aScale];
	}

	// public String evalTrack(int scale) {
	// ElementsPatterns pointer = root;
	// String res = "";
	// while (!pointer.isLeaf()) {
	// res = ", " + this.getRatio(pointer.getRatioLevel(), scale);
	// }
	// return res;
	// }

	private static StepDefinition step = new StepDefinition(0.01);

	public String evalNegTrack(int scale) {
		return evalTrack(scale, -1);
	}

	public String evalCurTrack(int scale) {
		return evalTrack(scale, 0);
	}

	public String evalTrack(int scale, int offset) {
		ElementsPatterns pointer = root;
		String res = "", finalRes = "";
		while (!pointer.isLeaf()) {
			double ratio = this
					.getRatio(pointer.getRatioLevel(), scale, offset);
			res += "" + step.round(ratio);
			for (ElementsPatterns e : pointer.getChildren()) {
				if (e.containsRatio(ratio)) {
					pointer = e;
					break;
				}
			}
			finalRes = res;
			res += ", ";
		}
		return "[" + finalRes + "]";
	}

	public double getPrevSwing(int aScale) {
		return Math.abs(getPivotPrice(0, aScale) - getPivotPrice(1, aScale));
	}

	// private double prevSwing(int aScale, int offset) {
	// return Math.abs(getPivotPrice(0+offset, aScale)-getPivotPrice(1+offset,
	// aScale));
	// }

	public int getSign(int scale) {
		return isGoingUP(scale) ? 1 : -1;
	}

	@Override
	public long getTargetPrice(int aIndex, int aScale) {
		return (long) getPriceFromTID(aIndex, aScale);
	}

	@Override
	public long getNegativeTargetPrice(int aIndex, int aScale) {
		return (long) getPriceFromNTID(aIndex, aScale);
	}

	@Override
	public void considerPrice(double aCurrentPrice) {
		for (int i = offSetScale; i < thdir.length; i++) {
			if (hasScale(i) && (ms || i == offSetScale)) {
				target[i] = Math.max(target[i],
						getSign(i) * (-getPivotPrice(0, i) + aCurrentPrice));
				if (aCurrentPrice == getHHLL(i)) {
					hhTime[i] = widget.getCurrentTime();
				} else {
					backTarget[i] = Math.max(backTarget[i],
							Math.abs(aCurrentPrice - getHHLL(i)));
					double candidate = -getSign(i)
							* (aCurrentPrice - lastTargetPrice[i]);
					if (candidate > backPTarget[i]) {
						backPTarget[i] = candidate;
						backPTime[i] = widget.getCurrentTime()
								- lastTargetTime[i];
					}
				}
			}
		}
	}

	@Override
	public void shiftConditions(boolean newTID) {
		if (ms) {
			for (int i = offSetScale + 1; i < saveddir.length; i++) {
				if (hasScale(i) && lastReferences[i] != null) {
					if (widget.isThereANewPivot(i) || newTID) {
						lastTargetPrice[i] = widget.getCurrentPrice();
						lastTargetTime[i] = widget.getCurrentTime();
						backPTarget[i] = 0;
						backPTime[i] = 0;
						saveddir[i] = widget.isSwingDown(i);
						savedthcount[i] = widget.getCurrentPivotsCount(i);
						savedPivots[i] = widget.getLastPivot(0, i);
						target[i] = lastReferences[i].getTarget();
					}
				}
			}
		}
		if (widget.isThereANewPivot(offSetScale) || newTID) {
			lastTargetPrice[offSetScale] = widget.getCurrentPrice();
			lastTargetTime[offSetScale] = widget.getCurrentTime();
			backPTarget[offSetScale] = 0;
			backPTime[offSetScale] = 0;
		}

	}

	// private double getTargetFromWidget(int level) {
	// Pivot p = widget.getLastPivot(0, level);
	// return getSign(level)*
	// (p.getPivotPrice() -
	// (isGoingUP(level)
	// ?widget.getChmaxprice(level)
	// :widget.getChminprice(level)
	// ));
	// }

	/***
	 * updates the last swing references of each scale.
	 */
	public void setLastReferences(SwingReference[] aLastReferences) {
		lastReferences = aLastReferences;
		for (int i = widget.getStartScaleLevelWidget(); i < thdir.length; i++) {
			if (hasScale(i) && i != offSetScale) {
				if (lastReferences[i] == null)
					target[i] = 0;
				else
					target[i] = lastReferences[i].getTarget();
			}
		}
	}

	// @Override
	// public void setPrevHSInfo(HSTargetInfo aPrevHSTI, int aScale) {
	// prevHSTI[aScale]=aPrevHSTI;
	// }
	//
	// @Override
	// public HSTargetInfo getPrevHSInfo(int aScale) {
	// return prevHSTI[aScale];
	// }

}
