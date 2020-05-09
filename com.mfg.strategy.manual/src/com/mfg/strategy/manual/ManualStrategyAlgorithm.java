package com.mfg.strategy.manual;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Assert;

import com.mfg.broker.IOrderExec;
import com.mfg.broker.IOrderMfg;
import com.mfg.broker.IOrderMfg.EAccountRouting;
import com.mfg.broker.IOrderMfg.EXECUTION_TYPE;
import com.mfg.broker.IOrderMfg.ORDER_TYPE;
import com.mfg.broker.events.OrderMessage;
import com.mfg.broker.events.TradeMessage;
import com.mfg.broker.orders.LimitOrder;
import com.mfg.broker.orders.MarketIfTouchedOrder;
import com.mfg.broker.orders.MarketOrder;
import com.mfg.broker.orders.OrderImpl;
import com.mfg.broker.orders.OrderUtils;
import com.mfg.broker.orders.StopLimitOrder;
import com.mfg.broker.orders.StopOrder;
import com.mfg.common.QueueTick;
import com.mfg.interfaces.indicator.IIndicator;
import com.mfg.strategy.AutoStop;
import com.mfg.strategy.ChildToExit;
import com.mfg.strategy.EntryExitOrderType;
import com.mfg.strategy.ManualStrategySettings;
import com.mfg.strategy.StopSettings;
import com.mfg.strategy.manual.interfaces.IManualStrategyAlgorithmEnvironment;
import com.mfg.utils.MathUtils;

/**
 * 
 * <p>
 * This class provide the algorithms of the Manual Strategy. Implementors
 * provide the connection of the strategy with the external world.
 * </p>
 * This class was developed with the functional paradigm in mind, it means, it
 * does not contain any state. Implementors must to follow this paradigm.
 * 
 * @author arian
 * 
 */
public class ManualStrategyAlgorithm {
	private static final String MANUAL_STRATEGY = "Manual Strategy algo";

	protected static void addOrder(Collection<IOrderMfg> ordersToSend,
			IOrderMfg order) {
		ordersToSend.add(order);
	}

	public static long average2Orders(IManualStrategyAlgorithmEnvironment env,
			IOrderMfg a, IOrderMfg b, Collection<IOrderMfg> ordersToSend) {
		if (a != null && b != null) {
			double avg = (getOrderPrice(env, a) + getOrderPrice(env, b)) / 2.0;

			int tickSize = (int) env.getTickSize();
			// move it to a valid tick value
			long lower = ((long) avg / tickSize) * tickSize;
			long upper = lower + tickSize;
			avg = avg - lower < upper - avg ? lower : upper;

			setOrderPrice(a, (int) avg, ordersToSend);
			setOrderPrice(b, (int) avg, ordersToSend);

			return (long) avg;
		}
		return -1;
	}

	/**
	 * Average the entries. Returns true if the last average was computed with
	 * Rule 1.
	 * 
	 * @param tickSize
	 * @param routing
	 * @param entries
	 * @return
	 */
	private static void averageEntries(IManualStrategyAlgorithmEnvironment env,
			Routing routing, Collection<IOrderMfg> ordersToSend,
			IOrderMfg... entries) {
		boolean isRule_1 = false;
		Assert.isTrue(entries.length > 1);
		int replayIndex = entries.length - ordersToSend.size();

		for (int i = 1; i < entries.length; i++) {
			IOrderMfg entry1 = entries[i - 1];
			IOrderMfg entry2 = entries[i];

			IOrderMfg TP_1 = getTP(entry1);
			IOrderMfg TP_2 = getTP(entry2);
			IOrderMfg SL_1 = getSL(entry1);
			IOrderMfg SL_2 = getSL(entry2);

			long entry1_price = getOrderPrice(env, entry1);
			long entry2_price = getOrderPrice(env, entry2);

			isRule_1 = routing.isLong() && entry2_price <= entry1_price
					|| routing.isShort() && entry2_price >= entry1_price;

			long TP_avg = -1;
			long SL_avg = -1;

			if (isRule_1) {
				// rule 1
				TP_avg = average2Orders(env, TP_1, TP_2, ordersToSend);
				SL_avg = average2Orders(env, SL_1, SL_2, ordersToSend);
			} else {
				// rule 2
				if (TP_1 != null && TP_2 != null) {
					TP_avg = getOrderPrice(env, TP_2);
					setOrderPrice(TP_1, (int) TP_avg, ordersToSend);
				}
				if (SL_1 != null && SL_2 != null) {
					SL_avg = getOrderPrice(env, SL_2);
					setOrderPrice(SL_1, (int) SL_avg, ordersToSend);
				}
			}

			String formattedPrice1 = TradeMessage.formatPriceWithScale(
					entry1_price, env.getTickScale());
			String formattedPrice2 = TradeMessage.formatPriceWithScale(
					entry2_price, env.getTickScale());

			if (TP_1 != null && TP_2 != null && SL_1 != null && SL_2 != null) {
				String event = (i < replayIndex ? "Replay " : "")
						+ (isRule_1 ? "Rule 1" : "Rule 2")
						+ " {"
						+ formattedPrice1
						+ (entry1_price == entry2_price ? "="
								: (entry1_price < entry2_price ? "<" : ">"))
						+ formattedPrice2
						+ "} : "
						+ (TP_avg == -1 ? "" : "TP(" + TP_1.getId() + ", "
								+ TP_2.getId() + ")=" + TP_avg + ". ")
						+ (SL_avg == -1 ? "" : "SL(" + SL_1.getId() + ","
								+ SL_2.getId() + ")=" + SL_avg) + ". ";

				env.log(TradeMessage.WHITE_COMMENT, event, -1);
			}
		}

	}

