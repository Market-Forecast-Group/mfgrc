package com.mfg.dfs.misc;

import java.util.Date;

import com.mfg.common.Bar;
import com.mfg.common.DFSException;
import com.mfg.common.Tick;
import com.mfg.common.UnparsedBar;
import com.mfg.dfs.serv.TimeBarsMDB;
import com.mfg.dfs.serv.TimeBarsMDB.RandomCursor;
import com.mfg.utils.MathUtils;

/**
 * This is a special class which is used to store a time bar in a more space
 * efficient manner.
 * <p>
 * The data is stored in this layout
 * 
 * <p>
 * timestamp: the usual start date of the bar, a millisecond long
 * 
 * <p>
 * low: the low value of the bar
 * 
 * <p>
 * to_open: the difference between open and low (open-low)
 * 
 * <p>
 * to_high: the difference between high and low (high-low)
 * 
 * <p>
 * to_close: the difference between close and low (close - low)
 * 
 * <p>
 * volume: the volume of the bar
 * 
 * <p>
 * Storing the differences instead of the real prices saves some bytes. Probably
 * storing tick differences instead of real prices can save a bit more (but need
 * to think about that)
 * 
 * @author Sergio
 * 
 */
public class DfsTimeBar extends TimeBarsMDB.Record implements DfsBar {

	@Override
	public String toString() {
		return "[(DfsTimeBar)," + new Date(this.timestamp) + "," + this.low
				+ "," + this.to_open + "," + this.to_high + "," + this.to_close
				+ "," + this.volume + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (!(obj instanceof DfsTimeBar)) {
			return false;
		}

		DfsTimeBar other = (DfsTimeBar) obj;

		if (this.timestamp != other.timestamp) {
			return false;
		}

		if (this.low != other.low) {
			return false;
		}

		if (this.to_open != other.to_open) {
			return false;
		}

		if (this.to_close != other.to_close) {
			return false;
		}

		if (this.to_high != other.to_high) {
			return false;
		}

		if (this.volume != other.volume) {
			return false;
		}

		return true;
	}

	/**
	 * I can build a time bar from a simple bar
	 * 
	 * @param aBar
	 */
	public DfsTimeBar(Bar aBar, int tick) {

		this.timestamp = aBar.getTime();

		this.low = aBar.getLow();
		this.to_open = _safePositiveDistanceInTicks(this.low, aBar.getOpen(),
				tick);
		this.to_high = _safePositiveDistanceInTicks(this.low, aBar.getHigh(),
				tick);
		this.to_close = _safePositiveDistanceInTicks(this.low, aBar.getClose(),
				tick);
		// this.to_open = MathUtils.intToShortSafe((aBar.getOpen() - this.low)
		// / tick);
		// this.to_high = MathUtils.intToShortSafe((aBar.getHigh() - this.low)
		// / tick);
		// this.to_close = MathUtils.intToShortSafe((aBar.getClose() - this.low)
		// / tick);
		this.volume = aBar.getVolume();

	}

	/**
	 * This important method is used to convert a price of a bar to a distance
	 * (measured with a short), between the low and this price.
	 * <p>
	 * The distance is divided by the tick size, for example the distance
	 * between 50 and 100 with a tick size of 25 is 2.
	 * <p>
	 * The method does all the necessary checks to ensure that the result is
	 * meaningful and it will throw otherwise. In particular it ensures that the
	 * distance is positive.
	 * 
	 * @param priceFrom
	 *            the base price, it MUST be less or equal to the priceTo
	 * @param priceTo
	 *            the price to convert (it will be usually the open, close or
	 *            high of a bar)
	 * @param tick
	 *            the tick size
	 * @return the distance between priceFrom and priceTo measured in ticks
	 * 
	 * @throws ArithmeticException
	 *             if the conversion cannot be done
	 */
	@SuppressWarnings("static-method")
	private short _safePositiveDistanceInTicks(int priceFrom, int priceTo,
			int tick) {
		if (priceFrom > priceTo) {
			throw new ArithmeticException();
		}

		int delta = priceTo - priceFrom;
		if (delta % tick != 0) {
			throw new ArithmeticException("not an exact multiple of tick "
					+ delta + " tick " + tick);
		}

		delta /= tick;

		return MathUtils.intToShortSafe(delta);
	}

