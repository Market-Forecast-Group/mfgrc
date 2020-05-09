package com.mfg.strategy;

import static com.mfg.utils.Utils.debug_var;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

import com.mfg.broker.IExecutionReport;
import com.mfg.broker.IMarketSimulatorListener.EOrderStatus;
import com.mfg.broker.IOrderExec;
import com.mfg.broker.IOrderMfg;
import com.mfg.broker.IOrderMfg.EAccountRouting;
import com.mfg.broker.IOrderStatus;
import com.mfg.broker.events.ITradeMessage;
import com.mfg.broker.events.OrderMessage;
import com.mfg.broker.events.TradeMessage;
import com.mfg.broker.events.TradeMessageType;
import com.mfg.broker.orders.OrderImpl;
import com.mfg.broker.orders.OrderUtils;
import com.mfg.common.QueueTick;
import com.mfg.common.TEAException;
import com.mfg.dm.TickDataSource;
import com.mfg.interfaces.indicator.IIndicator;
import com.mfg.interfaces.trading.Configuration;
import com.mfg.interfaces.trading.IPositionListener;
import com.mfg.interfaces.trading.IStrategy;
import com.mfg.interfaces.trading.IStrategyShell;
import com.mfg.interfaces.trading.PositionClosedEvent;
import com.mfg.interfaces.trading.PositionEvent;
import com.mfg.interfaces.trading.PositionOpenedEvent;
import com.mfg.interfaces.trading.StrategyType;
import com.mfg.logger.ILogger;
import com.mfg.logger.LogLevel;
import com.mfg.strategy.logger.StrategyMessage;
import com.mfg.strategy.logger.TradeMessageWrapper;
import com.mfg.strategy.ui.ConfirmationDialog;
import com.mfg.symbols.SymbolsPlugin;
import com.mfg.symbols.trading.configurations.TradingConfiguration;
import com.mfg.tea.conn.IDuplexStatistics;
import com.mfg.tea.conn.ISingleAccountStatistics;
import com.mfg.tea.conn.IVirtualBroker;
import com.mfg.tea.conn.IVirtualBrokerListener;
import com.mfg.ui.UIPlugin;
import com.mfg.utils.ObjectListenersGroup;
import com.mfg.utils.StepDefinition;
import com.mfg.utils.U;
import com.mfg.utils.Utils;
import com.mfg.widget.WidgetPlugin;
import com.mfg.widget.probabilities.DistributionsContainer;