	/**
	 * @param env
	 * @param routing
	 */
	public static void cancelPendingOrders(
			IManualStrategyAlgorithmEnvironment env, Routing routing) {
		List<IOrderMfg> pendingOrders = new ArrayList<>();
		if (routing == Routing.SHORT || routing == Routing.AUTO) {
			pendingOrders.addAll(Arrays.asList(env.getShortPendingOrders()));
		}

		if (routing == Routing.LONG || routing == Routing.AUTO) {
			pendingOrders.addAll(Arrays.asList(env.getLongPendingOrders()));
		}

		for (IOrderMfg order : pendingOrders) {
			env.cancelOrder(order);
		}
	}

	protected static void checkTrailingStop(
			IManualStrategyAlgorithmEnvironment env,
			Collection<IOrderMfg> ordersToSend, Routing routing,
			ManualStrategySettings settings) {

		QueueTick lastTick = env.getLastTick();
		if (lastTick != null) {
			int lastScale = env.getIndicator().getChscalelevels();
			for (int level = 2; level <= lastScale; level++) {
				TrailingStatus status = env.getTrailingStatus(level);

				boolean enabledRC = Trailing.RC.isTrailing(status, routing);
				boolean enabledCL = Trailing.CL.isTrailing(status, routing);
				boolean enabledSC = Trailing.SC.isTrailing(status, routing);

				IIndicator indicator = env.getIndicator();

				if (indicator.isLevelInformationPresent(level)) {

					boolean swingDown = indicator.isSwingDown(level);
					double bottomPrice = indicator
							.getCurrentBottomRegressionPrice(level);
					double topPrice = indicator
							.getCurrentTopRegressionPrice(level);
					double centerPrice = indicator
							.getCurrentCenterRegressionPrice(level);

					double CL = centerPrice;
					double RC = swingDown ? bottomPrice : topPrice;
					double SC = swingDown ? topPrice : bottomPrice;

					double currentPrice = env.getCurrentPrice();

					Trailing trailToExecute = null;

					// Rule #1
					boolean crossFromTopToBottom = lastTick.getPrice() > CL
							&& currentPrice < CL;
					boolean crossFromBottomToTop = lastTick.getPrice() < CL
							&& currentPrice > CL;
					if (enabledCL
							&& (crossFromTopToBottom || crossFromBottomToTop)) {
						trailToExecute = Trailing.CL;
					}

					// Rule #2
					if (enabledSC && currentPrice == SC) {
						trailToExecute = Trailing.SC;
					}

					// Rule #3
					if (enabledRC && currentPrice == RC) {
						trailToExecute = Trailing.RC;
					}

					if (trailToExecute != null) {
						executeTRS(env, ordersToSend, routing, trailToExecute,
								level, settings);
					}
				}
			}
		}
	}

	public static void checkTrailingStops(
			IManualStrategyAlgorithmEnvironment env,
			Collection<IOrderMfg> ordersToSend, ManualStrategySettings settings) {
		checkTrailingStop(env, ordersToSend, Routing.SHORT, settings);
		checkTrailingStop(env, ordersToSend, Routing.LONG, settings);
	}

