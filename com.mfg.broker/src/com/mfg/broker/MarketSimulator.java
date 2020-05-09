package com.mfg.broker;

import static com.mfg.utils.Utils.debug_var;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeMap;

import com.mfg.broker.IMarketSimulatorListener.EOrderStatus;
import com.mfg.broker.orders.OrderExecImpl;
import com.mfg.broker.orders.OrderImpl;
import com.mfg.common.QueueTick;

/**
 * This is the main class for the market simulator.
 * 
 * <p>
 * The simulator can be "in process" or "out of process". *
 * 
 * <p>
 * The market simulator is not thread safe, but this is not a problem because it
 * is called by a single thread of execution, usually the Trading Pipe thread
 * (in MFG all the strategies are single threaded).
 * 
 * <p>
 * This condition may be enforced easily (if needed).
 * 
 * <p>
 * The market simulator is <b>by design</b> limited to <i>one</i> symbol, or,
 * better, to one tick stream, so, in the new paradigm, to a virtual symbol.
 * 
 * <p>
 * In a certain sense there will be a market simulator object for each trading
 * session, even if TEA is remote, this because the remote market simulator will
 * handle a different tick stream.
 * 
 * <p>
 * A corollary to this is that all static fields will be removed.
 * 
 * <p>
 * Now the market simulator is tied to only one symbol. A virtual symbol.
 * 
 * <p>
 * The market simulator is tied actually to only one stream of prices, but it
 * could serve in theory, a set of virtual brokers.
 * 
 * 
 * @author Pasqualino
 * 
 */
public final class MarketSimulator /* implements IBroker */{

	private static void _modificationPrivate(SimulatedOrder so,
			IOrderMfg aNewOrder) {
		try {
			so.acknowledgeThisOrder(aNewOrder);
		} catch (IllegalStateException e) {
			debug_var(219155, "exc ", e, " while ack order, ", so);
		}

	}

	private final IMarketSimulatorListener fListener;

	/**
	 * Key is the internal id. Value is the order status.
	 */
	private final TreeMap<Integer, OrderStatus> fMapOrders = new TreeMap<>();

	private long _currentTime;

	private long _currentPrice;

	/**
	 * This boolean stores the fact that there have been orders' modifications
	 * in this tick, so the simulator must be rerun
	 */
	private boolean _modificationsInThisTick = false;

	private final ArrayList<SimulatedOrder> _pendingOrders = new ArrayList<>();

	// This contains the ids present in the system... it is used to speed up the
	// retrieval of
	// the orders (in case of modification).
	// It contains also the children ids.
	private final HashMap<Integer, SimulatedOrder> _presentIds = new HashMap<>();

	// /these are the orders which should be added during a next price traversal
	private final ArrayList<SimulatedOrder> _addendumOrders = new ArrayList<>();

	private final HashSet<Integer> _cancellanumOrders = new HashSet<>();

	private boolean _duringNextPrice = false;

	/**
	 * The tick is used to tell the simulated orders the slippage and extra tick
	 * quantity. They are no more static fields.
	 */
	private int _tick;

	/**
	 * The amount used to slip the execution price, it is common to all the
	 * orders for this particular symbol.
	 */
	private int _slippage;

	private int _extraPrice;

	/**
	 * I simply construct the market simulator.
	 * 
	 * @param aSlippageInTick
	 * @param aExtraPriceInTick
	 * 
	 * @param offline
	 *            true if the market is out_of_thread... that is it is run in
	 *            another thread so the orders must be copied.
	 * @param time_slippage
	 *            the number of ticks to wait (minimum) to accept the order in
	 *            the market.
	 */
	public MarketSimulator(IMarketSimulatorListener msl, int aSlippageInTick,
			int aExtraPriceInTick) {

		fListener = msl;

		/*
		 * They are saved here using the tick, and in the onStarting method they
		 * will be multiplied by the tick size.
		 */
		_slippage = aSlippageInTick;
		_extraPrice = aExtraPriceInTick;
	}

	/**
	 * adds a child order to the simulator, this child is supposed to be related
	 * to an executed parent.
	 * 
	 * @param oc
	 *            the child order.
	 */
	@SuppressWarnings("boxing")
	private void _addChildOrder(IOrderMfg oc) {

		OrderStatus os = fMapOrders.get(oc.getId());
		os.setStatus(EOrderStatus.ACCEPTED);
		fListener.orderStatus(os);

		_addSimulatedOrder(new SimulatedOrder(oc, _slippage, _extraPrice), true);

	}