	/**
	 * builds a Time bar from an unparsed bar.
	 * 
	 * <p>
	 * The dfstime bar is built with a own logic, that is the prices are encoded
	 * with the difference from the low price.
	 * 
	 * <p>
	 * The param isLenient is used to control the permissivity of the parsing.
	 * For example iq Feed will give to us bars in which, sometimes, the close
	 * is outside the high/low range.
	 * 
	 * <p>
	 * This is called "settlement", but we don't use it, so we adjust the
	 * high/low range to accomodate for this close.
	 * 
	 * @param ub
	 * @param scale
	 * @param isLenient
	 *            if true the parsing will not complain if the close is outside
	 *            the high/low range.
	 * @throws DFSException
	 */
	public DfsTimeBar(UnparsedBar ub, int scale, int tick, boolean isLenient)
			throws DFSException {
		this.timestamp = ub.start;

		this.low = MathUtils.longToIntSafe(Tick.stringToLongPrice(ub.low_s,
				scale));
		this.to_open = MathUtils.longToShortSafe(MathUtils.safeIntDivision(
				Tick.stringToLongPrice(ub.open_s, scale) - low, tick));
		this.to_high = MathUtils.longToShortSafe(MathUtils.safeIntDivision(
				Tick.stringToLongPrice(ub.high_s, scale) - low, tick));
		this.to_close = MathUtils.longToShortSafe(MathUtils.safeIntDivision(
				Tick.stringToLongPrice(ub.close_s, scale) - low, tick));

		if (this.to_close < 0 || (this.to_close > this.to_high)) {
			if (!isLenient) {
				throw new IllegalArgumentException(
						"close outside high/low range");
			}
			// I can simply update the high low range
			if (to_close < 0) {
				low += to_close; // close is negative so low gets lower.
				to_high -= to_close;
				to_open -= to_close;
				to_close = 0;
			} else {
				// I have simply to update the high, but low remains the same
				to_high = to_close;
			}
		}

		this.volume = ub.volume;
	}

	public DfsTimeBar(RandomCursor aCursor) {
		this.timestamp = aCursor.timestamp;
		this.low = aCursor.low;
		this.to_open = aCursor.to_open;
		this.to_close = aCursor.to_close;
		this.to_high = aCursor.to_high;
		this.volume = aCursor.volume;
	}

	@Override
	public Bar decodeTo(int tick) {
		Bar aBar = new Bar();
		this.decodeInPlace(aBar, tick);
		return aBar;
	}

	/**
	 * decodes the time bar in place.
	 * 
	 * <p>
	 * This means to take the record in the MDB file and converting it to the
	 * format the application needs.
	 * 
	 * @param aBar
	 *            the bar to overwrite.
	 * @param tick
	 *            The tick used to decode the bar
	 */
	public void decodeInPlace(Bar aBar, int tick) {
		aBar.setTime(this.timestamp);

		aBar.setLow(this.low);
		aBar.setOpen(this.low + (this.to_open * tick));
		aBar.setHigh(this.low + (this.to_high * tick));
		aBar.setClose(this.low + (this.to_close * tick));

		aBar.setInitialVolume(this.volume);
	}

	@Override
	public long getSignature() {
		return this.timestamp + this.low + this.to_high + this.to_open
				+ this.to_close + this.volume;
	}

	@Override
	public long getPrimaryKey() {
		return this.timestamp;
	}

	@Override
	public void offsetPrimaryKey(long offset) {
		this.timestamp += offset;
	}

	@Override
	public int hashCode() {
		return (int) (this.timestamp + this.low + this.to_high + this.to_close
				+ this.to_open + this.volume);
	}

	@Override
	public boolean equalsNoTime(DfsBar checkBar) {
		throw new UnsupportedOperationException(); // not here.
	}

}
