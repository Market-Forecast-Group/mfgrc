package com.mfg.widget.arc.strategy;

import static com.mfg.utils.Utils.debug_var;
import static com.mfg.utils.Utils.warn;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicBoolean;

import org.mfg.mdb.runtime.MDBList;

import com.mfg.common.QueueTick;
import com.mfg.inputdb.prices.mdb.PriceMDB.RandomCursor;
import com.mfg.inputdb.prices.mdb.PriceMDB.Record;
import com.mfg.interfaces.indicator.Pivot;
import com.mfg.interfaces.symbols.AbstractIndicatorParamBean.CenterLineAlgo;
import com.mfg.interfaces.symbols.AbstractIndicatorParamBean.SPAType;
import com.mfg.interfaces.symbols.AbstractIndicatorParamBean.StartPointLength;
import com.mfg.interfaces.symbols.AbstractIndicatorParamBean.TopBottomMaxDist;
import com.mfg.utils.IterativeMean;
import com.mfg.utils.MovingAverage;
import com.mfg.utils.U;
import com.mfg.widget.arc.data.PointRegressionLine;
import com.mfg.widget.arc.gui.IndicatorParamBean;
import com.mfg.widget.arc.math.geom.PolyEvaluator;
import com.mfg.widget.arc.strategy.BaseScaleIndicator.EState;

/**
 * This class models a generic indicator which has the logic to do the central
 * line processing. A generic indicator is of a single scale and has the
 * possibility to compute some "statistics" of the last N points where N is the
 * distance between the current time and a so-called <b>start point</b> which
 * can be modified by the external world.
 * 
 * <p>
 * The generic indicator has the possibility to have a Top and a Down distance,
 * these are two additional lines which form a <i>channel</i>.
 * 
 * <p>
 * A Channel is composed by three lines, usually (but not always) parallel. The
 * center line is decided by the indicator, the top and the bottom lines are set
 * by a top/down distance metering.
 * 
 * 
 * 
 * 
 * @author Sergio
 * 
 */
abstract class BaseChannel extends IndicatorComponent implements IChannelHelper {

	@Override
	public boolean supportsSlope() {
		return true;
	}

	@Override
	public final double eval(double x) {
		double coeff[] = getChannelCoefficients();
		if (coeff == null) {
			return Double.NaN;
		}
		return PolyEvaluator.evaluate(coeff, x);
	}

	/**
	 * the generic model of a channel is a straight line and this is enforced by
	 * the base method for all the channels.
	 * 
	 * <p>
	 * Only the polynomial channels will override this base implementation.
	 */
	@Override
	public double[] getChannelCoefficients() {
		double coefficients[] = new double[2];

		// the angular coefficient
		coefficients[1] = (getCenterY2() - getCenterY1()) / (getX2() - getX1());

		// the intercept
		coefficients[0] = getCenterY1() - (coefficients[1] * getX1());

		return coefficients;
	}

	@Override
	public double getTopDistance() {
		return fTop - fCenter;
	}

	@Override
	public double getBottomDistance() {
		return fCenter - fBottom;
	}

	/**
	 * simple protection method which will complain (in debug) if something is
	 * wrong with the size
	 */
	protected final void _checkCoherentSize() {
		/*
		 * fX1 could be negative if the window is starting from zero, in that
		 * case the size should not be computed in this way, I assume that it is
		 * coherent.
		 */
		if (fX1 < 0) {
			return;
		}
		if (_getN() != (fX2 - fX1 + 1)) {

			assert (false) : "lev " + (this.fInd.level + 1)
					+ "   no correct size, " + _getN() + " got expected: "
					+ (fX2 - fX1 + 1);
		}

	}

	/** True when a new channel starts */
	private boolean fNewChannel;

	private boolean fNewRc;

	private boolean fNewSc;

	protected boolean fCenterY1isComputed = false;

	IndicatorMemory fStorage;

	protected int fX1 = 0;

	protected int fX2 = -1; // this is the end point.

	private final boolean fAvoidTouch;

	private double fDelta = 0;

	protected BaseChannelHelper _maxDistanceGenerator;

	@Override
	public void hurryUp(int hurryUpQuota) {
		super.hurryUp(hurryUpQuota);
		_maxDistanceGenerator.hurryUp(hurryUpQuota);
	}

	@Override
	public void calmDown(int calmDownQuota) {
		super.calmDown(calmDownQuota);
		_maxDistanceGenerator.calmDown(calmDownQuota);
	}

	/** These are the values of the indicator */
	private double fCenter;

	private double fBottom;

	private double fTop;

	private int fRcTouches = 0;
	private int fScTouches = 0;

	private EState fOldState = EState.ZIG_STATE; // /just a state

	/**
	 * A value which is used to undo the correction that may be done by the
	 * {@link #newTick(QueueTick)} method. This will correct the {@link #fDelta}
	 * field until the channel is fixed to the new start point.
	 * */
	private double fUndoDelta;

	/** True is I have to put the center line in half way. */
	private final boolean fPutCenterLineInHalf;

	private double _rawCenter;

	private double _rawBottom;

	private double _rawTop;

	/**
	 * The list of the prices, used to compute the indicator. It is here because
	 * I may access it from multiple threads.
	 */
	protected MDBList<Record> _priceList;

	private RandomCursor _cursor;

	protected BaseChannel(BaseScaleIndicator aInd) {
		super(aInd);

		_maxDistanceGenerator = new BaseChannelHelper(aInd);

		IndicatorParamBean bean = aInd._compositeIndicator.bean;
		TopBottomMaxDist dist = bean.getIndicator_TopBottomMaxDist();

		try {
			_cursor = aInd._compositeIndicator.getMdbDatabase().randomCursor();
			aInd._compositeIndicator.getMdbDatabase().getSession()
					.defer(_cursor);
			_priceList = aInd._compositeIndicator.getMdbDatabase()
					.list(_cursor);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		switch (dist) {
		case BRUTE_FORCE:
		case CONVEX_HULL:
			fAvoidTouch = false;
			fPutCenterLineInHalf = false;
			break;
		case CONVEX_HULL_FIXED_TICK:
		case FIXED_TICK:
		case PERCENTAGE:
			fAvoidTouch = true;
			fPutCenterLineInHalf = true;
			break;
		default:
		case HALF_CONVEX_HULL:
			throw new UnsupportedOperationException();

		}

	}

	/**
	 * Initializes the values for the {@link #newTick(QueueTick)} and the
	 * {@link #_updateIndicatorValues()} method.
	 * 
	 * <p>
	 * They share some common code.
	 */
	private void _initUpdateIndicatorValue() {
		fNewSc = false;
		fNewRc = false;

		EState curState = fInd.fPivotsGenerator.fState;
		if (curState != fOldState) {
			fScTouches = 0;
			fRcTouches = 0;
			fOldState = curState;
		}
	}

	/**
	 * When this method is called the channel is not updated yet. fX2 is updated
	 * but fX1 is not.
	 * 
	 * @param newSp
	 *            the new fX1, it must be greater than fX1
	 */
	protected abstract void _moveToBaseChannelImpl(int newSp);

	/**
	 * returns the number of points used internally by the component.
	 * 
	 * <p>
	 * This is usually <code>fX2-fX1+1</code> but this method is here to enforce
	 * it during consistency check.
	 * 
	 * @return the internal number used by the indicator to compute its values
	 */
	protected abstract int _getN();

	private void _realUpdateIndicatorValues() {
		if (Double.isNaN(fCenter)) {
			return;
		}

		/*
		 * Asking the raw distances actually make them computed, because if the
		 * channel has moved than the raw distances have been resetted.
		 * 
		 * But I would have to ask them twice, one to trigger the computation
		 * (at least in the convex hull) and another time to have the real
		 * values.
		 * 
		 * Now I give the generator a chance to make
		 */
		_maxDistanceGenerator.updateTopBottomDistances();

		double topD = _maxDistanceGenerator.__tempGetRawMaxTop();
		double bottomD = _maxDistanceGenerator.__tempGetRawMaxBottom();

		/*
		 * Saving of the raw indicator values, this is used because the chart
		 * may need to display them. In the case of the indicator which does not
		 * avoid touch they will be equal to the normal values.
		 */
		_rawCenter = fCenter;
		_rawBottom = fCenter - bottomD;
		_rawTop = fCenter + topD;

		// fDelta + fUndoDelta is the previous delta!
		fBottom = fCenter + fDelta + fUndoDelta - bottomD;
		fTop = fCenter + fDelta + fUndoDelta + topD;

		if (fPutCenterLineInHalf) {
			fCenter = (fBottom + fTop) / 2.0;
			fStorage.replaceLast(fCenter);
		}

		long curPrice = fInd._compositeIndicator.lastTick.getPrice();
		if (fAvoidTouch) {

			// I must assess if the current price is inside the band
			double pBot = curPrice - fBottom;
			double pTop = curPrice - fTop;

			/*
			 * This assertion may fail if we have a mixed fixed ticks + convex
			 * hull, because this may mean that the channel has zero width
			 */
			// assert (fBottom != fTop);
			if (fBottom == fTop) {
				return; // zero width channel.
			}

			double multTopBot = pBot * pTop;
			if (multTopBot > 0) {
				double curDelta;
				if (curPrice <= fBottom) {
					curDelta = curPrice - fBottom;
					manage_down_touch();
				} else {
					assert (curPrice >= fTop);
					curDelta = curPrice - fTop;
					manage_up_touch();
				}

				fCenter += curDelta;
				fBottom += curDelta;
				fTop += curDelta;
				fDelta += fUndoDelta;
				fDelta += curDelta;
				fUndoDelta = -curDelta;

			} else if (multTopBot == 0) {
				if (curPrice == fBottom) {
					// ok, this is down touch
					manage_down_touch();
				} else {
					assert (curPrice == fTop);
					manage_up_touch();
				}
			}
		} else {
			/*
			 * Even if I don't avoid the touch I must see if I touch
			 * 
			 * The convex hull should take care of the touch, but if the center
			 * line is a polyline with a smoothing than I have to clamp here the
			 * price.
			 */
			if (fBottom >= curPrice) {
				manage_down_touch();
				fBottom = Math.min(fBottom, curPrice);
			} else if (fTop <= curPrice) {
				manage_up_touch();
				fTop = Math.max(fTop, curPrice);
			}
		}

	}

	@Override
	public void begin(int tick) {
		_maxDistanceGenerator.begin(tick);

	}

	@Override
	public final void drawOn(ChannelIndicator chInd) {

		if (_indicatorDisabled || Double.isNaN(fCenter)
				|| fInd.fPivotsGenerator.fNumPivots == 0) {
			return;
		}

		PointRegressionLine prl = new PointRegressionLine();
		prl.setTime(chInd.lastTick.getFakeTime());
		prl.setPriceCenter(fCenter);
		prl.setPriceBottom(fBottom);
		prl.setPriceTop(fTop);

		prl.setUnadjustedIndicator(_rawBottom, _rawCenter, _rawTop);

		prl.setLevel(fInd.level + 1);
		chInd.newPointRegressionLine(prl);

		if (Double.isNaN(getCenterY1())) {
			return;
		}

		if (!fNewChannel) {
			chInd.createAndDispatchNewRealTimeChannel(fInd.level, this);
		} else {
			chInd.createAndDispatchNewStartingChannel(fInd.level, this);
		}

		fNewChannel = false;

	}

	@Override
	public final double getBottomY2() {
		return fBottom;
	}

	@Override
	public final double getCenterY1() {
		if (fCenterY1isComputed) {
			return getRealCenterY1();
		}
		return fStorage.getVal(fX1);
	}

	@Override
	public final double getCenterY2() {
		return fCenter;
	}

	@Override
	public double getRawBottomY2() {
		return _rawBottom;
	}

	@Override
	public double getRawCenterY2() {
		return _rawCenter;
	}

	@Override
	public double getRawTopY2() {
		return _rawTop;
	}

	@Override
	public final int getRcTouches() {
		return fRcTouches;
	}

	@SuppressWarnings("static-method")
	protected double getRealCenterY1() {
		throw new UnsupportedOperationException();
	}

	protected abstract double getRealCenterY2();

	@Override
	public final int getScTouches() {
		return fScTouches;
	}

	@Override
	public final double getTopY2() {
		return fTop;
	}

	@Override
	public final int getX1() {
		return fX1;
	}

	@Override
	public final int getX2() {
		return fX2;
	}

	protected abstract void indicator_new_tick(QueueTick qt);

	@Override
	public boolean isCenterLineInHalf() {
		return fPutCenterLineInHalf;
	}

	@Override
	public final boolean isThereANewRc() {
		return fNewRc;
	}

	@Override
	public final boolean isThereANewSc() {
		return fNewSc;
	}

	private final void manage_down_touch() {
		if (fOldState == EState.ZIG_STATE) {
			// zig and down touch, this is support
			fScTouches++;
			fNewSc = true;
		} else {
			fRcTouches++;
			fNewRc = true;
		}

	}

	private final void manage_up_touch() {
		if (fOldState == EState.ZIG_STATE) {
			// zig and up touch this is resistance
			fRcTouches++;
			fNewRc = true;
		} else {
			// up touch and zag, this is support
			fScTouches++;
			fNewSc = true;
		}

	}

	@Override
	public final void moveTo(int newSp) {
		if (newSp < fX1) {
			throw new IllegalArgumentException(
					"Cannot move the channel backward!");
		}

		fNewChannel = true;
		if (fX1 != newSp) {
			_maxDistanceGenerator.moveTo(newSp);
			_moveToBaseChannelImpl(newSp);
			fX1 = newSp;
			_checkCoherentSize();
		}

		_updateIndicatorValues();
	}

	@Override
	public final void newTick(QueueTick qt) {
		_initUpdateIndicatorValue();

		fUndoDelta = 0;

		assert (qt.getFakeTime() >= fX1) : "qt " + qt + " x1 " + fX1 + " lev "
				+ this.fInd.level + " layer "
				+ this.fInd._compositeIndicator.fLayer;
		assert (qt.getFakeTime() - 1 == fX2) : " qt " + qt + " fx2 " + fX2;
		fX2++;

		indicator_new_tick_check(qt);

		fCenter = getRealCenterY2();
		fStorage.store(fX2, fCenter);

		assert (fStorage.getWindow() > 0 || _getN() == fStorage.size()) : "N "
				+ _getN() + " window " + fStorage.size();

		_maxDistanceGenerator.newTick(qt);
		_realUpdateIndicatorValues();
	}

	protected final void indicator_new_tick_check(QueueTick qt) {
		indicator_new_tick(qt);
		_checkCoherentSize();
	}

	/**
	 * updates the indicator values but not adding the last tick. This method
	 * assumes that the indicator and the top/bottom max generators are already
	 * updated; it does only the post processing.
	 * 
	 * <p>
	 * When you call this method the swing could also be changed, for example in
	 * the case of a negative on flat, so also the sc/rc count should be
	 * adjusted, in case.
	 * 
	 * 
	 * 
	 */
	private void _updateIndicatorValues() {

		_initUpdateIndicatorValue();

		fCenter = getRealCenterY2();
		fStorage.replaceLast(fCenter);

		_realUpdateIndicatorValues();

	}

}

abstract class BasePivotsGenerator extends IndicatorComponent {