	/**
	 * This function simply add an order. This function is different from the
	 * former because we build the SimulatedOrder object right here.
	 * 
	 * @return the id of this order.
	 */
	private int _addOrder(IOrderMfg o, long time) {
		assert (time >= _currentTime) : " t = " + time + " curTime "
				+ _currentTime;
		_currentTime = time;
		return _really_add_order(o);
	}

	@SuppressWarnings("boxing")
	private void _addOrderHelper(IOrderMfg ord) throws BrokerException {

		if (ord.isChild()) {
			// the order is a child order. Is the parent present?
			if (!fMapOrders.containsKey(ord.getParent().getId())) {
				throw new IllegalStateException(
						"Cannot add a child without first the parent!");
			}
			// Ok, the order is a child, so this is surely a modification
			if (fMapOrders.get(ord.getId()).getStatus() == EOrderStatus.WAITING_FOR_PARENT_EXECUTION
					|| fMapOrders.get(ord.getId()).getStatus() != EOrderStatus.ACCEPTED) {
				debug_var(391255, "The order ", ord,
						" cannot be modified. Status: ",
						fMapOrders.get(ord.getId()).getStatus());
				// fListener
				// .error(ECategoryError.SOFTWARE_ERROR, ord.getId(),
				// "Cannot modify this order, it is cancelled or executed.");
				// return;

				throw new BrokerException(
						"Cannot modify this order, it is cancelled or executed.");
			}
			// Ok, I can try to modify the child order... modification of child
			// orders may be different
			// for different brokers, because they can have different workflows.
			IOrderMfg ordOld = fMapOrders.get(ord.getId()).getOrder();
			debug_var(324213, "modify child order from ", ordOld, " to ", ord);
			_modifyChildOrder(fMapOrders.get(ord.getId()).getOrder(), ord);
			return;

		} else if (fMapOrders.containsKey(ord.getId())) {
			// this is a modification of a parent.
			IOrderMfg ordOld = fMapOrders.get(ord.getId()).getOrder();
			debug_var(291951, "modify parent order from ", ordOld, " to ", ord);
			_modifyOrderImpl(ordOld, ord);
			return;
		}

		// this is a real addition.
		int extId = _addOrderImpl(ord);

		if (extId > 0) {
			_initialize_maps(extId, ord);
		}

	}

	/**
	 * This method is used to add an order to the broker. The method should
	 * return the external id which is used to identify the order in the broker.
	 * Concrete brokers should return a negative number to signal an error. They
	 * should not throw any exception and every error (eventual) should be
	 * signaled through the callback interface.
	 * 
	 * <p>
	 * Note that we are speaking of synchronous errors, which can happen usually
	 * when there are connection problems. Any other error will be signaled
	 * asynchronously, as usual.
	 * 
	 * @param ord
	 *            the order to add to the market.
	 * 
	 * 
	 * @return the external id which is used to identify the order, a negative
	 *         number signals an error.
	 */
	private int _addOrderImpl(IOrderMfg ord) {
		_addOrder(ord, this._currentTime);
		if (ord.getBrokerId() < 0) {
			/*
			 * I have to set the broker id artificially equal to the application
			 * id.
			 */
			((OrderImpl) ord).setBrokerId(ord.getId());
			for (IOrderMfg child : ord.getChildren()) {
				((OrderImpl) child).setBrokerId(child.getId());
			}
		}
		return ord.getId(); // internal and external id are the same.
	}

	/**
	 * This is used to add an order to the market, the order inside has a
	 * "Strategy id" used to identify this order.
	 */
	@SuppressWarnings("boxing")
	private void _addSimulatedOrder(SimulatedOrder so, boolean isChildAccepted) {

		if (_duringNextPrice) {

			if (so.getOrder().isChild() && !isChildAccepted) {
				throw new IllegalStateException("Cannot add a child here "
						+ so.getOrder());
			}

			Iterator<SimulatedOrder> it;
			it = _addendumOrders.iterator();
			while (it.hasNext()) {
				SimulatedOrder sit = it.next();
				if (sit.getOrderId() == so.getOrderId()) {
					return;
				}
			}
			_addendumOrders.add(so);
		} else {
			_pendingOrders.add(so);
			_presentIds.put(so.getOrderId(), so);
		}
	}

