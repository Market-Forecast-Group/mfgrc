package com.mfg.widget.arc.strategy;

import static com.mfg.utils.Utils.debug_var;

import java.awt.Point;
import java.util.ArrayList;
import java.util.ListIterator;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import com.mfg.common.QueueTick;
import com.mfg.interfaces.indicator.Pivot;

/**
 * The base class for all scale based indicators
 * 
 * @author Sergio
 * 
 */
abstract class BaseScaleIndicator implements IIndicatorParticipant {

	/**
	 * This variable stores the gatekeeping value for the multithreaded
	 * indicator.
	 * 
	 * It is (2^level) - 1, for example for scale 6 it is 63
	 */
	protected final long ALL_LOWER_BITS;

	/**
	 * This is my bit. It is simply 2^level
	 */
	protected final long MY_BIT;

	protected final long LOWER_SCALES_AND_MYSELF_BIT;

	@Override
	public void hurryUp(int hurryUpQuota) {
		// empty
	}

	@Override
	public void calmDown(int calmDownQuota) {
		// empty
	}

	/**
	 * This enumeration is used to distinguish the various states of the
	 * indicator.
	 * <p>
	 * The first two states are only used by the <i>zero scale</i> indicator.
	 * <p>
	 * The undecided state is used by the other scales indicator, because only
	 * the other scales could be undecided.
	 * <p>
	 * The <b>zig</b> state is the state of a swing that is going up, so the
	 * last pivot is a <b>down</b> pivot. The <b>zag</b> state is the state of a
	 * swing which is going down, so the last pivot is an <b>up</b> pivot.
	 * 
	 * @author Sergio
	 * 
	 */
	protected enum EState {
		BEFORE_FIRST_PRICE, FIRST_PRICE, ZIG_STATE, ZAG_STATE, UNDECIDED
	}

	final boolean fDebugPrint;

	protected BasePivotsGenerator fPivotsGenerator;

	protected final int level;

	protected final ChannelIndicator _compositeIndicator; // cannot be null.
	protected final BaseScaleIndicator prevInd; // could be null

	IChannelHelper fIndicator;

	protected BaseScaleIndicator(ChannelIndicator aIndicator, int aLevel,
			BaseScaleIndicator prev) {
		level = aLevel;
		_compositeIndicator = aIndicator;
		prevInd = prev;

		this.fDebugPrint = _compositeIndicator == null ? false
				: _compositeIndicator.bean.isPrintMessagesForTesting();

		MY_BIT = (long) Math.pow(2, level);
		/*
		 * For the level zero we have an exception, because there are no lower
		 * scales...
		 */
		ALL_LOWER_BITS = Math.max(MY_BIT - 1, 1);

		LOWER_SCALES_AND_MYSELF_BIT = (long) (Math.pow(2, level + 1) - 1);

	}

	/**
	 * tells the pivots generator to save the pivots until a certain time.
	 * 
	 * @param aTime
	 *            the time to save to.
	 */
	public void forgetUntil(int aTime) {
		fPivotsGenerator.saveUntil(aTime);
	}

	public boolean getChIsGoingUp() {
		return fPivotsGenerator.fState == EState.ZIG_STATE;
	}

	/**
	 * returns the current channel to the outside.
	 * 
	 * <p>
	 * Only the level 1 and above have a real channel.
	 * 
	 * @return the current channel to the outside, it is a read only interface.
	 */
	public final IChannel getCurrentChannel() {
		return fIndicator;
	}

	public int getCurrentPivotsCount() {
		return fPivotsGenerator.fNumPivots;
	}

	public Point getCurrentTentativePivot() {
		if (fPivotsGenerator.fTentativePivot == null) {
			return null;
		}
		return new java.awt.Point(
				fPivotsGenerator.fTentativePivot.getFakeTime(),
				fPivotsGenerator.fTentativePivot.getPrice());
	}

	public long getHHPrice() {
		return fPivotsGenerator.fTentativePivot.getPrice();
	}

	public int getHHTime() {
		return fPivotsGenerator.fTentativePivot.getFakeTime();
	}

	public Pivot getLastPivot(int steps) {
		return fPivotsGenerator.getPastPivot(steps * -1);
	}

	public long getLLPrice() {
		return fPivotsGenerator.fTentativePivot.getPrice();
	}

	public int getLLTime() {
		return fPivotsGenerator.fTentativePivot.getFakeTime();
	}

	public boolean isThereATentativePivot() {
		return fPivotsGenerator.isThereANewTentative();
	}

