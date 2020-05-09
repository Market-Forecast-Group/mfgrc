package com.mfg.tea.conn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.mfg.broker.IOrderMfg;
import com.mfg.broker.orders.OrderExecImpl;
import com.mfg.broker.orders.OrderImpl;
import com.mfg.common.DFSQuote;
import com.mfg.common.DFSSymbolEvent;
import com.mfg.common.ISymbolListener;
import com.mfg.common.TEAException;
import com.mfg.tea.accounting.DuplexInventory;
import com.mfg.tea.accounting.MixedInventoriesFolder;
import com.mfg.tea.accounting.StockInfo;
import com.mfg.tea.accounting.Transaction;
import com.mfg.tea.db.Db;

/**
 * This is the base class for all the server's side virtual brokers.
 * 
 * <p>
 * Each of them will listen to a stream of prices which are used to compute the
 * open trade equity
 * 
 * <p>
 * The virtual broker is also able to connect to {@link MultiTEA}, this because
 * it is a server's side object. The connection to {@link MultiTEA} is
 * estabilished because the multi tea is like the central database where the
 * execution's log is stored.
 * 
 * <p>
 * A virtual broker is a <i>partition</i> of a broker.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
abstract class VirtualBrokerBase implements ISymbolListener, IVirtualBroker {

	public static void setMultiBroker(MultiTEA multiTEA) {
		_multiBroker = multiTEA;
	}

	/**
	 * Stores the opened short orders.
	 */
	private List<IOrderMfg> _openedShortOrders = new ArrayList<>();

	private List<IOrderMfg> _openedLongOrders = new ArrayList<>();

	private HashMap<Integer, IOrderMfg> _parkedOrders = new HashMap<>();

	protected final DbHookBrokerListener _listener;

	/**
	 * Each virtual broker base has a unique id which can be used to have a
	 * unique trail of transactions (orders) related to this virtual broker.
	 * This unique trail could be stored in a database to make reports, etc...
	 */
	protected final int _id;

	/**
	 * It is thought that the strategy will send id in order... so if I see an
	 * already seen id, I think this is a modification.
	 */
	private int _lastSentId = -1;

	/**
	 * The virtual broker has also an inventory by which it can track the stock
	 * quantities held by it.
	 * <p>
	 * Remember that a virtual broker is associated only to a single material.
	 * 
	 * 
	 * <p>
	 * This inventory has a "mixed" and a "homogeneous" parent. They are both
	 * inside the {@link SingleTeaHelper} class.
	 * 
	 * <p>
	 * The mixed parent is only a structural parent. It does <b>not</b> have
	 * stock updates, of course, but it gets the normal equity updates, because
	 * we can update the "money" value of a mixed portfolio, of course.
	 * 
	 */
	protected DuplexInventory _inventory;

	/**
	 * the broker used to send the orders. It is a multibroker because it will
	 * handle different sub-brokers.
	 */
	protected static MultiTEA _multiBroker;

	/**
	 * This stores the parameters used to create this virtual broker, it is used
	 * also to give a unique id for this virtual broker
	 */
	protected final VirtualBrokerParams _params;

	@SuppressWarnings("unused")
	private boolean _watchStats;

	/**
	 * This is the trading pipe identifier which is used to store the
	 * information about this object in the database. The database does not have
	 * the concept of a virtual broker but of a trading pipe. The two are
	 * equivalent.
	 */
	private int _tradingRunId;

	/**
	 * Creates the virtual broker basic class with a set of parameters.
	 * <p>
	 * The parameters are only used to store the type of virtual broker we have
	 * created, and when the broker stops it gives them back to the
	 * {@link MultiBroker} to properly clean up.
	 * 
	 * @param aParams
	 *            the parameters.
	 * @param aStockInfo
	 * @param aParent
	 *            Every virtual broker may have attached a parent account which
	 *            will be notified when an order is getting through the broker.
	 *            Some brokers may not have a parent, for example the simulated
	 *            brokers, because each counts by itself.
	 */
	protected VirtualBrokerBase(int aId, VirtualBrokerParams aParams,
			MixedInventoriesFolder aMixParent, StockInfo aStockInfo) {
		_params = aParams;

		_id = aId;

		_listener = new DbHookBrokerListener(aParams.listener);

		// /*
		// * Create the stock info associated with the traded symbol. If the
		// * homogeneus parent is null then it means that we have a simulated
		// * symbol, so the name must be uniquified.
		// */
		//
		// String stockName = aParams.tradingSymbol;
		// if (aParent == null) {
		// stockName += System.currentTimeMillis();
		// }
		//
		// StockInfo aStockInfo = new StockInfo(stockName, aParams.tickSize,
		// aParams.tickValue);

		_inventory = new DuplexInventory(aMixParent, aStockInfo);
	}

	protected abstract void _onNewQuoteImpl(DFSSymbolEvent anEvent);

	@SuppressWarnings("boxing")
	private void _parkOrder(IOrderMfg aOrder) throws TEAException {
		if (_parkedOrders.containsKey(aOrder.getId())) {
			throw new TEAException("Duplicate parked order " + aOrder);
		}
		_parkedOrders.put(aOrder.getId(), aOrder);

	}

	/**
	 * Each virtual broker in the server has its own account. This is <b>not</b>
	 * a leaf, even if there is only a strategy inside the trading pipe we will
	 * have only one leaf in the account itself.
	 * 
	 * <p>
	 * All the patterns inside the trading pipe by definition trade the same
	 * symbol, have the same indicator and receive the same ticks... so their
	 * execution log can be shared. We are simply aggregating their executions
	 * in a hierarchical way.
	 * 
	 * <p>
	 * Probably we can think of this like a double entry accounting system,
	 * where we have parent and children accounts.
	 * 
	 * ... tbd
	 * 
	 */

	protected abstract void _placeOrderImpl(OrderImpl aOrder)
			throws TEAException;

	@SuppressWarnings("boxing")
	void _placeOrderInternal(IOrderMfg aIOrder, boolean sendImmediately,
			boolean isCloningNecessary) throws TEAException {
		OrderImpl aOrder = (OrderImpl) aIOrder;

		/*
		 * Cloning is necessary only if the order comes from the same process,
		 * that is if TEA is embedded.
		 */
		if (isCloningNecessary) {
			aOrder = aOrder.clone();
		}
		aOrder.setShellId(_params.shellId);
		aOrder.setTradingSymbol(_params.tradingSymbol);

		if (sendImmediately) {
			/*
			 * I have to know if this is a modification or not. The strategy may
			 * want to modify a parked order, this is not supported yet.
			 */
			if (_parkedOrders.containsKey(aIOrder.getId())) {
				throw new TEAException("Cannot modify a parked order "
						+ aIOrder);
			}

			try {
				Db.i().beginTransaction();

				if (_lastSentId >= aIOrder.getId()) {
					/*
					 * This is a modification... I have to retrieve the db id of
					 * this order, if this is a remote order the id is not here,
					 * because the object has been serialized by the socket.
					 * 
					 * It may have already the Tea id, in that case it should be
					 * coherent.
					 */
					if (aOrder.getTeaId() == OrderImpl.INVALID_TEA_ID) {
						_listener.setTeaIdForOrder(aOrder);
					}

					/*
					 * Here I should call TEA to modify the order
					 */
					Db.i().modifyExistingOrder(aOrder);

				} else {
					/*
					 * Here I can put the logic about the pending orders map.
					 */

					// aOrder.setShellId(_params.shellId);
					// aOrder.setTradingSymbol(_params.tradingSymbol);

					long dbId = Db.i().putNewOrder(_tradingRunId, aOrder);
					aOrder.setTeaId(dbId);
					_lastSentId = Math.max(_lastSentId, aOrder.getId());
					_listener.associate(aOrder, dbId);

					/*
					 * Associate also the children!
					 * 
					 * TODO: maybe the db should add the children orders
					 * automatically.
					 */
					for (IOrderMfg child : aOrder.getChildren()) {
						dbId = Db.i().putNewOrder(_tradingRunId,
								(OrderImpl) child);
						_listener.associate(child, dbId);
						_lastSentId = Math.max(_lastSentId, child.getId());
					}

				}

				_placeOrderImpl(aOrder);

				Db.i().commit();
			} catch (TEAException e) {
				Db.i().rollback();
				throw e;
			}

		} else {
			_parkOrder(aIOrder);
		}

	}

	/**
	 * takes note of a new execution: it will in this way alter the equity.
	 * 
	 * @param anExec
	 *            the execution, may come from a real or simulated broker, it
	 *            does not matter.
	 * @throws TEAException
	 */
	final void _registerANewExecution(OrderExecImpl anExec) {
		/*
		 * Here I have to build a transaction object, because the execution is
		 * really a transaction.
		 */

		Transaction tr = new Transaction(anExec.getExecutionTime(),
				anExec.order.getQuantity(), (int) anExec.getExecutionPrice());

		boolean isLong = anExec.order.isSentToLongAccount();
		/*
		 * Ok, now I have the transaction and I have to pass it to the account.
		 * This is the only place where the new transaction is called.
		 */
		_inventory.newTransaction(isLong, tr);

		_inventory._testDump();

		/*
		 * updating the opened orders map
		 */
		IOrderMfg order = anExec.order;
		if (!order.isChild()) {
			// I am a parent
			if (isLong) {
				_openedLongOrders.add(order);
			} else {
				_openedShortOrders.add(order);
			}

			/*
			 * Here I should add the children orders as "pending".
			 */

		} else {
			// I am a child
			/*
			 * so I have closed the position..., let's remove the parent and add
			 * the child
			 */
			IOrderMfg parent = order.getParent();
			if (isLong) {
				_openedLongOrders.remove(parent);
			} else {
				_openedShortOrders.remove(parent);
			}

		}

	}

	protected abstract void _stopImpl() throws TEAException;

	/**
	 * This forced stop is sent only as a last resort from the {@link MultiTEA}
	 * object to forcely stop a virtual broker which may have been remained
	 * active.
	 * 
	 * <p>
	 * It won't send try to unregister itself from {@link MultiTEA}, as multi
	 * tea is itself trying to shutdown.
	 * 
	 * @throws TEAException
	 */
	public void forcedStop() throws TEAException {
		_stopImpl();
	}

	@SuppressWarnings("boxing")
	@Override
	public final void forgetParkedOrder(int aId) throws TEAException {
		IOrderMfg order;
		synchronized (_parkedOrders) {
			order = _parkedOrders.remove(aId);
			if (order == null) {
				throw new TEAException("Cannot find Parked order " + aId);
			}
		}

	}

	@Override
	public final IDuplexStatistics getAccountStats() {
		return _inventory.getStats();
	}

	// @Override
	// public List<IOrderMfg> getOpenenedOrders(boolean longOpenedOrders) {
	// if (longOpenedOrders) {
	// return _openedLongOrders;
	// }
	// return _openedShortOrders;
	// }

	DuplexInventory getInventory() {
		return _inventory;
	}

	/**
	 * Gets the listener for this broker, used by {@link MultiTEA} to have the
	 * 
	 * @return
	 */
	public DbHookBrokerListener getListener() {
		return _listener;
	}

	public final VirtualBrokerParams getParams() {
		return _params;
	}

	public int getTradingRunId() {
		return _tradingRunId;
	}

	@Override
	public final void onNewSymbolEvent(DFSSymbolEvent anEvent) {
		// for now it is a nop, but later I will add code to compute the open
		// trade equity.

		if (anEvent instanceof DFSQuote) {
			DFSQuote quote = (DFSQuote) anEvent;
			if (quote.tick.getReal() && quote.layer == 0) {
				_inventory.onNewStockPrice(quote.tick.getPhysicalTime(),
						quote.tick.getPrice());
			}

		}

		_onNewQuoteImpl(anEvent);
	}

	/**
	 * You cannot override this because here there is the logic for the parked
	 * orders.
	 */
	@Override
	public final void placeOrder(IOrderMfg aIOrder, boolean sendImmediately)
			throws TEAException {
		_placeOrderInternal(aIOrder, sendImmediately, true);

	}

	@SuppressWarnings("boxing")
	@Override
	public final void placeParkedOrder(int aId) throws TEAException {
		IOrderMfg order;
		synchronized (_parkedOrders) {
			order = _parkedOrders.remove(aId);
		}
		if (order == null) {
			throw new TEAException("Cannot find parked order " + aId);
		}
		placeOrder(order, true);

	}

	/**
	 * sets the trading pipe identifier.
	 * 
	 * <p>
	 * This is different from the {@link #_id}, because the id is not unique in
	 * the system
	 * 
	 * @param aTradingRunId
	 */
	public void setTradingRunId(int aTradingRunId) {
		_tradingRunId = aTradingRunId;
	}

	@Override
	public final void stop() throws TEAException {
		/*
		 * tell the multibroker that we have stopped.
		 */
		_multiBroker.stoppedVirtualBroker(this);
		/*
		 * Give a chance to the virtual broker to stop itself.
		 */
		_stopImpl();
	}

	@Override
	public final void unwatchAccountStats() {
		_watchStats = false;
	}

	@Override
	public final void watchAccountStats() {
		_watchStats = true;
	}
}