	protected boolean _newPivot;

	@Override
	public void warmUpFinished() {
		super.warmUpFinished();
		doS0S0PrimeStats();
	}

	protected Pivot fLastMinusOnePivot;

	// protected Pivot fNewPivot;

	protected Pivot fLastPivot;

	// this stores all the pivots.
	protected ArrayList<Pivot> fPivots = new ArrayList<>();

	/** The tentative pivot is the pivot which is not yet confirmed */
	protected QueueTick fTentativePivot = new QueueTick(-1, -1, 0, true, 1);

	/**
	 * Every time a new tentative pivot is created, the generator stores the
	 * price which is needed to confirm this tentative pivot, that is the
	 * threshold.
	 */
	protected int _nextConfirmPrice;

	/**
	 * HH is the tentative high pivot, LL is the tentative low pivot, valid when
	 * the state is undecided.
	 */
	protected QueueTick fHH;

	protected QueueTick fLL;

	protected QueueTick fFirstPrice;
	protected boolean fNewTentative;

	protected int fNumPivots;

	// The pivot generator part stores a state which is shared by all the
	// generating rules.
	BaseScaleIndicator.EState fState = com.mfg.widget.arc.strategy.BaseScaleIndicator.EState.BEFORE_FIRST_PRICE;

	private double[] stats;

	protected BasePivotsGenerator(BaseScaleIndicator aInd) {
		super(aInd);
	}

	protected void addNewPivot(QueueTick confirm) {
		// fNewPivot =
		fLastPivot = new Pivot(fTentativePivot, fInd.level, confirm, fLastPivot);
		synchronized (fPivots) {
			fPivots.add(fLastPivot);
		}

		fTentativePivot = confirm;
		fNumPivots++;
		fNewTentative = true;
		fState = fLastPivot.isStartingDownSwing() ? EState.ZAG_STATE
				: EState.ZIG_STATE;
		_newPivot = true;
	}

	protected void addNewTentative(QueueTick qt, int aConfirmPrice) {
		fNewTentative = true;
		fTentativePivot = qt;
		_nextConfirmPrice = aConfirmPrice;
	}

	//

	@Override
	public void begin(int tick) {
		// null
	}

	@Override
	public void drawOn(ChannelIndicator aCh) {
		// null
	}

	/**
	 * returns the past pivot at <b>steps</b> steps in the past.
	 * 
	 * @param pastSteps
	 *            The number of past steps. 0 means the current confirmed pivot,
	 *            1 means the last confirmed pivots, etc...
	 * 
	 * @return the pivot, or null if there is not present the information
	 */
	public Pivot getPastPivot(int pastSteps) {
		if (fPivots.size() == 0 || (fPivots.size() <= pastSteps)) {
			return null;
		}

		return fPivots.get(fPivots.size() - pastSteps - 1);

		// if (pastSteps == 0) {
		// if (fNewPivot != null) {
		// return fNewPivot;
		// }
		// return fLastPivot;
		// } else if (pastSteps == 1) {
		// if (fNewPivot != null) {
		// return fLastPivot;
		// }
		// return fLastMinusOnePivot;
		// }
		// return null;
	}

	public boolean isThereANewTentative() {
		return fNewTentative;
	}

	public int getConfirmThreshold() {
		return _nextConfirmPrice;
	}

	public boolean isThereANewPivot() {
		synchronized (fPivots) {
			if (fPivots.size() == 0) {
				return false;
			}
			Pivot lastPv = fPivots.get(fPivots.size() - 1);
			if (lastPv.fConfirmTime == fInd._compositeIndicator.lastTick
					.getFakeTime()) {
				return true;
			}
			return false;
		}

	}

	public abstract void reapplyRules(QueueTick qt);

	public void saveUntil(int aTime) {

		if (fPivots.size() < 1000) {
			return;
		}

		// I do a "cheap" binary search
		int idx = fPivots.size() / 2;

		if (fPivots.get(idx).fPivotTime < aTime) {
			fPivots.subList(0, idx + 1).clear();
		}
	}

	@SuppressWarnings("boxing")
	private void doS0S0PrimeStats() {

		if (fPivots.size() < 100 && this.fInd.level != 0) {
			U.debug_var(399352, "not enough data for this scale I have 	",
					fPivots.size(), " I copy from lower scale");
			stats = Arrays.copyOf(
					this.fInd._compositeIndicator._s_inds[this.fInd.level - 1]
							.getStats(), 4);

		} else {

			stats = new double[4];
			if (fPivots.size() < 100) {
				Arrays.fill(stats, 0);
			} else {
				// TreeSet<Double> ratios = new TreeSet<>();
				// ArrayList<Double> ratios = new ArrayList<Double>();
				double ratios[] = new double[fPivots.size() - 1];
				Iterator<Pivot> it = fPivots.iterator();
				Pivot curPivot, nextPivot;
				curPivot = it.next();
				int index = 0;
				while (it.hasNext()) {
					nextPivot = it.next();

					double S0 = Math.abs(curPivot.fConfirmPrice
							- curPivot.fPivotPrice);
					double S0Prime = Math.abs(nextPivot.fPivotPrice
							- curPivot.fPivotPrice);
					curPivot = nextPivot;
					double ratio;
					if (S0Prime == 0) {
						ratio = 0;
					}
					ratio = S0Prime / S0;
					ratios[index++] = ratio;
				}

				Arrays.sort(ratios);

				int threshold = ratios.length / 4;

				stats[0] = ratios[threshold];

				stats[1] = ratios[ratios.length / 2];

				stats[2] = ratios[threshold * 3];

				stats[3] = ratios[ratios.length - 1];
			}

		}

		U.debug_var(342421, "layer ", this.fInd._compositeIndicator.fLayer,
				" scale ", this.fInd.level + 1, " statistics for 25% ",
				stats[0], " 50% ", stats[1], " 75 ", stats[2], " 100% ",
				stats[3]);
	}

	public double[] getStats() {
		return stats;
	}
}

/**
 * This is the base class for the positive generator channel capability.
 * 
 * 
 * @author Sergio
 * 
 */
abstract class BasePositiveChannelGenerator extends IndicatorComponent {

	protected boolean fNewPositiveChannel;

	protected BasePositiveChannelGenerator(BaseScaleIndicator aInd) {
		super(aInd);
	}

	@Override
	public void begin(int tick) {
		//
	}

	@Override
	public final void drawOn(ChannelIndicator aCh) {
		throw new UnsupportedOperationException();
	}

	public boolean isCreatedNewChannel() {
		return fNewPositiveChannel;
	}

}

/**
 * this object manages the length of the channel based on rules.
 * 
 * <p>
 * The length of the channel follows some rules which can alter the channel
 * length with or without the presence of pivots.
 * <p>
 * If a pivot is born then the channel is called <b>negative</b> otherwise a
 * channel whose length is changed without a pivot is called <b>positive</b>.
 * 
 * <p>
 * For certain types of rules the length of the channel is fixed, so this
 * manager always do a no-op, because the length of the channel is decided
 * elsewhere.
 * 
 * @author Pasqualino
 * 
 */
abstract class ChannelLengthManager implements Serializable {
	protected BaseScaleIndicator fInd;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5122298892769414924L;

	ChannelLengthManager(BaseScaleIndicator aInd) {
		fInd = aInd;
	}

	/**
	 * generates a new start point, either for a positive or a negative channel.
	 * <p>
	 * The difference between the two is that the negative must succeed, or in
	 * any case the channel is created, even if the start point could not be
	 * advanced.
	 * <p>
	 * In the case of a positive channel the start point could not be
	 * changeable. In this case the channel will not be created.
	 * 
	 * <p>
	 * This method will change actually the channel in the indicator (if it
	 * returns true). If the method returns false than no changes have been done
	 * to the indicator.
	 * 
	 * @param forNegative
	 *            true if this is creatable for a negative channel.
	 * 
	 * @return true if the channel can be created (or if the channel is negative
	 *         and in any case we have made the new channel).
	 * 
	 */
	public abstract boolean tryMakeChannelShorter(boolean forNegative);

	/**
	 * returns the next positive start point, if there is one.
	 * 
	 * @return the next possible start point: it returns zero if there is not a
	 *         next start point available for positive channel.
	 */
	@SuppressWarnings("static-method")
	public int getNextPositiveStartPoint() {
		return 0;
	}
}

class ConvexHullGenerator implements IMaxDistanceGenerator {

	private IndicatorSingle fInd;
	private ConvexHull fCh;
	private double fMaxD;
	private final boolean _useTh;
	private final double _thPercentage;
	private MDBList<Record> _priceList;
	private RandomCursor _cursor;

	/**
	 * 
	 * @param aInd
	 * @param isUp
	 * @param useTh
	 *            this flag is true alters the min distance of the convex hull
	 *            generator
	 * @param thPercentage
	 *            the percentage of the channel relative the the last threshold.
	 * @param useBruteForce
	 *            if this flag is true the convex hull uses only brute force to
	 *            compute top/bottom max distance
	 */
	public ConvexHullGenerator(BaseScaleIndicator aInd, boolean isUp,
			boolean useTh, double thPercentage, boolean useBruteForce) {
		fInd = (IndicatorSingle) aInd;
		fCh = new ConvexHull(isUp, useBruteForce);

		_useTh = useTh;
		_thPercentage = thPercentage / 2.0;

		try {
			_cursor = aInd._compositeIndicator.getMdbDatabase().randomCursor();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		aInd._compositeIndicator.getMdbDatabase().getSession().defer(_cursor);
		_priceList = aInd._compositeIndicator.getMdbDatabase().list(_cursor);

	}

	@Override
	public void begin(int tick) {
		// nothing
	}

	@Override
	public double getMaxDistance() {
		if (fMaxD < 0) {
			IChannelHelper chan = fInd.fIndicator;
			fMaxD = fCh.getMaxDistanceFromPoly(chan.getChannelCoefficients(),
					chan.getX1(), chan.getX2(), _priceList);

			if (_useTh) {
				Pivot lastPivot = fInd.getLastPivot(0);
				if (lastPivot != null) {
					double th = Math.abs(lastPivot.getConfirmPrice()
							- lastPivot.getPivotPrice());
					th *= _thPercentage;
					if (fMaxD < th) {
						fMaxD = th;
					}
				}
			}
		}
		return fMaxD;
	}

	@Override
	public void moveTo(int newSp) {
		fCh.moveForwardTo(newSp, _priceList);
		fMaxD = -1; // invalidate the max distance.
	}

	@Override
	public void newTick(QueueTick qt) {
		fCh.addTick(qt);
		fMaxD = -1;
	}

	@Override
	public void hurryUp(int hurryUpQuota) {
		fCh.hurryUp(hurryUpQuota);

	}

	@Override
	public void calmDown(int calmDownQuota) {
		fCh.calmDown(calmDownQuota);
	}

}

/**
 * 
 * @author Sergio
 * 
 */
class FixedMovingAverage extends FixedStartPointChannel {

