package com.mfg.interfaces.probabilities;

import java.util.ArrayList;
import java.util.List;

import com.mfg.interfaces.indicator.Pivot;
import com.mfg.interfaces.indicator.IIndicator;
import com.mfg.interfaces.trading.RefType;

/**
 * represents a new Swing reference that helps us to know when a target relative
 * to the Pivot<sub>0</sub> comes. Also allows to know when we have a new SC
 * touch.
 * 
 * @author gardero
 * 
 */
public class SwingReference {
	private ElementsPatterns pattern;
	private boolean goingUP;
	private int scTouches = 0;
	private long pivotPrice;
	private long THPrice;
	private long THTime;
	private long p0Time;
	private long p0Price;
	private long pm1Time;
	private long pm1Price;
	private long pm2Price;
	private double maxPriceDiff;
	private int maxTarget;
	private int tID = -1, lastTID = -1;
	private boolean newTID;
	private int scale;
	private WidgetSwingsElement swingElement;
	private List<ProbabilitiesKey> ProbabilitiesKeyList;
	private RefType type = RefType.Swing0;
	public long lastTargetTime = -1;
	boolean count2ice = false;
	boolean prevDir = false;
	private long target = 0;
	private boolean onNewHHLL = true;
	private boolean thereAnewSCTouch;
	public boolean onNewSCTSection = false;
	public boolean firstTime = true;
	private SCTProbabilityKey lastkey = new SCTProbabilityKey(0, 0, 0);

	/**
	 * builds an instance of this class.
	 * 
	 * @param aType
	 *            the type of reference we want to process.
	 * @param aPattern
	 *            the corresponding pattern we have.
	 * @param aWselement
	 *            the element used to do the computations relative to all the
	 *            scales.
	 * @param aScale
	 *            the scale of the reference.
	 * @param aPivotPrice
	 *            the pivot price of the reference.
	 * @param aTHTime
	 *            the time of the TH<sub>0</sub>.
	 * @param aTHPrice
	 *            the price of the TH<sub>0</sub>.
	 * @param aP0Time
	 *            the time of the Pivot<sub>0</sub>.
	 * @param aP0Price
	 *            the price of the Pivot<sub>0</sub>.
	 * @param aPm1Time
	 *            the time of the Pivot<sub>-1</sub>.
	 * @param aPm1Price
	 *            the price of the TH<sub>-1</sub>.
	 * @param aGoingUP
	 *            is the swing is up or down.
	 * @param aPrevSwing
	 *            the length of Swing<sub>-1</sub>
	 * @param widget 
	 */
	public SwingReference(RefType aType, ElementsPatterns aPattern,
			WidgetSwingsElement aWselement, int aScale, long aPivotPrice,
			long aTHTime, long aTHPrice, long aP0Time, long aP0Price,
			long aPm1Time, long aPm1Price, long aPm2Price, boolean aGoingUP,
			double aPrevSwing, int aMaxTarget, IIndicator widget) {
		super();
		type = aType;
		swingElement = aWselement;
		scale = aScale;
		pattern = aPattern;
		pivotPrice = aPivotPrice;
		THTime = aTHTime;
		THPrice = aTHPrice;
		p0Time = aP0Time;
		p0Price = aP0Price;
		pm1Time = aPm1Time;
		pm1Price = aPm1Price;
		pm2Price = aPm2Price;
		goingUP = aGoingUP;
		maxPriceDiff = 0;
		this.maxTarget = aMaxTarget;
		ProbabilitiesKeyList = new ArrayList<>();
		compute(widget);
	}

	/**
	 * builds an instance of this class.
	 * 
	 * @param aType
	 *            the type of reference we want to process.
	 * @param aPattern
	 *            the corresponding pattern we have.
	 * @param aWselement
	 *            the element used to do the computations relative to all the
	 *            scales.
	 * @param aScale
	 *            the scale of the reference.
	 * @param lastPivot
	 *            Pivot<sub>0</sub> information.
	 * @param plastPivot
	 *            Pivot<sub>-1</sub> information.
	 * @param widget 
	 */
	public SwingReference(RefType aType, ElementsPatterns aPatternLeaf,
			WidgetSwingsElement aWselement, int aScale, Pivot lastPivot,
			Pivot plastPivot, Pivot refPivot, int aMaxTarget, IIndicator widget) {
		this(aType, aPatternLeaf, aWselement, aScale, lastPivot.fPivotPrice,
				lastPivot.fConfirmTime, lastPivot.fConfirmPrice,
				lastPivot.fPivotTime, lastPivot.fPivotPrice,
				plastPivot.fPivotTime, plastPivot.fPivotPrice,
				refPivot.getPivotPrice(), !lastPivot.isStartingDownSwing(),
				lastPivot.fLinearSwing, aMaxTarget, widget);
	}

	protected static long computePm1(Pivot plastPivot) {
		return plastPivot.fPivotPrice
				+ (plastPivot.isStartingDownSwing() ? -1 : 1)
				* plastPivot.fLinearSwing;
	}

