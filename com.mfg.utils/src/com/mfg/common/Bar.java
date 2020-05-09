/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 *
 * $Id: $
 */
/**
 * @author <a href="mailto:ramzy.arfawi@gmail.com">Ramzy ARFAWI</a>, MFG
 *
 * @version $Revision: $ $Date: $
 */

package com.mfg.common;

import static com.mfg.utils.FinancialMath.stringPriceToInt;

import java.text.ParseException;
import java.util.Date;

import com.ibm.icu.text.SimpleDateFormat;
import com.mfg.utils.U;

/**
 * A bar is a container of 4 prices and a volume. It is also called a Candle.
 * 
 * <p>
 * There are essentially two types of bars: the time bars and the range bars
 * (the volume bars are not used for now). The range bars have a distance fixed
 * between low and high. Time bars have a distance fixed from the start of the
 * bar and the start of the next bar (unless there is a close period in the
 * market).
 * 
 * @author Pasqualino
 * 
 */
public class Bar {

	/**
	 * the format used to serialize the bar to the stream.
	 */
	private static final SimpleDateFormat _barDateTimeFormat;

	static {
		_barDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss.SSS");
	}

	/**
	 * a simple helper method that builds the array which can be used to expand
	 * bars.
	 * 
	 * @return an array initialized to
	 */
	public static Tick[] getTickExpansionPlace() {
		Tick arr[] = new Tick[4];
		for (int i = 0; i < 4; ++i) {
			arr[i] = new Tick();
		}
		return arr;
	}

	/**
	 * does the inverse: it parses the string
	 * 
	 * @param aString
	 * @return
	 */
	public static Bar parseFromString(String aString) {
		String splits[] = U.commaPattern.split(aString);

		if (splits.length != 6) {
			throw new IllegalArgumentException("Cannot parse " + aString);
		}

		Bar bar = new Bar();

		try {
			synchronized (_barDateTimeFormat) {
				bar.time = _barDateTimeFormat.parse(splits[0]).getTime();
			}
		} catch (ParseException e) {
			throw new IllegalArgumentException("Cannot parse the date "
					+ splits[0]);
		}

		try {
			bar.open = Integer.parseInt(splits[1]);
			bar.high = Integer.parseInt(splits[2]);
			bar.low = Integer.parseInt(splits[3]);
			bar.close = Integer.parseInt(splits[4]);
			bar.volume = Integer.parseInt(splits[5]);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Cannot parse bar, reason "
					+ e.toString());
		}

