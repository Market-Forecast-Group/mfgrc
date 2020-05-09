package com.mfg.tea.conn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

import com.mfg.broker.BrokerException;
import com.mfg.broker.IMarketSimulatorListener;
import com.mfg.broker.IOrderMfg;
import com.mfg.broker.MarketSimulator;
import com.mfg.broker.OrderStatus;
import com.mfg.broker.orders.OrderExecImpl;
import com.mfg.broker.orders.OrderImpl;
import com.mfg.common.DFSException;
import com.mfg.common.DFSQuote;
import com.mfg.common.DFSStoppingSubscriptionEvent;
import com.mfg.common.DFSSubscriptionStartEvent;
import com.mfg.common.DFSSymbolEvent;
import com.mfg.common.ISymbolListener;
import com.mfg.common.TEAException;
import com.mfg.dfs.conn.IDFS;
import com.mfg.tea.accounting.MixedInventoriesFolder;
import com.mfg.utils.U;

/**
 * 
 * A real broker (as seen by the clients) implemented using a set of
 * {@link MarketSimulator} objects.
 * 
 * <p>
 * This is the <b>multibroker</b>, the one which handles the orders from
 * different clients!
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class SimulatedRealBroker extends BaseRealBroker {

	/**
	 * Builds the broker using the data provider as a parameter which will be
	 * used as the source to fill the orders. Remember that here we are in real
	 * time, as long as the client is concerned.
	 * 
	 * @param aDFS
	 *            the data provider.
	 * 
	 * @param aListener
	 *            the listener of this real broker (usually it will be
	 *            {@link MultiTEA})
	 */
	public SimulatedRealBroker(IDFS aDFS, IRealBrokerListener aListener,
			MixedInventoriesFolder multiTEaRoot) {
		super(aListener, multiTEaRoot);
		_dfs = aDFS;
	}

	final IDFS _dfs;

	/**
	 * This is the new integer ID which is used to order the external id.
	 */
	private AtomicInteger _nextOrderId = new AtomicInteger(10_000);

	/**
	 * key is the external id (the one which is given to the market simulator),
	 * and the value is the internal id, given to the application, which is a
	 * fake id, used to simulate a different id, but in practice the market
	 * simulator is using the application ids, just for convenience.
	 * 
	 * 
	 * 
	 * <p>
	 * In reality here the external id, given to the market simulator, is equal
	 * to the id which the application sees, because we do not change the order
	 * in any way. Only to simulate a real broker we have given to the outside a
	 * different id
	 * 
	 * <p>
	 * _extIntOrders.put(internalId, res[i]);
	 * 
	 * <p>
	 * So the key is the id which the market simulator sees and the key is
	 * <b>internal</b> id which I have sent to the application (for the
	 * application point of view this is external).
	 * 
	 * 
	 */
	// HashMap<Integer, Integer> _extIntOrders = new HashMap<>();

	/**
	 * This map holds the correspondence between the order and the simulators.
	 * 
	 * <p>
	 * The key is the same as in the preceding map.
	 */
	HashMap<Integer, MarketSimulatorHelper> _orderIdsToHelper = new HashMap<>();

	// /**
	// * This map holds the correspondence between a traded symbol and the
	// virtual
	// * symbols associated to this map.
	// *
	// * <p>
	// * Actually there is 1:many association, because each real trading symbol
	// * may have an array of virtual symbols to be associated to.
	// *
	// * <p>
	// * So I delete this map and I use the Set inside the
	// * {@link MarketSimulatorHelper}
	// */
	// private HashMap<String, String> _tradedVirtualSymbolMap = new
	// HashMap<>();

	/**
	 * Tells the simulated broker that the given trading symbol is associated to
	 * the given virtual symbol.
	 * 
	 * <p>
	 * This is used because in this way the simulated broker is able to know
	 * where to find the information (in the data feed) about a particular
	 * trading symbol.
	 * 
	 * 
	 * 
	 * @param aTradingSymbol
	 * 
	 * 
	 * @param aVirtualSymbol
	 * @throws TEAException
	 */
	public void associateSymbol(String aTradingSymbol, String aVirtualSymbol)
			throws TEAException {
		// _tradedVirtualSymbolMap.put(aTradingSymbol, aVirtualSymbol);
		MarketSimulatorHelper helper = _simulators.get(aTradingSymbol);
		if (helper == null) {
			helper = new MarketSimulatorHelper(aVirtualSymbol);
			_simulators.put(aTradingSymbol, helper);
		} else {
			// there is already the simulator, I pass to it the new virtual
			// symbol
			helper.addAssociatedVirtualSymbol(aVirtualSymbol);
		}

	}

	/**
	 * An helper class used to contain and manage a market simulator just for
	 * the use of this multi simulator.
	 * 
	 * <p>
	 * Every market simulator is attached to a virtual symbol.
	 * 
	 * <p>
	 * It is safe to share a market simulator for all the participants of a
	 * given symbol? In a certain sense yes, because even if the one participant
	 * associated with the given symbol V1 dies, the market simulator does not
	 * detach from the source and it takes it alive.
	 * 
	 * <p>
	 * In other words DFS will see a subscription always alive to this symbol.
	 * 
	 * <p>
	 * The broker could in some way delete the subscription after some time
	 * idle.
	 * 
	 * <p>
	 * The problem is that the simulator needs in any case a listener and that
	 * is the problem. Not here, anyway, because the real broker has, in fact,
	 * only one listener.
	 * 
	 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
	 * 
	 */
	class MarketSimulatorHelper implements IMarketSimulatorListener, ISymbolListener {

		/**
		 * How many ticks in slippage, this value is equal to all the
		 * simulators, but the actual value in points depends on the tick value,
		 * of course.
		 */
		private static final int SLIPPAGE_IN_TICKS = 1;

		/**
		 * How many extra ticks in the limit type orders.
		 */
		private static final int EXTRA_TICKS = 1;

		// private final String _virtualSymbol;
		/**
		 * The market simulator helper has a set of virtual symbols which can be
		 * used to track the different virtual symbols which insists on this
		 * market simulator.
		 * 
		 * <p>
		 * There is only one opened subscription to it, but when a subscription
		 * goes away the market simulator helper switches automatically on the
		 * next available.
		 * 
		 * <p>
		 * If the set becomes empty then it shuts down.
		 */
		private final HashSet<String> _virtualSymbols = new HashSet<>();

		private String _subscribedSymbol;

		/**
		 * 
		 * @param aVirtualSymbol
		 *            the virtual symbol used to
		 * @throws DFSException
		 * @throws TEAException
		 */
		public MarketSimulatorHelper(String aVirtualSymbol) throws TEAException {
			_ms = new MarketSimulator(this, SLIPPAGE_IN_TICKS, EXTRA_TICKS);
			_virtualSymbols.add(aVirtualSymbol);
			_connectToDataSource(aVirtualSymbol);

		}

		private void _connectToDataSource(String aVirtualSymbol)
				throws TEAException {
			/*
			 * Now I have to subscribe to the virtual symbol.
			 */
			try {
				_dfs.connectToExistingDataSource(aVirtualSymbol, this);
			} catch (DFSException e) {
				e.printStackTrace();
				throw new TEAException(e);
			}
			_subscribedSymbol = aVirtualSymbol;

		}

		public void addAssociatedVirtualSymbol(String aVirtualSymbol)
				throws TEAException {

			if (_virtualSymbols.size() == 0) {
				assert (_subscribedSymbol == null);
				// I have revived this symbol
				_connectToDataSource(aVirtualSymbol);
			}
			boolean add = _virtualSymbols.add(aVirtualSymbol);
			/*
			 * if this fails there is a duplicated trading symbol, it is a bad
			 * situation.
			 */
			assert (add);

		}

		MarketSimulator _ms;

		/**
		 * This is the order which is put in the simulator, this order may
		 * immediately receive statuses and executions, but I have to stop them
		 * until the client, {@link MultiTEA}, has fixed the external ids.
		 */
		private IOrderMfg _curOrderInSimulator;

		private ArrayList<Object> _queuedEvents = new ArrayList<>();

		// private ArrayList<IOrderStatus> _statuses = new ArrayList<>();

		@SuppressWarnings("boxing")
		@Override
		public void orderStatus(OrderStatus aStatus) {
			if (_curOrderInSimulator != null) {
				_queuedEvents.add(aStatus);
				return;
			}
			/*
			 * The status is ok as it is, we don't have to change ids, because
			 * we have already transmitted to the outside the new ids, look at
			 * the place order method.
			 * 
			 * Well, maybe not. Because here the order status has the id of the
			 * market simulator which should be translated.
			 */
			// OrderStatus os = (OrderStatus)aStatus;

			_listener.orderStatusRb(aStatus.getOrderId(), aStatus);

			/*
			 * Ok, but then if the order has been executed or deleted by the
			 * user (or the market) we have to update the maps.
			 */
			EOrderStatus status = aStatus.getStatus();

			if (status == EOrderStatus.CANCELLED
					|| status == EOrderStatus.TOTAL_FILLED) {

				/*
				 * Ok, let's remove them.
				 */

				// _extIntOrders.remove(aStatus.getOrderId());
				_orderIdsToHelper.remove(aStatus.getOrderId());

			}

		}

		@Override
		public void newExecution(OrderExecImpl anExec) {
			if (_curOrderInSimulator != null) {
				/*
				 * There is an order in the simulator, I do not pass this
				 * execution, yet.
				 */
				_queuedEvents.add(anExec);
				return;
			}
			_listener.newExecutionRb(anExec.getOrderId(), anExec);
		}

		/**
		 * This method is called using the
		 * {@link MultiTEA#placeOrder(int, TEAOrder)} method which is
		 * synchronized. So there is nothing to be synchronized here.
		 * 
		 * 
		 * @param aOrder
		 *            the order which you want to put in the market simulator.
		 * @throws TEAException
		 */
		public void placeOrder(IOrderMfg aOrder) throws TEAException {
			try {
				/*
				 * Before I can put the order I must "park" the id(s) of the
				 * order because I cannot send to the client the statuses before
				 * it has had a chance to store the new ids.
				 */
				_curOrderInSimulator = aOrder;
				_ms.addOrder(aOrder);
				_curOrderInSimulator = null;
			} catch (BrokerException e) {
				throw new TEAException(e);
			}
		}

		@Override
		public void onNewSymbolEvent(DFSSymbolEvent anEvent) {
			/*
			 * When I get the new quote I have to pass it on to the market
			 * simulator. The code is the same in the SingleSimulBroker, maybe I
			 * can
			 */
			if (anEvent instanceof DFSSubscriptionStartEvent) {
				DFSSubscriptionStartEvent ssse = (DFSSubscriptionStartEvent) anEvent;
				_ms.onStarting(ssse._tick, ssse._scale);
			} else if (anEvent instanceof DFSQuote) {
				DFSQuote dq = (DFSQuote) anEvent;

				/*
				 * Only real quotes are sent to simulator, and real quotes are
				 * the ones which are not in warm up and in layer zero.
				 */
				if (dq.layer == 0 && dq.warmUpTick == false
						&& dq.tick.getReal()) {
					try {
						_ms.newTick(dq.tick);
					} catch (BrokerException e) {
						e.printStackTrace();
					}
				}
			} else if (anEvent instanceof DFSStoppingSubscriptionEvent) {
				/*
				 * Ok, the market simulator is notified that the subscription is
				 * being cancelled, so I connect to another one
				 */
				DFSStoppingSubscriptionEvent sse = (DFSStoppingSubscriptionEvent) anEvent;

				_ms.reset(SLIPPAGE_IN_TICKS, EXTRA_TICKS);

				synchronized (_virtualSymbols) {
					boolean remove = _virtualSymbols.remove(sse.symbol);
					assert (remove);
				}
				if (sse.symbol.equals(_subscribedSymbol)) {
					/*
					 * Ok, the subscribed symbol is the one going to die. I will
					 * get another...
					 */

					if (_virtualSymbols.size() != 0) {
						// I get the first, just to test
						String newSymbol = _virtualSymbols.iterator().next();
						try {
							_connectToDataSource(newSymbol);
						} catch (TEAException e) {
							e.printStackTrace();
							// To do here, maybe I will have to notify the
							// client.
							throw new RuntimeException(e);
						}
					} else {
						// the market simulator is in pause, no one is
						// subscribed to this virtual symbol...
						U.debug_var(277518, "The virtual symbol ",
								_subscribedSymbol, " is going to die. I sleep");
						_subscribedSymbol = null;
					}

				}
			}

		}

		public void stop() throws DFSException {
			_dfs.disconnectFromExistingDataSource(_subscribedSymbol, this);

		}

		public void dropOrder(int aId) throws TEAException {
			try {
				_ms.cancelOrder(aId);
			} catch (BrokerException e) {
				throw new TEAException(e);
			}

		}

		public void updateOrder(int aId, OrderImpl aOrder) {
			_ms.modifyOrder(aId, aOrder);
		}

		public void freeQueuedEvents() {
			for (Object event : _queuedEvents) {
				if (event instanceof OrderStatus) {
					orderStatus((OrderStatus) event);
				} else {
					newExecution((OrderExecImpl) event);
				}
			}
			_queuedEvents.clear();
		}

	}

	private final HashMap<String, MarketSimulatorHelper> _simulators = new HashMap<>();

	@SuppressWarnings({ "boxing" })
	@Override
	public int[] placeOrder(OrderImpl aOrder) throws TEAException {

		/*
		 * This is the place where I have the traded symbol.
		 * 
		 * Now I can know if the traded symbol has already a simulator attached
		 * to it.
		 */

		/*
		 * When I place an order I have to find to which SingleSimulBroker must
		 * this order be dispatched. Then I have to find if the receiver of this
		 * order is ready to receive notifications.
		 * 
		 * 
		 * The order has the traded symbol which is the real symbol. But I have
		 * also to know the virtual symbol used to connect to the data feed.
		 * 
		 * This also for a real real broker.
		 * 
		 * 
		 * The only solution I have found is that MultiTea for now manages the
		 * traded symbol in the order to the virtual symbol and in this way the
		 * broker here has the virtual symbol to attach to... but that may mean
		 * that the simulated broker has one market simulator per symbol
		 * virtual... because it has no way to extract the common traded symbol
		 * from the virtual symbol... so how it has to connect?
		 */

		/*
		 * I have to know to which trading symbol it belongs, because I have to
		 * associate it to the correct broker.
		 */
		String tradedSymbol = aOrder.getTradingSymbol();

		/*
		 * This call gets the simulator for the trading symbol which is the
		 * "normal" representation of the symbol, for example "GOOG" or
		 * "@ES#mfg" (in the case of a simulated data feed).
		 */
		MarketSimulatorHelper simulator = _simulators.get(tradedSymbol);

		if (simulator == null) {
			/*
			 * create the simulator and start it!
			 * 
			 * 
			 * the simulator needs the virtual symbol identifier used to create
			 * the market simulator and connect to it.
			 * 
			 * The traded symbol may be shared, this is another thing to
			 * consider, the simulated broker may be shared so only the first
			 * will subscribe to the virtual symbol.
			 * 
			 * The problem is that we don't know here the virtual symbol
			 * identifier because we are in the real realm which is different:
			 * the real broker only knows about the traded symbol...
			 * 
			 * 
			 * ...but the real broker must have access also to the virtual
			 * symbol because this is needed for the equity account (the open
			 * trade equity).
			 * 
			 * Let's see.
			 */
			throw new IllegalStateException();

		}

		int res[];
		/*
		 * The ids are the same, but I advance of a constant factor just to test
		 * the maps, to test the multitea capability of handling orders of
		 * different ids.
		 * 
		 * This means that the market simulators are seeing the orders with the
		 * mfg id. Is that OK? Maybe I can make things simpler, because it is a
		 * bit of a mess to have these two sets of ids.
		 */
		int length = 1 + aOrder.getChildren().size();

		res = new int[length];

		if (aOrder.getBrokerId() < 0) {

			for (int i = 0; i < res.length; ++i) {
				res[i] = _nextOrderId.incrementAndGet();

				// int internalId;
				// if (i == 0) {
				// internalId = aOrder.getMfgOrder().getId();
				// } else {
				// internalId = aOrder.getMfgOrder().getChildAt(i - 1).getId();
				// }

				/*
				 * It seems strange but the external id is the internal.
				 */
				// _extIntOrders.put(internalId, res[i]);

				_orderIdsToHelper.put(res[i], simulator);
			}

			/*
			 * put the broker id...
			 */
			int aId = res[0];
			aOrder.setBrokerId(aId++);

			for (IOrderMfg child : aOrder.getChildren()) {
				((OrderImpl) child).setBrokerId(aId++);
			}

		} else {
			// this is already sent... so is a modification, the id is the same.

			int i = 0;
			res[i++] = aOrder.getBrokerId();
			for (IOrderMfg child : aOrder.getChildren()) {
				res[i++] = child.getBrokerId();
			}
		}

		// simulator.placeOrder(aOrder);

		/*
		 * the order is added later, because the market simulator may immediatly
		 * give a confirmation of order executed, so the maps should be already
		 * be ready.
		 */
		// IOrderMfg modOrder = OrderImpl.cloneWithStartingId(
		// (OrderImpl) aOrder.getMfgOrder(), res[0]);

		/*
		 * When I place the order to the simulator the order may be executed
		 * immediately, but the problem is that MultiTea does not have yet the
		 * updated ids.
		 */
		simulator.placeOrder(aOrder);

		return res;
	}

	@Override
	public void updateOrder(int aId, OrderImpl aOrder) throws TEAException {
		/*
		 * To know the simulator I need to track the trading symbol for this
		 * order.
		 */

		/*
		 * I need to know where is the simulator in charge for this order. The
		 * id is the id received by the
		 */

		MarketSimulatorHelper msh = _getSimulatorFor(aId);
		msh.updateOrder(aId, aOrder);

	}

	@Override
	public void dropOrder(int aId) throws TEAException {

		/*
		 * I have to access the simulator
		 */
		MarketSimulatorHelper msh = _getSimulatorFor(aId);
		msh.dropOrder(aId);

	}

	/**
	 * gets the simulator for the given order id.
	 * 
	 * <p>
	 * The id is the "external" id which has been made up in the
	 * {@link #placeOrder(TEAOrder)} order method.
	 * 
	 * 
	 * @param aExternalId
	 */
	@SuppressWarnings({ "boxing" })
	private MarketSimulatorHelper _getSimulatorFor(int aExternalId) {
		return _orderIdsToHelper.get(aExternalId);
	}

	@Override
	public void stop() throws TEAException {
		for (MarketSimulatorHelper simulator : _simulators.values()) {
			try {
				simulator.stop();
			} catch (DFSException e) {
				throw new TEAException(e);
			}
		}

	}

	@Override
	public void releaseMessagesWaitingWithId(int aId) {
		/*
		 * The ids are of course the new ids which have been created by the
		 * placeorder.
		 */
		// let's get the helper for this id
		MarketSimulatorHelper helper = _getSimulatorFor(aId);
		helper.freeQueuedEvents();
	}
}