/**
 * The portfolio is a temporary class which will be merged into the virtual
 * broker.
 * 
 * <p>
 * The portfolio links all the patterns which uses the same indicator and the
 * same virtual symbol. There may be more than one... but it does not handle
 * orders, they are routed to the virtual broker.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class PortfolioStrategy implements IStrategyShell,
		IVirtualBrokerListener {

	/*
	 * This class handles different strategies which go to the same virtual
	 * broker, they have also the same input (a data source) and the same widget
	 * (with the same parameters).
	 */

	private final List<IStrategy> myStrategies;

	private final ProbabilitiesDealer pdealer;

	private DistributionsContainer distribution;
	private Configuration configuration;
	private final boolean usingProbabilities;
	private IPositionListener[] positionListeners;
	protected IVirtualBroker broker;

	// private final GlobalAcount globalAcount;
	// private final PeriodAccount _account;
	private final HashMap<Integer, IOrderMfg> ordersMap;
	private final HashMap<Integer, IExecutionReport> reportsMap;
	private final Map<Integer, PendingOrderInfo> pendingOrdersMap;
	private PendingOrderInfo[] pendingOrders;
	private final List<Object> openedOrders = new ArrayList<>();
	private final List<PositionClosedEvent> closedOrders = new ArrayList<>();

	private final HashMap<Integer, PositionOpenedEvent> openedOrdersMap = new HashMap<>();
	private ILogger logger;
	// private final boolean waitingForConfirmation = false;

	private final ObjectListenersGroup<QueueTick> tickProcessed = new ObjectListenersGroup<>();
	protected long time;
	protected long price;

	// private boolean warmingUp;

	private boolean[] relScales;

	private PositionClosedEvent resClosed;

	private IIndicator indicator;

	private int tickSize;

	private StepDefinition tick;

	// private final String _tradedSymbol;

	// private final String _shellId;

	/**
	 * This data source is stored for the warm up flag.
	 */
	private final TickDataSource _tickDataSource;

	private TradingConfiguration _tradingConf;

	IOrderMfg _confirmOrder;

	Object _orderConfirmMutex = new Object();

	private Thread _confirmThread;

	/**
	 * 
	 * @param tickValue
	 * @param isUsingProbs
	 * @param probabilityName
	 * @param aTradingSymbol
	 * 
	 * @param aShellId
	 *            the shell identifier, this is usually the toString
	 *            representation of the {@code UUID} object which is associated
	 *            with the trading pipe.
	 * @param aTickDataSource
	 *            the data source which will give ticks to the portfolio.
	 */
	public PortfolioStrategy(double tickValue, boolean isUsingProbs,
			String probabilityName, TickDataSource aTickDataSource,
			TradingConfiguration tradingConf) {
		_tradingConf = tradingConf;

		_tickDataSource = aTickDataSource;

		// _tradedSymbol = aTradingSymbol;
		// _shellId = aShellId;

		ordersMap = new HashMap<>();
		reportsMap = new HashMap<>();
		pendingOrdersMap = new HashMap<>();
		pendingOrders = new PendingOrderInfo[0];

		// globalAcount = new GlobalAcount(tickValue, tick);
		// _account = new PeriodAccount(tickValue, tick);
		myStrategies = new ArrayList<>();
		pdealer = new ProbabilitiesDealer();
		usingProbabilities = isUsingProbs;
		if (usingProbabilities) {
			distribution = WidgetPlugin.getDefault().getProbabilitiesManager()
					.getProbabilityFromName(probabilityName);
			if (distribution != null)
				configuration = distribution.getConfiguration();
		}
	}

	public TradingConfiguration getTradingConfiguration() {
		return _tradingConf;
	}

	@Override
	public void addOrder(IStrategy strategy, IOrderMfg order) {

		if (isWarmingUp()) {
			U.debug_var(294825, "ignored order ", order,
					" from strategy during warming up, why did you send it?");
			throw new IllegalStateException();
		}

		theSendingCode(strategy, order);
	}

	/**
	 * Add a pending order to the map and re-build the pending orders array.
	 * 
	 * @param order
	 */
	private void addPendingOrder(IOrderMfg order) {
		PendingOrderInfo info = new PendingOrderInfo(getCurrentTime(), order);
		pendingOrdersMap.put(Integer.valueOf(order.getId()), info);
		PendingOrderInfo[] arr = new PendingOrderInfo[pendingOrders.length + 1];
		System.arraycopy(pendingOrders, 0, arr, 0, pendingOrders.length);
		arr[arr.length - 1] = info;
		pendingOrders = arr;
		insertOpenOrder(info);
	}

	public void addPositionListener(IPositionListener listener) {
		List<IPositionListener> list = new ArrayList<>();
		if (positionListeners != null) {
			list.addAll(Arrays.asList(positionListeners));
		}
		list.add(listener);
		positionListeners = list.toArray(new IPositionListener[list.size()]);
	}

	public void addProbabilitiesDealerListener(
			ProbabilitiesDealer.IListener aExecutionRecorder) {
		if (usingProbabilities)
			pdealer.addListener(aExecutionRecorder);
	}

	public void addStrategy(FinalStrategy strategy) {
		myStrategies.add(strategy);
		strategy.setShell(this);
		if (indicator != null)
			strategy.setIndicator(indicator);
	}

	public void addStrategyOnTheRun(FinalStrategy strategy) {
		myStrategies.add(strategy);
		strategy.setShell(this);
		if (indicator != null)
			strategy.setIndicator(indicator);
		strategy.setTick(tick);
		strategy.begin(tickSize);
		if (!isWarmingUp())
			strategy.endWarmUp();
	}

	public void begin(int aTickSize) {
		this.tickSize = aTickSize;
		// this.warmingUp = true;

		getLogger().begin("Running strategy " + toString());
		if (usingProbabilities)
			pdealer.begin(this, distribution, configuration);
		for (IStrategy s : myStrategies) {
			s.begin(aTickSize);
		}
		IIndicator widget = getIndicator();
		int dim = widget.getChscalelevels();
		relScales = new boolean[dim + 1];
		for (int s = widget.getChscalelevels(); s >= widget
				.getStartScaleLevelWidget(); s--) {
			for (IStrategy se : myStrategies) {
				relScales[s] |= se.isARelevantScale(s);
				if (relScales[s])
					break;
			}
		}
	}

	@SuppressWarnings("boxing")
	@Override
	public void cancelOrder(int orderID) {
		try {
			broker.dropOrder(orderID);
		} catch (TEAException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		/*
		 * notification..., if I am here the cancellation has been done.
		 */
		OrderMessage msg = new OrderMessage(TradeMessage.CANCELED, "[broker]",
				ordersMap.get(orderID));
		log(new TradeMessageWrapper(msg));
	}

	public void endWarmUp() {
		// this.warmingUp = false;
		Utils.debug_var(342345, "End warmup at time="
				+ getIndicator().getCurrentTime());
		for (IStrategy s : myStrategies) {
			s.endWarmUp();
		}
	}

	// /**
	// * @param aCategory
	// * @param aId
	// * @param anErrorDetail
	// */
	// public void error(ECategoryError aCategory, int aId, Object
	// anErrorDetail) {
	// // TO DO Auto-generated method stub
	//
	// }

	protected void firePositionChanged(PositionEvent event) {
		if (positionListeners != null) {
			for (IPositionListener listener : positionListeners) {
				if (event instanceof PositionOpenedEvent) {
					listener.positionOpened((PositionOpenedEvent) event);
				} else {
					listener.positionClosed((PositionClosedEvent) event);
				}
			}
		}
	}

	public IOrderMfg getConfirmOrder() {
		return _confirmOrder;
	}

	public List<PositionClosedEvent> getClosedOrders() {
		return closedOrders;
	}

	public long getCurrentPrice() {
		return price;
	}

	private long getCurrentTime() {
		return time;
	}

	public HashMap<Integer, IExecutionReport> getExecutionReportsMap() {
		return reportsMap;
	}

	public IDuplexStatistics getAccount() {
		// return _account;
		if (broker == null) {
			return null;
		}
		return broker.getAccountStats();
	}

	@Override
	public IIndicator getIndicator() {
		return this.indicator;
	}

	public ILogger getLogger() {
		return logger;
	}

	public double getMinMatchesPercent() {
		return pdealer.getMinMatchesPercent();
	}

	public HashMap<Integer, PositionOpenedEvent> getOpenedOrdersMap() {
		return openedOrdersMap;
	}

	public HashMap<Integer, IOrderMfg> getOrdersMap() {
		return ordersMap;
	}

	/**
	 * @return the pendingOrders
	 */
	public PendingOrderInfo[] getPendingOrders() {
		return pendingOrders;
	}

	public ProbabilitiesDealer getProbabilitiesDealer() {
		return pdealer;
	}

	public double getProbabilityLinesPercentValue() {
		return pdealer.getProbabilityLinesPercentValue();
	}

	/**
	 * @return the myStrategies
	 */
	@Override
	public List<IStrategy> getStrategies() {
		return myStrategies;
	}

	// public String getStrategyName() {
	// return getClass().getSimpleName();
	// }

	@SuppressWarnings("static-method")
	public StrategyType getStrategyType() {
		return StrategyType.MIXED;
	}

	public StepDefinition getTick() {
		return tick;
	}

	public ObjectListenersGroup<QueueTick> getTickProcessed() {
		return tickProcessed;
	}

	public List<Object> getTradingOrders() {
		return openedOrders;
	}

	private void insertOpenOrder(Object info) {
		if (openedOrders.size() == 0)
			openedOrders.add(info);
		else
			openedOrders.add(info);
	}

	@Override
	public boolean isARelevantScale(int aS) {
		return relScales[aS];
	}

	// /**
	// * tells if we will wait for a confirmation to send the order to the
	// market.
	// *
	// * @return
	// */
	// public boolean isWaitingForConfirmation() {
	// return waitingForConfirmation;
	// }

	public boolean isWarmingUp() {
		return _tickDataSource.isInWarmUp();
	}

	@Override
	public void log(TradeMessageType type, String event, int orderID) {
		TradeMessageWrapper msg = new TradeMessageWrapper(new StrategyMessage(
				type, "portfolio", event, null));

		msg.setOrderID(orderID);

		log(msg);
	}

	public void logAccount(TradeMessageType type, String event, int orderID,
			EAccountRouting account) {
		TradeMessageWrapper msg = new TradeMessageWrapper(new StrategyMessage(
				type, "portfolio", event, account));

		msg.setOrderID(orderID);

		log(msg);
	}

	public void log(TradeMessageWrapper msg) {
		// PeriodAccount account = _account; //
		// getGlobalAcount().getCurrentPeriodAccount();
		ISingleAccountStatistics longAccount = broker.getAccountStats()
				.getLongStatistics();
		ISingleAccountStatistics shortAccount = broker.getAccountStats()
				.getShortStatistics();
		long equity = longAccount.getOpenEquityMoney()
				+ shortAccount.getOpenEquityMoney();
		double longCapital = longAccount.getOpenEquityMoney();
		double shortCapital = shortAccount.getOpenEquityMoney();
		int longQuantity = longAccount.getQuantity();
		int shortQuantity = shortAccount.getQuantity();
		long longPricePL = longAccount.getOpenEquityMoney();
		long shortPricePL = shortAccount.getOpenEquityMoney();

		msg.setFakeTime(getCurrentTime());
		msg.setPrice(getCurrentPrice());
		msg.setEquity(equity);
		msg.setLongCapital(longCapital);
		msg.setShortCapital(shortCapital);
		msg.setLongQuantity(longQuantity);
		msg.setShortQuantity(shortQuantity);
		msg.setLongQuantity(longQuantity);
		msg.setLongPricePL(longPricePL);
		msg.setShortPricePL(shortPricePL);

		ITradeMessage brokerMessage = msg.getTradeMessage();
		StepDefinition tick2 = getTick();
		int stepScale = tick2.getStepScale();
		brokerMessage.setTickScale(stepScale);
		logger.log(LogLevel.ANY, msg);
	}

	private void logth(int s) {
		log(TradeMessage.NEW_TH, "New TH on scale " + s, 0);
	}

	public void manualExecution(IOrderExec anExec) {
		for (IStrategy s : myStrategies) {
			((AbstractStrategy) s).manualExecution(anExec);
		}
	}

	@SuppressWarnings("boxing")
	public void newExecution(IOrderExec anExec) {

		log(new TradeMessageWrapper(new OrderMessage(TradeMessage.EXECUTED,
				"[broker]", ordersMap.get(anExec.getOrderId()),
				anExec.getExecutionPrice(), null)));

		/*
		 * I do not know why the fake execution here was commented here and
		 * moved in the Trading pipe.
		 * 
		 * Maybe has something to do with the strategy log. We have to check
		 * better here.
		 */

		int fakeExecutionTime = indicator.getFakeTimeFor(
				anExec.getExecutionTime(), true);

		debug_var(929295, "new exec ", anExec);

		IOrderMfg order = ordersMap.get(Integer.valueOf(anExec.getOrderId()));
		PositionEvent tofire;
		if (order != null) {
			MyOrderReport report = null;
			/*
			 * temporary hack, I ask to the strategy if this order closes a
			 * position
			 */
			if (!order.isChild()
					&& !((FinalStrategy) this.myStrategies.get(0))
							.isAClosedPosition(anExec)) {
				report = new MyOrderReport(getOrdersMap().get(
						Integer.valueOf(anExec.getOrderId())),
						anExec.getExecutionPrice(), anExec.getExecutionTime(),
						fakeExecutionTime);
				reportsMap.put(Integer.valueOf(order.getId()), report);
				removePendingOrder(order.getId());
				long[] childrenOpenings = new long[order.getChildren().size()];
				for (int i = 0; i < childrenOpenings.length; i++) {
					childrenOpenings[i] = order.getChildAt(i).getOpeningPrice();
				}
				PositionOpenedEvent resOpen = new PositionOpenedEvent(order,
						report.getPhysicalExecutionTime(),
						report.getExecutionTime(), report.getExecutionPrice(),
						report.isLongPosition(), childrenOpenings);
				insertOpenOrder(resOpen);
				openedOrdersMap.put(Integer.valueOf(order.getId()), resOpen);
				tofire = resOpen;
			} else {
				/*
				 * very hack here... because the order that close the position
				 * may not be a child, but a parent of an opposite sign, this is
				 * in any case related to the old parent and here the
				 * relationship is formed
				 */
				IOrderMfg realOrBuiltParent = order.getParent();

				if (realOrBuiltParent == null) {
					/*
					 * very very hack, the fake parent is the order with the
					 * preceding id. Of course we could design a different
					 * relationship, for example a "linked" parent
					 */
					realOrBuiltParent = getOrdersMap().get(
							Integer.valueOf(order.getId() - 1));
				}
				int parentId = realOrBuiltParent.getId();

				report = new MyOrderReport(getOrdersMap().get(
						Integer.valueOf(anExec.getOrderId())),
						anExec.getExecutionPrice(), anExec.getExecutionTime(),
						fakeExecutionTime, reportsMap.get(Integer
								.valueOf(parentId)));
				IExecutionReport parent = report.getParentExecutionReport();

				long equity = (resClosed == null) ? 0 : resClosed.getTotal();
				resClosed = new PositionClosedEvent(
						report.getPhysicalExecutionTime(),
						report.getExecutionTime(), report.getExecutionPrice(),
						report.isLongPosition(), report.isClosingInGain(),
						parent.getExecutionTime(), parent.getExecutionPrice(),
						realOrBuiltParent, equity);
				if (closedOrders.size() == 0)
					closedOrders.add(resClosed);
				else
					closedOrders.add(0, resClosed);
				openedOrders.remove(openedOrdersMap.get(Integer
						.valueOf(parentId)));
				tofire = resClosed;
			}

			// _account.orderFilled(report);

			firePositionChanged(tofire);

			// order,
		} else {
			debug_var(299291, "Cannot find order after execution ",
					Integer.valueOf(anExec.getOrderId()));
		}

		// play sound
		IPreferenceStore store = SymbolsPlugin.getDefault().getPreferenceStore();
		if (store
				.getBoolean(SymbolsPlugin.PREF_PLAY_SOUND_ON_ORDER_FILLED)) {
			UIPlugin.getDefault().playSound(store.getString(SymbolsPlugin.PREF_SOUND_ON_ORDER_FILLED));
		}
		// ---

		for (IStrategy s : myStrategies) {
			s.newExecution(anExec);
		}

	}

	@Override
	public void newExecutionNew(IOrderExec anExec) {
		newExecution(anExec);
	}

	public final void newTick(QueueTick aTick) {
		time = aTick.getFakeTime();
		price = aTick.getPrice();

		newTickImpl(aTick);
	}

	protected void newTickImpl(QueueTick aTick) {
		// _account.newTick(aTick);

		if (usingProbabilities)
			pdealer.dealWithProbabilities();
		// super.newTick(aTick);
		IIndicator widget = this.indicator;
		for (int s = widget.getChscalelevels(); s >= widget
				.getStartScaleLevelWidget(); s--) {
			if (widget.isLevelInformationPresent(s)
					&& widget.isThereANewPivot(s) && isARelevantScale(s)) {
				logth(s);
			}
		}
		for (IStrategy s : myStrategies) {
			s.newTick(aTick);
		}
		tickProcessed.handle(aTick);
	}

	public void orderStatus(IOrderStatus aStatus) {

		/*
		 * I have to check the fake execution time.
		 * 
		 * all the strategies in a portfolio have the same indicator, so why is
		 * it that we have an indicator for each strategy?
		 */

		// this.myStrategies.get(0).indicator.getFakeTimeFor(physicalTime,
		// exactMatch);

		// this.indicator.

		// Enrique or Arian: you should check the code before removing orders.
		if (aStatus.getStatus() != EOrderStatus.ACCEPTED) {
			removePendingOrder(aStatus.getOrderId());
		}

		for (IStrategy s : myStrategies) {
			s.orderStatus(aStatus);
		}
	}

	@Override
	public void orderStatusNew(IOrderStatus aStatus) {
		orderStatus(aStatus);

	}

	/**
	 * Remove a pending order from the map and re-build the pending orders
	 * array.
	 * 
	 * @param orderId
	 */
	public void removePendingOrder(int orderId) {
		PendingOrderInfo deleted = pendingOrdersMap.remove(Integer
				.valueOf(orderId));
		if (deleted != null) {
			PendingOrderInfo[] arr = new PendingOrderInfo[pendingOrders.length - 1];
			System.arraycopy(pendingOrders, 0, arr, 0, arr.length);
			pendingOrders = arr;
			openedOrders.remove(deleted);
		}
	}

	public void removePositionListener(IPositionListener listener) {
		if (positionListeners != null) {
			List<IPositionListener> list = new ArrayList<>(
					Arrays.asList(positionListeners));
			list.remove(listener);
			positionListeners = list
					.toArray(new IPositionListener[list.size()]);
		}
	}

	public void removeProbabilitiesDealerListener(
			ProbabilitiesDealer.IListener aExecutionRecorder) {
		if (usingProbabilities)
			pdealer.removeListener(aExecutionRecorder);
	}

	public void removeStrategy(FinalStrategy strategy) {
		myStrategies.remove(strategy);
	}

	public void setBroker(IVirtualBroker aBroker) {
		this.broker = aBroker;
	}

	public void setIndicator(IIndicator indicator1) {
		this.indicator = indicator1;
		for (IStrategy s : myStrategies) {
			s.setIndicator(indicator1);
		}
	}

	public void setLogger(ILogger aLogger) {
		this.logger = aLogger;
	}

	public void setMinMatchesPercent(double minMatchesPercent) {
		pdealer.setMinMatchesPercent(minMatchesPercent);
	}

	public void setProbabilityLinesPercentValue(
			double aProbabilityLinesPercentValue) {
		pdealer.setProbabilityLinesPercentValue(aProbabilityLinesPercentValue);
	}

	public void setTick(StepDefinition stepDefinition) {
		tick = stepDefinition;
		// _account.setTick(stepDefinition);
		for (IStrategy s : myStrategies) {
			((AbstractStrategy) s).setTick(stepDefinition);
		}
	}

	/**
	 * Called when the user request to stop the trading. The portfolio should
	 * execute a stop routine.
	 */
	public void stop() {
		//
	}

	public void stopTrading() {
		for (IStrategy strategy : myStrategies) {
			strategy.stopTrading();
		}
		synchronized (_orderConfirmMutex) {
			_orderConfirmMutex.notifyAll();
		}
	}

	private void theSendingCode(IStrategy strategy, final IOrderMfg order) {
		TradeMessageWrapper msg = new TradeMessageWrapper(new OrderMessage(
				StrategyMessage.SENT, strategy.getStrategyName(), order));
		((FinalStrategy) strategy).log(msg);

		if (!OrderUtils.isMarketFamily(order.getExecType()) && !order.isChild()) {
			addPendingOrder(order);
		}

		ordersMap.put(Integer.valueOf(order.getId()), order);

		for (IOrderMfg child : order.getChildren()) {
			ordersMap.put(Integer.valueOf(child.getId()), child);

			// send the children
			msg = new TradeMessageWrapper(new OrderMessage(
					StrategyMessage.SENT, strategy.getStrategyName(), child));
			((FinalStrategy) strategy).log(msg);
		}

		// play the order sound
		final OrderImpl orderImpl = (OrderImpl) order;
		if (orderImpl.isPlaySound()) {
			String sound = orderImpl.getSoundPath();
			UIPlugin.getDefault().playSound(
					sound == null ? UIPlugin.SOUND_DING : sound);
		}

		// show confirm dialog
		try {
			boolean sendNow = order.getConformationMessage() == null;

			// the strategy name is set in the FinalStrategy.
			// ((OrderImpl) order).setStrategyId(strategy.getStrategyName());

			broker.placeOrder(order, sendNow);

			if (!sendNow) {
				_confirmOrder = order;
				_confirmThread = new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							int orderId = order.getId();
							EAccountRouting orderAccount = order.getRoutedAccount();
							logAccount(TradeMessage.COMMENT, "Open order ("
									+ orderId + ") confirmation dialog",
									orderId, orderAccount);

							boolean confirmed = askForUserConfirmation(order
									.getConformationMessage());
							if (confirmed) {
								synchronized (PortfolioStrategy.this) {
									logAccount(TradeMessage.COMMENT,
											"Place parked order (" + orderId
													+ ")", orderId,
											orderAccount);
									broker.placeParkedOrder(orderId);
									_confirmOrder = null;
								}
							} else {
								synchronized (PortfolioStrategy.this) {
									logAccount(TradeMessage.COMMENT,
											"Forget parked order (" + orderId
													+ ")", orderId,
											orderAccount);
									broker.forgetParkedOrder(orderId);
								}
							}
							for (IStrategy s : getStrategies()) {
								s.orderConfirmedByUser(order);
							}
						} catch (TEAException e) {
							e.printStackTrace();
						}
					}
				}, "Wait for confirmation - " + hashCode());
				_confirmThread.start();
			}
		} catch (TEAException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public void discardOrderConfirmation() {
		synchronized (_orderConfirmMutex) {
			_orderConfirmMutex.notifyAll();
		}
		_confirmOrder = null;
		if (_confirmThread != null) {
			try {
				_confirmThread.join(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean askForUserConfirmation(final String msg) {
		final AtomicBoolean confirm = new AtomicBoolean(false);
		final ConfirmationDialog[] dlgRef = new ConfirmationDialog[] { null };
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				final ConfirmationDialog dlg = new ConfirmationDialog(Display
						.getDefault().getActiveShell());
				dlg.setTitle("Order Confirmation");
				dlg.setQuestion("Do you confirm this order?");
				dlg.setMessage(msg);
				dlgRef[0] = dlg;
				dlg.open(new Runnable() {

					@Override
					public void run() {
						boolean result = dlg.getReturnCode() == Window.OK;
						confirm.set(result);
						synchronized (_orderConfirmMutex) {
							_orderConfirmMutex.notifyAll();
						}
					}
				});
				dlg.updateUI();
			}
		});
		synchronized (_orderConfirmMutex) {
			try {
				_orderConfirmMutex.wait();
			} catch (InterruptedException e) {
				//
			}
		}
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				dlgRef[0].close();
			}
		});
		boolean value = confirm.get();
		return value;
	}

	public List<IOrderMfg> getLongOpenedOrdersTotal() {
		ArrayList<IOrderMfg> res = new ArrayList<>();
		for (IStrategy aStratey : this.myStrategies) {
			FinalStrategy fs = (FinalStrategy) aStratey;
			res.addAll(fs.getLongOpenedOrders());
		}
		return res;
	}

	public List<IOrderMfg> getShortOpenedOrdersTotal() {
		ArrayList<IOrderMfg> res = new ArrayList<>();
		for (IStrategy aStratey : this.myStrategies) {
			FinalStrategy fs = (FinalStrategy) aStratey;
			res.addAll(fs.getShortOpenedOrders());
		}
		return res;
	}

}