	/**
	 * This function simply cancels an order The order can be also partially
	 * executed. If you cancel the father, you also cancel the children, so be
	 * careful.
	 * 
	 * It is package private, it can be called by the test code
	 * 
	 * @return true if the order has been deleted
	 * @throws BrokerException
	 * @throws MarketSimulException
	 */
	@SuppressWarnings("boxing")
	private synchronized boolean _cancelOrderPrivate(int orderId)
			throws BrokerException {
		if (isTotallyExecuted(orderId)) {
			throw new BrokerException("Cannot cancel executed order " + orderId);
		}

		if (_duringNextPrice) {
			_cancellanumOrders.add(orderId);
			return true;
		}
		Iterator<SimulatedOrder> it = _pendingOrders.iterator();
		while (it.hasNext()) {
			SimulatedOrder so = it.next();
			if (so.getOrderId() == orderId) {
				OrderImpl o = (OrderImpl) so.getOrder();
				o.cancelled();

				it.remove();
				_presentIds.remove(orderId);

				_notify_cancel_order_complete(o);
				for (IOrderMfg child_o : o.getChildren()) {
					_notify_cancel_order_complete(child_o);
				}

				return true;
			}
		}
		return false;
	}

	private void _clear() {
		_pendingOrders.clear();
		fMapOrders.clear();
		_presentIds.clear();
		_currentTime = -1;
	}

	/**
	 * initialize the maps after I have added the external id.
	 * 
	 * <p>
	 * For the broker this means to add the order and all its children to the
	 * maps.
	 * 
	 * @param extId
	 *            the external id which has come after adding the order
	 * 
	 * @param ord
	 *            the order added. It must be a parent!
	 */
	@SuppressWarnings("boxing")
	private void _initialize_maps(int extId, IOrderMfg ord) {
		assert (!ord.isChild());

		/* This is the only place where the Order Status object is created. */
		OrderStatus os = new OrderStatus(ord);
		os.setStatus(EOrderStatus.STILL_IN_APP);
		fMapOrders.put(ord.getId(), os);

		for (IOrderMfg oc : ord.getChildren()) {
			OrderStatus os1 = new OrderStatus(oc);
			os1.setStatus(EOrderStatus.WAITING_FOR_PARENT_EXECUTION);
			fMapOrders.put(oc.getId(), os1);
		}

	}

	private void _modifyChildOrder(IOrderMfg ordOld, IOrderMfg ordNew) {
		/*
		 * for the market simulator the modification of a child is equal to the
		 * modification of a parent.
		 */
		_modifyOrderImpl(ordOld, ordNew);
	}

	/**
	 * modifies (to the market) the order <code>ordOld</code> and substitutes it
	 * with <code>ordNew</code> For some brokers the modification is simple:
	 * just add the same order twice and the new will overwrite the old. For
	 * some other brokers, like IW, the modification is not so simple. The old
	 * order must be deleted and the new order inserted. This method returns
	 * immediately but the modification is done in two steps. The broker ensures
	 * that the new order is not sent until the old order is deleted from the
	 * market. If the cancellation is not possible then the old order reamins
	 * active. Every notification is done with the socket, using the
	 * {@linkplain IMarketSimulatorListener} interface
	 * 
	 * @param ordOld
	 *            the old order to be cancelled
	 * @param ordNew
	 *            the new order which will overwrite the old
	 */
	private void _modifyOrderImpl(IOrderMfg ordOld, IOrderMfg ordNew) {
		assert (ordOld.getId() == ordNew.getId());
		_addOrder(ordNew, this._currentTime);
	}

	private void _newPrice(long time, long price) throws BrokerException {
		assert (time >= _currentTime) : "t = " + time + " curTime "
				+ _currentTime;
		_currentTime = time;
		_currentPrice = price;

		do {
			_modificationsInThisTick = false;
			_nextPrice_internal();

		} while (_modificationsInThisTick || _addendumOrders.size() != 0);

	}

