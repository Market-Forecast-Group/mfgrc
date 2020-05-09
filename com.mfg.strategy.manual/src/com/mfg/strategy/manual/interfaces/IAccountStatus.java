package com.mfg.strategy.manual.interfaces;

/**
 * To provide the status of the Short and Long accounts.
 * 
 * @author arian
 * 
 */
public interface IAccountStatus {
	/**
	 * The Q of the Short account
	 * 
	 * @return
	 */
	public long getShortQuantity();

	/**
	 * The Q of the Long account
	 * 
	 * @return
	 */
	public long getLongQuantity();

	/**
	 * If has any pending order (limit order) for the short account.
	 * 
	 * @return
	 */
	public boolean hasShortPendingOrders();

	/**
	 * If has any pending order (limit order) for the long account.
	 * 
	 * @return
	 */
	public boolean hasLongPendingOrders();
}