	/**
	 * 
	 * Close the active orders modifying the order's children.
	 * 
	 * @param env
	 * @param routing
	 * @param ordersToSend
	 * @return The total closed Q.
	 */
	protected static int closeActiveOrdersWithChildren(
			IManualStrategyAlgorithmEnvironment env,
			Collection<IOrderMfg> ordersToSend, Routing routing,
			ManualStrategySettings settings) {
		int Q_closed = 0;

		IOrderMfg[] orders = routing.getActiveOrders(env);

		for (IOrderMfg order : orders) {
			assert !order.isChild();
			closeOrderWithChildren(env, ordersToSend, order, settings);
			Q_closed += order.getQuantity();
		}
		return Q_closed;
	}

	/**
	 * Close both positions, Long and Short.
	 * 
	 * @param env
	 */
	protected static void closeAllPostions(
			IManualStrategyAlgorithmEnvironment env,
			Collection<IOrderMfg> ordersToSend, ManualStrategySettings settings) {
		closePosition(env, ordersToSend, Routing.SHORT, settings);
		closePosition(env, ordersToSend, Routing.LONG, settings);
	}

	/**
	 * Close an order modifying the children.
	 * 
	 * @param env
	 * @param routing
	 * @param parent
	 * @param ordersToSend
	 */
	protected static void closeOrderWithChildren(
			IManualStrategyAlgorithmEnvironment env,
			Collection<IOrderMfg> ordersToSend, IOrderMfg parent,
			ManualStrategySettings settings) {
		EAccountRouting routing = parent.getAccountRouting();
		boolean longPosition = routing == EAccountRouting.LONG_ACCOUNT;
		ChildToExit childToExit = settings.getEntryExitChildToExit();
		int currentPrice = MathUtils.longToIntSafe(env.getCurrentPrice());

		List<IOrderMfg> children = parent.getChildren();
		if (children.isEmpty()) {
			// nothing to close
		} else {
			OrderImpl childToClose;
			if (children.size() == 1) {
				childToClose = (OrderImpl) parent.getChildAt(0);

				childToClose.turnIntoMarket(currentPrice,
						(int) env.getTickSize());
				// _logger.log(new StrategyMessage(StrategyMessageType.Comment,
				// "STRATEGY CHANGE CHILDREN PRICE: " + childToClose,
				// "Manual"));
			} else {
				OrderImpl child0 = (OrderImpl) parent.getChildAt(0);
				OrderImpl child1 = (OrderImpl) parent.getChildAt(1);
				OrderImpl maxChild = child0.getOpeningPrice() > child1
						.getOpeningPrice() ? child0 : child1;
				OrderImpl minChild = maxChild == child0 ? child1 : child0;
				// If we are trading long, the TP will have a higher price
				// of the SL. If we trade short, it will be the opposite.
				if (childToExit == ChildToExit.TAKE_PROFIT) {
					childToClose = longPosition ? maxChild : minChild;
				} else {
					childToClose = longPosition ? minChild : maxChild;
				}
				childToClose.turnIntoMarket(currentPrice,
						(int) env.getTickSize());
			}
			addOrder(ordersToSend, childToClose);
		}
	}

	protected static void closePartialPosition(
			IManualStrategyAlgorithmEnvironment env,
			Collection<IOrderMfg> ordersToSend, PositionCommand command) {
		final ManualStrategySettings settings = command.getSettings();
		Routing routing = command.getRouting();

		int Q_command = command.getSettings().getBasicQuantity();
		int Q_account = (int) routing.getQuantity(env.getAccountStatus());

		// close just if we have something to close
		if (Q_account > 0) {
			// the Q to close
			int Q_toClose = 0;
			// the remaining Q after close; that will be used to open
			int Q_toOpen = 0;

			if (Q_command <= Q_account) {
				// just close the position
				Q_toClose = Q_command;
			} else {
				// you will close the position and open the opposite
				// with the remaining Q
				Q_toClose = Q_account;
				Q_toOpen = Q_command - Q_account;
			}

			// close the active orders with children
			// while the closed Q is less than Q_toClose
			// and do it in reverse order
			IOrderMfg[] activeOrders = routing.getActiveOrders(env);
			for (int i = activeOrders.length - 1; i >= 0 && Q_toClose > 0; i--) {
				IOrderMfg order = activeOrders[i];

				assert !order.isChild();

				closeOrderWithChildren(env, ordersToSend, order, settings);
				Q_toClose -= Math.abs(order.getQuantity());

			}

			// if closing the children is not enough to consume the Q_toClose
			// we must to send the opposite order to the account
			if (Q_toClose > 0) {
				ManualStrategySettings settingsToClose = settings.clone();
				settingsToClose.getStopLossSettings()
						.setAutoStop(AutoStop.NONE);
				settingsToClose.getTakeProfitSettings().setAutoStop(
						AutoStop.NONE);

				ORDER_TYPE orderToClose = routing.getOrderToClose();
				sendQuantityToAccount(env, ordersToSend, routing, orderToClose,
						Q_toClose, settingsToClose);
			}

			// now we must to open the position if there is Q to open
			if (Q_toOpen > 0) {
				Routing oppositeRouting = routing.getOpposite();
				ORDER_TYPE orderToOpen = oppositeRouting.getOrderToOpen();

				sendQuantityToAccount(env, ordersToSend, oppositeRouting,
						orderToOpen, Q_toOpen, settings);
			}
		}
	}

