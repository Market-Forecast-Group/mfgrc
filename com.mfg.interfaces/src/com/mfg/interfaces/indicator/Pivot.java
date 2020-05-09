package com.mfg.interfaces.indicator;

import static com.mfg.utils.Utils.debug_var;

import java.io.Serializable;

import com.mfg.common.QueueTick;

/**
 * This is the class which simply models a pivot, that is a price which is a
 * local maximum or minimum for a series.
 * 
 * The pivot has a time and a state attached, because I can confirm a pivot
 * later in time
 * 
 * This class is serializable, I can store a vector of pivots on disk for quick
 * access purposes.
 */
public class Pivot implements Serializable, Cloneable {

	private static final long serialVersionUID = -7384199304252652919L;

	public final int fPivotPrice;

	public final int fPivotTime;

	public final int fConfirmPrice;

	public final int fConfirmTime;

	public final int fLinearSwing;

	public final int fTimeInterval;

	public final int fLevel;

	/**
	 * The level is zero based in the constructor.
	 * 
	 * @param lastPv
	 */
	public Pivot(final QueueTick qt, final int aLevel, final QueueTick confirm,
			Pivot lastPv) {
		fPivotTime = qt.getFakeTime();
		fPivotPrice = qt.getPrice();

		fConfirmPrice = confirm.getPrice();
		fConfirmTime = confirm.getFakeTime();

		if (lastPv == null) {
			this.fLinearSwing = Integer.MAX_VALUE;
			this.fTimeInterval = Integer.MAX_VALUE;
		} else {
			this.fLinearSwing = Math.abs(this.fPivotPrice - lastPv.fPivotPrice);
			this.fTimeInterval = Math.abs(this.fPivotTime - lastPv.fPivotTime);
		}

		this.fLevel = aLevel + 1;
		assert (isPivotConsistent(lastPv)) : " Pivot inconsistent time "
				+ this.fPivotTime + " confirm time " + this.fConfirmTime
				+ " pv " + this + " \n LAST PIVOT " + lastPv;
	}

	@Override
	public Pivot clone() {
		try {
			final Pivot pv = (Pivot) super.clone();
			return pv;
		} catch (final CloneNotSupportedException e) {
			return null;
		}
	}

	public double getConfirmPrice() {
		return fConfirmPrice;
	}

	public long getConfirmTime() {
		return fConfirmTime;
	}

	public int getLevel() {
		return fLevel;
	}

	public int getLinearSwing() {
		return fLinearSwing;
	}

	public int getPivotPrice() {
		return fPivotPrice;
	}

	public int getPivotTime() {
		return fPivotTime;
	}

	public long getPrice() {
		return getPivotPrice();
	}

	/**
	 * The IPivotChan interface
	 */
	public int getTime() {
		return getPivotTime();
	}

	public int getTimeInterval() {
		return fTimeInterval;
	}

	/**
	 * 
	 * @return true is the pivot is consistent.
	 */
	@SuppressWarnings("boxing")
	private boolean isPivotConsistent(Pivot lastPv) {
		if (this.fPivotTime > this.fConfirmTime) {
			debug_var(329235, "pivot time ", fPivotTime, " > confirm time ",
					fConfirmTime);
			return false;
		}

		if (this.fLinearSwing == 0) {
			debug_var(393592, "inconsistent> linear swing 0");
			return false;
		}

		if (this.fTimeInterval == 0) {
			debug_var(342123, "time interval zero");
			return false;
		}

		if (lastPv != null) {
			if ((fConfirmPrice == fPivotPrice)
					|| (lastPv.fConfirmPrice == lastPv.fPivotPrice)) {
				debug_var(939295, "Pivot at same level confirm");
				return true;
			}
			if (!(isStartingDownSwing() ^ lastPv.isStartingDownSwing())) {
				debug_var(919353, "this pv up? ", this.isStartingDownSwing(),
						" last up? ", lastPv.isStartingDownSwing());
				return false;
			}
		}

		return true;
	}

	/**
	 * 
	 * @return true if this pivot is a (negative) pivot that starts a down
	 *         swing. This is always true if the confirm price is less than the
	 *         confirm price.
	 */
	public boolean isStartingDownSwing() {
		return fConfirmPrice < fPivotPrice;
	}

	/**
	 * @deprecated this method is not very clear. Use the
	 *             <code>isStartingDownSwing</code> instead.
	 * 
	 * @return true if this pivot is starting a down swing.
	 */
	@Deprecated
	public boolean isUp() {
		return isStartingDownSwing();
	}

	@Override
	public String toString() {
		return "lev " + this.fLevel + " Pv price " + fPivotPrice + " time "
				+ fPivotTime + " linear sw. " + fLinearSwing + " time iv "
				+ fTimeInterval + " confirmT " + this.fConfirmTime;
	}
}
