package com.mfg.tea.conn;

import com.mfg.broker.IMarketSimulatorListener.EOrderStatus;
import com.mfg.broker.IOrderMfg;
import com.mfg.common.TEAException;
import com.mfg.dfs.misc.VirtualSymbol;

/**
 * The virtual broker is different from the IBroker.
 * <p>
 * The IBroker is something like the IDataFeed, this is the high level broker.
 * 
 * <p>
 * But there may be a correspondence, for example we may have the possibility to
 * have a low level broker which is synchronous... for example the simulated
 * broker.
 * 
 * <p>
 * So in that sense we may have a 1:1 correspondence between the
 * {@link IVirtualBroker} and the {@link IBroker} interfaces.
 * 
 * <p>
 * The virtual broker is tied to a shell, that is a portfolio... so if we ask
 * the virtual broker a log it will give us the log of the trading pipe (which
 * is tied to a particular trading symbol).
 * 
 * <p>
 * The id of a vbroker is the virtual symbol.
 * 
 * <p>
 * A virtual broker is now tied to a portfolio, actually the two classes are
 * very related and probably they will be merged.
 * 
 * <p>
 * Virtual Broker is in some sense an execution... MFG to execute a strategy
 * needs a executor and this is the virtual broker. In the past this class was
 * called ShellExecutor, by the way.
 * 
 * <P>
 * The virtual broker does not know anything about the fake time, this was done
 * on purpose because the broker does fill the order in a certain physical time
 * which may be unrelated to the fake time of the data source.
 * 
 * <p>
 * The broker is stateless regarding to orders. If an exception is thrown the
 * order is NOT automatically resent, much like in a database if an insert fails
 * the insert is not repeated automatically.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public interface IVirtualBroker {

	/**
	 * Starts the virtual broker. It will subscribe to the virtual symbol.
	 * 
	 * @throws TEAException
	 */
	public void start() throws TEAException;

	public void stop() throws TEAException;

	/**
	 * 
	 * Get the latest snapshot for the account statistics.
	 * 
	 * @return the account statistics for this broker. If the broker is a proxy
	 *         broker than it will handle itself the statistical counting, it
	 *         will update an internal proxy copy of the statistics itself.
	 */
	public IDuplexStatistics getAccountStats();

	/**
	 * Watches the virtual account statistics, the virtual account information
	 * is pushed with the virtual broker's push key.
	 * 
	 * <p>
	 * All the account information is pushed by default, maybe later we may have
	 * a more detailed interface in which the information can be choosen, for
	 * example to have only some properties pushed. Or we could define some
	 * alarms, for example if the drawdown is over a certain threshold or
	 * whatever.
	 */
	public void watchAccountStats();

	/**
	 * 
	 */
	public void unwatchAccountStats();

	/**
	 * adds an order to the broker.
	 * 
	 * <p>
	 * The virtual broker does already know the symbol attached to this order,
	 * because a virtual broker is only attached to a symbol, it cannot manage
	 * orders for different symbols...
	 * 
	 * <p>
	 * The virtual broker is attached to a {@link VirtualSymbol}.
	 * 
	 * <p>
	 * The order has a numeric identifier which is unique in the application,
	 * but may be not unique in TEA, because different applications may have the
	 * same identifier.
	 * 
	 * <p>
	 * The return of the function is tricky. A <i>pure</i> asynchronous function
	 * would not return anything, and all the notifications would be done
	 * through the callback interface.
	 * 
	 * <p>
	 * But here we have two levels of notifications... we want to know if the
	 * order is at least accepted by the broker or not.
	 * 
	 * <p>
	 * Another level of notification is to know if the order is arrived to the
	 * market, or at least to the broker's servers. In this way the application
	 * is free to "do other things" because the broker is now responsible for
	 * the order.
	 * 
	 * <p>
	 * From the experience it is more useful a second type of safety, that is to
	 * return from the method only when either the order is rejected or it has
	 * arrived to the real broker and we are sure that it is commited to their
	 * servers.
	 * 
	 * @param aOrder
	 *            the order which the strategy wants to put into the market.
	 * 
	 * 
	 * @param sendImmediately
	 *            if true the order is sent immediately to the broker, otherwise
	 *            it is parked.
	 * 
	 * @throws TEAException
	 *             if something goes wrong while delivering this order to the
	 *             market.
	 * 
	 */
	public void placeOrder(IOrderMfg aOrder, boolean sendImmediately)
			throws TEAException;

	/**
	 * Places to the market the parked order with the given id. A parked order
	 * is an order which has been placed with the method
	 * {@link #placeOrder(TEAOrder, boolean)} and the parameter true.
	 * 
	 * @param aId
	 * @throws TEAException
	 */
	public void placeParkedOrder(int aId) throws TEAException;

	/**
	 * Forgets the parked order.
	 * 
	 * @param aId
	 * @throws TEAException
	 */
	public void forgetParkedOrder(int aId) throws TEAException;

	/**
	 * modifies the order associated with the id to the new TeaOrder.
	 * 
	 * <p>
	 * The IB implicit assumption that to modify an order I have to send it
	 * twice maybe is too much error prone, because this means that the client
	 * have only one channel to do the insertion and the modification.
	 * 
	 * <p>
	 * As in database language we have 3 kinds of modifications: insert, update
	 * and delete. These are different because they are conceptually different
	 * so it makes sense to render them separated also for the client.
	 * 
	 * <p>
	 * This method is equivalent to the SQL code
	 * {@code UPDATE orders SET order = newOrder WHERE id = newOrder.getId()}.
	 * 
	 * <p>
	 * The only difference with the SQL code is that the order may have children
	 * and this will modify the parent AND the children.
	 * 
	 * <p>
	 * Also in this case there are two steps for the update of an order: the
	 * first step is that the modification is <b>accepted</b> by the broker, and
	 * the second when the modification has been performed (or not) by the
	 * broker itself.
	 * 
	 * <p>
	 * These two moments may be the same moment (for example in the simulator)
	 * but in the real broker these two moments are separate in time, because at
	 * first there is the connection to the broker, and then the time when the
	 * order will be permanently modified in the market's servers.
	 * 
	 * <p>
	 * This method returns normally only after the first phase has been
	 * completed.
	 * 
	 * @param newOrder
	 * @throws TEAException
	 *             if the update request has not been put in the market's
	 *             servers.
	 */
	public void updateOrder(IOrderMfg newOrder) throws TEAException;

	/**
	 * 
	 * This method, like the {@link #placeOrder(IOrderMfg)}, returns <b>only</b>
	 * when the <b>delete request</b> is confirmed by the broker <i>or</i> by an
	 * exception if the order cannot be canceled for any reason. It does
	 * <b>not</b> wait for the <b>real</b> dropping, as it may be impossible or,
	 * in any case, like the execution of it, it depends on the market, so it is
	 * asynchronous.
	 * 
	 * <p>
	 * The <b>real</b> dropping happens when the listener receives a
	 * {@link IVirtualBrokerListener#orderStatusNew(com.mfg.broker.IOrderStatus)}
	 * message with the {@link EOrderStatus#CANCELLED} status.
	 * 
	 * <p>
	 * In this way the interface is not synchronous, but the strategy may look
	 * at the exception if the reason of the rejection is definitive or it can
	 * be repeated.
	 * 
	 * <p>
	 * For example the broker may be disconnected, but the disconnection is
	 * temporary... so it is safe to redo the sending.
	 * 
	 * <p>
	 * The method is called "drop" like in database terms, to indicate that this
	 * operation should be regarded as a transaction, either the method will
	 * succeed or it will fail completely.
	 * 
	 * 
	 * @param aOrderId
	 * @throws TEAException
	 *             if something goes wrong.
	 */
	public void dropOrder(int aOrderId) throws TEAException;

	// /**
	// * A legacy method to get the opened orders.
	// *
	// * <p>
	// * This list is maintained in the proxy side of the broker, in this way
	// also
	// * remote clients may be able to access it locally.
	// *
	// * @param longOrders
	// * true if you want to get the long opened orders.
	// */
	// public List<IOrderMfg> getOpenenedOrders(boolean longOrders);

}