	IterativeMean fAvg = new IterativeMean();

	protected FixedMovingAverage(IndicatorSingle indicatorSingle,
			int minimumWindow) {
		super(indicatorSingle, minimumWindow);
		debug_var(198939, "Created a fixed moving average");
		fCenterY1isComputed = false;
	}

	@Override
	protected void _clearAll() {
		fAvg.clear();
	}

	@Override
	protected void _remove_one_tick(QueueTick queueTick) {
		fAvg.remove(queueTick.getPrice());
	}

	@Override
	protected double getRealCenterY2() {
		return fAvg.getAvg();
	}

	@Override
	public double getSlope() {
		if (Double.isNaN(getCenterY1())) {
			return Double.NaN;
		}
		return (getCenterY2() - getCenterY1()) / (getX2() - getX1());
	}

	@Override
	protected void indicator_new_tick(QueueTick qt) {
		fAvg.add(qt.getPrice());
	}

	@Override
	protected int _getN() {
		return fAvg.getN();
	}

}

abstract class FixedStartPointChannel extends BaseChannel {

	/**
	 * The minimum window is used only by the
	 * {@link MixedPivotWindowLengthManager} because it needs to know the
	 * minimum indow used by the channel after a pivot.
	 */
	private final int _minimumWindow;

	@Override
	public int getMinimumWindow() {
		return _minimumWindow;
	}

	/**
	 * 
	 * @param aInd
	 * @param aMinimumWindow
	 *            the minimum window of this channel. A variable width channel
	 *            usually has not a minimum window, the window is determined by
	 *            the start point which is moved by the pivot or the spa array.
	 *            But there is a mixed mode length in which there is a minimum
	 *            window which is enforced by the outside, and then the channel
	 *            length manager will update the length only at the starting of
	 *            a new swing. If you do not want this parameter put it zero.
	 */
	protected FixedStartPointChannel(BaseScaleIndicator aInd, int aMinimumWindow) {
		super(aInd);
		_minimumWindow = aMinimumWindow;
		fStorage = new IndicatorMemory(-1);
	}

	protected abstract void _clearAll();

	// protected abstract void _moveToFixedSpChan(int newSp);

	@Override
	protected final void _moveToBaseChannelImpl(int newSp) {
		_moveToFixedSpChan_base(newSp);
		fStorage.moveStartPoint(newSp);
	}

	protected void _moveToFixedSpChan_base(int newSp) {

		int delta = newSp - fX1;
		int totDelta = fX2 - fX1;
		int startTime = fX1
		/*- fInd._compositeIndicator.savedTicks.get(0).getFakeTime()*/;
		assert (startTime >= 0);
		int endTime = startTime + (newSp - fX1);

		if (delta > totDelta / 2) {
			// I can redo all the points
			_clearAll();
			QueueTick qt;
			/*
			 * It should be <= but the last tick is not stored (yet) in db, so
			 * the cycle is strictly with < and then the last tick is added
			 * manually after.
			 */
			for (int i = endTime; i < fInd._compositeIndicator.lastTick
					.getFakeTime(); ++i) {
				qt = new QueueTick();
				com.mfg.inputdb.prices.mdb.PriceMDB.Record rec = _priceList
						.get(i);
				rec.copyTo(qt);
				indicator_new_tick(qt);
			}
			indicator_new_tick(fInd._compositeIndicator.lastTick);

		} else {
			for (int i = startTime; i < endTime; ++i) {
				QueueTick qt = new QueueTick();
				com.mfg.inputdb.prices.mdb.PriceMDB.Record rec = _priceList
						.get(i);
				rec.copyTo(qt);
				_remove_one_tick(qt);
			}
		}

	}

	/**
	 * removes one tick from the indicator: the tick is then removed and the
	 * 
	 * 
	 * @param queueTick
	 */
	protected abstract void _remove_one_tick(QueueTick queueTick);

}

/**
 * a simple class which gives the maximum distance using the number of ticks.
 * 
 * @author Sergio
 * 
 */
class FixedTickMaxDistanceGenerator implements IMaxDistanceGenerator {

	double fMaxD;

	@SuppressWarnings("boxing")
	public FixedTickMaxDistanceGenerator(BaseScaleIndicator aInd) {

		// int startTicks = aInd._compositeIndicator.bean
		// .getFixWindow_deltaTopBottomTicks();
		//
		// int multiplier = aInd._compositeIndicator.bean
		// .getFixWindow_deltaTopBottomMultiplier();
		//
		// multiplier = (int) Math.pow(multiplier, aInd.level - 1);
		// startTicks *= multiplier;
		// //debug_var(103915, "Created a delta distance of ", startTicks,
		// " ticks");
		// fMaxD = startTicks;

		fMaxD = getMaxDistanceInTicksFromBeanInTicks(aInd);
		debug_var(103915, "Created a delta distance of ", fMaxD, " ticks");
	}

	/**
	 * Simple helper method which is used to get the fixed distance in ticks.
	 * 
	 * @param aInd
	 * @return
	 */
	public static int getMaxDistanceInTicksFromBeanInTicks(
			BaseScaleIndicator aInd) {
		int startTicks = aInd._compositeIndicator.bean
				.getFixWindow_deltaTopBottomTicks();

		int multiplier = aInd._compositeIndicator.bean
				.getFixWindow_deltaTopBottomMultiplier();

		multiplier = (int) Math.pow(multiplier, aInd.level - 1);
		startTicks *= multiplier;
		return startTicks;
	}

	@SuppressWarnings("boxing")
	@Override
	public void begin(int tick) {
		fMaxD *= tick;
		fMaxD /= 2.0;
		debug_var(183919, "Created max distance of ", fMaxD);
	}

	@Override
	public FixedTickMaxDistanceGenerator clone() {
		try {
			return (FixedTickMaxDistanceGenerator) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public double getMaxDistance() {
		return fMaxD;
	}

	@Override
	public void moveTo(int newSp) {
		// here it is void.
	}

	@Override
	public void newTick(QueueTick qt) {
		//
	}

	@Override
	public void hurryUp(int hurryUpQuota) {
		// empty

	}

	@Override
	public void calmDown(int calmDownQuota) {
		// empty

	}

}

/**
 * It gives the name to the file, but it is not used.
 * 
 * @author Sergio
 * 
 */
class HelperClasses {
	private HelperClasses() {
		throw new IllegalArgumentException();
	}
}

/**
 * An interface to make the channel mutable.
 * 
 * <p>
 * A channel has only two types of movement really: on the right it can go one
 * price at a time. On the left, the past, it could become shorter of more than
 * one price at a time.
 * 
 * <p>
 * The first movement is done using the {@link #newTick(QueueTick)} method, the
 * second using the {@linkplain IChannelHelper#moveTo(int)}
 * 
 * 
 * @author Sergio
 * 
 */
interface IChannelHelper extends IIndicatorParticipant, IChannel {
	/**
	 * moves the channel to a new start point. Moving the channel to a start
	 * point which is the same is not an error, it simply creates a new channel
	 * in the same place. Moving the channel to a start point which is older
	 * than the current start point is an error
	 * 
	 * @param newSp
	 *            the new start point. Must be equal or greater than
	 *            {@link #getX1()}
	 * 
	 * @throws IllegalArgumentException
	 *             if the new start point is older than the current start point,
	 *             that is if {@link #getX1()} &gt newSp
	 */
	public void moveTo(int newSp);

}

/**
 * A blind participant, is an object which does not draw anything, but it needs
 * to be updated.
 * 
 * @author Sergio
 * 
 */
interface IIndicatorBlindParticipant {

	/**
	 * Signals that there are many real time ticks in queue and that this
	 * participant should try to be as fast as possible.
	 * 
	 * <p>
	 * Multiple {@link #hurryUp()} calls are possibile, each one is like raising
	 * the priority of the alarm.
	 * 
	 * <p>
	 * The caller will eventually pair each {@link #hurryUp()} call with a
	 * corresponding {@link #calmDown()}.
	 * 
	 * @param hurryUpQuota
	 *            how much the participant should hurry up. The parameter is
	 *            positive and never zero. It has not a upper limit but usually
	 *            it is a small integer (say less than 10). Successive
	 *            {@link #hurryUp(int)} calls have increasing hurryUpQuotas.
	 */
	public void hurryUp(int hurryUpQuota);

	/**
	 * Signals that the queue is a little more crowded and that the participant
	 * can calm down.
	 * 
	 * @param calmDownQuota
	 *            how much the participant could calm down. When this parameter
	 *            is zero it means that the participant can go at full depth.
	 */
	public void calmDown(int calmDownQuota);

	/**
	 * called when we know the tick size (useful for some components)
	 * 
	 * @param tick
	 */
	public void begin(int tick);

	/**
	 * gets the new tick from the outside and updates its internal state.
	 * 
	 * @param qt
	 *            the new tick. The ticks should be sent in ascending orders
	 *            without gaps.
	 * 
	 */
	public void newTick(QueueTick qt);
}

/**
 * This is the base interface for all the indicator participants.
 * 
 * <p>
 * An indicator participant responds to basically two messages. A message to
 * update its state and to draw on a "big" channel indicator.
 * 
 * @author Sergio
 * 
 */
interface IIndicatorParticipant extends IIndicatorBlindParticipant {

	/**
	 * draws on the channel indicator.
	 * 
	 * @param aCh
	 *            a channel indicator
	 */
	public void drawOn(ChannelIndicator aCh);

	/**
	 * 
	 * Signals the end of warm up. An indicator has three main states regarding
	 * the warm up: the warm up itself (when usually it is naked and does not
	 * compute the indicator), the warm up (when the indicator is real but it
	 * could also remain mute) and then the normal real time phase.
	 * 
	 * <p>
	 * This method is called at the end of the real warm up, just before the
	 * starting of the real time
	 * 
	 */
	public void warmUpFinished();

}

interface IMaxDistanceGenerator extends IIndicatorBlindParticipant {

	/**
	 * returns the max distance of the indicator with respect to the central
	 * line
	 * 
	 * @return the max distance at the end of the indicator.
	 */
	public double getMaxDistance();

	/**
	 * moves the generator forward. This is the same method which is present int
	 * the {@linkplain IChannelHelper#moveTo(int)}, and the semantic is the
	 * same. The method will simply move the generator; for some generators the
	 * moving is a no-op, for others (mainly the convex hull) the advance could
	 * be potentially an expensive operation.
	 * 
	 */
	public void moveTo(int newSp);

}

/**
 * Simple helper class to get the indicator single for all the components.
 * 
 * <p>
 * All the indicators share a central line and some code to compute the top and
 * the bottom line.
 * <p>
 * In the simplest case the top/bottom lines are simply put at some ticks above
 * or below the central line. In the most complex case the indicator does take
 * all the prices using a convex hull which does not cross them
 * 
 * <p>
 * The indicator defines a central line which is composed of these points:
 * <b>(x1,y1)</b> and <b>(x2,y2)</b>. X1 is usually called the <i>start
 * point</i> and x2 is the current time.
 * 
 * <p>
 * Y1 and Y2 are the values of the indicator at the start and end of the period.
 * Usually Y1 does not vary as time passes, but it may, for example for a
 * regression indicator it usually does.
 * 
 * @author Sergio
 * 
 */
abstract class IndicatorComponent implements IIndicatorParticipant {

	@Override
	public void hurryUp(int hurryUpQuota) {
		// empty
	}

	@Override
	public void calmDown(int calmDownQuota) {
		// empty
	}

	/**
	 * if true the indicator will be disabled. This means that the indicator
	 * will silence itself and do not compute and do not draw itself. Usually
	 * this is done in warm up.
	 */
	protected boolean _indicatorDisabled;

	@Override
	public void warmUpFinished() {
		_indicatorDisabled = false;
	}

	protected BaseScaleIndicator fInd;

	protected IndicatorComponent(BaseScaleIndicator aInd) {
		fInd = aInd;
		_indicatorDisabled = aInd._compositeIndicator.bean
				.isNoIndicatorInWarmUp();
	}

}

class IndicatorException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3393590147765401249L;

}

/**
 * A indicator memory is simply a storage of past indicator values, a short term
 * memory which is cleared when the start point moves.
 * 
 * @author Sergio
 * 
 */
class IndicatorMemory implements Serializable, Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6143941893195787234L;

