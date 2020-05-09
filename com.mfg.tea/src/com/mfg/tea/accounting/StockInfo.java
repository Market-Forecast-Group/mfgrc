package com.mfg.tea.accounting;

/**
 * Every account leaf is tied to a stock information. This is in correspondence
 * with the "traded symbol" in {@link MultiTEA} language. But we may have a tree
 * of accounts of anything that can be traded.
 * 
 * <p>
 * The only constraint on the stock that can be tracked it is that it is
 * countable (no infinitely divisible items) in <b>units</b> or some kind and
 * that there is a predetermined <b>fixed</b> conversion between a unit of stock
 * and unit of currency.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class StockInfo {

	@Override
	public String toString() {
		return "[" + this.stockName + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (!(obj instanceof StockInfo)) {
			return false;
		}

		StockInfo that = (StockInfo) obj;

		if (that.tickSize != this.tickSize) {
			return false;
		}

		if (that.tickValue != this.tickValue) {
			return false;
		}

		if (!this.stockName.equals(this.stockName)) {
			return false;
		}

		return true;
	}

	/**
	 * Creates a stock info based on the information given.
	 * 
	 * <p>
	 * For example if I create a stock info with this constructor:
	 * {@code StockInfo si = new StockInfo("@ES", 25, 12_50);} it means that I
	 * create an instrument with a tick size of 25 and every tick values $12.50.
	 * 
	 * <p>
	 * This takes into account also batch size, for example I may have a stock
	 * {@code StockInfo si = new StockInfo("GOOG", 1, 100_00);}, meaning that I
	 * have a stock whose tick size is 1, but every tick values 100$, because I
	 * have to buy at least a certain amount of stocks, which is the minimum
	 * batch size.
	 * 
	 * <p>
	 * The prices here must always be a multiple of the tick size.
	 * 
	 * <p>
	 * The scale is implicitly considered. The scale of a currency is implicitly
	 * 2, because we have cents.
	 * 
	 * <p>
	 * The tick value is the value of the tick in cents, not the batch size,
	 * that is a different thing.
	 */
	public StockInfo(String aStockName, int aTickSize, int aTickValue) {
		stockName = aStockName;
		tickSize = aTickSize;
		tickValue = aTickValue;
	}

	/**
	 * Creates a stock information where the tick value is implicitly set to the
	 * tick size.
	 * 
	 * <p>
	 * In this way the batch size is 1. That is we are able to trade one unit of
	 * that stock.
	 * 
	 * @param aStockName
	 * @param aTickSize
	 */
	public StockInfo(String aStockName, int aTickSize) {
		stockName = aStockName;
		tickSize = aTickSize;
		tickValue = aTickSize;
	}

	public final String stockName;

	// /**
	// * The scale used to shift the money value from internal bookkeeping to
	// the
	// * externa view of "cents". This is used to continue to use integer
	// * arithmetic but to allow the management of mini and micro batch sizes of
	// * the commodity.
	// *
	// * <p>
	// * For the most part this is 1, but it can be positive or negative, this
	// * will be interpreted as 10 <sup>_scaleToMoney</sup> multiplied to the
	// * money number.
	// */
	// @SuppressWarnings("unused")
	// private int _scaleToMoney;

	/**
	 * How much cents a tick values?
	 * 
	 * 
	 * <p>
	 * This is a tricky question, because it depends on the nature of the
	 * commodity.
	 * 
	 * <p>
	 * There are "things" which are not things, like indeces which are bought
	 * and sold at "conventional" numbers which have a tick size and a scale,
	 * for example @ES has a tick size of 25 and a scale of 2.
	 * 
	 * <p>
	 * So when we "buy" a contract actually we are buying a number. To buy a
	 * number we have to give a certain amount of money depending on this
	 * number. That amount of money may be very low because we buy at margin...,
	 * but theoretically we should have that amount of money.
	 * 
	 * <p>
	 * This number stores the values (in cents or whatever the least unit of
	 * currency is used) of a <b>entire</b> ticksize movement in the price of
	 * the stock.
	 * 
	 * <p>
	 * That of course will depend also on the unit of the batch, the least
	 * amount of stock that it must be traded.
	 * 
	 */
	public final int tickValue;

	/**
	 * This is the overall present tick size, it is used to convert the price of
	 * the single transaction into a number of ticks. The scale is not used here
	 * because numbers are scaled only when they exit or enter the application.
	 * After that we have always integers.
	 */
	private final int tickSize;

	/**
	 * Converts the given number (which is can also be a price, or an index) to
	 * a price, using the tick size and the tick value.
	 * 
	 * @param aNumber
	 *            the number to be converted.
	 * @return the price, unscaled.
	 */
	public long convertToPrice(long aNumber) {
		return aNumber / tickSize * tickValue;
	}

	@Override
	public int hashCode() {
		return this.stockName.hashCode() + tickSize + tickValue;
	}

	/**
	 * @param aPrice
	 *            the price to be converted.
	 * @return the given price converted in points for this material
	 */
	public long convertToPoints(long aPrice) {
		return aPrice / tickValue * tickSize;
	}

	public int convertToTicks(long equity) {
		return (int) (equity / tickSize);
	}

	/**
	 * 
	 * 
	 * @param aPrice
	 *            the price to be checked against the tick size. The price has
	 *            to be an exact multiplier of the tick size.
	 */
	public boolean checkPriceCoherence(long aPrice) {
		if (aPrice % tickSize != 0) {
			return false;
		}
		return true;

	}

	public void ensurePriceCoherence(long aPrice) {
		if (!checkPriceCoherence(aPrice)) {
			throw new IllegalArgumentException("Price " + aPrice
					+ " is not coherent with " + this.tickSize);
		}

	}

}
