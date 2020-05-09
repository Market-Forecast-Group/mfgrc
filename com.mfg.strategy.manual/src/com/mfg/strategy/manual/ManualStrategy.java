package com.mfg.strategy.manual;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.mfg.broker.IExecutionReport;
import com.mfg.broker.IOrderExec;
import com.mfg.broker.IOrderMfg;
import com.mfg.broker.IOrderMfg.EAccountRouting;
import com.mfg.broker.events.TradeMessage;
import com.mfg.common.QueueTick;
import com.mfg.interfaces.indicator.IIndicator;
import com.mfg.interfaces.trading.StrategyType;
import com.mfg.logger.ILogger;
import com.mfg.strategy.FinalStrategy;
import com.mfg.strategy.ManualStrategySettings;
import com.mfg.strategy.PendingOrderInfo;
import com.mfg.strategy.manual.interfaces.IAccountStatus;
import com.mfg.strategy.manual.interfaces.IManualStrategy2;
import com.mfg.strategy.manual.interfaces.IManualStrategyAlgorithmEnvironment;
import com.mfg.tea.conn.IDuplexStatistics;
import com.mfg.tea.conn.ISingleAccountStatistics;
import com.mfg.utils.ListenerSupport;

public class ManualStrategy extends FinalStrategy implements IManualStrategy2,
		IManualStrategyAlgorithmEnvironment, IAccountStatus {

	private TrailingStatus[] trailingStatusMap;
	private ManualStrategySettings settings;
	private boolean stopped;
	private final ListenerSupport listenerSupport;
	private QueueTick lastTick;

	public ManualStrategy() {
		settings = new ManualStrategySettings();
		stopped = false;
		listenerSupport = new ListenerSupport();
	}

	@Override
	public void setIndicator(IIndicator indicator1) {
		super.setIndicator(indicator1);
		trailingStatusMap = new TrailingStatus[getScaleCount() + 1];
		for (int level = 1; level <= getScaleCount(); level++) {
			trailingStatusMap[level] = new TrailingStatus();
		}
	}

	@Override
	public void setManualStrategySettings(ManualStrategySettings settings1) {
		this.settings = settings1;
	}

	@Override
	public ManualStrategySettings getManualStrategySettings() {
		return settings;
	}

	public IManualStrategyAlgorithmEnvironment getStrategyEnvironment() {
		return this;
	}

	@Override
	public StrategyType getStrategyType() {
		return StrategyType.MANUAL;
	}

	@Override
	public String getStrategyName() {
		return "Manual Strategy";
	}

	@Override
	public void newTickImpl(QueueTick aTick) {
		// super.newTick(tick);
		if (!stopped) {
			ManualStrategyAlgorithm.processNewTick(this, settings);
		}
		lastTick = aTick;
	}

	/**
	 * @return the lastTick
	 */
	@Override
	public QueueTick getLastTick() {
		return lastTick;
	}

	@Override
	public void newExecution(IOrderExec anExec) {
		super.newExecution(anExec);
		if (getOrdersMap().containsKey(Integer.valueOf(anExec.getOrderId()))) {
			if (!stopped) {
				ManualStrategyAlgorithm.newExecution(this, anExec);
			}
			getStrategyEnvironment().fireStateChanged();
			getPortfolio().manualExecution(anExec);
		}
	}

	@Override
	public void executeCommand(Command command) {
		if (!stopped) {
			ManualStrategyAlgorithm.processCommand(this, command);
		}
	}

	@Override
	public void stopTrading() {
		log(TradeMessage.WHITE_COMMENT, "Closing Manual Strategy trading", 0);
		ManualStrategyAlgorithm.stopTrading(this);
		stopped = true;
	}

	@Override
	public void begin(int aTickSize) {
		super.begin(aTickSize);
	}

	@Override
	public IOrderMfg[] getLongActiveOrders() {
		List<IOrderMfg> list = getLongOpenedOrders();
		return list.toArray(new IOrderMfg[list.size()]);
	}

	@Override
	public IOrderMfg[] getShortActiveOrders() {
		List<IOrderMfg> list = getShortOpenedOrders();
		return list.toArray(new IOrderMfg[list.size()]);
	}

	private IOrderMfg[] getPendingOrders(EAccountRouting routing) {
		PendingOrderInfo[] pendingOrders = getPortfolio().getPendingOrders();
		List<IOrderMfg> result = new ArrayList<>();
		for (PendingOrderInfo info : pendingOrders) {
			IOrderMfg order = info.getOrder();
			if (order.getAccountRouting() == routing) {
				result.add(order);
			}
		}
		return result.toArray(new IOrderMfg[result.size()]);
	}

	@Override
	public IOrderMfg[] getLongPendingOrders() {
		return getPendingOrders(EAccountRouting.LONG_ACCOUNT);
	}

	@Override
	public IOrderMfg[] getShortPendingOrders() {
		return getPendingOrders(EAccountRouting.SHORT_ACCOUNT);
	}

	@Override
	public void sendOrder(IOrderMfg order) {
		addOrder(order);
	}

	@Override
	public void addStateChangedListener(Runnable runnable) {
		listenerSupport.addListener(runnable);
	}

	@Override
	public void removeStateChangedListener(Runnable runnable) {
		listenerSupport.removeListener(runnable);
	}

	@Override
	public void fireStateChanged() {
		listenerSupport.fire();
	}

	@Override
	public Long getExecutionPrice(IOrderMfg order) {
		HashMap<Integer, IExecutionReport> map = getPortfolio()
				.getExecutionReportsMap();
		IExecutionReport report = map.get(Integer.valueOf(order.getId()));
		return report == null ? null : Long.valueOf(report.getExecutionPrice());
	}

	@Override
	public int getTickScale() {
		return getPortfolio().getTick().getStepScale();
	}

	@Override
	public void cancelOrder(IOrderMfg order) {
		getPortfolio().cancelOrder(order.getId());
	}

	public ILogger getLogger() {
		return getPortfolio().getLogger();
	}

	public int getScaleCount() {
		return getIndicator().getParamBean().getIndicatorNumberOfScales();
	}

	@Override
	public TrailingStatus getTrailingStatus(int level) {
		return trailingStatusMap[level];
	}

	@Override
	public void cancelTrailings(Routing routing) {
		for (TrailingStatus status : trailingStatusMap) {
			if (status != null) {
				Trailing.CL.setTrailing(status, routing, false);
				Trailing.RC.setTrailing(status, routing, false);
				Trailing.SC.setTrailing(status, routing, false);
			}
		}
	}

	@Override
	public IAccountStatus getAccountStatus() {
		return this;
	}

	@Override
	public long getShortQuantity() {
		// GlobalAcount globalAcount = getPortfolio().getGlobalAcount();
		IDuplexStatistics currentPeriodAccount = getPortfolio().getAccount();
		if (currentPeriodAccount == null) {
			return 0;
		}
		ISingleAccountStatistics shortAccount = currentPeriodAccount
				.getShortStatistics();
		return Math.abs(shortAccount.getQuantity());
	}

	@Override
	public long getLongQuantity() {
		IDuplexStatistics currentPeriodAccount = getPortfolio().getAccount();
		if (currentPeriodAccount == null) {
			return 0;
		}
		ISingleAccountStatistics longAccount = currentPeriodAccount
				.getLongStatistics();
		return longAccount.getQuantity();
	}

	@Override
	public boolean hasShortPendingOrders() {
		return getPendingOrders(EAccountRouting.SHORT_ACCOUNT).length > 0;
	}

	@Override
	public boolean hasLongPendingOrders() {
		return getPendingOrders(EAccountRouting.LONG_ACCOUNT).length > 0;
	}

	// @Override
	// protected void _toJsonEmbedded(JSONStringer stringer) throws
	// JSONException {
	// assert (false);
	// }
	//
	// @Override
	// protected void _updateFromJSON(JSONObject json) throws JSONException {
	// assert (false);
	// }
}