	private ArrayList<Double> fValues = new ArrayList<>();
	/** This is the starting memory time */
	private int fStartMemoryTime = -1;
	private final int fMaximumSize;

	/**
	 * creates an indicator memory with a maximum size. If the maxSize is not -1
	 * then the memory uses this as the maximum size (fixed). Otherwise it uses
	 * the start point movements.
	 * 
	 * @param maxSize
	 */
	public IndicatorMemory(int maxSize) {
		fMaximumSize = maxSize;
	}

	/**
	 * returns the size of this temporary memory.
	 * 
	 * @return the size of the temporary memory.
	 */
	public int size() {
		return fValues.size();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object clone() throws CloneNotSupportedException {
		IndicatorMemory cloned = (IndicatorMemory) super.clone();
		cloned.fValues = (ArrayList<Double>) fValues.clone();

		return cloned;
	}

	@SuppressWarnings("boxing")
	public double getVal(int time) {
		int diff = time - fStartMemoryTime;
		if (time < fStartMemoryTime || diff >= fValues.size()) {
			// debug_var(199103, "time req. " + time + " start " +
			// fStartMemoryTime + " size " + fValues.size(), " return nan");
			return Double.NaN;
			// throw new IllegalArgumentException();
		}
		return fValues.get(diff);
	}

	public int getWindow() {
		return fMaximumSize;
	}

	/**
	 * @param newStartPoint
	 */
	public void moveStartPoint(int newStartPoint) {
		if (fMaximumSize == -1) {
			// assert(false) : " to do move from " + fStartMemoryTime + " to " +
			// newStartPoint;
			int amountToDelete = newStartPoint - fStartMemoryTime;
			fValues.subList(0, amountToDelete).clear();
			fStartMemoryTime = newStartPoint;
		}
	}

	/**
	 * replaces the last value in the list.
	 * 
	 * @param aVal
	 */
	@SuppressWarnings("boxing")
	public void replaceLast(double aVal) {
		fValues.set(fValues.size() - 1, aVal);
	}

	@SuppressWarnings("boxing")
	public void store(int time, double val) {
		if (fStartMemoryTime < 0) {
			assert (fValues.size() == 0);
			fStartMemoryTime = time;
		}
		// If this assert fails I am not going time by time.
		assert (time - fStartMemoryTime == fValues.size());
		fValues.add(val);

		if (fMaximumSize != -1 && fValues.size() > (fMaximumSize + 1001)) {
			fValues.subList(0, 1000).clear();
			fStartMemoryTime += 1000;
			// debug_var(299101, "Resized the storage to new size ",
			// fValues.size(), " start memory is ", fStartMemoryTime);
		}
	}
}

/**
 * this is the generic class which defines a single scale indicator (generic)
 * 
 * @author Sergio
 * 
 */
class IndicatorSingle extends BaseScaleIndicator {

	@Override
	public void onStopping() {
		if (!_IS_MULTITHREAD)
			return;

		_queueThread.interrupt();
		try {
			_queueThread.join();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	AtomicBoolean _readyTick = new AtomicBoolean();
	QueueTick _queuedQt;

	@Override
	public void queueNewTick(QueueTick qt) {

		if (_queuedQt != null) {
			throw new IllegalStateException("qt is not null" + _queuedQt
					+ " I want to insert " + qt);
		}

		_queuedQt = qt;

		synchronized (_readyTick) {
			_readyTick.set(true);
			_readyTick.notify();
		}
	}

	@Override
	public void hurryUp(int hurryUpQuota) {
		fIndicator.hurryUp(hurryUpQuota);
	}

	@Override
	public void calmDown(int calmDownQuota) {
		fIndicator.calmDown(calmDownQuota);
	}

	protected ChannelLengthManager fChanLengthManager;

	@Override
	public final void warmUpFinished() {
		super.warmUpFinished();
		/*
		 * In this default implementation only the indicator has a meaningful
		 * light update state.
		 */
		fIndicator.warmUpFinished();
	}

	BasePositiveChannelGenerator fPositiveGenerator;

	private Thread _queueThread;
	private final boolean _IS_MULTITHREAD;

	private static final int MIN_PIVOTS = 5;

	private static final String LENGHT_OF_PRE_COMPUTED_WINDOW_KEY = "PRE_COMPUTED_WINDOW_LAYER_";

	@SuppressWarnings("boxing")
	public IndicatorSingle(ChannelIndicator aIndicator, int aLevel,
			BaseScaleIndicator prev, boolean isMultiThread) {
		super(aIndicator, aLevel, prev);
		assert (aLevel > 0);

		_IS_MULTITHREAD = isMultiThread;

		if (aIndicator.bean.getProperties().getBooleanDef(
				MultiscaleIndicator.PRE_COMPUTED_WINDOWS_KEY, false) == true) {
			U.debug_var(918385,
					"Using the pre computed windows to create the indicator");
			String key = LENGHT_OF_PRE_COMPUTED_WINDOW_KEY + aIndicator.fLayer
					+ "_LEV_" + aLevel;
			int def = -1;
			int window = aIndicator.bean.getProperties().getIntDef(key, def);
			if (window == def) {
				// throw new IllegalArgumentException(
				// "not existent value for key " + key);
				U.debug_var(295820, "not existing value for key " + key
						+ " defaulting to the normal indicator");
				_createNormalIndicator(aIndicator, aLevel, null, null);
			} else {
				debug_var(
						391032,
						"Scale ",
						aLevel,
						" creating indicator component with pre computed window ",
						window);

				_createIndicatorWithWindow(aIndicator.bean, window);
			}

		} else if (aIndicator.fNaked) {
			fIndicator = new NakedIndicator();
			fPositiveGenerator = new NullPositiveChannelGenerator(this);
		} else {
			_createNormalIndicator(aIndicator, aLevel, null, null);
		}

		// This must be the last to be created because it needs the indicator
		// (in the rules).
		fPivotsGenerator = new PivotsGenerator(this);

		if (_IS_MULTITHREAD)
			_createQueueThread();
	}

	/**
	 * Creates the queue thread which will
	 */
	private void _createQueueThread() {

		if (_queueThread != null) {
			return;
		}
		_queueThread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						synchronized (_readyTick) {
							if (_readyTick.compareAndSet(true, false)) {
								// U.debug_var(838295, "lev ", level,
								// " queuing ",
								// _queuedQt);
								newTick(_queuedQt);
							}
							_readyTick.wait();
						}
						if (Thread.currentThread().isInterrupted()) {
							break;
						}

					} catch (InterruptedException e) {
						// nothing I exit normally.
						break;
					}
				}
			}
		});

		_queueThread.setName("queueThread for level " + this.level);
		U.debug_var(839952, "Create queued thread for level " + this.level
				+ " my bit " + MY_BIT + " ALL BITS " + ALL_LOWER_BITS);
		_queueThread.start();
	}

	/**
	 * @param backup
	 *            the current naked indicator which is used to build the custom
	 *            indicator with the good statistics.
	 * @param isMultithread
	 *            true if the indicator single should create the multithreaded
	 *            queue.
	 */
	public IndicatorSingle(MultiscaleIndicator aIndicator, int aLevel,
			BaseScaleIndicator prev, BaseScaleIndicator[] backup,
			double[] computedWindows, boolean isMultithread) {
		super(aIndicator, aLevel, prev);
		assert (aLevel > 0);
		assert (backup != null && !aIndicator.fNaked);
		debug_var(119391, "Created the indicator in the second pass!");

		_createNormalIndicator(aIndicator, aLevel, backup, computedWindows);

		// This must be the last to be created because it needs the indicator
		// (in the rules).
		fPivotsGenerator = new PivotsGenerator(this);

		_IS_MULTITHREAD = isMultithread;
		if (isMultithread) {
			_createQueueThread();
		}
	}

	/**
	 * Creates the indicator. This function is part of the constructor. It
	 * creates the indicator (and the positive channel generator). If backup is
	 * not null then it means that it does not create a naked indicator, but a
	 * normal one.
	 * 
	 * <p>
	 * if backup is null it means that the indicator is not naked and it is
	 * built from scratch with the the normal window computed with the initial
	 * window and the multiplier.
	 * 
	 * @param aIndicator
	 * @param aLevel
	 * @param backup
	 *            the array which contains all the naked indicators used to
	 *            prepare the statistics.
	 */
	@SuppressWarnings("boxing")
	private void _createNormalIndicator(ChannelIndicator aIndicator,
			int aLevel, BaseScaleIndicator[] backup, double[] computedWindows) {

		int window, multiplier;

		window = aIndicator.bean.getFixWindow_startWindow();
		multiplier = aIndicator.bean.getFixWindow_windowMultiplier();

		multiplier = (int) Math.pow(multiplier, aLevel - 1);
		window *= multiplier;

		if (backup != null) {
			// I have to know the statistics...
			IndicatorSingle curNaked = (IndicatorSingle) (backup[aLevel]);
			assert (curNaked != null);
			int numPivots = curNaked.fPivotsGenerator.fNumPivots;
			int numTicks = aIndicator.lastTick.getFakeTime() + 1;
			if (numPivots < MIN_PIVOTS) {
				debug_var(391015, numPivots, " pivots for scale ", aLevel,
						" I estimate the window");
				if (aLevel < 2) {
					window = aIndicator.bean.getFixWindow_startWindow();
					debug_var(
							939931,
							"Because I do not have 2 lower scales I fallback to ",
							window);
				} else {
					BaseScaleIndicator prevNaked = (backup[aLevel - 1]);
					BaseScaleIndicator prevPrevNaked = (backup[aLevel - 2]);
					int prevPivots = prevNaked.fPivotsGenerator.fNumPivots;
					int prevPrevPivots = prevPrevNaked.fPivotsGenerator.fNumPivots;

					if (prevPivots >= MIN_PIVOTS
							&& prevPrevPivots >= MIN_PIVOTS) {
						double ratio = prevPrevPivots / (double) prevPivots;
						assert (ratio > 1.0); // if this fails than the
												// indicator is flawed, the
												// lower scale has less pivots!

						// I compute the lower window
						double avgPrevSwingLength = numTicks
								/ (double) prevPivots;
						double avgCurSwingLength = avgPrevSwingLength * ratio; // this
																				// is
																				// an
																				// estimate
						debug_var(139193, "Prev pivots ", prevPivots,
								" prev prev pivot ", prevPrevPivots, " ratio ",
								ratio, " I will estimate w = ",
								avgCurSwingLength);
						window = (int) avgCurSwingLength;
					} else {
						// there are no sufficient pivots in the lower scales,
						// but I am at scale 2 or upper, so
						// I try to know the ratio of the window
						debug_var(391901, "lev ", aLevel,
								" no pivots at lower scale I take the ratio of the windows instead");
						double prevWindow = computedWindows[aLevel - 1];
						double prevPrevWindow = computedWindows[aLevel - 2];
						double ratio = prevWindow / prevPrevWindow;
						window = (int) (ratio * prevWindow);
						debug_var(729234, " pre Wind ", prevWindow,
								" prepreW ", prevPrevWindow, " ratio ", ratio,
								" new window will be: ", window, " wide.");
					}
				}
			} else {
				double avgSwingLength = (double) numTicks / (double) numPivots;
				debug_var(291934, "I have ", numPivots, " pivots in ",
						numTicks, " ticks avg: ", avgSwingLength);
				window = (int) avgSwingLength;
			}

		}

		if (backup != null) {
			computedWindows[aLevel] = window;
			if (aLevel < 2) {
				computedWindows[0] = window
						/ (double) aIndicator.bean
								.getFixWindow_windowMultiplier();
			}
		}

		// real creation of the indicator.
		debug_var(391032, "Scale ", aLevel,
				" creating indicator component with window ", window,
				" multiplier ", multiplier);

		/*
		 * store of the computed values to the properties, this step is only
		 * needed because in this way the properties are updated with the
		 * computed values and then can be stored to be retrieved later.
		 */
		String key = LENGHT_OF_PRE_COMPUTED_WINDOW_KEY + aIndicator.fLayer
				+ "_LEV_" + aLevel;
		aIndicator.bean.getProperties()
				.put(key, new Integer(window).toString());

		_createIndicatorWithWindow(aIndicator.bean, window);

	}