	/**
	 * Close a particular position, Long or Short.
	 * 
	 * @param env
	 * @param routing
	 */
	protected static void closePosition(
			IManualStrategyAlgorithmEnvironment env,
			Collection<IOrderMfg> ordersToSend, Routing routing,
			ManualStrategySettings settings) {
		assert routing != Routing.AUTO;

		final int Q_account = (int) routing.getQuantity(env.getAccountStatus());

		// counter of closed Q
		int Q_closed = 0;

		// close the position with children
		Q_closed = closeActiveOrdersWithChildren(env, ordersToSend, routing,
				settings);

		// the remaining Q after close the active orders
		int Q_noChildren = Q_account - Math.abs(Q_closed);

		if (Q_noChildren > 0) {
			ORDER_TYPE orderType = routing.getOrderToClose();
			sendQuantityToAccount(env, ordersToSend, routing, orderType,
					Q_noChildren, settings);
		}
	}

	protected static double computeChildPrice(
			IManualStrategyAlgorithmEnvironment env,
			StopSettings childSettings, ORDER_TYPE childOrderType,
			double entryPrice, double triggerPrice) {
		double childPrice = 0;
		if (childSettings.getAutoStop() == AutoStop.AUTO) {
			// this is the Enrique's way of do entryPrice + nticks * tickSize
			double tickSize = env.getTickSize();
			int numberOfTicks = childSettings.getNumberOfTicks();
			childSettings.getStopType();
			childPrice = OrderUtils.moveToNonExecutingDirection(
					childSettings.getStopType(), childOrderType,
					(long) entryPrice, (numberOfTicks * (long) tickSize));
		} else {
			childPrice = triggerPrice;
		}

		return childPrice;
	}

	protected static double computeEntryExitLimitPrice(
			IManualStrategyAlgorithmEnvironment env,
			ManualStrategySettings settings) {
		int limitPrice = settings.getEntryExitLimitPrice();

		if (limitPrice == 0) {
			return env.getCurrentPrice();
		}
		return limitPrice;
	}

	protected static IOrderMfg createLimitOrder(int aId, ORDER_TYPE orderType,
			Routing routing, int q, double limitPrice) {
		LimitOrder order = new LimitOrder(aId, orderType,
				OrderUtils.getSign(orderType) * q, (int) limitPrice);
		order.setAccountRouting(routing.getAccountRouting());
		return order;
	}

	protected static IOrderMfg createMarketIfTouchedOrder(int aId,
			ORDER_TYPE orderType, Routing routing, int q, double stopPrice) {
		MarketIfTouchedOrder order = new MarketIfTouchedOrder(aId, orderType,
				OrderUtils.getSign(orderType) * q, (long) stopPrice);
		order.setAccountRouting(routing.getAccountRouting());
		return order;
	}

	private static IOrderMfg createParentMarketOrder(int aId,
			ORDER_TYPE orderType, Routing routing, int q) {
		MarketOrder marketOrder = new MarketOrder(aId, orderType,
				OrderUtils.getSign(orderType) * q);
		marketOrder.setAccountRouting(routing.getAccountRouting());
		return marketOrder;
	}

	protected static IOrderMfg createStopLimitOrder(int aId,
			ORDER_TYPE orderType, int q, double childPrice, double limitPrice) {
		return new StopLimitOrder(aId, orderType, q, (int) childPrice,
				(int) limitPrice);
	}