	/**
	 * This is called whenever I receive a new price from the ticker simulator.
	 * 
	 * @throws MarketSimulException
	 * @throws BrokerException
	 */
	@SuppressWarnings("boxing")
	private void _nextPrice_internal() throws BrokerException {
		_duringNextPrice = true;
		_addendumOrders.clear();
		_cancellanumOrders.clear();

		Iterator<SimulatedOrder> it = _pendingOrders.iterator();
		@SuppressWarnings("unused")
		boolean someExecution = false;
		while (it.hasNext()) {
			SimulatedOrder so = it.next();

			// Is this order cancelled?
			if (_cancellanumOrders.contains(so.getOrderId())) {
				continue;
			}

			/* before asking to the order, I change the state */
			OrderStatus os = fMapOrders.get(so.getOrderId());
			if (os.getStatus() == EOrderStatus.STILL_IN_APP) {
				if (os.getOrder().isChild()) {
					os.setStatus(EOrderStatus.WAITING_FOR_PARENT_EXECUTION);
				} else {
					os.setStatus(EOrderStatus.ACCEPTED);
				}

				fListener.orderStatus(os);
			}

			so.nextPrice(_currentPrice, _currentTime);

			if (so.areYouExecuted()) {

				if (fMapOrders.containsKey(so.getOrderId())
						&& fMapOrders.get(so.getOrderId()).isTotallyExecuted()) {
					throw new IllegalStateException(
							"Cannot execute an order twice! + " + so.getOrder());
				}

				someExecution = true;

				addTotalExecution(so.getOrder(), so.getExecutionTime(),
						so.getExecutionPrice());

				boolean isChild = so.getOrder().isChild();

				if (so.getOrder().getChildren().size() != 0) {
					Iterator<IOrderMfg> itc = so.getOrder().getChildren()
							.iterator();
					while (itc.hasNext()) {
						IOrderMfg oc = itc.next();
						_addChildOrder(oc);
					}
				}

				// I update FIRST the log of trade, then I notify the others...

				if (isChild) {

					// debug code...
					boolean childRemoved = false;
					// end debug code

					// let's cancel the children
					Iterator<IOrderMfg> itc = so.getOrder().getParent()
							.getChildren().iterator();
					while (itc.hasNext()) {
						IOrderMfg oc = itc.next();
						if (oc.getId() != so.getOrderId()) {

							boolean res = _cancelOrderPrivate(oc.getId());
							assert (res);
						} else {
							childRemoved = true;
						}
					}

					assert (childRemoved) : "Cannot find the child "
							+ so.getOrder() + " in the children of "
							+ so.getOrder().getParent();

					OrderStatus parentStatus = this.fMapOrders.get(so
							.getOrder().getParent().getId());
					parentStatus
							.setStatus(EOrderStatus.NEUTRALIZED_BY_CHILD_EXECUTION);
					fListener.orderStatus(parentStatus);

				}

				it.remove(); // In any case I remove this simulated order!
				_presentIds.remove(so.getOrderId());

				_notifyAnExecution(so);
			}

		}
		_duringNextPrice = false;

		// I add the _addendumOrders
		it = _addendumOrders.iterator();
		while (it.hasNext()) {
			_addSimulatedOrder(it.next(), false);
		}

		Iterator<Integer> itIt = _cancellanumOrders.iterator();

		while (itIt.hasNext()) {
			boolean res = _cancelOrderPrivate(itIt.next().intValue());
			assert (res);
		}

		assert (_pendingAndPresentIdsAreCoherent());
	}

	/**
	 * Private helper function used to assert that the order is not in the
	 * addendum...
	 */
	private boolean _notContainedInTheAddendumOrdersVector(IOrderMfg o) {
		for (SimulatedOrder so : _addendumOrders) {
			if (so.getOrderId() == o.getId()) {
				return false; // auch! It is present!
			}
		}
		return true;
	}

	/**
	 * Notifies and logs the real cancellation of an order.
	 */
	@SuppressWarnings("boxing")
	private void _notify_cancel_order_complete(IOrderMfg o) {
		OrderStatus os = fMapOrders.get(o.getId());
		os.setStatus(EOrderStatus.CANCELLED);
		fListener.orderStatus(os);
	}

	private void _notifyAnExecution(SimulatedOrder so) {
		OrderExecImpl oei = new OrderExecImpl(so.getOrder(),
				so.getExecutionTime(), so.getExecutionPrice());
		fListener.newExecution(oei);
	}

	/**
	 */
	@SuppressWarnings("boxing")
	private boolean _pendingAndPresentIdsAreCoherent() {
		boolean res = true;

		assert (_pendingOrders.size() == _presentIds.size());

		for (SimulatedOrder so : _pendingOrders) {
			if (!_presentIds.containsKey(so.getOrderId())) {
				res = false;
				break;
			}
		}

		return res;
	}

