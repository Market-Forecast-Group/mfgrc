package com.mfg.tea.conn;

import com.mfg.broker.IMarketSimulatorListener;
import com.mfg.broker.IOrderExec;
import com.mfg.broker.IOrderMfg;
import com.mfg.broker.IOrderStatus;

/**
 * The high level interface used to listen to broker events.
 * 
 * <p>
 * This high level interface is used to signal either order status changes
 * (apart from the receiving and deleting of the order itself, because the
 * {@link IVirtualBroker} interface is synchronous about those events).
 * 
 * <p>
 * It will have also notifications about properties of the virtual account which
 * are changed, in order to let the proxy client build a mirror of it.
 * 
 * <p>
 * This interface is closely related to the {@link IMarketSimulatorListener}, even if the
 * latter is more low level, and it is not any more visible to the outside,
 * there is the Shell that implements it but it is going to be deprecated.
 * 
 * <p>
 * The usual implementor of this interface is a Shell or a Portfolio of
 * strategies, something which creates a {@link IVirtualBroker} object.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public interface IVirtualBrokerListener {

	/**
	 * gets a new order status.
	 * 
	 * @param aStatus
	 */
	void orderStatusNew(IOrderStatus aStatus);

	/**
	 * receives a notification about an order executed in the broker.
	 * 
	 * <p>
	 * The entire order is not repeated here, only its internal id (the one of
	 * {@link IOrderMfg} interface).
	 * 
	 * <p>
	 * As this interface is remotable it does not have sense to repeat all the
	 * information (at least now, maybe we could change that later).
	 */
	void newExecutionNew(IOrderExec anExec);

}