	/**
	 * returns the minimum (farthest) time to save. The time is the fake time.
	 * <p>
	 * Then the composite indicator will forget all data which is inferior to
	 * the minimum of the prices.
	 * 
	 * @return the minimum price to save, if this indicator does not need the
	 *         dataset then it returns {@linkplain Integer#MAX_VALUE}.
	 */
	public int getMinimunTimeToSave() {
		Pivot pv = fPivotsGenerator.getPastPivot(0);
		return Math.min(getStartPoint(), pv == null ? Integer.MAX_VALUE
				: pv.fPivotTime);
	}

	public ArrayList<Pivot> getPivotsList() {
		return fPivotsGenerator.fPivots;
	}

	public abstract int getStartPoint();

	@SuppressWarnings("static-method")
	public boolean isChannelCoherent() {
		throw new UnsupportedOperationException(); // here is not good.
	}

	public boolean isSwingDown() {
		if (fPivotsGenerator.fState == EState.ZAG_STATE) {
			return true;
		}
		return false;
	}

	public boolean isThereANewPivot() {
		return fPivotsGenerator.isThereANewPivot();
	}

	/**
	 * returns true if there has been a touch.
	 * 
	 * @return
	 */
	public boolean isThereATouch() {
		return (getCurrentChannel().isThereANewRc() || getCurrentChannel()
				.isThereANewSc());
	}

	@SuppressWarnings("boxing")
	public void printLastPivot() {
		if (fPivotsGenerator.fPivots.size() == 0) {
			debug_var(391933, "level ", this.level, " no pivots!");
		} else {
			Pivot lastPv = fPivotsGenerator.fPivots
					.get(fPivotsGenerator.fPivots.size() - 1);
			debug_var(382811, lastPv, " up? ", lastPv.isStartingDownSwing());
		}

	}

	public void printStats() {
		//
	}

	@Override
	public void warmUpFinished() {
		// default void implementation
		fPivotsGenerator.warmUpFinished();
	}

	// @Override
	// public void setLightUpdateState(boolean isOn) {
	// // default void implementation
	// }

	/**
	 * @param newSp
	 *            the new start point
	 */
	@SuppressWarnings("static-method")
	public void shortenChannel(int newSp) {
		throw new UnsupportedOperationException();// here is not good.
	}

	/**
	 * Queues the new tick to be processed by the indicator. Now the indicator
	 * works in a thread on its own and this triggers the computation.
	 * 
	 * 
	 * @param qt
	 */
	public abstract void queueNewTick(QueueTick qt);

	public void onStopping() {
		// nothing here
	}

	/**
	 * does the statistics, called at the end of warm up
	 */
	// @SuppressWarnings("static-method")
	// public final double[] doS0S0PrimeStats() {
	// // return fPivotsGenerator.doS0S0PrimeStats();
	// double[] res = new double[4];
	// Arrays.fill(res, 1);
	// return res;
	// }

	public int getConfirmThreshold() {
		return fPivotsGenerator.getConfirmThreshold();
	}

	public double[] getStats() {
		return fPivotsGenerator.getStats();
	}

}

/**
 * The clonable regression is a simple regression with the availability to be
 * cloned. The Apache SimpleRegression class can be cloned because it has only
 * primitive fields.
 * 
 * @author Sergio
 * 
 */