	/**
	 * Really adds the order in the simulator, either because the time slip is
	 * zero, or if the time has come.
	 */
	@SuppressWarnings("boxing")
	private int _really_add_order(IOrderMfg o) {

		if (isTotallyExecuted(o.getId())) {
			throw new IllegalStateException(
					"You cannot add or modify an executed order!" + o.getId());
		}

		// YOU CANNOT ADD A CHILD (it is added automatically by the system...).
		// So if this order is a child... well, I assume that it is a
		// modification!
		if (o.isChild()) {
			SimulatedOrder so = _presentIds.get(o.getId());
			if (so != null) {
				_modificationPrivate(so, o);
			} else {
				// there isn't the simulated order... but maybe it is just
				// added!
				// it is in the addendum vector
				for (SimulatedOrder so1 : _addendumOrders) {
					if (o.getId() == so1.getOrderId()) {
						_modificationPrivate(so1, o);
						break;
					}
				}
			}
			_modificationsInThisTick = true;
			return o.getId();
		}

		// Order is a parent
		if (_presentIds.containsKey(o.getId())) {
			// Ok, this is a modification!!! don't do anything, the
			// simulated order wraps already the modified object!
			SimulatedOrder so = _presentIds.get(o.getId());
			_modificationPrivate(so, o);
			_modificationsInThisTick = true;
			return o.getId();
		}
		assert (_notContainedInTheAddendumOrdersVector(o)) : "You are trying to add an order twice, why?";
		assert (!_presentIds.containsKey(o.getId())) : "wrong!!!!";
		// let's add finally this order.

		SimulatedOrder so = new SimulatedOrder(o, _slippage, _extraPrice);
		_addSimulatedOrder(so, false);
		return so.getOrderId();
	}

	public synchronized void addOrder(IOrderMfg ord) throws BrokerException {

		_addOrderHelper(ord);
		if (_currentTime != -1) { // Have I received a valid price?
			_newPrice(_currentTime, _currentPrice);
		}

	}

	/**
	 * Adds a total execution in the map, the order is considered to be totally
	 * executed
	 * 
	 * @param anOrder
	 *            the order which has been totally executed
	 * 
	 * @param anExecutionTime
	 *            the execution time (can be fake or physical depending on the
	 *            broker).
	 * 
	 * @param anExecutionPrice
	 */
	@SuppressWarnings("boxing")
	private void addTotalExecution(IOrderMfg anOrder, long anExecutionTime,
			long anExecutionPrice) {
		OrderStatus os = fMapOrders.get(anOrder.getId());
		os.setTotalFilled(anExecutionTime, anExecutionPrice);
	}

	@SuppressWarnings("boxing")
	public synchronized void cancelOrder(int aId) throws BrokerException {
		if (!_cancelOrderPrivate(aId)) {
			debug_var(213145, "Cannot cancel the order ", aId);
			throw new BrokerException("Cannot cancel the order " + aId);
		}
		debug_var(299144, "MS: the order ", aId, " has been cancelled");
	}

	public int getPendingOrdersCount() {
		return _pendingOrders.size();
	}

	/**
	 * returns true if the order is totally executed.
	 * 
	 * @param aOid
	 *            an order id.
	 * 
	 * @return true if the order is totally executed.
	 */
	@SuppressWarnings("boxing")
	private boolean isTotallyExecuted(int aOid) {
		if (fMapOrders.containsKey(aOid)
				&& fMapOrders.get(aOid).getStatus() == EOrderStatus.TOTAL_FILLED) {
			return true;
		}
		return false;
	}

	/**
	 * @param aId
	 * @param aOrder
	 */
	public synchronized void modifyOrder(int aId, IOrderMfg aOrder) {
		//
		// this is a modification, so the order must be already present in the
		// map.
	}

	public synchronized void newTick(QueueTick qt) throws BrokerException {
		if (qt.getReal()) {
			_currentTime = qt.getPhysicalTime();
			_currentPrice = qt.getPrice();
			_newPrice(qt.getPhysicalTime(), qt.getPrice());
		}
	}

	/**
	 * @param scale
	 */
	public void onStarting(int tick, int scale) {
		_clear();
		_tick = tick;
		_slippage *= _tick;
		_extraPrice *= tick;
	}

	/**
	 * Resets the parameter to initial values, because they will be then altered
	 * by the {@link #onStarting(int, int)} method.
	 * 
	 * @param slippageInTick
	 * @param extraPriceInTick
	 */
	public void reset(int slippageInTick, int extraPriceInTick) {
		_slippage = slippageInTick;
		_extraPrice = extraPriceInTick;
	}

}
