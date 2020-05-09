package com.mfg.tea.conn;

import com.mfg.common.TEAException;

/**
 * The interface to the Trade Executing Application, the part of the application
 * (maybe embedded) which is responsible for sending and receiving orders.
 * 
 * <p>
 * Like the DFS interface this interface is asynchronous by nature, but every
 * asynch message has the possibility to be blocking by calling a suitable lock
 * method provided by the tea application itself in a asynch interface.
 * 
 * <p>
 * Only the simulator embedded is inherently synchronous, all the other data is
 * asynchronous.
 * 
 * 
 * TEA by its nature is multiclient, but this interface is not multiclient, it
 * will refer to a singleton, either an embedded tea or a proxy tea.
 * 
 * <p>
 * TEA is dependent on IDFS because it needs quotes for the market simulator.
 * 
 * <p>
 * This is also independent on the nature of the <b>real</b> broker which is
 * inside tea. A client is not able (by design) to know if it is attached to a
 * real broker or a simulated one, as MFG is not able to know if DFS is
 * connected to a real data feed or a simulated data feed, the interface is the
 * same.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public interface ITEA {

	// /**
	// * gets the root of the inventory.
	// *
	// * @return
	// */
	// IInventory getRootStockHolder();

	/**
	 * 
	 * creates a virtual broker which can be used by a portfolio or in any case
	 * an object which is able to send orders (it may be also the manual
	 * strategy).
	 * 
	 * @param virtualSymbol
	 *            the virtual symbol identifier which is connected to the
	 *            portfolio, the virtual symbol is a combination of the real
	 *            symbol and the tick data request.
	 * 
	 *            <p>
	 *            From the virtual symbol string TEA is able to derive the real
	 *            symbol, which may be a continuous symbol, for example the
	 *            trading pipe is referring to the continuous contract so the
	 *            real part of the virtual symbol string is something which ends
	 *            with "#mfg".
	 * 
	 *            <p>
	 *            This is sufficient for tea to subscribe to that symbol in case
	 *            the broker is simulated (so it needs to get the stream of data
	 *            from DFS..., in any case the virtual symbol itself is
	 *            sufficient, because the virtual symbol string inside DFS is
	 *            the signature for the subscription... the difference is that
	 *            if the request is real time than the virtual broker will be
	 *            shared by the trading symbol, otherwise not.
	 * 
	 *            <p>
	 *            As the virtual symbol is unique in all the mfg environment it
	 *            may be uses as an identifier of the trading pipe which has
	 *            created it. We can think of it as a strategy log.
	 * 
	 * @param tradingSymbol
	 *            the trading symbol is the identifier (broker dependent) of the
	 *            contract which is being traded.
	 *            <p>
	 *            For a simulated broker this is only a identifier used to
	 *            collect the statistics of different trades which are done.
	 *            <p>
	 *            The trading symbol could NOT be a continuous symbol, for
	 *            example something that ends with "#mfg"... because even if TEA
	 *            could be able to transform it in the "normal" symbol, we still
	 *            need the broker representation of the thing that we want to
	 *            buy or sell
	 *            <p>
	 *            The trading symbol is only a string used by the broker to
	 *            identify a contract. It can be a XML rerpresentation of a
	 *            complex thing, it can be a simple string like "GOOG" or
	 *            something like that, the important thing is that this string
	 *            is NOT used by tea to subscribe to the symbol.
	 *            <p>
	 *            The subscription is always done using the virtual symbol.
	 * 
	 * 
	 * @param isRealTimeRequest
	 *            true if the trading symbol is part of a real time request.
	 *            This changes the way in which the multi server will handle the
	 *            orders and how it will account the statistics of the account.
	 * @param listener
	 *            A callback which will receive the notifications of the virtual
	 *            broker. Usually this is a portfolio. The virtual broker is in
	 *            1:1 correspondence with a trading pipe which is in 1:1
	 *            correspondence with a virtual symbol. But the trading pipe
	 *            itself may serve different strategies, all of them are linked
	 *            to the same traded symbol. The logic to split the orders is
	 *            done in the portfolio, the {@link IVirtualBroker} interface is
	 *            not able to distinguish different strategies inside it.
	 * 
	 * @param isRealBrokerWanted
	 *            if true this means that the virtual broker will send the
	 *            orders to the real broker. If this is a database request it
	 *            won't be possible.
	 * @return
	 * @throws TEAException
	 */
	public IVirtualBroker createVirtualBroker(VirtualBrokerParams aParams)
			throws TEAException;

	public void start() throws TEAException;

	public void stop() throws TEAException;

	/**
	 * Returns the query interface for this particular sub tea.
	 * 
	 * <p>
	 * From this interface only facts about this subtea can be known.
	 * 
	 * 
	 * @return the query interface for this particular subtea, the id of the
	 *         query is fixed to the string which has been used to register to
	 *         this tea.
	 */
	public ITEAQuery getQueryTea();

	/*
	 * Statistical queries.
	 * 
	 * The statistical queries for tea are about
	 * 
	 * a. One virtual broker
	 * 
	 * b. A set of virtual brokers (a virtual portfolio)
	 * 
	 * c. The all orders inside this TEA. (from all the brokers).
	 * 
	 * The statistics can be active or passive, in the sense that we may ask
	 * also past logs for past executions.
	 * 
	 * A past execution is simply identified by a virtual symbol, as the virtual
	 * symbol is unique... Well it is not unique for now, it is "statistically"
	 * unique, but we may enforce this uniqueness because in this way we may
	 * have a history log of all the executions from this TEA
	 */

	/*
	 * The different types of broker are three for now
	 * 
	 * REAL request and REAL broker: this is the "all real" case, in which real
	 * orders are sent to the real broker (but the real broker may be in paper
	 * trading mode, of course).
	 * 
	 * REAL TIME request and SIMULATED orders, in this case the orders are sent
	 * to an internal market simulator which uses the real time prices which are
	 * sent by DFS. All stats are the same as in the first case, the difference
	 * is that the orders not sent in the wire.
	 * 
	 * DATABASE request and REAL broker: impossible, nonsense.
	 * 
	 * DATABASE request and SIMULATED broker: in this case each virtual broker
	 * will be separated, each one, because there is not addition (CUMULATION of
	 * statistics) possible.
	 * 
	 * So the strategy log is cumulated using the virtual symbol?
	 * 
	 * But the virtual symbol changes every time, also for the same trading
	 * pipe... maybe the user wants to collect statistics of the same trading
	 * pipe during different runs... so there should be a common identifier.
	 * 
	 * Well, this is the shell id, which is inside the order.
	 */

	/*
	 * The virtual broker created is independent from the real broker
	 * implementation.
	 */

}
