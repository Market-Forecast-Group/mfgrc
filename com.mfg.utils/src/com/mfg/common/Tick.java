package com.mfg.common;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.mfg.utils.FinancialMath;
import com.mfg.utils.U;

/**
 * The Tick class is the basic information about an instrument. It associates a
 * price and a time together.
 * 
 * <p>
 * The time is the "normal" java time in milliseconds from 1970. Usually this
 * time is then represented in the local time zone.
 * 
 * <p>
 * The price is an integer. With an integer we can represent prices with 9
 * significant digits, from 0 to 999_999_999, which is sufficient for all
 * <b>single</b> prices, that is a price of a single unit of something (stock,
 * contract, etc...).
 * 
 * <p>
 * Of course the price of a multiple quantity can be greater than an int size.
 * 
 * @author Pasqualino
 * 
 * @since 1.0
 * 
 */
public class Tick implements Cloneable, Serializable {

	/**
     * 
     */
	private static final long serialVersionUID = -856917840168209727L;

	/**
	 * Utility function to convert a string price in the corresponding long
	 * 
	 * @param price
	 *            the price in the form of a string, for example "145.25"
	 * @param scale
	 *            the scale, for example 2
	 * @return The price converted, in this case the integer 14525, as a java
	 *         long, -1 in case of error (for example converting 145.25 with a
	 *         scale of 0 means rounding is necessary).
	 * @throws DFSException
	 */
	public static long stringToLongPrice(final String price_s, final int scale)
			throws DFSException {
		final BigDecimal bd = new BigDecimal(price_s);

		long price = -1;
		try {
			price = bd.setScale(scale).unscaledValue().longValue();
		} catch (final ArithmeticException e) {
			U.debug_var(390192, "exception price " + price_s + " scale "
					+ scale);
			throw new DFSException(e);
		}
		return price;
	}

	protected long physicalTime;

	protected int price;

	/**
	 * Every tick has associated a volume, which is the size of the trade at
	 * this particular point in time.
	 */
	protected int _volume;

	public Tick() {
		_volume = 0;
	}

	public Tick(DFSQuote quote) {
		this.physicalTime = quote.tick.physicalTime;
		this.price = quote.tick.getPrice();
		this._volume = quote.tick._volume;
	}

	/**
	 * Useful constructor with the price already parsed.
	 * 
	 * @param physicalTyme
	 *            The time of the tick
	 * @param price1
	 *            The price of the tick
	 */
	public Tick(final long physicalTyme, final int price1) {
		this.physicalTime = physicalTyme;
		this.price = price1;
		_volume = 0;
	}

	/**
	 * Builds a tick from a unparsed price.
	 * 
	 * <p>
	 * This constructor will make sure that the price can be converted in an
	 * integer without rounding, with the scale given.
	 * 
	 * @param date
	 *            the date, the usual milliseconds from 1970.
	 * @param scale
	 *            the integer representing the scale, for example 2
	 * @param priceS
	 *            the unparsed price, for example "1924.25"
	 * @throws DFSException
	 *             if the price cannot be parsed without rounding in the scale
	 *             given.
	 */
	public Tick(final long date, final int scale, final String priceS,
			int aVolume) throws DFSException {
		this(date, FinancialMath.stringPriceToInt(priceS, scale), aVolume);
	}

	public Tick(long physicalTime2, int price2, int volume) {
		physicalTime = physicalTime2;
		price = price2;
		_volume = volume;
	}

	public int accumulateVolume(int aVolume) {
		_volume += aVolume;
		return _volume;
	}

	@Override
	public Tick clone() {
		try {
			return (Tick) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * simple field by field comparison.
	 * 
	 * @param obj
	 * @return
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (!(obj instanceof Tick)) {
			return false;
		}

		Tick other = (Tick) obj;
		if (other.physicalTime != this.physicalTime) {
			return false;
		}

		if (other.price != this.price) {
			return false;
		}

		if (other._volume != this._volume) {
			return false;
		}

		return true; // all ok.
	}

	/**
	 * returns true if this tick has the same price as the other, the times are
	 * not compared.
	 * 
	 * @param other
	 *            the other tick
	 * @return
	 */
	public boolean equalsPrice(Tick other) {
		return price == other.price;
	}

	/**
	 * Helper function to get the exact absolute distance in ticks from another
	 * tick.
	 * 
	 * @param aTick
	 * @param tick
	 * @return the exact distance, if the prices are not a multiple an exception
	 *         is thrown
	 * @throws IllegalArgumentException
	 *             if the ticks are not comparable given the current tick
	 * 
	 */
	public int getExactDeltaTicksFrom(Tick aTick, int tick) {
		return FinancialMath.getExactDeltaTicks(price, aTick.price, tick);
	}

	public long getPhysicalTime() {
		return physicalTime;
	}

	public int getPrice() {
		return price;
	}

	public int getVolume() {
		return _volume;
	}

	/**
	 * returns true if this tick is after the other tick
	 * 
	 * @param aTick
	 *            the tick which you want to check
	 * @return true if this tick is after (in time) to the other tick
	 */
	public boolean happensAfter(Tick aTick) {
		return this.physicalTime > aTick.getPhysicalTime();
	}

	@Override
	public int hashCode() {
		return (int) (physicalTime + price);
	}

	/**
	 * Offsets the price by a certain amount.
	 * 
	 * @param offset
	 */
	public void offsetPrice(int offset) {
		price += offset;
	}

	public void setPhysicalTime(long physicalTime1) {
		this.physicalTime = physicalTime1;
	}

	public void setPrice(int price2) {
		this.price = price2;
	}

	public void setVolume(int aVolume) {
		_volume = aVolume;
	}

	@Override
	public String toString() {
		return "Tick [t " + new Date(getPhysicalTime()) + ", tRAW "
				+ physicalTime + " p " + getPrice() + " v " + _volume + "]";
	}
}