	protected static IOrderMfg createStopOrder(int aId, ORDER_TYPE orderType,
			Routing routing, int q, double childPrice) {
		StopOrder order = new StopOrder(aId, orderType,
				OrderUtils.getSign(orderType) * q, (long) childPrice);
		order.setAccountRouting(routing.getAccountRouting());
		return order;
	}

	protected static void executeTRS(IManualStrategyAlgorithmEnvironment env,
			Collection<IOrderMfg> ordersToSend, Routing routing,
			Trailing trail, int level, ManualStrategySettings settings) {

		// disable trailings
		TrailingStatus status = env.getTrailingStatus(level);
		int len = ordersToSend.size();
		// close the position
		closePosition(env, ordersToSend, routing, settings);

		if (ordersToSend.size() != len) {
			trail.setTrailing(status, routing, false);
			env.fireStateChanged();
		}
	}

	private static long getOrderPrice(IManualStrategyAlgorithmEnvironment env,
			IOrderMfg order) {
		Long price;
		if (!order.isChild() && order instanceof MarketOrder) {
			price = env.getExecutionPrice(order);
			if (price == null) {
				price = Long.valueOf(env.getCurrentPrice());
			}
		} else {
			price = Long
					.valueOf(OrderUtils.isMarketFamily(order.getExecType()) ? order
							.getAuxPrice() : order.getLimitPrice());
		}
		return price.longValue();
	}

	private static IOrderMfg getSL(IOrderMfg parent) {
		IOrderMfg child_SL = null;
		for (IOrderMfg child : parent.getChildren()) {
			if (child instanceof StopOrder || child instanceof StopLimitOrder) {
				child_SL = child;
			}
		}
		return child_SL;
	}

	private static IOrderMfg getTP(IOrderMfg parent) {
		IOrderMfg child_TP = null;
		for (IOrderMfg child : parent.getChildren()) {
			boolean is_SL = child instanceof StopOrder
					|| child instanceof StopLimitOrder;
			if (!is_SL) {
				child_TP = child;
			}
		}
		return child_TP;
	}

	/**
	 * @param manualStrategy
	 * @param anExec
	 */
	public static void newExecution(IManualStrategyAlgorithmEnvironment env,
			IOrderExec anExec) {
		if (Routing.LONG.getQuantity(env.getAccountStatus()) == 0) {
			env.cancelTrailings(Routing.LONG);
		}
		if (Routing.SHORT.getQuantity(env.getAccountStatus()) == 0) {
			env.cancelTrailings(Routing.SHORT);
		}
	}

	protected static void processAvgCommand(
			IManualStrategyAlgorithmEnvironment env,
			Collection<IOrderMfg> ordersToSend, AvgCommand command) {
		processPositionCommand(env, ordersToSend, command);

		Routing routing = command.getRouting();

		env.log(TradeMessage.WHITE_COMMENT, "Avg " + routing, -1);

		int tickScale = env.getTickScale();

		for (IOrderMfg entry : ordersToSend) {
			OrderMessage msg = new OrderMessage(TradeMessage.WHITE_COMMENT,
					MANUAL_STRATEGY, entry);

			msg.setTickScale(tickScale);
			env.log(TradeMessage.WHITE_COMMENT, "Entry: " + msg.getEvent(), -1);

			for (IOrderMfg child : entry.getChildren()) {
				msg = new OrderMessage(TradeMessage.WHITE_COMMENT,
						MANUAL_STRATEGY, child);
				msg.setTickScale(tickScale);
				env.log(TradeMessage.WHITE_COMMENT, " - " + msg.getEvent(), -1);
			}
		}

		List<IOrderMfg> list = new ArrayList<>(Arrays.asList(routing
				.getActiveOrders(env)));
		list.addAll(ordersToSend);

		IOrderMfg[] entries = list.toArray(new IOrderMfg[ordersToSend.size()]);

		Assert.isTrue(entries.length > 1);

		averageEntries(env, routing, ordersToSend, entries);

	}

	/**
	 * @param env
	 * @param command
	 */
	protected static void processCancelPendingCommand(
			IManualStrategyAlgorithmEnvironment env,
			CancelPendingCommand command) {
		Routing routing = command.getRouting();
		cancelPendingOrders(env, routing);
	}