	/**
	 * The real creation of the indicator.
	 * 
	 * <p>
	 * The real creation happens after we have decided the window to use.
	 * 
	 * 
	 * @param aBean
	 *            the bean which collects all the parameters for the indicator.
	 * @param windowPar
	 *            the window to use.
	 */
	@SuppressWarnings("deprecation")
	private void _createIndicatorWithWindow(IndicatorParamBean aBean,
			int windowPar) {

		int window = windowPar;

		StartPointLength spa_length = aBean.getIndicator_StartPointLength();
		boolean isStartPointFixed = true;
		if (spa_length == StartPointLength.FIX_WINDOW) {
			isStartPointFixed = false;
		}

		int maxWindowLength = aBean.getFixWindow_maximumWindowLength();

		CenterLineAlgo algo = aBean.getIndicator_centerLineAlgo();

		// if negative the window is in overflow.
		if (window > maxWindowLength || window < 0) {
			warn("cannot create an indicator with window " + window
					+ " forcing it to " + maxWindowLength);
			window = maxWindowLength;
		}

		if (isStartPointFixed) {
			fPositiveGenerator = new PositiveChannelsGenerator(this);
		} else {
			fPositiveGenerator = new NullPositiveChannelGenerator(this);
		}

		if (algo == CenterLineAlgo.LR_P2) {
			if (isStartPointFixed) {
				fIndicator = new MixedPolyLinearFixedChannel(this, window, 2);
			} else {
				throw new UnsupportedOperationException();
			}

		} else if (algo == CenterLineAlgo.MOVING_AVERAGE) {

			if (isStartPointFixed) {
				fIndicator = new FixedMovingAverage(this, window);
			} else {
				fIndicator = new MovingAvgIndicatorComponent(this, window);
			}

		} else if (algo == CenterLineAlgo.LINEAR_REGRESSION) {
			if (isStartPointFixed) {
				fIndicator = new LinearRegressionComponent(this, window);
			} else {
				fIndicator = new MovingLinearRegressionComponent(this, window);
			}
		} else {
			// polynomial fit...
			int aDegree;

			switch (algo) {
			case LINEAR_REGRESSION:
			case MOVING_AVERAGE:
			case POLYNOMIAL_FIT:
			case LR_P2:
			default:
				throw new IllegalStateException();
			case POLYLINES_2:
				aDegree = 2;
				break;
			case POLYLINES_3:
				aDegree = 3;
				break;
			case POLYLINES_4:
				aDegree = 4;
				break;
			}

			if (isStartPointFixed) {
				fIndicator = new FixedPolyIndicatorComponent(this, window,
						aDegree);
			} else {
				fIndicator = new MovingPolyIndicatorComponent(this, window,
						aDegree);
			}
		}

		/*
		 * If I am smoothing, then I create the smoothing version of the
		 * indicator, probably it is not more used, but in any case it is
		 * conceptually a wrapper for the indicator.
		 */
		boolean smoothing = aBean.isSmoothing();

		if (smoothing) {
			fIndicator = new SmoothingChannel(this, fIndicator);
		}

	}

	@Override
	public void begin(int tick) {

		// super.begin(tick);

		StartPointLength spa_length = this._compositeIndicator.bean
				.getIndicator_StartPointLength();

		switch (spa_length) {
		case FIX_WINDOW:
			fChanLengthManager = new WindowChannelLengthManager(this);
			break;
		case FIX_WINDOW_PIVOT:
			fChanLengthManager = new MixedPivotWindowLengthManager(this);
			break;
		case FIX_WINDOW_SPA:
			throw new UnsupportedOperationException();
			// break;
		case PIVOT:
			fChanLengthManager = new PivotChannelLengthManager(this);
			break;
		case SPA:
			fChanLengthManager = new SPAChannelLengthGenerator(this);
			break;
		default:
			break;

		}

		fIndicator.begin(tick);
		fPivotsGenerator.begin(tick);

	}

	@Override
	public void drawOn(ChannelIndicator numericThIndicator) {
		fIndicator.drawOn(numericThIndicator);
		fPivotsGenerator.drawOn(numericThIndicator);
	}

	@Override
	public int getStartPoint() {
		return fIndicator.getX1();
	}

	@Override
	public boolean isChannelCoherent() {

		if (fIndicator.supportsSlope()) {
			return (fPivotsGenerator.fState == EState.ZIG_STATE && fIndicator
					.getSlope() > 0)
					|| (fPivotsGenerator.fState == EState.ZAG_STATE && fIndicator
							.getSlope() < 0);
		}
		return true;
	}

	@Override
	public void newTick(QueueTick qt) {
		/*
		 * The indicator updates first, because it may trigger some changes
		 * (touches, etc). The final appearance of the indicator will be after.
		 */
		fIndicator.newTick(qt);

		// U.debug_var(283845, "lev ", level, " indicator done @ ",
		// qt.getFakeTime(), " now I wait for the lower scales...");

		// /////////////////////////////////////////////
		// //// multi thread code
		while (_IS_MULTITHREAD) {
			try {
				synchronized (_compositeIndicator._scaleCounter) {
					long val = _compositeIndicator._scaleCounter.get();

					/*
					 * The equality is because I wait for ALL the scales before
					 * this one, and ALL the upper scales are blocked, so the
					 * highest pattern is 11...1100000 where the first zero is
					 * my bit
					 */

					if (val == ALL_LOWER_BITS) {
						// U.debug_var(239423, "lev ", level,
						// " I have finished counter ", val);
						break;
					}
					_compositeIndicator._scaleCounter.wait();
				}

			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}

		}
		// end of multi thread code.
		// ////////////////////////////////////////////////

		fPivotsGenerator.newTick(qt);

		// I must have at least one pivot to create a new positive channel.
		if (!fPivotsGenerator.isThereANewPivot()) {
			// I cannot create a new positive if there has been a negative.

			fPositiveGenerator.newTick(qt);
			if (fPositiveGenerator.isCreatedNewChannel()) {
				// If the channel has been created then I have
				// simply to reapply the rules.
				fPivotsGenerator.reapplyRules(qt);
			}
		} else {
			// I have created a new pivot, so I try to make the
			// channel shorter for a negative pivot, I do not care
			// about the result of this, it could also be false, because
			// in any case a new channel will be created.
			fChanLengthManager.tryMakeChannelShorter(true);
		}

		// ////////////////////////////////////////////////////
		// I have finished my computation, so I set my bit.
		if (_IS_MULTITHREAD)
			synchronized (_compositeIndicator._scaleCounter) {
				// int time = _queuedQt.getFakeTime();
				_queuedQt = null;
				long newVal = _compositeIndicator._scaleCounter.get() | MY_BIT;
				_compositeIndicator._scaleCounter.set(newVal);

				_compositeIndicator._scaleCounter.notifyAll();
				// U.debug_var(292895, "lev ", level,
				// " finished my computation for ",
				// time, " setting counter to ", newVal);
			}
		// ////////////////////////////////////////////////////
	}

	@Override
	public void shortenChannel(int newSp) {
		fIndicator.moveTo(newSp);
	}

}

class IndicatorSingleZeroScale extends BaseScaleIndicator {

	private final boolean _IS_MULTITHREAD;

	/**
	 * By definition the zero scale has the first bit, so one.
	 */
	// private static final long MY_BIT = 1;

	// private NakedIndicator _channel;

	protected IndicatorSingleZeroScale(ChannelIndicator aIndicator,
			BaseScaleIndicator prev, boolean isMultithread) {
		super(aIndicator, 0, prev);
		// the indicator at zero scale has only zero scale pivots, no indicator,
		// no channels
		fPivotsGenerator = new ZeroScalePivotsGenerator(this);

		fIndicator = new NakedIndicator();

		_IS_MULTITHREAD = isMultithread;
	}

	@Override
	public void drawOn(ChannelIndicator aCh) {
		// nothing here
	}

	@Override
	public int getStartPoint() {
		return Integer.MAX_VALUE;
	}

	@Override
	public void newTick(QueueTick qt) {
		fPivotsGenerator.newTick(qt);

		// ////////////////////////////////////////////////////
		// I have finished my computation, so I set my bit.
		if (_IS_MULTITHREAD)
			synchronized (_compositeIndicator._scaleCounter) {
				_compositeIndicator._scaleCounter
						.set(_compositeIndicator._scaleCounter.get() | MY_BIT);

				// U.debug_var(837823, "level zero setting the value to ",
				// _compositeIndicator._scaleCounter.get());
				_compositeIndicator._scaleCounter.notifyAll();
			}
		// ////////////////////////////////////////////////////
	}

	@Override
	public void begin(int tick) {
		// nothing
	}

	@Override
	public void queueNewTick(QueueTick qt) {
		/*
		 * In this case I simply pass the message without threading it
		 */
		newTick(qt);

	}

}

class LinearRegressionComponent extends FixedStartPointChannel {

	private CloneableRegression fReg = new CloneableRegression();

	protected LinearRegressionComponent(IndicatorSingle indicatorSingle,
			int minimumWindow) {
		super(indicatorSingle, minimumWindow);
		fCenterY1isComputed = true;
	}

	@Override
	protected void _clearAll() {
		fReg.clear();
	}

	@Override
	protected void _remove_one_tick(QueueTick queueTick) {
		fReg.removeTick(queueTick);
	}

	@Override
	public void begin(int tick) {
		super.begin(tick);
	}

	@Override
	protected double getRealCenterY1() {
		double realCY1 = fReg.predict(fX1);
		return realCY1;
	}

	@Override
	protected double getRealCenterY2() {
		double predX2 = fReg.predict(fX2);
		return predX2;
	}

	@Override
	public double getSlope() {
		return fReg.getSlope();
	}

	@Override
	protected void indicator_new_tick(QueueTick qt) {
		assert (fReg.getN() <= qt.getFakeTime()) : " n " + fReg.getN() + " qt "
				+ qt.getFakeTime();
		fReg.addTick(qt);
	}

	@Override
	protected int _getN() {
		return (int) fReg.getN();
	}

}

class MovingAvgIndicatorComponent extends MovingChannel {

	private MovingAverage fAvg;

	protected MovingAvgIndicatorComponent(IndicatorSingle aInd, int window) {
		super(aInd, window);
		fAvg = new MovingAverage(window);
	}

	@Override
	protected int _getN() {
		return fAvg.getWindow();
	}

	@Override
	protected void _moving_channel_add_tick(QueueTick qt) {
		fAvg.addValue(qt.getPrice());
	}

	@Override
	protected void _moving_channel_remove_tick(QueueTick qt) {
		// nothing
	}

	@Override
	public void begin(int tick) {
		super.begin(tick);
	}

	@Override
	protected double getRealCenterY2() {
		if (fX2 < fAvg.getWindow()) {
			return Double.NaN;
		}
		return fAvg.getAvg();
	}

}

/**
 * The base class for all the channels which have a moving window of fixed size
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
abstract class MovingChannel extends BaseChannel {

	@Override
	public int getMinimumWindow() {
		/*
		 * for the moving channel the minimum window is equal to the maximum
		 * one. This is a real fixed window channel. only the
		 * FixedStartPointChannel is the base class for the variable width
		 * channels.
		 */
		return fStorage.getWindow();
	}

	@Override
	public double getSlope() {
		if (Double.isNaN(getCenterY1())) {
			return Double.NaN;
		}
		return (getCenterY2() - getCenterY1()) / (getX2() - getX1());
	}

	protected MovingChannel(BaseScaleIndicator aInd, int window) {
		super(aInd);
		fX1 = -window;
		fStorage = new IndicatorMemory(window);
	}

	// protected abstract int _getN();

	@Override
	protected final void _moveToBaseChannelImpl(int newSp) {
		// nothing here
	}

	protected abstract void _moving_channel_add_tick(QueueTick qt);

	protected abstract void _moving_channel_remove_tick(QueueTick qt);

	@Override
	protected final void indicator_new_tick(QueueTick qt) {
		_moving_channel_add_tick(qt);

		int index = fX1;
		fX1++; // the start point is variable!
		if (index < 0) {
			return;
		}

		QueueTick remQt = new QueueTick();
		com.mfg.inputdb.prices.mdb.PriceMDB.Record rec = _priceList.get(index);
		rec.copyTo(remQt);

		if (remQt.getFakeTime() != (fX1 - 1)) {
			assert (false);
		}
		// assert (remQt.getFakeTime() == (fX1 - 1)) : "remQT "
		// + remQt.getFakeTime() + " fx1 " + fX1;

		_moving_channel_remove_tick(remQt);

		// U.debug_var(283492, "removing ", remQt);
		/*
		 * If you want to remove a point to a convex hull from the left you have
		 * to move it to the next point, this is the reason of the +1
		 */
		_maxDistanceGenerator.moveTo(remQt.getFakeTime() + 1);

		assert (_getN() == fStorage.getWindow());

	}

}