class CloneableRegression extends SimpleRegression implements Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -673039223895227972L;

	/**
	 * adds the tick to the regression. The tick is a queue tick and the fake
	 * time is considered.
	 * 
	 * @param qt
	 *            the queue tick to be added.
	 */
	public void addTick(QueueTick qt) {
		addData(qt.getFakeTime(), qt.getPrice());
	}

	@Override
	public CloneableRegression clone() {

		try {
			CloneableRegression cr = (CloneableRegression) super.clone();
			return cr;
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public void removeTick(QueueTick qt) {
		removeData(qt.getFakeTime(), qt.getPrice());
	}

	@Override
	public String toString() {
		return "[N= " + getN() + " R= " + getR() + " ]";
	}
}

enum ETouch {
	NO_TOUCH, UP_TOUCH, DOWN_TOUCH
}

/**
 * All the channels provide this interface which can give the client the 6
 * points of the channel (bottom, center, top), start and end.
 * 
 * <p>
 * This is the read only version of the channel, all the methods are only get
 * and do not alter the state of the channel.
 * 
 * <p>
 * For some channels these points could be identical.
 * 
 * @author Sergio
 * 
 */
interface IChannel {

	/**
	 * evaluates the channel at the point x and returns the result. x is
	 * unbounded, it can be also before the start point or after the end point.
	 * 
	 * <p>
	 * the value returned is the <b>central</b> line value, if you need the top
	 * or bottom you can use the {@link #getTopDistance()} and
	 * {@link #getBottomDistance()} methods to alter the value.
	 * 
	 * <p>
	 * This is a short hand method to avoid to use the coefficients of the
	 * channel.
	 * 
	 * @return the value of the channel at the point x
	 */
	double eval(double x);

	/**
	 * returns the bottom distance of the bottom line from the center, as a
	 * positive value.
	 * 
	 * @return the bottom distance.
	 */
	double getBottomDistance();

	double getBottomY2();

	double getCenterY1();

	double getCenterY2();

	/**
	 * The generic function that gets the coefficient for this channel (it is
	 * assumed to be a polynomial)
	 * 
	 * <p>
	 * It returns a polynomial representation of the <b>central</b> line of this
	 * channel, in the form of a polynomial. The degree of the polynomial is
	 * fixed and was fixated at build time, certain indicators are only capable
	 * of giving a straight line, in this sense the returned array is always of
	 * length two.
	 * 
	 * <p>
	 * The first number of the array is the constant term, the second the term
	 * for the x, the third the term for the x squared, etc...
	 * 
	 * <p>
	 * The array returned should be treated as read only, it may be safe to
	 * alter it, but it is better to make a copy
	 */
	double[] getChannelCoefficients();

	/**
	 * returns the minimum window used to compute the indicator.
	 * 
	 * <P>
	 * This is the fixed window, not the <b>actual</b> window, that is if this
	 * is a SPA indicator the fixed window is not defined, so the method will
	 * return 0
	 * 
	 * @return the minimum window used to compute the indicator, for some
	 *         indicators this is zero, they do not have a minimum window.
	 */
	public int getMinimumWindow();

	double getRawBottomY2();

	double getRawCenterY2();

	double getRawTopY2();

	/**
	 * returns the current number of RC touches in the current swing. The count
	 * is zeroed at each starting of the new swing.
	 * 
	 * @return the current number of RC touches.
	 */
	public int getRcTouches();

	/**
	 * returns the current number of Sc touches in the current swing. The count
	 * is zeroed at each starting of the new swing.
	 * 
	 * @return the current number of SC touches.
	 */
	public int getScTouches();

	double getSlope();

	/**
	 * returns the top distance of the top line from the center.
	 * 
	 * @return the top distance.
	 */
	double getTopDistance();

	double getTopY2();

	int getX1();

	int getX2();

	/**
	 * returns true if I have to put the center line in half.
	 * 
	 * @return
	 */
	boolean isCenterLineInHalf();

	/**
	 * returns true if there has been a new rc touch
	 * 
	 * @return
	 */
	public boolean isThereANewRc();

	/**
	 * returns true if there has been a new sc touch
	 * 
	 * @return
	 */
	public boolean isThereANewSc();

	/**
	 * some channel types do not support the concept of slope, so this method is
	 * used to ask the channel if it supports the slope concept.
	 * 
	 * @return true if the {@link #getSlope()}is supported
	 */
	public boolean supportsSlope();

}

/**
 * unused
 * 
 * @author Sergio
 * 
 */
@Deprecated
abstract class IndicatorImpl {
	// unused
}

/**
 * A start point array is an object which gives to the indicator a rainbow of
 * points from which the channel can start
 * 
 * @author Sergio
 * 
 */
class StartPointArray {

	private transient ListIterator<Pivot> fIt;

	int fMin;
	int fMaxForNegative;
	int fMaxForPositive;
	int fCurMax;

	ArrayList<Pivot> f3ls;
	boolean fWindowReverted;
	int fNextTry;

	private boolean fAlreadyGivenMax;
	private BaseScaleIndicator fInd;

	/**
	 * initializes the start point array which moves using the 3ls pivots.
	 */
	public StartPointArray(BaseScaleIndicator indImpl) {
		fInd = indImpl;
		if (indImpl.level > 2) {
			f3ls = indImpl.prevInd.prevInd.prevInd.getPivotsList();
		} else if (indImpl.level > 1) {
			f3ls = indImpl.prevInd.prevInd.getPivotsList();
		} else if (indImpl.level > 0) {
			f3ls = indImpl.prevInd.getPivotsList();
		} else {
			f3ls = indImpl.getPivotsList();
		}
	}

	public void begin(boolean forNegative, int aStartpoint) {
		fAlreadyGivenMax = false;
		fIt = null;
		if (forNegative) {
			fCurMax = fMaxForNegative;
		} else {
			fCurMax = fMaxForPositive;
		}

		fWindowReverted = false;
		if (fMin >= fCurMax) {
			fWindowReverted = true;
			// in case of reverted window I have only one try.
			fNextTry = fMin != aStartpoint ? fMin : -1;
		} else {
			// Ok the window is not reverted.
			int starting = Math.max(aStartpoint, fMin);
			if (starting > fCurMax) {
				fNextTry = -1;
				return;
			}

			fIt = f3ls.listIterator(f3ls.size());

			int howManyTries = 0;
			int breakingTime = -1;
			boolean touchedMinimum = false;
			boolean touchedMaximum = false;
			while (fIt.hasPrevious()) {
				Pivot pv = fIt.previous();
				breakingTime = pv.getPivotTime();
				if (breakingTime > fCurMax) {
					continue;
				}

				if (breakingTime >= starting) {
					if (breakingTime == fCurMax) {
						touchedMaximum = true;
					}
					if (breakingTime == starting) {
						touchedMinimum = true;
					}
					howManyTries++;
					continue;
				}

				break;
			}
			// assert(breakingTime == aStartpoint);
			if (starting == aStartpoint && touchedMinimum) {
				howManyTries--; // The tries are one less, because the start
								// point is not a try
			}

			if (!touchedMaximum && (fCurMax != aStartpoint)) {
				// this is a try!
				howManyTries++;
			}

			if (howManyTries < 1) {
				fNextTry = -1; // I have nothing available (apart the current
								// sp)
			} else {

				if (!touchedMaximum && howManyTries == 1) {
					fNextTry = fCurMax;
					fAlreadyGivenMax = true;
					while (breakingTime < starting
							|| breakingTime == aStartpoint) {
						breakingTime = fIt.next().fPivotTime;
					}
					return;
				}

				fNextTry = fIt.next().fPivotTime;
				while (fNextTry < starting || fNextTry == aStartpoint) {
					fNextTry = fIt.next().fPivotTime;
					// This above IS the next UNLESS I have not touched the
					// minimum
				}
				if (!touchedMinimum && starting != aStartpoint) {
					fNextTry = starting;
					fIt.previous(); // go before, please!
				}
				if (fNextTry == fCurMax) {
					fAlreadyGivenMax = true;
				}

			}

		}

	}

	/**
	 * 
	 * This method computes the maximum available start point from the SPA
	 * accordingly to the 3-5-13 rule.
	 * 
	 * This start point is FIXED.
	 * 
	 * @return the max available start point from the SPA, the old start point
	 *         if nothing is available.
	 */
	@SuppressWarnings("boxing")
	int getMaxPossibleStartPoint(boolean forNegative) {
		ArrayList<Pivot> prevPivots = fInd.prevInd.getPivotsList();
		int candidateStartPoint_rule1 = -1;
		int candidateStartPoint_rule2 = -1;
		int candidateStartPoint_rule3 = -1;
		int maxPossibleStartPoint;

		if (prevPivots.size() == 0) {
			return fInd.getStartPoint(); // nothing to do.
		}

		boolean rule1, rule2, rule3;
		rule1 = fInd._compositeIndicator.bean.is_3PivotsAt1LS();
		rule2 = fInd._compositeIndicator.bean.is_5PivotsAt2LS();
		rule3 = fInd._compositeIndicator.bean.is_13PivotsAt3LS();

		// I must take the last 1ls pivot
		Pivot last1lsPivot = prevPivots.get(prevPivots.size() - 1);
		long timeToBreak = last1lsPivot.getPivotTime();

		if (!rule1 && !rule2 && !rule3) {
			rule1 = true;
		}

		// boolean dynamic = this._compositeIndicator.bean.isDinamyc_New();
		if (rule1) {
			// START POINT RULE 1
			if (prevPivots.size() >= 3) {
				final Pivot l3Ls = prevPivots.get(prevPivots.size() - 3);
				// int oldStartPoint = startPoint;
				candidateStartPoint_rule1 = l3Ls.getPivotTime();
			}
		}

		// maxPossibleStartPoint = Math.max(this.startPoint,
		// candidateStartPoint_rule1);

		// Rule 2.
		// candidateStartPoint = Integer.MIN_VALUE;
		if (rule2) {
			if (fInd.level > 1) {
				ArrayList<Pivot> _2ls = fInd.prevInd.prevInd.getPivotsList();
				if (_2ls.size() >= 5) {

					ListIterator<Pivot> it = _2ls.listIterator(_2ls.size() - 4);
					while (it.hasPrevious()) {
						Pivot _2ls_pv = it.previous();
						if (_2ls_pv.getPivotTime() < fInd.getStartPoint()) {
							break;
						}

						if (_2ls_pv.getPivotTime() < timeToBreak) {
							candidateStartPoint_rule2 = _2ls_pv.getPivotTime();
							break;
						}
					}
				}
			}
		}
		// maxPossibleStartPoint = Math.max(this.startPoint,
		// candidateStartPoint_rule2);

		// Rule 3
		// candidateStartPoint = Integer.MIN_VALUE;
		if (rule3) {
			if (fInd.level > 2) {
				ArrayList<Pivot> _2ls = fInd.prevInd.prevInd.getPivotsList();
				ArrayList<Pivot> _3ls = fInd.prevInd.prevInd.prevInd
						.getPivotsList();

				if (_3ls.size() > 13 && _2ls.size() > 2) {
					Pivot _3rd_2ls = _2ls.get(_2ls.size() - 3);
					long timeToBreak2ls = _3rd_2ls.getPivotTime();
					long timeToBreak3 = Math.min(timeToBreak, timeToBreak2ls);

					// Ok, let's go backward
					ListIterator<Pivot> it = _3ls
							.listIterator(_3ls.size() - 12);
					while (it.hasPrevious()) {
						Pivot _3ls_pv = it.previous();
						if (_3ls_pv.getPivotTime() < fInd.getStartPoint()) {
							break;
						}

						if (_3ls_pv.getPivotTime() < timeToBreak3) {
							candidateStartPoint_rule3 = _3ls_pv.getPivotTime();
							break;
						}
					}

				}
			}
		}
		// maxPossibleStartPoint = Math.max(this.startPoint,
		// candidateStartPoint_rule3);

		maxPossibleStartPoint = Math.max(candidateStartPoint_rule1,
				candidateStartPoint_rule2);
		maxPossibleStartPoint = Math.max(maxPossibleStartPoint,
				candidateStartPoint_rule3);

		// The start point must be not greater than the last pivot
		if (forNegative) {
			Pivot p0ls = fInd.getPivotsList().get(
					fInd.getPivotsList().size() - 1);
			maxPossibleStartPoint = Math.min(maxPossibleStartPoint,
					p0ls.getPivotTime());
		}

		maxPossibleStartPoint = Math.max(maxPossibleStartPoint,
				fInd.getStartPoint());

		if (fInd.fDebugPrint)
			debug_var(259125, "max poss sp for level ", fInd.level + 1,
					" for negative? ", forNegative, " rule 1 ",
					candidateStartPoint_rule1, " rule 2 ",
					candidateStartPoint_rule2, " rule 3 ",
					candidateStartPoint_rule3, " returning ",
					maxPossibleStartPoint, " @ time ",
					fInd._compositeIndicator.lastTick.getFakeTime());

		assert (fInd.getStartPoint() <= maxPossibleStartPoint) : " what? I was @ "
				+ fInd.getStartPoint()
				+ " you want me to go to "
				+ maxPossibleStartPoint;

		return maxPossibleStartPoint;
	}

	public boolean hasNext() {
		if (fWindowReverted) {
			if (fNextTry != -1) {
				return true;
			}
			return false;
		}
		return fNextTry != -1;
	}

	public void init() {
		//
		fMaxForNegative = getMaxPossibleStartPoint(true);
		fMaxForPositive = getMaxPossibleStartPoint(false);
		assert (fMaxForPositive >= fMaxForNegative);

		/* The minimum try start point is the last pivot! */
		long pivotMinusOneTime = Long.MIN_VALUE;
		if (fInd.getPivotsList().size() > 1) {
			pivotMinusOneTime = fInd.getPivotsList().get(
					fInd.getPivotsList().size() - 2).fPivotTime;
		}

		fMin = (int) pivotMinusOneTime;
	}

	public int next() {
		if (fNextTry == -1) {
			throw new IllegalStateException();
		}

		int trysp = fNextTry;
		if (fWindowReverted) {
			fNextTry = -1;
		} else {
			// no window reverted
			if (fIt.hasNext()) {
				Pivot pv = fIt.next();
				if (pv.getPivotTime() > fCurMax && fAlreadyGivenMax) {
					fNextTry = -1;
				} else {
					fNextTry = pv.getPivotTime();
					if (fNextTry >= fCurMax) {
						fAlreadyGivenMax = true; // this to simulate the fact
													// that I give the max at
													// 2ls
						// even if it is not a pivot at 3ls.
						fNextTry = Math.min(fCurMax, fNextTry); // in any case
																// at most I
																// return the
																// max.
					}
					// if (fNextTry < trysp){
					// assert(false) : " next try " + fNextTry + " try sp " +
					// trysp;
					// }
					// assert(fNextTry > trysp);
				}
			} else {
				fNextTry = -1;
			}
		}
		return trysp;
	}
}