	/**
	 * gets the proportion of the distance from Pivot<sub>0</sub> to the price
	 * in question relative to Swing<sub>-1</sub>
	 * 
	 * @param currentPrice
	 * @return
	 */
	public double getCurrentTargetPoints(double currentPrice) {
		return swingElement.getTarget(scale);
		// return getMaxPriceDiff(currentPrice) / prevSwing;
	}

	/**
	 * gets the proportion of the current extension of the swing relative to
	 * Swing<sub>-1</sub>.
	 * 
	 * @return
	 */
	public double getCurrentTargetPoints() {
		return swingElement.getTarget(scale);
		// return maxPriceDiff / prevSwing;
	}

	/**
	 * gets the price at which the target is located relative to
	 * Pivot<sub>0</sub>
	 * 
	 * @param TID
	 *            the target ID in [1,Inf)
	 * @return a price.
	 */
	public double getPriceFromTID(int TID) {
		return swingElement.getTargetPrice(TID, getScale());
		// return pivotPrice + getSign() * pattern.getTarget(TID) * prevSwing;
	}

	/**
	 * the sign of the swing direction.
	 * 
	 * @return 1 if swing is up, -1 otherwise.
	 */
	/*private int getSign() {
		return goingUP ? 1 : -1;
	}*/

	/**
	 * tells when a reference is important and can provide more statistics or
	 * not.
	 * 
	 * @param currentPrice
	 * @param widget
	 * @return
	 */
	public boolean isStillActive(double currentPrice,
			IIndicator widget) {
		switch (type) {
		case PreCompute:
			return ((lastTID <= 0) || pattern.isInfinite())
					&& (widget.getLastPivot(0, scale).getConfirmTime() == THTime);
		case Target0:
			return goingUP ? (pivotPrice <= currentPrice)
					: (pivotPrice >= currentPrice);
		case Target2:
			return goingUP ? (pm2Price <= currentPrice)
					: (pm2Price >= currentPrice);
			//$CASES-OMITTED$
		default:
			return widget.getLastPivot(0, scale).getConfirmTime() == THTime;
		}
	}

	/**
	 * gets the maximum extension of the swing.
	 * 
	 * @param currentPrice
	 *            the current price to be considered in the computation
	 * @return the maximum difference to Pivot<sub>0</sub>
	 */
	public double getMaxPriceDiff(double currentPrice) {
		maxPriceDiff = Math.max(maxPriceDiff,
				Math.abs(pivotPrice - currentPrice));

		return maxPriceDiff;
	}

	/**
	 * gets the current extension of the swing.
	 * 
	 * @param currentPrice
	 *            the current price to be considered in the computation
	 * @return the current difference to Pivot<sub>0</sub>
	 */
	public double getCurrentPriceDiff(double currentPrice) {
		return Math.abs(pivotPrice - currentPrice);
	}

	/***
	 * gets the direction of the swing.
	 * 
	 * @return {@code true} iff the swing is going up.
	 */
	public boolean isGoingUP() {
		return goingUP;
	}

	/***
	 * sets the direction of the swing.
	 * 
	 * @param aGoingUP
	 *            {@code true} iff the swing is going up.
	 */
	public void setGoingUP(boolean aGoingUP) {
		goingUP = aGoingUP;
	}

	/***
	 * gets the pivot price.
	 * 
	 * @return a price.
	 */
	public double getPivotPrice() {
		return pivotPrice;
	}

	public void setPivotPrice(long aPivotPrice) {
		pivotPrice = aPivotPrice;
	}

	/**
	 * gets the quantity of SC touches we have till the moment on the swing.
	 * 
	 * @return
	 */
	public int getScTouches() {
		return scTouches;
	}

	public void setScTouches(int aScTouches) {
		scTouches = aScTouches;
	}

	/**
	 * gets the current Target ID
	 * 
	 * @return an Integer in [1,Inf)
	 */
	public int getTID() {
		return tID;
	}

	public int getLastTID() {
		return lastTID;
	}

	/**
	 * gets if a new TID arrived.
	 * 
	 * @return {@code true} iff a new TID arrived.
	 */
	public boolean isThereAnewTID() {
		return newTID;
	}

	protected void setTID(int aTID) {
		newTID = aTID > tID;
		if (newTID) {
			if (tID == -1)
				lastTID = 0;
			else
				lastTID = tID;
			tID = aTID;
		}
	}

	/**
	 * computes the TID and the SCT counter
	 * 
	 * @param widget
	 *            the indicator.
	 */
	public void compute(IIndicator widget) {
		double currentPrice = widget.getCurrentPrice();
		swingElement.considerPrice(currentPrice);
//		target = (long) Math.max(target, getSign()
//				* (-pivotPrice + currentPrice));
		int tid = swingElement.getTargetID(scale);
		setTID(Math.min(tid, maxTarget));
		onNewHHLL |= getCurrentPriceDiff(currentPrice) >= getMaxPriceDiff(currentPrice);
		if (isThereAnewSCTouch(widget) && !type.equals(RefType.PreCompute)) {
			scTouches++;
			thereAnewSCTouch = true;
		} else {
			thereAnewSCTouch = false;
		}
	}

