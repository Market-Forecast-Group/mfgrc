package com.mfg.strategy;

import static com.mfg.utils.Utils.debug_var;

import com.mfg.broker.IOrderExec;
import com.mfg.broker.IOrderStatus;
import com.mfg.broker.events.TradeMessageType;
import com.mfg.common.QueueTick;
import com.mfg.interfaces.indicator.IIndicator;
import com.mfg.interfaces.trading.IStrategy;
import com.mfg.interfaces.trading.IStrategyShell;
import com.mfg.strategy.logger.StrategyMessage;
import com.mfg.strategy.logger.TradeMessageWrapper;
import com.mfg.utils.StepDefinition;
import com.mfg.utils.XmlIdentifier;

public abstract class AbstractStrategy extends XmlIdentifier implements
		IStrategy {

	protected IStrategyShell _shell;

	/**
	 * @param aShell
	 *            the shell to set
	 */
	@Override
	public void setShell(IStrategyShell aShell) {
		this._shell = aShell;
	}

	protected IIndicator indicator;

	protected long time;

	protected long price;

	protected int tickSize;
	private boolean warmingUp;
	private int strategyIndex = 0;
	protected StepDefinition tick;

	public AbstractStrategy() {

	}

	@Override
	public void begin(int tickSize1) {
		setTickSize(tickSize1);
		setWarmingUp(true);
	}

	@Override
	public void endWarmUp() {
		setWarmingUp(false);
	}

	public long getCurrentPrice() {
		return price;
	}

	public int getCurrentTime() {
		return (int) time;
	}

	public IIndicator getIndicator() {
		return indicator;
	}

	public int getStrategyIndex() {
		return strategyIndex;
	}

	@Override
	public String getStrategyName() {
		return getClass().getSimpleName();
	}

	public final StepDefinition getTick() {
		return tick;
	}

	public long getTickSize() {
		return tickSize;
	}

	/**
	 * @param aS
	 *            A relevant scale
	 */

	// Used on inner classes.
	@Override
	public boolean isARelevantScale(int aS) {
		return false;
	}

	public boolean isWarmingUp() {
		return warmingUp;
	}

	public void log(TradeMessageType type, String event, int orderID) {
		TradeMessageWrapper msg = new TradeMessageWrapper(new StrategyMessage(
				type, getStrategyName(), event, null));

		msg.setOrderID(orderID);

		log(msg);
	}

	public abstract void log(TradeMessageWrapper msg);

	/**
	 * 
	 * @param anExec
	 */
	public void manualExecution(IOrderExec anExec) {
		// Adding a comment to avoid empty block warning.
	}

	@Override
	public final void newTick(QueueTick aTick) {
		time = aTick.getFakeTime();
		price = aTick.getPrice();

		newTickImpl(aTick);
	}

	/**
	 * Called by the abstract strategy when a new tick comes. The current price
	 * and time are already been set.
	 * 
	 * @param aTick
	 */
	protected abstract void newTickImpl(QueueTick aTick);

	@Override
	public void orderStatus(IOrderStatus aStatus) {
		debug_var(329133, "Order status in the strategy oid ",
				Integer.valueOf(aStatus.getOrderId()), " status ",
				aStatus.getStatus());
	}

	@Override
	public void setIndicator(IIndicator indicator1) {
		this.indicator = indicator1;
	}

	public void setStrategyIndex(int aStrategyIndex) {
		strategyIndex = aStrategyIndex;
	}

	public void setTick(StepDefinition stepDefinition) {
		tick = stepDefinition;
		setTickSize(tick.getStepInteger());
	}

	private void setTickSize(int tickSize1) {
		this.tickSize = tickSize1;
	}

	public void setWarmingUp(boolean warmingUp1) {
		this.warmingUp = warmingUp1;
	}

	// /**
	// * @param brokerMsg
	// */
	// public void userEvent(ITradeMessage brokerMsg) {
	// //
	// }

}
