package com.mfg.tea.conn;

import java.util.ArrayList;
import java.util.HashMap;

import com.mfg.broker.IMarketSimulatorListener.EOrderStatus;
import com.mfg.broker.IOrderMfg;
import com.mfg.broker.OrderStatus;
import com.mfg.broker.orders.OrderExecImpl;
import com.mfg.broker.orders.OrderImpl;
import com.mfg.common.DFSException;
import com.mfg.common.TEAException;
import com.mfg.dfs.conn.IDFS;
import com.mfg.dm.TickDataRequest;
import com.mfg.tea.accounting.LogicalInventoriesHolder;
import com.mfg.tea.accounting.MixedInventoriesFolder;
import com.mfg.tea.accounting.StockInfo;
import com.mfg.tea.db.Db;
import com.mfg.utils.U;

/**
 * The Trade Executing Application which is used to send orders to the market.
 * 
 * <p>
 * This class is a singleton for every computer, not only application, because
 * it relies on a global file lock to ensure that only one is connected.
 * 
 * <p>
 * The broker, the real broker, will have its own gateway mechanism to ensure
 * that only one client will connect. This means that
 * 
 * <p>
 * The class is final and protected because it has only sense to have it here.
 * 
 * <p>
 * MultiTEA is actually a shell that handles all the stuff to the
 * {@link IRealBroker} interface which is really a multi broker implementation.
 * 
 * <p>
 * The class is a multibroker implementation. It handles also the tea
 * register/unregister.
 * 
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
final class MultiTEA implements IRealBrokerListener {

	/**
	 * This object shares the common things related to a real broker. The outer
	 * class (the {@link MultiTEA}), acts as a marshaller for all the
	 * notifications.
	 * 
	 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
	 * 
	 */
	static final class RealBrokersHelper {

		final DbHookBrokerListener _listener;
		/**
		 * 
		 */
		final VirtualBrokerBase _vb;

		/**
		 * This map holds the orders present in the system, the key is the
		 * internal id and the value is the external id. This is used by the
		 * {@link MultiTEA} object when the client asks to modify or to cancel a
		 * particular order.
		 * 
		 * <p>
		 * Key: Internal id ---- Value : External id
		 */
		HashMap<Integer, Integer> _presentOrders = new HashMap<>();

		/**
		 * Key: external / value: internal id.
		 */
		HashMap<Integer, Integer> _extIntIds = new HashMap<>();

		public RealBrokersHelper(VirtualBrokerBase vb) {
			// _id = aId;
			_vb = vb;
			_listener = vb.getListener();
		}

		/**
		 * 
		 * register the ids that have been given by the broker
		 * 
		 * @param aOrder
		 *            the order which has been issued by this virtual real
		 *            broker.
		 * 
		 * @param externalIds
		 *            the array of the external ids given by the real broker,
		 *            connected to the real world (or to a simulator).
		 */
		@SuppressWarnings("boxing")
		void _registerTheIds(IOrderMfg aOrder, int[] externalIds) {

			/*
			 * The first id is the parent id
			 */

			/*
			 * If I am here it means that the ids are valid and that I have to
			 * store them in the map in order to have the correspondence between
			 * the external id and the real virtual broker that has issued it.
			 */

			int parentId = aOrder.getId();
			int parentExtId = externalIds[0]; // the first is the parent id.

			_presentOrders.put(parentId, parentExtId);
			_extIntIds.put(parentExtId, parentId);

			/*
			 * we start from 1 because the first id has been already been used
			 * by the parent.
			 */
			for (int i = 1; i < externalIds.length; ++i) {
				int childExtId = externalIds[i];
				int childId = aOrder.getChildAt(i - 1).getId();

				_presentOrders.put(childId, childExtId);
				_extIntIds.put(childExtId, childId);
			}

		}

		public void newExecution(OrderExecImpl anExec) {
			_vb._registerANewExecution(anExec);

			// Integer internalId = _extIntIds.get(anExec.getOrderId());
			// /*
			// * If this fails you have to check a race condition.
			// */
			// assert (internalId != null);

			// aStatus.setOrderId(internalId);

			// anExec.setOrderId(internalId);

			_listener.newExecutionNew(anExec);

		}

		/**
		 * @param aStatus
		 */
		@SuppressWarnings("boxing")
		public void sendOrderStatus(OrderStatus aStatus) {

			int realId = aStatus.getOrderId();

			Integer internalId = _extIntIds.get(realId);
			/*
			 * If this fails you have to check a race condition.
			 */
			assert (internalId != null) : "cannot find internal id for "
					+ realId;

			/*
			 * You have to clone the status because otherwise you will modify
			 * the order id, and this is not good.
			 */
			OrderStatus clonedStatus = aStatus.clone();

			clonedStatus.setOrderId(internalId);

			_listener.orderStatusNew(clonedStatus);

		}
	}

	/**
	 * The symbol inventory is used to store all the brokers which are linked to
	 * the same symbol.
	 * 
	 * <p>
	 * The symbol is a real time symbol or a paper trading symbol, in any case
	 * it is a <i>real</i> symbol which is used to group equal brokers.
	 * 
	 * <p>
	 * That is a <b>physical</b> connection because it contains all the symbols
	 * from whatever client.
	 * 
	 * <p>
	 * Then we may also have the logical parent which is different, it may be a
	 * "logical" portfolio of symbols, for example the client may define a
	 * "future" folders which contains all the futures and we have the combined
	 * equity of all the trades done with the futures.
	 * 
	 * <p>
	 * The {@link SymbolInventory} is able to track real time and paper trading
	 * brokers.
	 * 
	 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
	 * 
	 * 
	 *         //TODO this class may be renamed, because it seems not so useful
	 */
	static final class SymbolInventory {

		/**
		 * This is the name for the traded symbol.
		 */
		@SuppressWarnings("unused")
		private final String _name;

		/**
		 * This is the list of all the brokers which are linked by the same
		 * traded symbol, of course the request must be in real time. But the
		 * execution itself may be a paper trading request.
		 */
		ArrayList<IVirtualBroker> _brokers = new ArrayList<>();

		/**
		 * @param root
		 *            the root for all the
		 * @param aInfo
		 *            the stock information for this symbol
		 */
		public SymbolInventory(String tradedName, MixedInventoriesFolder root,
				StockInfo aInfo) {
			_name = tradedName;
		}

	}

	/**
	 * The root for all the symbols, real or simulated. Every time a new virtual
	 * broker is requested a new symbol is created or attached to a root for
	 * other symbols homogeneuos to that one.
	 * 
	 * <p>
	 * When a trading is stopped the inventory is removed. This class is able to
	 * listen when a broker is stopped, (stoppedVirtualBroker) method
	 * 
	 * <p>
	 * This root has three children, which are: all the stocks which are traded
	 * in real time, the stocks which are traded in paper trade real time and
	 * all the simulated stocks.
	 * 
	 * <p>
	 * The multibroker has three sets of brokers. The real time brokers, the
	 * paper trading brokers and the database request brokers.
	 * 
	 * <p
	 * These brokers are different and they also have different accounts, but
	 * some of these accounts could be merged together to have a common account
	 * family which can be seen from the outside.
	 */
	private final LogicalInventoriesHolder _root;

	private int _nextVirtualBrokerId = 1;

	private IDFS _dfs;

	/**
	 * Key is the broker id, value is the real broker helper associated with
	 * this id.
	 * 
	 * <p>
	 * The broker id is unique, and later it will be unique for this
	 * {@link MultiTEA} object even across different runs.
	 */
	private HashMap<Integer, RealBrokersHelper> _realBrokersMap = new HashMap<>();

	/**
	 * key is the external order id, value is the broker helper responsible for
	 * this order.
	 * 
	 * <p>
	 * The external id is unique for the current session, it need not be
	 * globally unique, but it is unique in the current run for all the clients
	 * connected to it.
	 * 
	 */
	private HashMap<Integer, RealBrokersHelper> _extOrdIdsToBrokersMap = new HashMap<>();

	/**
	 * This maps holds the list of all the virtual brokers associated to a
	 * certain real symbol. For example if we have several trading pipes which
	 * are connected to the real symbol "@ESU14" we list them here.
	 * 
	 * <p>
	 * The symbol maybe real but the datafeed is simulated so the "real" virtual
	 * broker will actually be a simulated broker which subscribes to the
	 * virtual symbol, but this is transparent from the point of view of the
	 * client of the virtual broker itself.
	 * 
	 * <p>
	 * We cannot share the subscriptions because each one needs the push to push
	 * notification to synchronize with the remote data feed (but this only in
	 * case of a simulated run... please refine).
	 * 
	 * <p>
	 * In case of a real time request and a real broker and a real data feed
	 * then only in this case the virtual broker will be connected to the real
	 * broker here.
	 * 
	 * <p>
	 * key = real traded symbol, not virtual, value = list of virtual brokers
	 * attached to the symbol,
	 * 
	 * <p>
	 * The base account for all these brokers is inside the real broker
	 * implementation, unless we have a simulated data feed, in that case we
	 * don't have a real broker and we have simply a distinct root for all the
	 * accounts.
	 * 
	 */
	private HashMap<String, SymbolInventory> _realVirtualBrokers = new HashMap<>();

	/**
	 * This is like the previous hash map, the difference is that here we
	 * <b>ALWAYS</b> have simulated brokers in the values of the map, because
	 * the client wants a paper trading.
	 * 
	 * <p>
	 * key = real traded symbol, <b>not virtual</b>, value = list of
	 * <i>simulated</i> brokers attached to the symbol.
	 * 
	 * <p>
	 * The paper trading share a common account which is the global pater
	 * trading account.
	 * 
	 * <p>
	 * But how to handle the symbols?
	 * 
	 * <p>
	 * The symbols are handled differently for each one of them
	 * 
	 */
	private HashMap<String, SymbolInventory> _paperTradingBrokers = new HashMap<>();

	/**
	 * The simulated brokers have as the key the virtual symbol, and they are
	 * unique because each virtual symbol is unique, even if it has the same
	 * {@link TickDataRequest} object.
	 * 
	 * <p>
	 * 
	 * key = the virtual symbol (so it is unique) and value is the simulated
	 * virtual broker attached to this symbol
	 * 
	 * <p>
	 * Every broker in it has a leaf account, which is enabled to process every
	 * transaction.
	 * 
	 * <p>
	 * The transaction is a general term which I prefer over "order execution"
	 * because it better conveys the accounting nature of the accounts.
	 * 
	 * <p>
	 * Probably the simulated brokers have accounts which are not cumulative,
	 * because they refer to different entities and in different time-frames. So
	 * it is not possible even to group them by money, which is the common
	 * denominator for the TEA accounts in real time.
	 * 
	 */
	private HashMap<String, IVirtualBroker> _simulatedVirtualBrokers = new HashMap<>();

	/**
	 * This map holds the interfaces which are registered to the currently
	 * running TEA.
	 * 
	 * <p>
	 * In reality we have only two types of TEA registering to this server: a
	 * local tea or a stub tea (which acts as a local tea impersonating a proxy
	 * object).
	 */
	@SuppressWarnings("unused")
	private HashMap<Integer, ITEAListener> _listeners;

	/**
	 * This map holds all the sub teas which are attached to this
	 * {@link MultiTEA} object.
	 * 
	 * <p>
	 * key is the tea id, value is the helper that holds the common data for
	 * this tea, for example the equity, the inventory folder, stuff like that.
	 */
	private HashMap<String, SingleTeaHelper> _subTeas = new HashMap<>();

	/**
	 * The multi tea has the <b>unique</b> connection to the broker, unique with
	 * respect to all the Mfg which are in the LAN, in the sense that the broker
	 * will see only one connection towards it, not a set of connections.
	 * 
	 * <p>
	 * For the broker there is only <b>one</b> client which is connect to it, so
	 * from its point of view it will have a unique client, maybe with two
	 * accounts, one for longs and the other for short trades, but that's it.
	 * 
	 * 
	 */
	private final IRealBroker _broker;

	/**
	 * To know whether we are attached to a real broker is different from
	 * knowing if we are attached to a simulated data feed.
	 * 
	 * <p>
	 * This is internal information which does not look into the details.
	 * 
	 */
	private final boolean _isSimulatedRealBroker;

	private RealBrokersHelper _currentHelper;

	/**
	 * 
	 * @param useSimulatedBroker
	 *            true means that the TEA will connect to a simulated broker.
	 *            (in reality we may have different connection parameters,
	 *            etc...).
	 * 
	 * @param aDFS
	 *            an interface to a local (or remote) DFS, it must point to a
	 *            valid DFS if the simulator is on, because the simulator needs
	 *            a stream of prices.
	 * @throws TEAException
	 * 
	 * @throws NullPointerException
	 *             if the data provider is null, it must have it for the open
	 *             trade equity...
	 */
	MultiTEA(boolean useSimulatedBroker, IDFS aDataProvider)
			throws TEAException {

		_root = new LogicalInventoriesHolder("rootTea", null);

		// this is really a mixed inventory folder, not a logical parent.
		_root.addChild(LogicalInventoriesHolder.REAL_TIME_FOLDER,
				new MixedInventoriesFolder(
						LogicalInventoriesHolder.REAL_TIME_FOLDER, null));

		// the root for all the paper tradings, also this folder is a "real"
		// folder, not a logical one.
		_root.addChild(LogicalInventoriesHolder.PAPER_TRADING_FOLDER,
				new MixedInventoriesFolder(
						LogicalInventoriesHolder.PAPER_TRADING_FOLDER, null));

		/*
		 * The database tradings are inside each singleTeaHelper object.
		 */

		/*
		 * I add the root for all the paper trading
		 */

		if (aDataProvider == null) {
			throw new NullPointerException();
		}

		_isSimulatedRealBroker = useSimulatedBroker;

		boolean isConnectedToSimulatedDataFeed;
		try {
			isConnectedToSimulatedDataFeed = aDataProvider
					.isConnectedToSimulatedDataFeed();
		} catch (DFSException e) {
			e.printStackTrace();
			throw new TEAException(e);
		}

		if (isConnectedToSimulatedDataFeed && !useSimulatedBroker) {
			throw new TEAException(
					"cannot create a real broker with a simulated data feed.");
		}

		_dfs = aDataProvider;

		// _isPrincipalSimulated = useSimulatedBroker;

		if (!(useSimulatedBroker || isConnectedToSimulatedDataFeed)) {
			/*
			 * useSimulatedBroker = false AND _isConnectedToSimulatedDataFeed =
			 * false
			 * 
			 * This means that DFS is connected to a real data feed.
			 */

			/*
			 * I cannot create a real broker connected to a real data feed, yet.
			 * But we may create a simulated broker connected to a real data
			 * feed.
			 */

			_broker = null;
			throw new UnsupportedOperationException();

		}

		/*
		 * If the real broker is null we are connected to a simulated data feed,
		 * so we create an artificial root for all the accounts which is then
		 * used to group all the real tradings started in this server.
		 */
		_broker = new SimulatedRealBroker(_dfs, this,
				(MixedInventoriesFolder) _root
						.getChild(LogicalInventoriesHolder.REAL_TIME_FOLDER));

		/*
		 * The multibroker is clearly a singleton. All the virtual brokers
		 * created share this multitea (which can be regarded as a multibroker)
		 * to send orders and to receive confirmations.
		 */
		VirtualBrokerBase.setMultiBroker(this);
	}

	/**
	 * actually creates the virtual broker and it will also adjust the parent /
	 * child relationship (if needed) in the virtual account.
	 * 
	 * @param aHelper
	 *            This is the tea helper which will contain the root of the
	 *            accounts for this virtual broker.
	 * 
	 * @param params
	 *            the parameters used to create the broker.
	 */
	private VirtualBrokerBase _createVirtualBroker(SingleTeaHelper aHelper,
			VirtualBrokerParams params) throws TEAException {

		VirtualBrokerBase vb = null;

		// MixedInventoriesFolder subTeaMixedParent =
		// aHelper._mixedRealTradingSymbols;

		try {

			/*
			 * the first difference is between a real time request and a
			 * database request.
			 */
			if (!params.isRealTimeRequest) {

				/*
				 * the request is database so it must be a paper trading. I
				 * simply add it to the list of the virtual brokers.
				 */
				if (params.isPaperTradingRequested) {
					/*
					 * The simulated broker has not a homogeneous parent because
					 * all the symbols are considered differently, so the stock
					 * info has a unique name.
					 */

					String tradedName = params.tradingSymbol + " "
							+ params.shellId + System.currentTimeMillis();
					StockInfo si = new StockInfo(tradedName, params.tickSize,
							params.tickValue);

					/*
					 * The null argument is correct, as we simply do not have a
					 * mixed folder in which adding the various equities.
					 */
					vb = new SingleSimulBroker(_nextVirtualBrokerId++, params,
							this._dfs, null, si);

					/*
					 * But we should need to add the logical link
					 */
					aHelper._dbSymbolsRoot.addSingleChild(tradedName,
							vb.getInventory());
					_simulatedVirtualBrokers.put(params.virtualSymbol, vb);
				}

			} else {
				/*
				 * The request is real time. If DFS is connected to the real
				 * time data feed and we want a real trading session then we use
				 * the real broker.
				 */

				/*
				 * I can create the stock info object using the virtual broker
				 * parameters, the request is real time so the stock info object
				 * is fixed (I will group the same trading configurations
				 * together).
				 */

				StockInfo si = new StockInfo(params.tradingSymbol,
						params.tickSize, params.tickValue);

				if (params.isPaperTradingRequested) {
					/*
					 * the request is a paper trading request, and we have a
					 * real time request, so this goes to the paper trading
					 * requests.
					 */
					if (!_paperTradingBrokers.containsKey(params.tradingSymbol)) {
						_paperTradingBrokers.put(params.tradingSymbol,
								new SymbolInventory(params.tradingSymbol,
										aHelper._mixedPaperTradingSymbols, si));
					}

					/*
					 * The virtual broker is still a simulated broker
					 */
					SymbolInventory aInv = _paperTradingBrokers
							.get(params.tradingSymbol);
					vb = new SingleSimulBroker(_nextVirtualBrokerId++, params,
							this._dfs, aHelper._mixedPaperTradingSymbols, si);
					aInv._brokers.add(vb);

				} else {
					/*
					 * This is the point where I know that the client wants to
					 * connect to a real broker to do real trading.
					 * 
					 * But... here I also know if the real broker is real or
					 * simulated.
					 */
					if (_isSimulatedRealBroker) {

						SimulatedRealBroker srb = (SimulatedRealBroker) _broker;

						/*
						 * If I do this association now the simulated data feed
						 * will then provide data for the market simulators
						 * inside it.
						 */
						srb.associateSymbol(params.tradingSymbol,
								params.virtualSymbol);
					}

					if (!_realVirtualBrokers.containsKey(params.tradingSymbol)) {
						_realVirtualBrokers.put(params.tradingSymbol,
								new SymbolInventory(params.tradingSymbol,
										aHelper._mixedRealTradingSymbols, si));
					}

					/*
					 * We are not connected to a simulated data feed (but we may
					 * have a simulated broker in any case...) so I pass to the
					 * real virtual broker the real account root.
					 */
					vb = new RealVirtualBroker(_nextVirtualBrokerId++, params,
							aHelper._mixedRealTradingSymbols, si);

					/*
					 * This call makes the real association between the id of
					 * the virtual broker and the listener (it may be a proxy).
					 */
					_registerRealBroker(vb, params.listener);
					// }

					_realVirtualBrokers.get(params.tradingSymbol)._brokers
							.add(vb);
				}
			}

		} catch (DFSException e) {
			throw new TEAException(e);
		}

		if (vb != null) {
			return vb;
		}
		params.listener = null;
		throw new TEAException("unsupported case " + params.serializeToString());
	}

	@SuppressWarnings("boxing")
	private int _getExternalId(int aBrokerId, int aOrderId) throws TEAException {
		RealBrokersHelper rbh = _realBrokersMap.get(aBrokerId);

		Integer externalId = rbh._presentOrders.get(aOrderId);

		if (externalId == null) {
			throw new TEAException("Order " + aOrderId + " not present!");
		}

		return externalId;
	}

	/**
	 * Register the following real broker. This will create a real child account
	 * in the real broker.
	 * 
	 * <p>
	 * The real broker may of course have the possibility to subscribe to a data
	 * source to update the mutable characteristics of an account (the open
	 * trade equity).
	 * 
	 * @param vb
	 *            the id of this real virtual broker which has been registered
	 *            right now.
	 * 
	 * @param aListener
	 *            the listener which is associated to this virtual broker.
	 * 
	 * 
	 * 
	 */
	@SuppressWarnings("boxing")
	private void _registerRealBroker(VirtualBrokerBase vb,
			IVirtualBrokerListener aListener) {
		/*
		 * The real virtual broker may need to be registered if the real broker
		 * is simulated, because the simulated real broker will need to attach
		 * to the existing data source to fill the orders.
		 */

		// are you registering it twice?
		assert (!_realBrokersMap.containsKey(vb._id));

		RealBrokersHelper rbp = new RealBrokersHelper(vb);

		_realBrokersMap.put(vb._id, rbp);

	}

	/**
	 * @param params
	 *            the parameters used to
	 * 
	 * 
	 * 
	 * 
	 * @throws TEAException
	 */
	public synchronized IVirtualBroker createVirtualBroker(
			VirtualBrokerParams params) throws TEAException {

		SingleTeaHelper aHelper = _subTeas.get(params.teaId);

		if (aHelper == null) {
			throw new TEAException("tea " + params.teaId + " is not registered");
		}

		// ArrayList<VirtualBrokerBase> brokers = aHelper._brokers;

		/*
		 * I create here the virtual broker as the real broker does not know the
		 * existence of different brokers.
		 * 
		 * Each virtual broker has a set of accounts, because it can serve
		 * different strategies which are tied to the same request in the same
		 * trading pipe. This account multiplicity is hidden inside the virtual
		 * broker itself and here it is not enforced.
		 * 
		 * the children of the virtual broker are created on demand as soon as a
		 * new order is put inside the virtual broker itself.
		 * 
		 * Another solution could be to list the strategies attached to the
		 * portfolio (a.k.a. trading pipe) here, but that would add complexity
		 * to the VirtualBrokerParams structure and moreover it will prevent the
		 * user to attach dynamically patterns to the trading pipe.
		 * 
		 * For now the solution is to create in the virtual broker a simple
		 * parent account which will later be populated with children accounts
		 * as soon as the orders are issued.
		 */

		VirtualBrokerBase vb = _createVirtualBroker(aHelper, params);

		aHelper.newTradingRun(vb, params);
		// brokers.add(vb);

		return vb;

	}

	/**
	 * drops the order with a particular id (that may change, because I need to
	 * know more information about the order that you want to drop).
	 * 
	 * @param aBrokerId
	 *            the id of the broker which wants to drop an order.
	 * 
	 * 
	 * @param aOrderId
	 *            the internal id of the order the broker wants to drop.
	 * 
	 * 
	 * @throws TEAException
	 */
	synchronized void dropOrder(int aBrokerId, int aOrderId)
			throws TEAException {
		/*
		 * to know the real id I have to get first the internal (broker)
		 * identifier for this order.
		 */

		int extId = _getExternalId(aBrokerId, aOrderId);

		_broker.dropOrder(extId);

		/*
		 * If I am here I can delete the order from the map... or wait the
		 * notification.
		 */

	}

	// /**
	// * inserts a new order in DB
	// *
	// * @param aOrder
	// */
	// void insertNewOrderDb(TEAOrder aOrder) {
	//
	// /*
	// * for now the orders are stored in a hash map, this is of course a
	// * temporary solution, but it has the advantage of being a practical
	// * thing.
	// */
	//
	// /*
	// * foreign key handling.
	// */
	//
	// }

	@SuppressWarnings("boxing")
	@Override
	public synchronized void newExecutionRb(int aOrderId, OrderExecImpl anExec) {
		/*
		 * dispatch the status to the correct real broker.
		 */

		RealBrokersHelper rbh = _extOrdIdsToBrokersMap.get(aOrderId);

		if (rbh == null) {
			rbh = _currentHelper;
		}

		/*
		 * I have to tell the real broker of this new execution, because this
		 * will update the inventory (and maybe the equity, if this execution
		 * closes totally or partially a position).
		 */
		rbh.newExecution(anExec);

		/*
		 * Also in this case if the execution is done in the same call chain of
		 * the place order the MultiTEA object has not had time to update the
		 * maps.
		 */

		// rbh._listener.newExecutionNew(anExec);

	}

	@SuppressWarnings("boxing")
	@Override
	public synchronized void orderStatusRb(int aOrderId, OrderStatus aStatus) {
		/*
		 * get the broker listener associated to this status.
		 */

		RealBrokersHelper rbh = _extOrdIdsToBrokersMap.get(aOrderId);

		if (rbh == null) {
			/*
			 * I assume that the real broker helper is not yet registered
			 * because this call has been done in the placeOrder chain, I use
			 * the current helper
			 */
			rbh = _currentHelper;
		}

		rbh.sendOrderStatus(aStatus);

		// rbh._listener.orderStatusNew(aStatus);

		/*
		 * If the order has been executed or cancelled I may remove it from the
		 * map, later if I get a NPE I know that something is bad about the
		 * broker, because after the cancelled or total executed state the
		 * broker should not send any more status updates about this order.
		 */

		EOrderStatus status = aStatus.getStatus();
		if (status == EOrderStatus.CANCELLED
				|| status == EOrderStatus.TOTAL_FILLED) {
			_extOrdIdsToBrokersMap.remove(aOrderId);
		}

		/*
		 * TODO I have also to delete the order from the RealBrokerHelper
		 */

	}

	/**
	 * Places the order to the real broker.
	 * 
	 * <p>
	 * The real broker of course does some checking requirements about the order
	 * and it may not accept it. If the order arrives here, however, it means
	 * that it has passed the virtual broker check.
	 * 
	 * @param brokerId
	 *            the id of the broker which does really add the order to the
	 *            system.
	 * 
	 * @param aOrder
	 *            the order which is added.
	 * @throws TEAException
	 */
	@SuppressWarnings("boxing")
	synchronized void placeOrder(int brokerId, OrderImpl aOrder)
			throws TEAException {

		/*
		 * If I am connected to a simulated broker I have to manage the traded
		 * symbol, because it has to be changed to the virtual symbol as the
		 * broker is a simulated one and needs the virtual symbol to subscribe
		 * to the feed.
		 */

		/*
		 * The virtual broker has issued an order. It is a real broker, this is
		 * for sure... but it has an individuality, it must have one, because
		 * then multi tea is going to dispatch the correct message to the
		 * correct virtual broker.
		 */

		_currentHelper = _realBrokersMap.get(brokerId);

		/*
		 * I simply place the order here, if there is an exception it goes
		 * directly to the real broker and then it will be caught by the handler
		 * which is inside the TradingPipe, if this tea is local, or by the
		 * TeaStub, if the object which has sent the order is a proxy.
		 */
		int[] externalIds = _broker.placeOrder(aOrder);

		/*
		 * Here we have a problem, because the broker could give me the
		 * notification before the place order returns, especially in case of
		 * market orders.
		 */

		/*
		 * I put the id of the external orders in the map, this is used to give
		 * the notifications to the correct broker.
		 */
		for (int extId : externalIds) {
			_extOrdIdsToBrokersMap.put(extId, _currentHelper);
		}

		_realBrokersMap.get(brokerId)._registerTheIds(aOrder, externalIds);

		for (int extId : externalIds) {
			_broker.releaseMessagesWaitingWithId(extId);
		}

	}

	/**
	 * Register the given tea to the present tea set in the system.
	 * 
	 * <p>
	 * This is the first act that a client needs to do before having an active
	 * session with TEA.
	 * 
	 * @param aTeaId
	 *            a tea identifier. It is a string.
	 * @param aTea
	 *            the tea used to be the holding of the mixed folder.
	 * @param isEmbedded
	 *            true if this TEA is an embedded tea.
	 * @return
	 * @throws TEAException
	 */
	public synchronized SingleTeaHelper registerTea(String aTeaId,
			IServerSideTea aTea, boolean isEmbedded) throws TEAException {
		/*
		 * put tea in the map. The map lists all the TEAs attached to this
		 * particular multiserver.
		 * 
		 * The server may serve different sub tea identifiers but it cannot have
		 * two local (or one local and one remote) sub teas with the same id.
		 */
		if (_subTeas.containsKey(aTeaId)) {
			throw new TEAException("Tea string already in use " + aTeaId);
		}

		/*
		 * I prepare the list of virtual brokers which are inside this tea.
		 * 
		 * This tea then may have the possibility to have a personal equity,
		 * which is shared by all the real time brokers and another equity which
		 * is shared by all the paper trading brokers. The database trading,
		 * instead, does not have a common equity, as they also do not share a
		 * common virtual broker.
		 */
		SingleTeaHelper helper = new SingleTeaHelper(aTeaId, _root);
		_subTeas.put(aTeaId, helper);

		// /*
		// * This will create a new trading session for the given tea
		// identifier.
		// */
		// helper.sessionId = Db.i().newTradingSession(aTeaId);

		return helper;
	}

	/**
	 * Stops the multitea, which in turn will stop all the virtual brokers.
	 * 
	 * @throws TEAException
	 */
	public synchronized void stop() throws TEAException {
		for (String aTeaId : _subTeas.keySet()) {
			unregisterTea(aTeaId, false);
		}
		_broker.stop();
		Db.i().close();
	}

	/**
	 * @param aBroker
	 */
	synchronized void stoppedVirtualBroker(VirtualBrokerBase aBroker) {

		VirtualBrokerParams vbp = aBroker.getParams();
		_subTeas.get(vbp.teaId).closedTradingRun(aBroker);

		/*
		 * I have to unregister the broker from the tea which has been created
		 * it.
		 */
		// ArrayList<VirtualBrokerBase> brokers =
		// _subTeas.get(vbp.teaId)._brokers;
		// assert (brokers != null);
		//
		// brokers.remove(aBroker);

		/*
		 * Now I have to distinguish the different kind of parameters.
		 */
		if (!vbp.isRealTimeRequest) {
			/*
			 * a database request, so this is a simulated broker
			 */
			IVirtualBroker check = _simulatedVirtualBrokers
					.remove(vbp.virtualSymbol);
			assert (check != null); // it must be present.
		} else {
			/*
			 * real time request. It can be real, or a paper trading
			 */
			ArrayList<IVirtualBroker> arrToRemove;
			if (vbp.isPaperTradingRequested) {
				arrToRemove = _paperTradingBrokers.get(vbp.tradingSymbol)._brokers;
			} else {
				arrToRemove = _realVirtualBrokers.get(vbp.tradingSymbol)._brokers;
			}
			/*
			 * Now I remove the broker
			 */
			boolean removed = arrToRemove.remove(aBroker);
			assert (removed);
		}
	}

	/**
	 * unregisters the sub TEA.
	 * 
	 * <p>
	 * This actually signals that the client is going to end, most probably the
	 * application is stopping.
	 * 
	 * @param aTeaId
	 * @param allowRepeatedClose
	 *            if true it is safe to unregister a TEA which has not
	 *            registered or has been already unregistered. This is used by
	 *            the {@link TEAStub} in the last chance before the stub thread
	 *            dies.
	 * 
	 * @throws TEAException
	 */
	public synchronized void unregisterTea(String aTeaId,
			boolean allowRepeatedClose) throws TEAException {
		SingleTeaHelper singleTeaHelper = _subTeas.get(aTeaId);
		if (singleTeaHelper == null) {
			if (allowRepeatedClose) {
				U.debug_var(210591, "TEA id ", aTeaId,
						" was already closed or never opened");
				return; // it's ok
			}
			throw new TEAException("Unknown " + aTeaId + " to close");
		}

		singleTeaHelper.unregister(_root);
		_subTeas.remove(aTeaId);

	}

	/**
	 * updates the present order in the system.
	 * 
	 * <p>
	 * The order must be already present there.
	 * 
	 * @param aBrokerId
	 * @param aOrder
	 * @throws TEAException
	 */
	synchronized void updateOrder(int aBrokerId, OrderImpl aOrder)
			throws TEAException {
		int externalId = _getExternalId(aBrokerId, aOrder.getId());
		_broker.updateOrder(externalId, aOrder);
	}

}
