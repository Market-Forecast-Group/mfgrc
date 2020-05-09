package com.mfg.tea.conn;

import com.mfg.broker.IOrderMfg;
import com.mfg.broker.MarketSimulator;
import com.mfg.broker.orders.OrderImpl;
import com.mfg.common.DFSSymbolEvent;
import com.mfg.common.TEAException;
import com.mfg.tea.accounting.MixedInventoriesFolder;
import com.mfg.tea.accounting.StockInfo;

/**
 * The real virtual broker is a broker connected to a real broker, not a market
 * simulator.
 * 
 * <p>
 * Well, it is a bit more complicated. The virtual broker <b>may</b> be
 * connected to a simulator but it does not know.
 * 
 * <p>
 * This is the difference between the {@link SingleSimulBroker} and this class.
 * The {@link SingleSimulBroker} contains a {@link MarketSimulator} which is
 * used to fill the orders, and it is a private property of the Simulated
 * broker.
 * 
 * <p>
 * Now, instead, we have a real broker whose only individuality is composed by
 * the listener, which connects directly to the local or proxy tea from which
 * the orders are issued.
 * 
 * <p>
 * This class does not implement the {@link IRealBrokerListener} interface
 * because only {@link MultiTEA} does it.
 * 
 * <p>
 * In reality this is only a stub for the implementation of the single broker
 * interface which simply passes the broker id parameter to the {@link MultiTEA}
 * object.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
class RealVirtualBroker extends VirtualBrokerBase {

	/**
	 * The real virtual broker needs MultiTEA to be notified
	 * 
	 * @param aParams
	 * @param aParent
	 * @param aStockInfo
	 */
	protected RealVirtualBroker(int aId, VirtualBrokerParams aParams,
			MixedInventoriesFolder aMixParent, StockInfo aStockInfo) {
		super(aId, aParams, aMixParent, aStockInfo);
	}

	@Override
	public void start() throws TEAException {
		/*
		 * no op here
		 */

		/*
		 * Well, the real virtual broker must connect to the virtual symbol
		 * which is attached to the trading pipe associated with this virtual
		 * broker in order to update the local open equity of this investor.
		 * 
		 * Parallel to this open equity we will have of course the global open
		 * equity of the composite real broker, but that may be updated by
		 * chaining (I have to look at this better).
		 */

	}

	@Override
	public void _stopImpl() {
		/*
		 * no op here
		 */
	}

	@Override
	public void _placeOrderImpl(OrderImpl aOrder) throws TEAException {
		/*
		 * OK. I place an order but I do not know if this is connected to a
		 * simulated data feed. Is necessary to do this for this broker? Maybe
		 * not... because this broker is "REAL" and it behaves only as a real
		 * virtual broker, the only thing that it knows is the real trading
		 * symbol which must be coherent to the issued order.
		 */

		// aOrder.setTradingSymbol(_params.tradingSymbol);
		_multiBroker.placeOrder(_id, aOrder);
	}

	@Override
	public void dropOrder(int aOrderId) throws TEAException {
		_multiBroker.dropOrder(_id, aOrderId);
	}

	@Override
	public void updateOrder(IOrderMfg newOrder) throws TEAException {
		assert (false) : "to do";
		// _multiBroker.updateOrder(_id, newOrder);
	}

	@Override
	protected void _onNewQuoteImpl(DFSSymbolEvent anEvent) {
		/*
		 * Here it is a no-op, because, even if the real broker is connected to
		 * a simulator, the simulator is inside the real broker, not here.
		 */
	}

}
