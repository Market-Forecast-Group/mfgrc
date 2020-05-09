package com.mfg.strategy.manual.interfaces;

import com.mfg.broker.IOrderMfg;
import com.mfg.broker.events.TradeMessageType;
import com.mfg.common.QueueTick;
import com.mfg.interfaces.indicator.IIndicator;
import com.mfg.strategy.FinalStrategy;
import com.mfg.strategy.logger.TradeMessageWrapper;
import com.mfg.strategy.manual.Routing;
import com.mfg.strategy.manual.TrailingStatus;

/**
 * This interface provide a strategy environment
 * 
 * @author arian
 * 
 */
public interface IManualStrategyAlgorithmEnvironment {
	public IIndicator getIndicator();

	/**
	 * The active orders in the Long account
	 * 
	 * @return
	 */
	public IOrderMfg[] getLongActiveOrders();

	/**
	 * The active orders in the Short account
	 * 
	 * @return
	 */
	public IOrderMfg[] getShortActiveOrders();

	/**
	 * The pending orders in the Long account
	 * 
	 * @return
	 */
	public IOrderMfg[] getLongPendingOrders();

	/**
	 * The pending orders in the Short account
	 * 
	 * @return
	 */
	public IOrderMfg[] getShortPendingOrders();

	/**
	 * Current price
	 * 
	 * @return
	 */
	public long getCurrentPrice();

	/**
	 * With this method the strategy send an order to the market
	 * 
	 * @param order
	 */
	public void sendOrder(IOrderMfg order);

	/**
	 * Get the tick size
	 * 
	 * @return
	 */
	public long getTickSize();

	public void addStateChangedListener(Runnable runnable);

	public void removeStateChangedListener(Runnable runnable);

	public void fireStateChanged();

	public void log(TradeMessageWrapper msg);

	public void log(TradeMessageType type, String event, int orderID);

	public Long getExecutionPrice(IOrderMfg order);

	public int getTickScale();

	public void cancelOrder(IOrderMfg order);

	public IAccountStatus getAccountStatus();

	public TrailingStatus getTrailingStatus(int level);

	public void cancelTrailings(Routing routing);

	public QueueTick getLastTick();

	/**
	 * 
	 * @return the next valid id for this strategy, this is simply routed to the
	 *         {@link FinalStrategy} class.
	 */
	public int getNextOrderId();
}
