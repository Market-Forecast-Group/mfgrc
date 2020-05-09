package com.mfg.broker;

import com.mfg.broker.IMarketSimulatorListener.EOrderStatus;

/**
 * A collection of properties of an order sent to the broker.
 * 
 * <p>
 * This collection is in some way related to {@linkplain IOrderExec} but it is
 * stored in the broker.
 * 
 * @author Sergio
 * 
 */
public interface IOrderStatus {

	/**
	 * returns the order id (internal) of this order. This method is equal to
	 * <code>this.getOrder().getId()</code>. That is only a shorthand to avoid
	 * calling {@linkplain IOrderMfg#getId()}.
	 * 
	 */
	int getOrderId();

	/**
	 * returns the status of this order.
	 * 
	 * @return the status of this order as an enumeration.
	 */
	EOrderStatus getStatus();

	// int getFilled();
	//
	// int getRemaining();
	//
	// double getAverageFillPrice();
	//
	// int getParentOrderId();
	//
	// long getLastFilledPrice();
	//
	// long getLastExecutionTime();

	boolean isTotallyExecuted();

}