class MovingLinearRegressionComponent extends MovingChannel {

	private CloneableRegression fReg = new CloneableRegression();

	protected MovingLinearRegressionComponent(BaseScaleIndicator aInd,
			int window) {
		super(aInd, window);
		fCenterY1isComputed = true;
	}

	@Override
	protected int _getN() {
		return (int) fReg.getN();
	}

	@Override
	protected void _moving_channel_add_tick(QueueTick qt) {
		fReg.addTick(qt);
	}

	@Override
	protected void _moving_channel_remove_tick(QueueTick qt) {
		fReg.removeTick(qt);
	}

	@Override
	protected double getRealCenterY1() {
		double realCY1 = fReg.predict(fX1);
		// debug_var(919415, "lev ", fInd.level, " real Y1Cent ", realCY1);
		return realCY1;
	}

	@Override
	protected double getRealCenterY2() {
		if (fX2 < fStorage.getWindow()) {
			return Double.NaN;
		}
		return fReg.predict(fX2);
	}

	@Override
	public double getSlope() {
		return fReg.getSlope();
	}

}

/**
 * A null indicator, used when the {@linkplain IndicatorSingle} it is used in
 * the naked way, just to make a statistics on the pivots.
 * 
 * @author Sergio
 * 
 */
class NakedIndicator implements IChannelHelper {

	@Override
	public void begin(int tick) {
		//
	}

	@Override
	public IChannelHelper clone() {
		return this; // the cloned version of myself is the same object
	}

	@Override
	public void drawOn(ChannelIndicator aCh) {
		// nothing
	}

	@Override
	public double getBottomY2() {
		return 0;
	}

	@Override
	public double getCenterY1() {
		return 0;
	}

	@Override
	public double getCenterY2() {
		return 0;
	}

	@Override
	public double getRawBottomY2() {
		return 0;
	}

	@Override
	public double getRawCenterY2() {
		return 0;
	}

	@Override
	public double getRawTopY2() {
		return 0;
	}

	@Override
	public int getRcTouches() {
		return 0;
	}

	@Override
	public int getScTouches() {
		return 0;
	}

	@Override
	public double getSlope() {
		return 0;
	}

	@Override
	public double getTopY2() {
		return 0;
	}

	@Override
	public int getX1() {
		return 0; // the start point is always zero, this makes me save all the
					// saved ticks!
	}

	@Override
	public int getX2() {
		return 0;
	}

	@Override
	public boolean isCenterLineInHalf() {
		return false;
	}

	@Override
	public boolean isThereANewRc() {
		return false;
	}

	@Override
	public boolean isThereANewSc() {
		return false;
	}

	@Override
	public void moveTo(int newSp) {
		// nothing
	}

	@Override
	public void newTick(QueueTick qt) {
		// fLastQt = qt;
	}

	@Override
	public double getTopDistance() {
		return 0;
	}

	@Override
	public double getBottomDistance() {
		return 0;
	}

	@Override
	public double[] getChannelCoefficients() {
		return null;
	}

	@Override
	public void warmUpFinished() {
		// nothing here.
	}

	@Override
	public boolean supportsSlope() {
		return false;
	}

	@Override
	public int getMinimumWindow() {
		return 0;
	}

	@Override
	public double eval(double x) {
		return 0;
	}

	@Override
	public void hurryUp(int hurryUpQuota) {
		// empty
	}

	@Override
	public void calmDown(int calmDownQuota) {
		// empty
	}

}

/**
 * The negative on flat rule is a state machine, because it needs an exact
 * series of event to be fired.
 * 
 * <p>
 * This means that the rule needs an access to the other indicator to get the
 * slope of the channel and then it needs the 2ls pivots to know if they are
 * broken or not.
 * 
 * @author Sergio
 * 
 */
class NegativeOnFlatRule extends PivotGeneratorRule {

	private enum State {
		IDLE, CHANGED_SLOPE
	}

	private State fState = State.IDLE;
	private double fOldSlope;

	private IChannelHelper fInd;
	private boolean fWasZig;

	NegativeOnFlatRule(PivotsGenerator aGen, IChannelHelper aInd) {
		super(aGen);
		fInd = aInd;
	}

	@Override
	public void begin(int tick) {
		//
	}

	private boolean breaksPivots2ls() {

		ArrayList<Pivot> _2ls = this.fGenerator.fInd.prevInd.prevInd.fPivotsGenerator.fPivots;

		ListIterator<Pivot> it = _2ls.listIterator(_2ls.size());
		int brokenPivots = 0;
		long priceOfLastPivot = -1;

		// boolean up = fCurChannel.getSlope() > 0;
		boolean up = fGenerator.fState == EState.ZAG_STATE;

		long lastPivotTime = -1;
		if (fGenerator.fPivots.size() != 0) {
			lastPivotTime = fGenerator.fPivots
					.get(fGenerator.fPivots.size() - 1).fPivotTime;
		}

		while (it.hasPrevious()) {
			Pivot testPivot = it.previous();

			if (testPivot.fPivotTime < lastPivotTime) {
				// debug_var(819194, "The test pivot time ",
				// testPivot.fPivotTime,
				// " is before the last pivot ",
				// lastPivotTime, " exiting (sp) ",
				// this.fGenerator.getLastChoosenStartPoint());
				break;
			}

			if (up ^ testPivot.isStartingDownSwing()) {
				// debug_var(391351, "The pivot ", testPivot,
				// " is opposite, up? ",
				// testPivot.isStartingDownSwing(), "skipping");
				continue;
			}

			// debug_var(291906, "Testing ", testPivot, " isUP? ",
			// testPivot.isStartingDownSwing(), " polp ",
			// priceOfLastPivot);

			if (priceOfLastPivot == testPivot.getPivotPrice()) {
				continue;
			}

			if (isBreakingThePivot(testPivot,
					this.fGenerator.fInd._compositeIndicator.lastTick)) {
				// debug_var(272015, "lev ", fGenerator.fInd.level + 1,
				// " broken the pivot ", testPivot);
				brokenPivots++;
			}

			if (brokenPivots >= 2) {
				break;
			}
			priceOfLastPivot = testPivot.getPivotPrice();
		}

		if (brokenPivots >= 2) {
			// a change in sign! I can create a new negative channel
			// debug_var(327525,
			// "Created a new negative on flat on scale ",
			// fGenerator.fInd.level + 1, " on time ",
			// this.fGenerator.fInd._compositeIndicator.lastTick.getFakeTime());
			return true;
		}

		return false;
	}

	private boolean isBreakingThePivot(Pivot pivot, QueueTick qt) {
		if ((fGenerator.fState == EState.ZIG_STATE && (pivot.getPivotPrice() > qt
				.getPrice()))
				|| (fGenerator.fState == EState.ZAG_STATE && (pivot
						.getPivotPrice() < qt.getPrice()))) {
			return true;
		}
		return false;
	}

	@Override
	public boolean reapply(QueueTick qt) {
		return tryToFire(qt);
	}

	@Override
	protected boolean tryToFire(QueueTick qt) {
		double curSlope = fInd.getSlope();
		switch (fState) {
		case IDLE:

			if (curSlope == Double.NaN || fOldSlope == Double.NaN) {
				return false;
			}

			if ((fGenerator.fState == EState.ZIG_STATE && fOldSlope > 0 && curSlope < 0)
					|| (fGenerator.fState == EState.ZAG_STATE && fOldSlope < 0 && curSlope > 0)) {
				fState = State.CHANGED_SLOPE;
				// debug_var(881815, "lev ", fGenerator.fInd.level,
				// " changed slope @ ", qt);
				fWasZig = fGenerator.fState == EState.ZIG_STATE;
				return false;
			}

			fOldSlope = curSlope;
			break;
		case CHANGED_SLOPE:

			// curSlope = fInd.getSlope();
			if ((curSlope > 0 && fGenerator.fState == EState.ZIG_STATE)
					|| (curSlope < 0 && fGenerator.fState == EState.ZAG_STATE)) {
				// debug_var(881885, "kev ", fGenerator.fInd.level,
				// " channel rechanged slope ", qt);
				fState = State.IDLE;
			}

			if ((fWasZig && fGenerator.fState == EState.ZAG_STATE)
					|| (!fWasZig && fGenerator.fState == EState.ZIG_STATE)) {
				// debug_var(881515, "lev ", fGenerator.fInd.level,
				// " re-changed slope @ ", qt);
				fState = State.IDLE;
			}

			if (breaksPivots2ls()) {
				fState = State.IDLE;
				return true;
			}

			break;

		default:
			break;
		}
		return false;
	}

}

/**
 * A positive channel generator which does not create positive channels, ever.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
class NullPositiveChannelGenerator extends BasePositiveChannelGenerator {

	protected NullPositiveChannelGenerator(BaseScaleIndicator aInd) {
		super(aInd);
		//
	}

	@Override
	public void begin(int tick) {
		//
	}

	@Override
	public void newTick(QueueTick qt) {
		//
	}

}

class NumericZigZagRule extends PivotGeneratorRule {

	@Override
	public int getConfirmThreshold(QueueTick qt) {
		switch (fGenerator.fState) {
		case BEFORE_FIRST_PRICE:
			return Integer.MAX_VALUE;
		case FIRST_PRICE:
			return Integer.MAX_VALUE;
		case UNDECIDED:
			return Integer.MAX_VALUE;
		case ZAG_STATE:
			return qt.getPrice() + fThreshold;
		case ZIG_STATE:
			return qt.getPrice() - fThreshold;
		default:
			return Integer.MAX_VALUE;
		}
	}

	private int fThreshold;

	public NumericZigZagRule(PivotsGenerator pivotsGenerator) {
		super(pivotsGenerator);
		IndicatorParamBean ipb = pivotsGenerator.fInd._compositeIndicator
				.getParamBean();
		int multiplier = ipb.getNegativeOnPriceMultiplier_priceMultiplier();
		int startMultiplier = ipb
				.getNegativeOnPriceMultiplier_startTicksNumbers();

		multiplier = (int) Math.pow(multiplier, pivotsGenerator.fInd.level - 1);
		fThreshold = startMultiplier * multiplier;
	}

	@SuppressWarnings("boxing")
	@Override
	public void begin(int tick) {
		fThreshold *= tick;
		debug_var(199133, "numeric zig zag rule initialized with threshold ",
				fThreshold);
	}

	@Override
	protected boolean tryToFire(QueueTick qt) {

		switch (fGenerator.fState) {
		case UNDECIDED:

			if (qt.getPrice() <= fGenerator.fHH.getPrice() - fThreshold) {
				// Ok, I have started a zag
				fGenerator.fTentativePivot = fGenerator.fHH;
				return true;
			} else if (qt.getPrice() >= fGenerator.fLL.getPrice() + fThreshold) {
				// I have started a zig, ll is the pivot
				fGenerator.fTentativePivot = fGenerator.fLL;
				return true;
			}

			break;
		case ZIG_STATE:
			if (qt.getPrice() <= fGenerator.fTentativePivot.getPrice()
					- fThreshold) {
				return true;
			}
			break;
		case ZAG_STATE:
			if (qt.getPrice() >= fGenerator.fTentativePivot.getPrice()
					+ fThreshold) {
				return true;
			}
			break;
		case BEFORE_FIRST_PRICE:
		case FIRST_PRICE:
		default:
			assert (false);
			break;
		}

		return false;

	}

}

class PercentageMaxDistanceGenerator implements IMaxDistanceGenerator {

	private double fPercentage;
	private double fCurDist;

	@SuppressWarnings("boxing")
	public PercentageMaxDistanceGenerator(BaseScaleIndicator aInd) {
		fPercentage = aInd._compositeIndicator.bean
				.getFixWindow_deltaTopBottomPerc();

		int multiplier = aInd._compositeIndicator.bean
				.getFixWindow_deltaTopBottomMultiplier();
		multiplier = (int) Math.pow(multiplier, aInd.level - 1);
		fPercentage *= multiplier;
		// fPercentage += 1.0;
		debug_var(299521, "Created a max distance generator percentage ",
				fPercentage);
	}

	@Override
	public void begin(int tick) {
		// Nothing

	}

	@Override
	public IMaxDistanceGenerator clone() {
		try {
			return (IMaxDistanceGenerator) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public double getMaxDistance() {
		return fCurDist;
	}

	@Override
	public void moveTo(int newSp) {
		// here it is void.
	}

	@Override
	public void newTick(QueueTick qt) {
		fCurDist = fPercentage * qt.getPrice();
	}

	@Override
	public void hurryUp(int hurryUpQuota) {
		// nothing

	}

	@Override
	public void calmDown(int calmDownQuota) {
		// nothing

	}

}

class PivotBreakoutRule extends PivotGeneratorRule {

	private BasePivotsGenerator fPrevGenerator;

	PivotBreakoutRule(PivotsGenerator aGen, BasePivotsGenerator fPivotsGenerator) {
		super(aGen);
		fPrevGenerator = fPivotsGenerator;

	}

	@Override
	public void begin(int tick) {
		// nothing
	}

	@Override
	protected boolean tryToFire(QueueTick qt) {
		Pivot last2Pivot = fPrevGenerator.getPastPivot(1);
		if (last2Pivot == null) {
			return false;
		}

		switch (fGenerator.fState) {
		case BEFORE_FIRST_PRICE:
		case FIRST_PRICE:
			assert (false); // cannot happen!
			break;
		case UNDECIDED:
			switch (fPrevGenerator.fState) {
			case ZIG_STATE:
				// Ok, the previous is zig, do I break this with a down
				// movement?
				if (qt.getPrice() > last2Pivot.getPrice()) {
					fGenerator.fTentativePivot = fGenerator.fLL;
					return true;
				}
				break;
			case ZAG_STATE:
				if (qt.getPrice() < last2Pivot.getPrice()) {
					fGenerator.fTentativePivot = fGenerator.fHH;
					return true;
				}
				break;
			case UNDECIDED:
			case BEFORE_FIRST_PRICE:
			case FIRST_PRICE:
				// should not happen, because last pivot is not null
				assert (false);
				break;
			}
			break;
		case ZAG_STATE:
			if (fPrevGenerator.fState == EState.ZIG_STATE) {
				if (qt.getPrice() > last2Pivot.getPivotPrice()) {
					return true;
				}
			}
			break;
		case ZIG_STATE:
			if (fPrevGenerator.fState == EState.ZAG_STATE) {
				if (qt.getPrice() < last2Pivot.getPivotPrice()) {
					return true;
				}
			}
			break;
		}

		return false;
	}

}

class PivotChannelLengthManager extends ChannelLengthManager {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5032452703528680922L;

	PivotChannelLengthManager(BaseScaleIndicator aInd) {
		super(aInd);
	}

	@Override
	public boolean tryMakeChannelShorter(boolean forNegative) {
		if (!forNegative) {
			return false; // I cannot make the channel shorter if there is not a
							// new pivot
		}

		// In this case I always succeed
		Pivot pv = this.fInd.getLastPivot(0);
		assert (pv.fPivotTime >= fInd.getStartPoint());

		fInd.shortenChannel(pv.fPivotTime);

		return true;
	}

}

/**
 * a pivot generator rule is simply a rule which knows how to create a pivot.
 * 
 * <p>
 * A Pivot is the starting of a swing of opposite sign.
 * 
 * @author Sergio
 * 
 */
