package com.mfg.common;

/**
 * the base class for all the symbols in mfg.
 * 
 * <p>
 * At the present moment we have two different symbols: the csv symbol and the
 * dfs symbol.
 * 
 * <p>
 * This because the application is using two different data providers, at least
 * in this current implementation.
 * <p>
 * The difference between a csv symbol and the dfs symbol is that the csv symbol
 * has the tick and scale which are computed from the outside
 * 
 * @author Sergio
 * 
 */
public abstract class MfgSymbol {

	/**
	 * 
	 * @return the tick size, that is the minimum delta of a price movement.
	 * 
	 */
	public abstract int getTick();

	/**
	 * 
	 * @return the scale for the symbol, that is how many decimal places are
	 *         used. Symbols use fix point arithmetic.
	 */
	public abstract int getScale();

	public abstract String getSymbol();

	/**
	 * returns the tick value, in cents, of a single tick movement of the
	 * symbol. It is supposed here that a tick movement will have an integer
	 * number of cents of value. If this is not so then we would also have a
	 * scale for the tick value, not only for the symbol.
	 * 
	 * <p>
	 * The implicit scale for the tick value is 2, that is we have cents.
	 * 
	 * @return the tick value in cents.
	 */
	public abstract int getTickValue();
}
