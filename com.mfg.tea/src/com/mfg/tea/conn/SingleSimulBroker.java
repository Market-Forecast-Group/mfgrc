package com.mfg.tea.conn;

import com.mfg.broker.BrokerException;
import com.mfg.broker.IMarketSimulatorListener;
import com.mfg.broker.IOrderMfg;
import com.mfg.broker.MarketSimulator;
import com.mfg.broker.OrderStatus;
import com.mfg.broker.orders.OrderExecImpl;
import com.mfg.broker.orders.OrderImpl;
import com.mfg.common.DFSException;
import com.mfg.common.DFSQuote;
import com.mfg.common.DFSSubscriptionStartEvent;
import com.mfg.common.DFSSymbolEvent;
import com.mfg.common.TEAException;
import com.mfg.dfs.conn.IDFS;
import com.mfg.tea.accounting.MixedInventoriesFolder;
import com.mfg.tea.accounting.StockInfo;
import com.mfg.tea.conn.SimulatedRealBroker.MarketSimulatorHelper;

/**
 * The virtual broker class is the top most object in the chain towards the real
 * broker (which is inside tea).
 * 
 * <p>
 * The virtual broker is used to give to TradingPipe a consistent view (limited
 * to this trading pipe) of a broker and a portfolio.
 * 
 * <p>
 * The virtual broker is like a "TEA" sharing, because it gives to the client
 * the illusion to be the only connection to the broker but it is not.
 * 
 * <p>
 * All the notifications are filtered for this only virtual client.
 * 
 * <p>
 * An object which uses this object will see only his orders and his account. No
 * method is able to circumvent this limitation (but objects which have the
 * reference to the "global" tea will be able to do this).
 * 
 * <p>
 * The virtual broker is inside the Server... it connects to a virtual symbol.
 * 
 * <p>
 * This only broker is directly tied to a Market simulator. The other brokers
 * are linked to a {@link MarketSimulator} using a middle-object which handles
 * the notifications. See the {@link SimulatedRealBroker}.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
final class SingleSimulBroker extends VirtualBrokerBase implements
		IMarketSimulatorListener {

	/**
	 * Every virtual broker is connected to a virtual symbol and uses a market
	 * simulator to manage the orders.
	 * 
	 * <p>
	 * If I am paper trading I have a unique market simulator? Yes, if the
	 * request is database.
	 * 
	 * <p>
	 * Is there a case in which different trading pipes share the same virtual
	 * symbol? Maybe not. Because each subscritpion may need an acknowledge.
	 * 
	 * 
	 * <p>
	 * We could use the {@link MarketSimulatorHelper} class inside here, in this
	 * case the broker use the helper and there is not a direct connection to
	 * the market simulator.
	 * 
	 */
	private final MarketSimulator _ms;

	/**
	 * The trading symbol for this virtual broker.
	 * 
	 * A virtual broker is only able to trade one symbol.
	 * 
	 * <p>
	 * The symbol used to trade can be the long virtual symbol string or a
	 * shorter string representation.
	 * 
	 * <p>
	 * In the virtual broker we have only the traded symbol, not the virtual
	 * symbol (which is inside the {@link SimulatedBroker}).
	 * 
	 * <p>
	 * Probably not. If the connection to the broker is simulated then the
	 * traded symbol is a virtual symbol... tbd.
	 * 
	 */
	private final String _tradedSymbol;

	private final IDFS _dfs;

	/**
	 * 
	 * 
	 * The {@link SingleSimulBroker} is <i>always</i> created on MFG side, even
	 * when TEA is remote a {@link SingleSimulBroker} is a TEA slice which
	 * happens inside the mfg process space.
	 * 
	 * <p>
	 * In case of a Trading Pipe which is connected to a CSV file or... a
	 * database request, something different must be done to allow the
	 * {@link SingleSimulBroker} to
	 * 
	 * 
	 * maybe we should add the tea id as a parameter...
	 * 
	 * @param aStockInfo
	 * 
	 * @param _dp
	 *            used to subscribe to the given symbol.
	 * 
	 * @param subTeaId
	 * @param symbol
	 *            The symbol used to send orders. The symbol <b>must</b> be a
	 *            virtual symbol.
	 * 
	 * @param aParent
	 *            The parent account which will be linked to the account of this
	 *            broker.A simulated broker can play by itself, for example in a
	 *            database request (every database request is different and it
	 *            will create a unique run) [maybe a batch is possible, for
	 *            example in a monte carlo simulation it was possible to have a
	 *            batch of runs, but in TEA we do not have a learning
	 *            possibility].
	 * 
	 * @throws DFSException
	 */
	SingleSimulBroker(int aId, VirtualBrokerParams aParams, IDFS aDFS,
			MixedInventoriesFolder aMixParent, StockInfo aStockInfo)
			throws DFSException {
		super(aId, aParams, aMixParent, aStockInfo);
		_tradedSymbol = aParams.virtualSymbol;
		_ms = new MarketSimulator(this, 1, 1);
		_dfs = aDFS;
	}

	@Override
	public void _placeOrderImpl(OrderImpl aOrder) throws TEAException {
		try {
			_ms.addOrder(aOrder);
		} catch (BrokerException e) {
			throw new TEAException(e);
		}

	}

	@Override
	public void dropOrder(int aOrderId) throws TEAException {
		try {
			_ms.cancelOrder(aOrderId);
		} catch (BrokerException e) {
			throw new TEAException(e);
		}
	}

	@Override
	public void orderStatus(OrderStatus aStatus) {
		_listener.orderStatusNew(aStatus);

	}

	@Override
	public void newExecution(OrderExecImpl anExec) {
		/*
		 * Accounting!
		 */
		// _account.orderExecuted(null);

		_listener.newExecutionNew(anExec);

		_registerANewExecution(anExec);
	}

	@Override
	protected void _onNewQuoteImpl(DFSSymbolEvent anEvent) {
		if (anEvent instanceof DFSSubscriptionStartEvent) {
			DFSSubscriptionStartEvent ssse = (DFSSubscriptionStartEvent) anEvent;
			_ms.onStarting(ssse._tick, ssse._scale);
		} else if (anEvent instanceof DFSQuote) {
			DFSQuote dq = (DFSQuote) anEvent;

			/*
			 * Only real quotes are sent to simulator, and real quotes are the
			 * ones which are not in warm up and in layer zero.
			 */
			if (dq.layer == 0 && dq.warmUpTick == false && dq.tick.getReal()) {
				try {
					_ms.newTick(dq.tick);
				} catch (BrokerException e) {
					e.printStackTrace();
				}
			}

		}

	}

	@Override
	public void start() throws TEAException {
		try {
			_dfs.connectToExistingDataSource(_tradedSymbol, this);
		} catch (DFSException e) {
			e.printStackTrace();
			throw new TEAException(e);
		}

	}

	@Override
	public void _stopImpl() throws TEAException {
		try {
			_dfs.disconnectFromExistingDataSource(_tradedSymbol, this);
		} catch (DFSException e) {
			e.printStackTrace();
			throw new TEAException(e);
		}
	}

	@Override
	public void updateOrder(IOrderMfg newOrder) throws TEAException {
		// /*
		// * the market simulator makes no difference between an add and one
		// * update.
		// */
		// try {
		// _ms.addOrder(newOrder);
		// } catch (BrokerException e) {
		// throw new TEAException(e);
		// }
		/*
		 * The modification has to be translated in an add for the market
		 * simulator.
		 */
		throw new UnsupportedOperationException();
	}

}