	/**
	 * tells if we have a new HHLL
	 * 
	 * @return {@code true} iff we have a new HHLL.
	 */
	public boolean isOnNewHHLL() {
		return onNewHHLL;
	}

	public void resetHHLL() {
		onNewHHLL = false;
	}

	/**
	 * tells if there is a new SC touch.
	 * 
	 * @return {@code true} iff we have a new SC touch
	 */
	public boolean isThereAnewSCTouch() {
		return thereAnewSCTouch;
	}

	/**
	 * checks if there is a new SC touch. The SC touch needs to occur after a
	 * new HHLL.
	 * 
	 * @param aWidget
	 *            the indicator.
	 * @return {@code true} iff there is a new SC touch.
	 */
	private boolean isThereAnewSCTouch(IIndicator aWidget) {
		boolean onSC = aWidget.getCurrentPrice() == getSC(aWidget);
		boolean res = onSC && onNewHHLL;
		if (onSC)
			onNewHHLL = false;
		return res;
	}

	/**
	 * gets the current SC price.
	 * 
	 * @param aWidget
	 *            the indicator.
	 * @return
	 */
	protected double getSC(IIndicator aWidget) {
		return (goingUP) ? aWidget.getCurrentBottomRegressionPrice(scale)
				: aWidget.getCurrentTopRegressionPrice(scale);
	}

	public boolean isFirstTime() {
		return firstTime;
	}

	public void setFirstTime(boolean aFirstTime) {
		firstTime = aFirstTime;
	}

	/***
	 * gets the corresponding pattern of this reference.
	 * 
	 * @return
	 */
	public ElementsPatterns getPattern() {
		return pattern;
	}

	public void setPattern(ElementsPatterns aPattern) {
		pattern = aPattern;
	}

	/**
	 * gets the scale of this reference.
	 * 
	 * @return
	 */
	public int getScale() {
		return scale;
	}

	/**
	 * sets the scale of this reference.
	 * 
	 * @param aScale
	 */
	public void setScale(int aScale) {
		scale = aScale;
	}

	/**
	 * gets the price of TH<sub>0</sub>
	 * 
	 * @return
	 */
	public long getTHPrice() {
		return THPrice;
	}

	/**
	 * gets the time of TH<sub>0</sub>
	 * 
	 * @return
	 */
	public long getTHTime() {
		return THTime;
	}

	/**
	 * gets the time of Pivot<sub>0</sub>
	 * 
	 * @return
	 */
	public long getP0Time() {
		return p0Time;
	}

	public void setP0Time(long aP0Time) {
		p0Time = aP0Time;
	}

	/**
	 * gets the price of Price<sub>0</sub>
	 * 
	 * @return
	 */
	public long getP0Price() {
		return p0Price;
	}

	public void setP0Price(long aP0Price) {
		p0Price = aP0Price;
	}

	/**
	 * gets the time of Pivot<sub>-1</sub>
	 * 
	 * @return
	 */
	public long getPm1Time() {
		return pm1Time;
	}

	public void setPm1Time(long aPm1Time) {
		pm1Time = aPm1Time;
	}

	/**
	 * gets the price of Pivot<sub>-1</sub>
	 * 
	 * @return
	 */
	public long getPm1Price() {
		return pm1Price;
	}

	public void setPm1Price(long aPm1Price) {
		pm1Price = aPm1Price;
	}

	/**
	 * gets the element that contains information about swing ratios in all
	 * scales.
	 * 
	 * @return
	 */
	public WidgetSwingsElement getSwingElement() {
		return swingElement;
	}

	@Deprecated
	public List<ProbabilitiesKey> getProbabilitiesKeyList() {
		return ProbabilitiesKeyList;
	}

	public void setProbabilitiesKeyList(
			List<ProbabilitiesKey> aProbabilitiesKeyList) {
		ProbabilitiesKeyList = aProbabilitiesKeyList;
	}

	/**
	 * gets the type of the reference according to the calculation we are
	 * performing.
	 * 
	 * @return
	 */
	public RefType getType() {
		return type;
	}

	public void setType(RefType aType) {
		type = aType;
	}

	/**
	 * gets the target in price
	 * 
	 * @return a price delta relative to Pivot<sub>0</sub>
	 */
	public long getTarget() {
		return target;
	}

	/**
	 * gets if the Key for the SC touch computation has changed.
	 * 
	 * @param newKey
	 * @return
	 */
	public boolean isThereAnewSCTouchKey(SCTProbabilityKey newKey) {
		boolean res = !lastkey.equals(newKey);
		lastkey = newKey;
		return res;
	}

	/**
	 * gets the last key of the SX touch statistics.
	 * 
	 * @return an SC touch key.
	 */
	public SCTProbabilityKey getLastkey() {
		return lastkey;
	}

}
