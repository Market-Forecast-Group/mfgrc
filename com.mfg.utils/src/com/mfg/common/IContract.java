package com.mfg.common;

/**
 * A Contract is a way to identify a financial instrument in the system.
 * 
 * <p>
 * Different data providers (for example IB and eSignal) have different contract
 * implementations. This interface should be the least common multiple of the
 * information required by the instrument.
 * 
 * <p>
 * The interface is immutable (except from the tick part, which can be
 * automatically computed). This is by design; you could not change the object
 * in the interface because the object can be part of a data request.
 * 
 * @author Pasqualino
 * 
 */
public interface IContract {

	public String getSymbol();

	/**
	 * The local symbol is the string which is used to identify the symbol in
	 * the broker, usually it is a (short) string, less than 10 characters, with
	 * special symbols in it, like the pound, the percentage or the dollar.
	 * 
	 * Every broker, and every data provider has its own conventions to identify
	 * a contract.
	 * 
	 * @return the local symbol, with all the special characters, used to
	 *         identify the contract
	 */
	public String getLocalSymbol();

	public String getExpiry();

	public double getStrike();

	public String getCurrency();

	public String getExchange();

	public String getType();

	/**
	 * The id is a number which uniquely defines the contract in the market.
	 * 
	 * @return the contract identifier
	 */
	public int getId();

	public int getTickValue();

	/**
	 * @return the tick size
	 */
	public int getContractTick();

	/**
	 * @return The scale, that is the number of decimal places. 0 means that the
	 *         prices are integers
	 */
	public int getContractScale();

	public void setComputedValues(int computedTick, int computedScale);

	public int getManualTick();

	public int getManualScale();

	/**
	 * Stringify the price using the scale of this contract, for example if this
	 * contract has a scale of 2 and we give 12345, it will return the string
	 * "123.45"
	 * 
	 * @param price
	 *            the price to be converted
	 * @return the stringification of this price given the contract scale
	 */
	public String stringifyPrice(int price);

	/**
	 * this simply converts the price from string to long using the contract's
	 * tick and scale.
	 * 
	 * @param price
	 *            the price (as a string)
	 * 
	 * @return a long which is the (integer) equivalent of the price; for
	 *         example, if we have "103.25" with a scale of 2, the method will
	 *         return 10325
	 */
	public int parsePrice(String price);
}