		return bar;
	}

	// The time is expressed in UTC coordinates, the usual java time.
	long time;
	protected int open;
	protected int high;

	protected int low;

	protected int close;

	protected int volume;

	public Bar() {

	}

	public Bar(long time2, int open2, int high2, int low2, int close2,
			int aVolume) {
		this.time = time2;
		this.open = open2;
		this.high = high2;
		this.low = low2;
		this.close = close2;
		this.volume = aVolume;
	}

	/**
	 * builds a bar from an unparsed bar.
	 * 
	 * <p>
	 * The scale is an integer which says how many decimal points (at maximum)
	 * we have.
	 * 
	 * <p>
	 * The constructor will fail if the conversion is not possible, for example
	 * we have a string price "134.001" and we pass 2 as a scale (It is not
	 * possible without rounding, and rounding prices is not allowed).
	 * 
	 * @param ub
	 *            the unparsed bar
	 * 
	 * @param scale
	 *            the scale with which we want to parse it.
	 * @throws DFSException
	 */
	public Bar(UnparsedBar ub, int scale) throws DFSException {
		parse(ub, scale);
	}

	public void accumulateVolume(Tick aTick) {
		this.volume += aTick.getVolume();
	}

	public void adjustVolume(int vol) {
		volume = vol;
	}

	@Override
	public boolean equals(Object otherBar) {
		if (this == otherBar) {
			return true;
		}

		if (otherBar == null) {
			return false;
		}

		if (!(otherBar instanceof Bar)) {
			return false;
		}

		Bar second = (Bar) otherBar;

		if (this.time != second.time) {
			return false;
		}

		if (this.open != second.open) {
			return false;
		}

		if (this.low != second.low) {
			return false;
		}

		if (this.high != second.high) {
			return false;
		}

		if (this.close != second.close) {
			return false;
		}

		if (this.volume != second.volume) {
			return false;
		}

		return true;

	}

	/**
	 * expands the bar in place using the supplied array.
	 * <p>
	 * The array must be at least 4 Tick long, longer arrays are accepted,
	 * shorter arrays will throw an {@linkplain ArrayIndexOutOfBoundsException}.
	 * 
	 * <p>
	 * The prices are not filtered in any way, the bar is expanded as is, with
	 * the prices distribuited equally along the timespan provided.
	 * 
	 * <p>
	 * The time span is divided in 4 parts, and the last price will get the time
	 * of the bar, because we follow the convention that the time of the bar is
	 * the close.
	 * 
	 * @param arr
	 */
	public void expand(Tick arr[], long duration) {
		long durationFourth = duration / 4;
		int volumeFourth = volume / 4;

		long tempTime = this.time;

		// I go backward, because I have to subtract the time to the closing of
		// the bar
		arr[3].physicalTime = tempTime;
		arr[3].price = this.close;
		arr[3]._volume = volumeFourth;

		boolean up = this.close > this.open;

		tempTime -= durationFourth;
		arr[2].physicalTime = tempTime;
		arr[2].price = up ? this.high : this.low;
		arr[2]._volume = volumeFourth;

		tempTime -= durationFourth;
		arr[1].physicalTime = tempTime;
		arr[1].price = up ? this.low : this.high;
		arr[1]._volume = volumeFourth;

		tempTime -= durationFourth;
		arr[0].physicalTime = tempTime;
		arr[0].price = this.open;
		/*
		 * I add the modulus because in this way we have a perfect match.
		 */
		arr[0]._volume = volumeFourth + this.volume % 4;

	}

	public int getClose() {
		return close;
	}

	/**
	 * 
	 * @return
	 */
	public Date getEndDate() {
		return new Date(this.time);
	}

	public int getHigh() {
		return high;
	}

	public int getLow() {
		return low;
	}

	public int getOpen() {
		return open;
	}

	public long getTime() {
		return time;
	}

	public int getVolume() {
		return volume;
	}

	@Override
	public int hashCode() {
		return (int) (this.time + this.open + this.high + this.low + this.close + this.volume);
	}

	/**
	 * simple query function to know if this is a (one tick) range bar. Only one
	 * tick range is considered, not all the range bars are OK.
	 * 
	 * @param tick
	 *            the tick used to code this particular bar.
	 * 
	 * @return true if this bar is one tick range, false otherwise.
	 */
	public boolean isRangeBar(int tick) {

		if (high - low > tick) {
			return false;
		}

		if (Math.abs(open - high) > tick) {
			return false;
		}

		if (Math.abs(close - open) > tick) {
			return false;
		}

		return true;
	}

	/**
	 * This method is used to alter the prices.
	 * 
	 * <p>
	 * this method is really only useful in the continuous table.
	 * 
	 * @param offset
	 */
	public void offsetsPrices(int offset) {
		this.open += offset;
		this.close += offset;
		this.high += offset;
		this.low += offset;
	}

	/**
	 * tries to parse the unparsed bar given with the predetermined scale.
	 * 
	 * <p>
	 * the scale is not stored, it usually comes from the contract
	 * 
	 * @param ub
	 * @param scale
	 * @throws DFSException
	 */
	public void parse(UnparsedBar ubar, int scale) throws DFSException {
		this.time = ubar.start;

		open = stringPriceToInt(ubar.open_s, scale);
		high = stringPriceToInt(ubar.high_s, scale);
		low = stringPriceToInt(ubar.low_s, scale);
		close = stringPriceToInt(ubar.close_s, scale);

		volume = ubar.volume;
	}

	/**
	 * a simple serializator used to serialize the bar to a socket (or another
	 * text based medium)
	 * 
	 * @return the text serialization for this bar
	 */
	public String serialize() {
		StringBuilder sb = new StringBuilder();
		synchronized (_barDateTimeFormat) {
			sb.append(_barDateTimeFormat.format(new Date(this.time)));
		}
		sb.append(",").append(this.open);
		sb.append(",").append(this.high);
		sb.append(",").append(this.low);
		sb.append(",").append(this.close);
		sb.append(",").append(this.volume);
		return sb.toString();
	}

	public void setClose(final int close1) {
		this.close = close1;
	}

	public void setHigh(final int high1) {
		this.high = high1;
	}

	/**
	 * Sets the initial volume of the bar. That is a
	 * 
	 * @param aVolume
	 */
	public void setInitialVolume(int aVolume) {
		assert (volume == 0);
		this.volume = aVolume;
	}

	public void setLow(final int low1) {
		this.low = low1;
	}

	public void setOpen(final int open1) {
		this.open = open1;
	}

	public void setTime(final long l) {
		this.time = l;
	}

	@Override
	public String toString() {
		return "[" + new Date(time) + "," + this.open + "," + this.high + ","
				+ this.low + "," + this.close + " vol " + this.volume + "]";
	}

	public void accumulateVolume(int aVolume) {
		this.volume += aVolume;
	}
}
