package com.mfg.tea.accounting;

import com.mfg.tea.conn.IAccountStatisticsMoney;

public interface IBasicInventory {

	/**
	 * Returns the total statistics in this inventory. If you want the
	 * statistics for a given symbol you have another method.
	 * 
	 * @return the total (mixed) stats for this inventory.
	 */
	public IAccountStatisticsMoney getStats();

	/**
	 * 
	 * @return the total cost of this inventory, in money, this is the sum of
	 *         the costs for all the inventories in a case of a mixed folder. As
	 *         it is in money I can add it all.
	 *         <p>
	 *         In accounting terms this is equal to the <b>historical</b> asset
	 *         value, without taking into account the current market price.
	 */
	public long getTotalCost();

	/**
	 * Gets the average cost for this inventory, that is the market price for
	 * which the {@link #getUnrealizedPL(int)} would return zero.
	 * 
	 * <p>
	 * Of course this price is defined only if the quantity is different from
	 * zero, otherwise it is undetermined. In that case it will return
	 * {@link Double#NaN}
	 * 
	 * @return the average cost for which the Unrealized PL is zero.
	 */
	public double getAverageCost();

	/**
	 * 
	 * @return the total <b>value</b> of this inventory based on the last
	 *         received market price. If this inventory is void then the
	 *         unrealized PL is zero.
	 */
	public long getUnrealizedPL();

	/**
	 * The same as {@link #getUnrealizedPL()} but with a price given from
	 * outside.
	 * 
	 * <p>
	 * This is a theoretical gain as it does not take into account slippage,
	 * commissions, etc.
	 * 
	 * <p>
	 * Of course if the quantity held is zero, the unrealized P/L is zero for
	 * whatever price.
	 * 
	 * @param aPrice
	 *            the price given by the outside.
	 * @return the profit (or loss) that the user would have if he/she closed
	 *         all the positions (long and short) of this inventory.
	 */
	public long getUnrealizedPL(int aPrice);
}