	protected static void processCancelTrailCommand(
			IManualStrategyAlgorithmEnvironment env, CancelTrailCommand command) {
		env.cancelTrailings(command.getRouting());
	}

	protected static void processCloseAllCommand(
			Collection<IOrderMfg> ordersToSend,
			IManualStrategyAlgorithmEnvironment env, CloseAllCommand command) {

		Routing routing = command.getRouting();

		cancelPendingOrders(env, routing);

		final ManualStrategySettings settings = command.getSettings();

		if (routing == Routing.AUTO) {
			closeAllPostions(env, ordersToSend, settings);
		} else {
			closePosition(env, ordersToSend, routing, settings);
		}
	}

	/**
	 * Main method. It must to be called by the UI.
	 * 
	 * @param command
	 */
	public static void processCommand(IManualStrategyAlgorithmEnvironment env,
			Command command) {

		Set<IOrderMfg> ordersToSend = new LinkedHashSet<>();

		if (command instanceof CloseAllCommand) {
			processCloseAllCommand(ordersToSend, env, (CloseAllCommand) command);
		} else if (command instanceof SARCommand) {
			processSARCommand(env, ordersToSend, (SARCommand) command);
		} else if (command instanceof CancelTrailCommand) {
			processCancelTrailCommand(env, (CancelTrailCommand) command);
		} else if (command instanceof TrailCommand) {
			processTrailCommand(env, (TrailCommand) command);
		} else if (command instanceof AvgCommand) {
			processAvgCommand(env, ordersToSend, (AvgCommand) command);
		} else if (command instanceof CancelPendingCommand) {
			processCancelPendingCommand(env, (CancelPendingCommand) command);
		} else if (command instanceof PositionCommand) {
			processPositionCommand(env, ordersToSend, (PositionCommand) command);
		}

		for (IOrderMfg order : ordersToSend) {
			env.sendOrder(order);
		}
	}

	public static void processNewTick(IManualStrategyAlgorithmEnvironment env,
			ManualStrategySettings settings) {
		List<IOrderMfg> ordersToSend = new LinkedList<>();
		checkTrailingStops(env, ordersToSend, settings);

		for (IOrderMfg order : ordersToSend) {
			env.sendOrder(order);
		}
	}

	protected static void processPositionCommand(
			IManualStrategyAlgorithmEnvironment env,
			Collection<IOrderMfg> ordersToSend, PositionCommand command) {
		if (command.isForOpen()) {
			sendCommandToAccount(env, ordersToSend, command.getRouting(),
					command);
		} else {
			cancelPendingOrders(env, command.getRouting());
			closePartialPosition(env, ordersToSend, command);
		}
	}

	protected static void processSARCommand(
			IManualStrategyAlgorithmEnvironment env,
			Collection<IOrderMfg> ordersToSend, SARCommand command) {
		final ManualStrategySettings settings = command.getSettings();
		Routing routing = command.getRouting();
		int Q = (int) routing.getQuantity(env.getAccountStatus());
		if (Q > 0) {
			// close the position
			closePosition(env, ordersToSend, routing, settings);

			// open the opposite position
			sendCommandToAccount(env, ordersToSend, routing.getOpposite(),
					command);
		}
	}

	protected static void processTrailCommand(
			IManualStrategyAlgorithmEnvironment env, TrailCommand command) {
		Routing routing = command.getRouting();
		final ManualStrategySettings settings = command.getSettings();
		int level = routing.getTrailingLevel(settings);
		Trailing trail = command.getTrail();
		TrailingStatus trailingStatus = env.getTrailingStatus(level);

		trail.revertTrail(trailingStatus, routing);
	}

	protected static void sendCommandToAccount(
			IManualStrategyAlgorithmEnvironment env,
			Collection<IOrderMfg> ordersToSend, Routing routing, Command command) {
		final ManualStrategySettings settings = command.getSettings();
		int q = settings.getBasicQuantity();
		int maxQ = settings.getBasicMaxQuantity();
		ORDER_TYPE order = routing.getOrderToOpen();

		if (q < maxQ) {
			sendQuantityToAccount(env, ordersToSend, routing, order, q,
					settings);
		} else {
			while (q >= maxQ) {
				sendQuantityToAccount(env, ordersToSend, routing, order, maxQ,
						settings);
				q -= maxQ;
			}
		}
	}

