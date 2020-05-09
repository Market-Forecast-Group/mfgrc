package com.mfg.broker;

import com.mfg.broker.orders.OrderExecImpl;

/**
 * The IBroker callback defines the methods that the broker will invoke when
 * certain asynchronous events happen in the market. The methods usually are
 * called from a <b>different</b> thread.
 * 
 * The thread that does the request should not expect that the error will be
 * given in the same thread, as the methods in the {@linkplain IBroker}
 * interface are not blocking.
 * 
 * Different brokers behave differently, at the moment we have Interactive
 * Brokers and IWBank, they behave differently, they have different interfaces.
 * In this interface care has been taken to ensure that there is a common
 * interface for the two brokers.
 * 
 * @author Sergio
 */
public interface IMarketSimulatorListener {

	/**
	 * The order status is a super set of all the order status of the various
	 * broker defined in the system. Some statuses are coincident, and others
	 * are only of a broker. For every state there is written if the status is
	 * sent by IB or by IWbank or both.
	 * 
	 * Not all the broker statuses are mapped in the {@linkplain EOrderStatus}
	 * enumeration, this because probably most of them are not useful.
	 * 
	 * @author Sergio
	 * 
	 */
	public enum EOrderStatus {
		/**
		 * This is the status of the order which is still in the app but not
		 * sent to the broker yet. As soon as the order enters the broker it has
		 * this state. After that the order will undergo some other states.
		 * Every state can end in a ERROR state, from which is won't recover.
		 */
		STILL_IN_APP,
		/**
		 * This is the status of a child object that is waiting for the parent
		 * to be executed. All the children orders starts at this state. In a
		 * certain sense they live in this limbo until the parent is executed
		 * and at that moment they live as normal orders until their execution.
		 * The only difference is that the children orders are usually connected
		 * in pairs and the execution of one directly cancels the other.
		 */
		WAITING_FOR_PARENT_EXECUTION,

		/**
		 * The order is accepted from the bank.
		 * 
		 * TWS: submitted
		 */
		ACCEPTED,

		CANCELLED,

		TOTAL_FILLED,
		/**
		 * This state is valid only for a parent and this is the last state,
		 * when a parent has been filled and its position then closed by one of
		 * its children execution.
		 * 
		 * <p>
		 * The order in this case becomes "history", the position is not opened
		 * any more.
		 */
		NEUTRALIZED_BY_CHILD_EXECUTION

	}

	/**
	 * This is the generic method that is called whenever an order with a
	 * certain id changes status in the market. This method is called
	 * <i>always</i> asynchronously by another thread. Every broker, usually
	 * (even it is not required), is implemented with a "reader" thread that
	 * continuously waits for events (usually on a socket).
	 * 
	 * @param aStatus
	 *            the order status.
	 */
	void orderStatus(OrderStatus aStatus);

	void newExecution(OrderExecImpl anExec);

}