abstract class PivotGeneratorRule extends Rule {

	protected final PivotsGenerator fGenerator;

	PivotGeneratorRule(PivotsGenerator aGen) {
		fGenerator = aGen;
	}

	/**
	 * reapplies the current rule; a pivot generator rule could be called two
	 * times in the same tick. This because the rule could be fired another time
	 * using the new start point.
	 * 
	 * <p>
	 * In fact the reapply method should be called only if there has been a
	 * moving of the start point (which is the so-called <i>positive</i>
	 * channel.
	 * 
	 * <p>
	 * Some rules has no possibility of reapply, for them the reapply will
	 * always return false, for example the pivot breakout rule and the numeric
	 * zig zag rule will fire only in the case of a new tick which breaks a
	 * certain threshold.
	 * 
	 * @param qt
	 *            the last tick
	 * 
	 * @return true if a new pivot has been created.
	 */
	@SuppressWarnings("static-method")
	public boolean reapply(QueueTick qt) {
		return false;
	}

	/**
	 * @param qt
	 *            the new tentative pivot
	 */
	@SuppressWarnings("static-method")
	public int getConfirmThreshold(QueueTick qt) {
		return Integer.MAX_VALUE;
	}
}

/**
 * the pivots generator is a class that for a single scale will create a stream
 * of pivots. The stream of pivots is then fed to the chart.
 * 
 * <p>
 * The indicator is not aware of the pivots. It needs only to know a generic
 * <i>start point</i> which is used to compute the real time channel.
 * 
 * <p>
 * Pivots and start points are loosely correlated. A start point usually is on a
 * pivot of a lower scale, but this could also not be the case.
 * 
 * @author Sergio
 * 
 */
class PivotsGenerator extends BasePivotsGenerator {

	/**
	 * These are the rules to generate the pivots. The rules are fired in order.
	 * When a rule fires other rules are not called. A rule should be able to
	 * fire only if there is a change in state.
	 */
	protected ArrayList<PivotGeneratorRule> fRules = new ArrayList<>();

	protected PivotsGenerator(BaseScaleIndicator aInd) {
		super(aInd);
		assert (aInd.level != 0);
		IndicatorParamBean bean = aInd._compositeIndicator.bean;

		if (bean.isNegativeOnPriceMultiplier()) {
			fRules.add(new NumericZigZagRule(this));
		}
		if (bean.isNegativeOnPivotBreakOut()) {
			fRules.add(new PivotBreakoutRule(this,
					(aInd.prevInd).fPivotsGenerator));
		}
		if (aInd.level >= 2 && bean.isNegativeOnFlatChannel()) {
			fRules.add(new NegativeOnFlatRule(this,
					((IndicatorSingle) aInd).fIndicator));
		}

		if ((bean.isNegativeOnSCTouch_S0RatioEnabled() || bean
				.isNegativeOnSCTouch_S0TimeRatioEnabled())) {

			int startScaleNegativeScTouch = bean
					.getNegativeOnSCTouch_startScale();

			if ((aInd.level + 1) >= startScaleNegativeScTouch) {
				fRules.add(new NegativeOnScTouchRule(this,
						aInd.prevInd.fPivotsGenerator,
						((IndicatorSingle) aInd).fIndicator));
			}
		}

		fState = EState.UNDECIDED; // the initial state of the pivot generator.

	}

	@Override
	public void begin(int tick) {
		for (Rule rule : fRules) {
			rule.begin(tick);
		}

	}

	private void checkRules(QueueTick qt) {
		for (Rule rule : fRules) {
			rule.newTick(qt);
			if (rule.isFired()) {
				addNewPivot(qt);
				break;
			}
		}

	}

	@Override
	public void drawOn(ChannelIndicator chInd) {
		if (_newPivot) {
			chInd.newPivot(fLastPivot);
			// fLastMinusOnePivot = fLastPivot;
			// fLastPivot = fNewPivot;
			// fNewPivot = null;

		}
	}

	@Override
	public void newTick(QueueTick qt) {

		_newPivot = false;
		fNewTentative = false;

		switch (fState) {
		case BEFORE_FIRST_PRICE:
		case FIRST_PRICE:
			assert (false); // should not happen.
			break;
		case UNDECIDED:
			// hh / ll handling
			// In any case I will check the rules

			if (fHH == null) {
				fHH = qt;
			} else if (qt.getPrice() >= fHH.getPrice()) {
				fHH = qt;
			}

			if (fLL == null) {
				fLL = qt;
			} else if (qt.getPrice() <= fLL.getPrice()) {
				fLL = qt;
			}

			checkRules(qt);

			break;
		case ZIG_STATE:
			if (qt.getPrice() >= fTentativePivot.getPrice()) {
				addNewTentative(qt, _getConfirmThreshold(qt));
			} else {
				checkRules(qt);
			}
			break;
		case ZAG_STATE:
			if (qt.getPrice() <= fTentativePivot.getPrice()) {
				addNewTentative(qt, _getConfirmThreshold(qt));
			} else {
				checkRules(qt);
			}
			break;

		default:
			break;
		}
	}

	// @Override
	// public int getMinimunTimeToSave() {
	// return Integer.MAX_VALUE;
	// }

	private int _getConfirmThreshold(QueueTick qt) {
		int bestConfirm = Integer.MAX_VALUE;
		for (PivotGeneratorRule pgr : fRules) {
			int confirmPrice = pgr.getConfirmThreshold(qt);
			if (Math.abs(qt.getPrice() - confirmPrice) < Math.abs(bestConfirm
					- qt.getPrice())) {
				bestConfirm = confirmPrice;
			}
		}
		return bestConfirm;
	}

	/**
	 * tries to reapply the rules; if a new rule fires then a new pivot is
	 * added.
	 * 
	 * @param qt
	 *            the last tick
	 */
	@Override
	public void reapplyRules(QueueTick qt) {
		for (PivotGeneratorRule pgr : fRules) {
			if (pgr.reapply(qt)) {
				addNewPivot(qt);
				break;
			}
		}
	}

}

abstract class PositiveChannelCreationRule extends Rule {

	protected PositiveChannelsGenerator fPositiveGenerator;

	/**
	 * I can simply have the generator;
	 * 
	 * @param pcg
	 */
	public PositiveChannelCreationRule(PositiveChannelsGenerator pcg) {
		fPositiveGenerator = pcg;
	}

}

/**
 * A <b>positive </b> channel is a channel that is begun without the creation of
 * a pivot.
 * 
 * <p>
 * In other words: the channel is in some way shortened (it <b>cannot</b> be
 * longer) but the swing remains the same (if it was a zig remains a zig,
 * otherwise a zag).
 * 
 * 
 * @author Sergio
 * 
 */
class PositiveChannelsGenerator extends BasePositiveChannelGenerator {

	/**
	 * These are the rules which manages the creation of a positive channel.
	 */
	private ArrayList<PositiveChannelCreationRule> fRules = new ArrayList<>();

	protected PositiveChannelsGenerator(IndicatorSingle indicatorSingle) {
		super(indicatorSingle);

		IndicatorParamBean bean = indicatorSingle._compositeIndicator.bean;
		if (bean.isPositiveOnSCRCTouch()
				&& (bean.getPositiveOnSCRCTouch_startScale() <= (fInd.level + 1))) {
			fRules.add(new ScRcTouchRule(this));
		}

		if (bean.isBestChannelFittingEnabled()
				&& (bean.getPositiveOnSCRCTouch_startScale() <= (fInd.level + 1))) {
			fRules.add(new BestFittingPositiveRule(this, bean
					.getBestChannelFitting()));
		}

	}

	@Override
	public void begin(int tick) {
		for (Rule rl : fRules) {
			rl.begin(tick);
		}
	}

	@Override
	public void newTick(QueueTick qt) {
		if (fInd.fPivotsGenerator.fNumPivots == 0) {
			// nothing to do
			return;
		}
		fNewPositiveChannel = false;
		for (Rule rl : fRules) {
			if (rl.tryToFire(qt)) {
				// OK! I can move the channel.
				if (((IndicatorSingle) fInd).fChanLengthManager
						.tryMakeChannelShorter(false)) {
					// OK! I have created a new channel.
					// this is a COMMIT, because the channel has been moved..
					// debug_var(881755, "lev ", fInd.level, " new positive @ ",
					// qt);
					fNewPositiveChannel = true;
				}
			}
		}
	}

}

abstract class Rule implements IIndicatorBlindParticipant {

	@Override
	public void hurryUp(int hurryUpQuota) {
		// empty here
	}

	@Override
	public void calmDown(int calmDownQuota) {
		// empty here
	}

	private boolean fIsFired = false;

	@Override
	public abstract void begin(int tick);

	boolean isFired() {
		return fIsFired;
	}

	@Override
	public final void newTick(QueueTick qt) {
		fIsFired = tryToFire(qt);
	}

	protected abstract boolean tryToFire(QueueTick qt);
}

/**
 * A class which is
 * 
 * @author Sergio
 * 
 */
class ScRcTouchRule extends PositiveChannelCreationRule {

	public ScRcTouchRule(PositiveChannelsGenerator pcg) {
		super(pcg);
	}

	@Override
	public void begin(int tick) {
		// nothing.
	}

