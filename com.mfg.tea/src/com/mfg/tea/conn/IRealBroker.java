package com.mfg.tea.conn;

import com.mfg.broker.IOrderMfg;
import com.mfg.broker.orders.OrderImpl;
import com.mfg.common.TEAException;

/**
 * The interface to the <b>real</b> broker, one as TWS or iwbank...
 * 
 * <p>
 * The real broker does not know <i>anything</i> about the virtual broker, the
 * accounting, etc... for the real broker perspective its <b>only</b> client is
 * {@link MultiTEA}.
 * 
 * <p>
 * The <i>simulated</i> broker, however, needs the data provider to know how to
 * connect to the string of prices.
 * 
 * <p>
 * to be defined the accounting check.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
interface IRealBroker {

	/**
	 * Stops the real broker. It will disconnect the client from the broker.
	 * 
	 * <p>
	 * Maybe we can have the possibility to close all the positions...
	 * 
	 * <p>
	 * 
	 * @throws TEAException
	 *             if anything goes wrong.
	 * 
	 */
	public void stop() throws TEAException;

	/**
	 * Places the order given into the market.
	 * 
	 * <p>
	 * The order can be a parent order alone, a parent order with children. It
	 * cannot be a child order, in that case there is a modification which is
	 * going to happen.
	 * 
	 * <p>
	 * This method will <i>always</i> try to put a new order in the market, even
	 * if the parameter has the same identifier. This because this interface is
	 * only visible inside the class {@link MultiTEA} which is responsible for
	 * the book keeping between internal and external orders.
	 * 
	 * <p>
	 * Mfg does not know the difference between them and it will simply resend
	 * the same order to mean a modification.
	 * 
	 * <p>
	 * If the method returns normally then we have the guarantee that the order
	 * has been accepted by the broker (it may be rejected by the market, but
	 * this is another step...), in any case if the method returns an id then
	 * the order is inside the broker's servers and that is enough. *
	 * 
	 * 
	 * <p>
	 * There is a nasty thing to be defined, and it is the fact that the real
	 * broker may immediately call the
	 * {@link IRealBrokerListener#orderStatusRb(int, com.mfg.broker.IOrderStatus)}
	 * with the status of the order <strong>before</strong> the client that
	 * calls this method has had a chance to store the new ids...
	 * 
	 * 
	 * @param aOrder
	 *            this is the order which is added to the broker.
	 * 
	 * 
	 * @return the id (internal of the broker) for this order. If the real
	 *         broker does not have integer id (maybe a string id) it will be
	 *         converted by the concrete class in a integer id. It returns an
	 *         array because it returns also the id which will be given to the
	 *         children. This is not so important for a client, but it is
	 *         important for the multibroker. Usually they are consecutive, that
	 *         is if parent is N, the children are N+1, N+2, etc, but we have to
	 *         be sure.
	 * 
	 *         <p>
	 *         The children ID are of course fixed as long as the parent is
	 *         issued, because the method will not place the parent unless also
	 *         the children are committed to the server, this in fact is the
	 *         real purpose of the parent child order.
	 * 
	 *         <p>
	 *         These ids are necessary for the
	 *         {@link #modifyOrder(String, int, IOrderMfg)} and the
	 *         {@link #dropOrder(String, int)} methods.
	 * 
	 * @throws TEAException
	 *             if something is wrong with the order or with the broker.
	 */
	public int[] placeOrder(OrderImpl aOrder) throws TEAException;

	/**
	 * This is a very technical method, used because there may be race
	 * conditions in the order ids.
	 * 
	 * <p>
	 * The method tells the real broker that it can send (using the
	 * {@link IRealBrokerListener} interface) the messages which may be waiting
	 * for the id given.
	 * 
	 * @param aId
	 */
	public void releaseMessagesWaitingWithId(int aId);

	/**
	 * updates the (internal) order represented by the id aId with the new order
	 * given.
	 * 
	 * <p>
	 * The id must be one id which has been returned by the method
	 * {@link #placeOrder(String, IOrderMfg)}. Not all the modifications are
	 * possible, for example modifying the structure of the order (adding or
	 * removing children) may not be always simple or possible, or if the order
	 * has been executed before the modification...
	 * 
	 * <p>
	 * In any case the method will return only if the modification has taken
	 * into account by the broker, otherwise an exception will be thrown.
	 * 
	 * @param tradingSymbol
	 * @param aId
	 *            the order to be modified.
	 * @param aOrder
	 */
	public void updateOrder(int aId, OrderImpl aOrder) throws TEAException;

	/**
	 * drops the order identified by the id aId from the broker's servers.
	 * 
	 * <p>
	 * As in database terms the "drop" is like a transaction. If the method
	 * returns it represent a success and the order (and its children, if any)
	 * are successfully removed. Otherwise an exception is thrown.
	 * 
	 * <p>
	 * Removing a child may not be possible.
	 * 
	 * @param aTradingSymbol
	 * @param aId
	 * @throws TEAException
	 */
	public void dropOrder(int aId) throws TEAException;

}
