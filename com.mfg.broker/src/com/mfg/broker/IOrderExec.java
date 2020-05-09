package com.mfg.broker;

/**
 * This represent an object which has the information of an execution of an
 * order.
 */
public interface IOrderExec {

	/**
	 * @return the id of the order executed.
	 */
	public int getOrderId();

	/**
	 * @return the physical time in which the order has been executed.
	 */
	public long getExecutionTime();

	/**
	 * @return the execution price.
	 */
	public long getExecutionPrice();

}