	protected static void sendQuantityToAccount(
			IManualStrategyAlgorithmEnvironment env,
			Collection<IOrderMfg> ordersToSend, Routing routing,
			ORDER_TYPE orderType, int Q, ManualStrategySettings settings) {

		// create the parent order

		EntryExitOrderType entryExitOrderType = settings
				.getEntryExitOrderType();

		double limitPrice = computeEntryExitLimitPrice(env, settings);

		IOrderMfg parentOrder;

		if (entryExitOrderType == EntryExitOrderType.MARKET) {
			parentOrder = createParentMarketOrder(env.getNextOrderId(),
					orderType, routing, Q);
		} else {
			parentOrder = createLimitOrder(env.getNextOrderId(), orderType,
					routing, Q, limitPrice);
		}

		// attach the children
		double entryPrice = entryExitOrderType == EntryExitOrderType.MARKET ? env
				.getCurrentPrice() : limitPrice;
		IOrderMfg childOrder;
		StopSettings childSettings;
		ORDER_TYPE childOrderType = OrderUtils.getOpposite(orderType);
		EXECUTION_TYPE childExecType;

		// add Stop Loss
		childSettings = settings.getStopLossSettings();
		childExecType = childSettings.getStopType();

		if (childSettings.getAutoStop() != AutoStop.NONE) {
			double childPrice = computeChildPrice(env, childSettings,
					childOrderType, entryPrice, childSettings.getTriggerPrice());

			if (childExecType == EXECUTION_TYPE.STOP) {
				childOrder = createStopOrder(env.getNextOrderId(),
						childOrderType, routing, Q, childPrice);
			} else {
				childOrder = createStopLimitOrder(env.getNextOrderId(),
						OrderUtils.getOpposite(orderType), Q, childPrice,
						childSettings.getLimitPrice());
			}
			// the type is set by the setStopLoss method
			// childOrder.setChildType(OrderChildType.STOP_LOSS);
			((OrderImpl) parentOrder).setStopLoss(childOrder);
		}
		// add Take Profit
		childSettings = settings.getTakeProfitSettings();
		childExecType = childSettings.getStopType();
		if (childSettings.getAutoStop() != AutoStop.NONE) {
			double manualPrice = childExecType == EXECUTION_TYPE.LIMIT ? childSettings
					.getLimitPrice() : childSettings.getTriggerPrice();

			double childPrice = computeChildPrice(env, childSettings,
					childOrderType, entryPrice, manualPrice);

			if (childExecType == EXECUTION_TYPE.LIMIT) {
				childOrder = createLimitOrder(env.getNextOrderId(),
						childOrderType, routing, Q, childPrice);
			} else {
				childOrder = createMarketIfTouchedOrder(env.getNextOrderId(),
						OrderUtils.getOpposite(orderType), routing, Q,
						childPrice);
			}
			// childOrder.setChildType(OrderChildType.TAKE_PROFIT);
			((OrderImpl) parentOrder).setTakeProfit(childOrder);
		}
		addOrder(ordersToSend, parentOrder);
	}

	/**
	 * Change the price of the order. It takes in count if the order belongs to
	 * the market family or not. This modification will has effect if the order
	 * is send again to the market. To do this use the method
	 * {@link #addOrder(List, IOrderMfg)}.
	 * 
	 * @param order
	 * @param price
	 */
	private static void setOrderPrice(IOrderMfg order, int price,
			Collection<IOrderMfg> ordersToSend) {
		if (OrderUtils.isMarketFamily(order.getExecType())) {
			((OrderImpl) order).setAuxPrice(price);
		} else {
			((OrderImpl) order).setLimitPrice(price);
		}
		addOrder(ordersToSend, order);
	}

	public static void stopTrading(IManualStrategyAlgorithmEnvironment env) {
		for (Routing routing : new Routing[] { Routing.LONG, Routing.SHORT }) {
			long q = routing.getQuantity(env.getAccountStatus());
			if (q != 0) {
				ORDER_TYPE orderTypeToClose = routing.getOrderToClose();
				// int q_ToClose = (int) (OrderUtils.getSign(orderTypeToClose) *
				// q);
				/*
				 * The quantity is always opposite, because the getQuantity
				 * returns the relative quantity of the account (negative if I
				 * am short).
				 */
				int q_ToClose = (int) (-1 * q);
				MarketOrder order = new MarketOrder(env.getNextOrderId(),
						orderTypeToClose, q_ToClose);
				order.setAccountRouting(routing.getAccountRouting());
				env.sendOrder(order);
			}
		}
	}
}
