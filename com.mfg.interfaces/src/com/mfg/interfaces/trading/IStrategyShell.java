package com.mfg.interfaces.trading;

import java.util.List;

import com.mfg.broker.BrokerException;
import com.mfg.broker.IOrderMfg;
import com.mfg.broker.events.TradeMessageType;
import com.mfg.interfaces.indicator.IIndicator;

/**
 * The strategy shell is an object that gives a definite environment for the
 * strategies and it is able to get orders and give orders notifications.
 * 
 * <p>
 * For now the shell is very simple; there are two concrete shells which are
 * defined in the system, a simple shell used to optimize the strategy, it is
 * always connected to the market simulator, and a normal shell which is for now
 * the {@link PortfolioStrategy} class, which has all the logic for order log
 * and accounting and it is used in real time. This last one can also be
 * connected to a real broker
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public interface IStrategyShell {

	/**
	 * adds an order from the strategy given.
	 * 
	 * <p>
	 * The shell can run multiple strategies and it is able to organize its maps
	 * in order to make the notifications to the correct strategy, when the need
	 * arises.
	 * 
	 * <p>
	 * This method is asynchronous because the answer may come later.
	 * 
	 * @param strategy
	 *            the strategy that issues the order
	 * 
	 * @param order
	 *            the issued order.
	 * @throws MarketSimulException
	 */
	public void addOrder(IStrategy strategy, IOrderMfg order)
			throws BrokerException;

	/**
	 * cancels an order with a particular id.
	 * 
	 * @param aId
	 *            the id of the order you want to cancel. It must be an order
	 *            which has been added before by the same shell.
	 * @throws MarketSimulException
	 */
	public void cancelOrder(int aId) throws BrokerException;

	public IIndicator getIndicator();

	public void log(TradeMessageType type, String event, int orderID);

	public boolean isARelevantScale(int aWidgetScale);

	public List<IStrategy> getStrategies();
}
