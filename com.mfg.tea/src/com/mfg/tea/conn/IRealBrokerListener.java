package com.mfg.tea.conn;

import com.mfg.broker.IOrderMfg;
import com.mfg.broker.OrderStatus;
import com.mfg.broker.orders.OrderExecImpl;

/**
 * This interface is the base for all the real broker listeners, in fact only
 * {@link MultiTEA}.
 * 
 * <p>
 * For now we simply copy the methods from the {@link IVirtualBrokerListener}
 * interface, changing the name to avoid confusion.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
interface IRealBrokerListener {

	/**
	 * gets a new order status.
	 * 
	 * @param aOrderId
	 *            the order id associated to this order. This is the same id
	 *            which has been returned by the
	 *            {@link IVirtualBroker#placeOrder(TEAOrder)} method.
	 * 
	 * @param aStatus
	 */
	void orderStatusRb(int aOrderId, OrderStatus aStatus);

	/**
	 * receives a notification about an order executed in the broker.
	 * 
	 * <p>
	 * The entire order is not repeated here, only its internal id (the one of
	 * {@link IOrderMfg} interface).
	 * 
	 * <p>
	 * This interface is <b>not</b> remotable. It is only inside TEA.
	 * 
	 * @param aOrderId
	 *            the broker's id as returned by the
	 *            {@link IVirtualBroker#placeOrder(TEAOrder)} method.
	 */
	void newExecutionRb(int aOrderId, OrderExecImpl anExec);
}