	@Override
	protected boolean tryToFire(QueueTick qt) {
		// I look if there is a touch.
		if (this.fPositiveGenerator.fInd.isThereATouch()) {
			return true;
		}
		return false;
	}

}

/**
 * A smoothing channel to make the transitions between channels as smooth as
 * possibile.
 * 
 * <p>
 * The smoothing is done feeding two channels at a time and making a
 * <i>third</i> virtual indicator which is a sort of average between the two
 * indicators.
 * 
 * <p>
 * From the outside the external world does not know that this is a smoothing
 * version of the indicator.
 * 
 * @author Sergio
 * 
 */
final class SmoothingChannel extends IndicatorComponent implements
		IChannelHelper {

	// the base channel which is fed by this smoothing version.
	private IChannelHelper fCurChan;
	// private IChannelHelper fPrevChan;
	// /now I have the indicator's crossers.
	IndicatorCrosser fTopCrosser;

	IndicatorCrosser fCenterCrosser;
	IndicatorCrosser fBottomCrosser;
	// private EState fPrevState;
	private double fTop;
	private double fCenter;
	private double fBottom;
	private boolean fNewChannel;
	private final boolean fPutCenterLineInHalf;

	/**
	 * Builds a smoothing channel, that is a channel which simply has the
	 * capability of render the transition between one channel and the next as
	 * smooth as possible.
	 * 
	 * 
	 * @param aInd
	 * @param fIndicator
	 */
	protected SmoothingChannel(BaseScaleIndicator aInd,
			IChannelHelper fIndicator) {
		super(aInd);
		fCurChan = fIndicator;

		fTopCrosser = new IndicatorCrosser(aInd);
		fCenterCrosser = new IndicatorCrosser(aInd);
		fBottomCrosser = new IndicatorCrosser(aInd);

		fPutCenterLineInHalf = fIndicator.isCenterLineInHalf();
	}

	@SuppressWarnings("boxing")
	private void _update_indicator(QueueTick qt) {
		long curPrice = qt.getPrice();
		double newTop, newCenter, newBottom;

		newCenter = fCenterCrosser.getCurIndicator(fCurChan.getCenterY2());

		if (Double.isNaN(newCenter)) {
			fCenter = newCenter;
			fBottom = newCenter;
			fTop = newCenter;
			return;
		}

		// if (fInd.level == 5) {
		// System.out.println("(((");
		// }

		newTop = fTopCrosser.getCurIndicator(fCurChan.getTopY2());
		newTop = Math.max(curPrice, newTop);

		newBottom = fBottomCrosser.getCurIndicator(fCurChan.getBottomY2());
		newBottom = Math.min(newBottom, curPrice);

		if (newTop < newBottom || newCenter > newTop || newCenter < newBottom) {
			/*
			 * The smoothed channel is incoherent, I take the old values
			 */

			U.debug_var(388925, "channel incoherent @ ",
					this.fInd._compositeIndicator.lastTick.getFakeTime(),
					" top ", newTop, " cent ", newCenter, " bot ", newBottom);

		} else {

			fTop = newTop;
			fBottom = newBottom;
			fCenter = newCenter;

			if (fPutCenterLineInHalf) {
				fCenter = (fTop + fBottom) / 2.0;
			} else {
				fCenter = Math.max(fCenter, fBottom);
				fCenter = Math.min(fTop, fCenter);
			}

		}

		assert (fTop >= fBottom);
		assert (fCenter <= fTop) : " center " + fCenter + " top " + fTop;
		assert (fCenter >= fBottom);

		// if (fInd.level == 1) {
		// debug_var(899356, "[t ", qt.getFakeTime(), ", p ", qt.getPrice(),
		// " t ", fTop, " c ", fCenter, " b ", fBottom);
		// }

		assert (getTopDistance() >= 0);
		assert (getBottomDistance() >= 0);
	}

	//

	@Override
	public void begin(int tick) {
		fCurChan.begin(tick);
	}

	@Override
	public IChannelHelper clone() {
		throw new UnsupportedOperationException(); // here you may not call me.
	}

	@Override
	public void drawOn(ChannelIndicator chInd) {

		if (_indicatorDisabled || Double.isNaN(fCenter)
				|| fInd.fPivotsGenerator.fNumPivots == 0) {
			return;
		}

		PointRegressionLine prl = new PointRegressionLine();
		prl.setTime(chInd.lastTick.getFakeTime());
		prl.setPriceCenter(fCenter);
		// prl.setPrice(fCenter);

		prl.setPriceBottom(fBottom);
		prl.setPriceTop(fTop);

		prl.setLevel(fInd.level + 1);
		chInd.newPointRegressionLine(prl);

		if (Double.isNaN(getCenterY1())) {
			return;
		}

		if (!fNewChannel) {
			chInd.createAndDispatchNewRealTimeChannel(fInd.level, this);
		} else {
			chInd.createAndDispatchNewStartingChannel(fInd.level, this);
		}

		fNewChannel = false;

	}

	@Override
	public double getBottomY2() {
		return fBottom;
	}

	@Override
	public double getCenterY1() {
		return fCurChan.getCenterY1();
	}

	@Override
	public double getCenterY2() {
		return fCenter;
	}

	@Override
	public double getRawBottomY2() {
		return 0;
	}

	@Override
	public double getRawCenterY2() {
		return 0;
	}

	@Override
	public double getRawTopY2() {
		return 0;
	}

	@Override
	public int getRcTouches() {
		return fCurChan.getRcTouches();
	}

	@Override
	public int getScTouches() {
		return fCurChan.getScTouches();
	}

	@Override
	public double getSlope() {
		return (fCenter - getCenterY1())
				/ (fCurChan.getX2() - fCurChan.getX1());
	}

	@Override
	public double getTopY2() {
		return fTop;
	}

	@Override
	public int getX1() {
		return fCurChan.getX1();
	}

	@Override
	public void warmUpFinished() {
		super.warmUpFinished();
		fCurChan.warmUpFinished();
	}

	@Override
	public int getX2() {
		return fCurChan.getX2();
	}

	@Override
	public boolean isCenterLineInHalf() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isThereANewRc() {
		return fCurChan.isThereANewRc();
	}

	@Override
	public boolean isThereANewSc() {
		return fCurChan.isThereANewSc();
	}

	@Override
	public void moveTo(int newSp) {
		/*
		 * The moveTo method could be called several times, because in the Spa
		 * version of the length manager we have the array of start points which
		 * are choosen one by one. So the method should not call the change
		 * state more than one time.
		 */

		fTopCrosser.newChan();
		fBottomCrosser.newChan();
		fCenterCrosser.newChan();

		fCurChan.moveTo(newSp);

		_update_indicator(fInd._compositeIndicator.lastTick);

		fNewChannel = true;

	}

	@Override
	public void newTick(QueueTick qt) {
		fCurChan.newTick(qt);
		_update_indicator(qt);
	}

	@Override
	public double getTopDistance() {
		return fCurChan.getTopDistance();
	}

	@Override
	public double getBottomDistance() {
		return fCurChan.getBottomDistance();
	}

	@Override
	public double[] getChannelCoefficients() {
		return fCurChan.getChannelCoefficients();
	}

	@Override
	public boolean supportsSlope() {
		return fCurChan.supportsSlope();
	}

	@Override
	public int getMinimumWindow() {
		return fCurChan.getMinimumWindow();
	}

	@Override
	public double eval(double x) {
		/*
		 * the evaluation for the smoothing channel is rather complex, because
		 * you must distinguish the three cases.
		 * 
		 * a. at time x only the old channel existed (but it could also be the
		 * new channel of an older one... !)
		 * 
		 * b. at time x both channels exist
		 * 
		 * c. at time x only the new channel exists.
		 */

		if (Double.isNaN(fCenter)) {
			return Double.NaN;
		}
		return fCurChan.eval(x);
	}
}

class SPAChannelLengthGenerator extends ChannelLengthManager {

	@Override
	public int getNextPositiveStartPoint() {
		fSPA.init();
		fSPA.begin(false, fInd.getStartPoint());

		if (fSPA.hasNext()) {
			int nextSp = fSPA.next();
			if (nextSp != fInd.getStartPoint()) {
				return nextSp;
			}
		}

		return 0; // nothing
	}

	StartPointArray fSPA;

	private final boolean fGreedy;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1373717770114434893L;

	SPAChannelLengthGenerator(BaseScaleIndicator aInd) {
		super(aInd);
		fSPA = new StartPointArray(aInd);

		SPAType spatype = aInd._compositeIndicator.bean.getIndicator_SPAType();
		fGreedy = spatype == SPAType.MAX_SP;
	}

	@Override
	public boolean tryMakeChannelShorter(boolean forNegative) {
		fSPA.init();
		// Initialize the iterator for the start point array
		fSPA.begin(forNegative, fInd.getStartPoint());

		if (fSPA.fWindowReverted) {

			// the window is reverted, so I simply move the channel to the
			// maximum, if I can do it!
			if (fSPA.hasNext()) {
				// assert(forNegative);
				fInd.shortenChannel(fSPA.next());
				return true;
			}
			// shortening the channel to the same start point is not an error,
			// it simply creates a new channel there, this is only for negative!
			if (forNegative) {
				fInd.shortenChannel(fInd.getStartPoint());
				return true;
			}
			return false;
		}

		// Now the window is not reverted.
		boolean moved = false; // no reposition?

		if (fSPA.hasNext()) {
			int adjusted_start = Math.max(fInd.getStartPoint(), fSPA.fMin);

			if (adjusted_start != fInd.getStartPoint()) {
				fSPA.next();
				fInd.shortenChannel(adjusted_start);
				moved = true;
			}
		}

		while (true) {
			boolean resolvedTouch, isChannelCoherent;
			resolvedTouch = false;
			isChannelCoherent = false;

			if (!fSPA.hasNext()) {
				break;
			}

			int spatry = fSPA.next();
			fInd.shortenChannel(spatry);
			moved = true;

			resolvedTouch = !fInd.isThereATouch();

			isChannelCoherent = fInd.isChannelCoherent();

			if (!forNegative && resolvedTouch) {
				// debug_var(818195, "positive resolved touch!");
				break;
			}

			if (fGreedy) {
				if (forNegative && isChannelCoherent && resolvedTouch) {
					break;
				}
			} else {
				if (forNegative && resolvedTouch) {
					break;
				}
			}

		}

		return moved;
	}

}

class WindowChannelLengthManager extends ChannelLengthManager {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2256727413120770252L;

	WindowChannelLengthManager(BaseScaleIndicator bsi) {
		super(bsi);
	}

	@Override
	public boolean tryMakeChannelShorter(boolean forNegative) {
		if (!forNegative) {
			return false;
		}
		fInd.shortenChannel(fInd.getStartPoint());
		return true;
	}

}

class ZeroScalePivotsGenerator extends BasePivotsGenerator {

	protected ZeroScalePivotsGenerator(
			IndicatorSingleZeroScale indicatorSingleZeroScale) {
		super(indicatorSingleZeroScale);
	}

	// @Override
	// protected void addNewPivot(QueueTick confirm) {
	// super.addNewPivot(confirm);
	// // debug_var(239915, "Zero scale pivot created ", fNewPivot);
	// // fLastMinusOnePivot = fLastPivot;
	// // fLastPivot = fNewPivot;
	// // fNewPivot = null;
	// /*
	// * I do not "draw" here
	// */
	// _newPivot = false;
	// }

	@Override
	public void newTick(QueueTick qt) {
		_newPivot = false;
		fNewTentative = false;

		switch (fState) {
		case BEFORE_FIRST_PRICE:
			fFirstPrice = qt;
			fTentativePivot = qt;
			fState = EState.FIRST_PRICE;
			break;
		case FIRST_PRICE:
			if (qt.getPrice() > fFirstPrice.getPrice()) {
				fState = EState.ZIG_STATE;
			} else {
				fState = EState.ZAG_STATE;
			}
			fTentativePivot = qt;
			break;
		case ZIG_STATE:
			if (qt.getPrice() < fTentativePivot.getPrice()) {
				addNewPivot(qt);

			} else {
				addNewTentative(qt, qt.getPrice()
						- this.fInd._compositeIndicator._tick);
			}
			break;
		case ZAG_STATE:
			if (qt.getPrice() > fTentativePivot.getPrice()) {
				addNewPivot(qt);
			} else {
				addNewTentative(qt, qt.getPrice()
						+ this.fInd._compositeIndicator._tick);
			}
			break;
		case UNDECIDED:

		default:
			break;
		}
	}

	@Override
	public void reapplyRules(QueueTick qt) {
		throw new UnsupportedOperationException();
	}

}