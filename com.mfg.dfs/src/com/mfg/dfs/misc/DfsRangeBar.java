package com.mfg.dfs.misc;

import java.util.Date;

import com.mfg.common.Bar;
import com.mfg.common.DFSException;
import com.mfg.common.RangeBar;
import com.mfg.common.Tick;
import com.mfg.common.UnparsedBar;
import com.mfg.dfs.serv.RangeBarsMDB;
import com.mfg.dfs.serv.RangeBarsMDB.RandomCursor;
import com.mfg.utils.MathUtils;

/**
 * The Dfs range bar is a range bar encoded in the database with a simple code.
 * 
 * <p>
 * There are 4 types of range bars: "up-down", "down-up", "down", "up".
 * 
 * <p>
 * Actually there can be a degenerate case in which all four prices are equal.
 * 
 * <p>
 * These four types of baras are stored in the database as is; the purpouse of
 * this class is to encode a particular bar in a type or, from the type, to
 * recreate the bar.
 * 
 * 
 * @author Sergio
 * 
 */
public class DfsRangeBar extends RangeBarsMDB.Record implements DfsBar {

	/**
	 * This enumeration lists the four possible states in the range bar.
	 * 
	 * <p>
	 * The values <b>must</b> be the same in the RANGETYPE table.
	 * 
	 * @author Sergio
	 * 
	 */
	enum EBarType {

		UP_DOWN(1), DOWN_UP(2), DOWN(3), UP(4), ALL_EQUAL(5);

		public final int _val;

		EBarType(int val) {
			_val = val;
		}

		public int getVal() {
			return _val;
		}
	}

	public DfsRangeBar() {
		// simple void constructor
	}

	/**
	 * This constructor is able to encode a <b>range</b> bar in a
	 * {@linkplain DfsRangeBar}.
	 * 
	 * 
	 * @param aBar
	 *            a bar to be encoded. The bar must be a range bar one tick
	 *            wide.
	 * 
	 * @param tick
	 *            the tick size, must be not negative.
	 */
	public DfsRangeBar(RangeBar aBar, int tick) {
		assert (aBar.isRangeBar(tick));

		int barOpen = aBar.getOpen();
		this.open = barOpen; // this is always true.
		this.timeStamp = aBar.getTime();

		this.volFirst = aBar.getFirstVolume();
		this.volSecond = aBar.getSecondVolume();

		if (aBar.getClose() == aBar.getOpen()) {
			// this is a up/down or down/up bar, or maybe a bar with all 4
			// prices equal
			if (aBar.getHigh() == aBar.getLow()) {
				assert (aBar.getOpen() == aBar.getLow());
				this.type = (byte) EBarType.ALL_EQUAL.getVal();
			} else if (aBar.getHigh() != aBar.getOpen()) {
				// this is a up/down bar
				this.type = (byte) EBarType.UP_DOWN.getVal();
			} else {
				// this is a down/up bar
				this.type = (byte) EBarType.DOWN_UP.getVal();
			}
		} else {
			if (aBar.getClose() > aBar.getOpen()) {
				// this is an up bar
				this.type = (byte) EBarType.UP.getVal();
			} else {
				this.type = (byte) EBarType.DOWN.getVal();
			}
		}

	}

	// copy constructor from a Cursor
	public DfsRangeBar(RandomCursor aCursor) {
		this.timeStamp = aCursor.timeStamp;
		this.open = aCursor.open;
		this.type = aCursor.type;
		this.volFirst = aCursor.volFirst;
		this.volSecond = aCursor.volSecond;
		this.volume = aCursor.volume;
	}

