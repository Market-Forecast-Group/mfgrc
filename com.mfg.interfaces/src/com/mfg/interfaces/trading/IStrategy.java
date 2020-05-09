package com.mfg.interfaces.trading;

import com.mfg.broker.IOrderExec;
import com.mfg.broker.IOrderMfg;
import com.mfg.broker.IOrderStatus;
import com.mfg.common.QueueTick;
import com.mfg.interfaces.indicator.IIndicator;

/**
 * This is the interface for all the strategies in the system. A strategy can
 * participate in a trading session which is composed of two different periods:
 * the warm up and the normal trading. During the warm up period no orders are
 * issued.
 * 
 */
public interface IStrategy {

	/**
	 * Initializes the strategy with the given tick size. The strategy needs the
	 * tick size only because it needs to create orders at a definite price, a
	 * strategy in itself is independent on the tick size, in particular its
	 * representation on disk does not impose a tick size, because that is given
	 * at the start of the strategy.
	 * 
	 * <p>
	 * The scale is not so important, because for the strategy all the prices
	 * are integer.
	 * 
	 * <p>
	 * Scale is important only when the real broker transmits the price orders
	 * to the outside and when it transforms the real world notifications for
	 * the strategy
	 * 
	 * @param tickSize
	 */
	public void begin(int tickSize);

	/**
	 * This function is called whenever the warm up finishes and the strategy is
	 * free to send real time orders.
	 */
	public void endWarmUp();

	public String getStrategyName();

	public StrategyType getStrategyType();

	public boolean isARelevantScale(int s);

	public void newExecution(IOrderExec anExec);

	public void newTick(QueueTick tick);

	public void orderStatus(IOrderStatus aStatus);

	public void setIndicator(IIndicator indicator);

	/**
	 * sets the shell for this strategy.
	 * 
	 * <p>
	 * The shell will be the main (unique!) point of communication between the
	 * strategy and the outside world.
	 * 
	 * @param shell
	 *            a shell (can be real or simulated).
	 */
	public void setShell(IStrategyShell shell);

	/**
	 * It should stop the trading closing the open positions with a market
	 * order.
	 */
	public void stopTrading();

	/**
	 * This method is called by the portfolio when an order is confirmed or
	 * cancelled by the user.
	 * 
	 * @param order
	 */
	public void orderConfirmedByUser(IOrderMfg order);
}
