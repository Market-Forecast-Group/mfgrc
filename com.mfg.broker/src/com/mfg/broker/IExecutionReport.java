package com.mfg.broker;

/**
 * This class is used to communicate an execution report to the patterns from
 * the outside world. The outside world could be real or simulated, but the
 * patterns are not aware of that.
 * 
 * This interface was called ISimulatedOrder once ago, now it is renamed because
 * this data is used also in real time.
 */
public interface IExecutionReport {

	/**
	 * This returns the order which was executed. For now the simulation does
	 * not simulate a partial fill. So the order is executed always in full.
	 */
	public IOrderMfg getOrder();

	/**
	 * This is the execution price. This is the last execution price, not the
	 * averaged.
	 */
	public long getExecutionPrice();

	/**
	 * This is the execution time, it is always a physical time.
	 */
	public long getExecutionTime();

	/**
	 * A short hand form for getOrder().getQuantity();
	 * 
	 * @return the quantity executed. For a sell order this quantity is
	 *         negative.
	 */
	public int getQuantity();

	/**
	 * @return the order id
	 */
	public int getOrderId();

	/**
	 * @return true if this has been a long position
	 */
	public boolean isLongPosition();

	/**
	 * @return true if this order has been executed
	 */
	// public boolean areYouExecuted();

	/**
	 * This method will return the parent execution report, if it exists, null
	 * otherwise.
	 * 
	 * @return the parent execution report, if this is a child, null if this is
	 *         the report of a parent order.
	 */
	public IExecutionReport getParentExecutionReport();

	/**
	 * This method is only valid for child execution reports.
	 * 
	 * @return true if this trade has closed in gain. It does not return the
	 *         size of the gain.
	 */
	public boolean isClosingInGain();

	public long getPhysicalExecutionTime();
}