	public DfsRangeBar(UnparsedBar ub, int scale) throws DFSException {
		this.open = MathUtils.longToIntSafe(Tick.stringToLongPrice(ub.open_s,
				scale));
		this.timeStamp = ub.start;

		long close = Tick.stringToLongPrice(ub.close_s, scale);
		long high = Tick.stringToLongPrice(ub.high_s, scale);
		long low = Tick.stringToLongPrice(ub.low_s, scale);

		if (close == open) {
			// this is a up/down or down/up bar, or maybe a bar with all 4
			// prices equal
			if (high == low) {
				assert (open == low);
				this.type = (byte) EBarType.ALL_EQUAL.getVal();
			} else if (high != open) {
				// this is a up/down bar
				this.type = (byte) EBarType.UP_DOWN.getVal();
			} else {
				// this is a down/up bar
				this.type = (byte) EBarType.DOWN_UP.getVal();
			}
		} else {
			if (close > open) {
				// this is an up bar, a "green" bar in eSignal
				this.type = (byte) EBarType.UP.getVal();
			} else {
				this.type = (byte) EBarType.DOWN.getVal();
			}
		}
	}

	/**
	 * This constructor is used by the import csv file, but now it is
	 * unsupported because the range bar has two volumes, where the bar in the
	 * csv file has only one volume, so we may drop the support for the import
	 * of range files.
	 * 
	 * @param aBar
	 * @param computed_tick_size
	 */
	public DfsRangeBar(Bar aBar, int computed_tick_size) {
		throw new UnsupportedOperationException();
	}

	/**
	 * decodes this bar in another overwriting the values.
	 * 
	 * @param aBar
	 * @throws DFSException
	 */
	public void decodeInPlace(RangeBar aBar, int tick) throws DFSException {
		aBar.setOpen(this.open);
		aBar.setTime(this.timeStamp);
		aBar.setInitialVolume(this.volFirst);
		aBar.setSecondaryVolume(this.volSecond);

		switch (this.type) {
		case 2:
			aBar.setLow(open - tick);
			aBar.setHigh(open);
			aBar.setClose(open);
			break;
		case 1:
			aBar.setLow(open);
			aBar.setHigh(open + tick);
			aBar.setClose(open);
			break;
		case 4:
			aBar.setLow(open);
			aBar.setHigh(open + tick);
			aBar.setClose(open + tick);
			break;
		case 3:
			aBar.setLow(open - tick);
			aBar.setHigh(open);
			aBar.setClose(open - tick);
			break;
		case 5:
			// all equal...
			aBar.setLow(open);
			aBar.setHigh(open);
			aBar.setClose(open);
			break;
		default:
			throw new DFSException("whaaaaaaat? Unkown type in db " + this.type);
		}

		if (type != DfsRangeBar.EBarType.ALL_EQUAL.getVal()) {
			assert (aBar.isRangeBar(tick)) : " is not a range bar " + tick
					+ " bar " + aBar;
		}

	}

	/**
	 * decodes this bar into a normal range bar; it needs the tick size.
	 * 
	 * @return a new bar
	 * @throws DFSException
	 */
	@Override
	public Bar decodeTo(int tick) throws DFSException {
		RangeBar bar = new RangeBar();
		this.decodeInPlace(bar, tick);
		return bar;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (!(obj instanceof DfsRangeBar)) {
			return false;
		}

		DfsRangeBar other = (DfsRangeBar) obj;

		if (this.timeStamp != other.timeStamp) {
			return false;
		}

		if (this.open != other.open) {
			return false;
		}

		if (this.type != other.type) {
			return false;
		}

		// all equal, true
		return true;
	}

	@Override
	public boolean equalsNoTime(DfsBar other) {
		if (!(other instanceof DfsRangeBar)) {
			throw new IllegalArgumentException();
		}

		DfsRangeBar otherR = (DfsRangeBar) other;

		if (this.open != otherR.open) {
			return false;
		}

		if (this.type != otherR.type) {
			return false;
		}

		// all equal, true
		return true;
	}

	@Override
	public long getPrimaryKey() {
		return this.timeStamp;
	}

	@Override
	public long getSignature() {
		return this.timeStamp + this.open + this.type;
	}

	@Override
	public int hashCode() {
		return (int) (timeStamp + open + type);
	}

	@Override
	public void offsetPrimaryKey(long offset) {
		this.timeStamp += offset;
	}

	@Override
	public String toString() {
		return "[(DfsRangeBar)," + new Date(this.timeStamp) + "," + this.open
				+ "," + this.type + "]";
	}

}