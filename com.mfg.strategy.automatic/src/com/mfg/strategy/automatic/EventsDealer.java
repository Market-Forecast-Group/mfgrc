/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 *
 * $Id: $
 */
/**
 * @author <a href="mailto:gardero@gmail.com">Enrique Matos Alfonso</a>, MFG
 *
 * @version $Revision: $ $Date: $
 */

package com.mfg.strategy.automatic;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.widgets.Display;

import com.mfg.broker.IExecutionReport;
import com.mfg.broker.IOrderMfg;
import com.mfg.broker.IOrderMfg.EAccountRouting;
import com.mfg.broker.IOrderMfg.ORDER_TYPE;
import com.mfg.broker.events.TradeMessage;
import com.mfg.broker.events.TradeMessageType;
import com.mfg.broker.orders.LimitOrder;
import com.mfg.broker.orders.MarketOrder;
import com.mfg.broker.orders.OrderImpl;
import com.mfg.broker.orders.OrderUtils;
import com.mfg.interfaces.indicator.IIndicator;
import com.mfg.strategy.PortfolioStrategy;
import com.mfg.strategy.automatic.eventPatterns.EventAtomCommand;
import com.mfg.strategy.automatic.eventPatterns.EventAtomEntry;
import com.mfg.strategy.automatic.eventPatterns.EventAtomTH;
import com.mfg.strategy.automatic.eventPatterns.EventCommandContainer;
import com.mfg.strategy.automatic.eventPatterns.EventGeneral;
import com.mfg.strategy.automatic.eventPatterns.IOrderFilledListener;
import com.mfg.strategy.automatic.eventPatterns.MY_BOOLEAN;
import com.mfg.strategy.logger.StrategyMessage;
import com.mfg.strategy.logger.TradeMessageWrapper;
import com.mfg.strategy.ui.ConfirmationDialog;
import com.mfg.symbols.SymbolsPlugin;
import com.mfg.symbols.trading.configurations.TradingConfiguration;
import com.mfg.ui.UIPlugin;
import com.mfg.utils.Utils;
import com.mfg.utils.ui.HtmlUtils;

public class EventsDealer {

	// private ArrayList<EventAtom> currentEvents;
	// private ArrayList<EventAtom> allEvents;
	private IIndicator widget;
	private final Hashtable<Integer, IOrderFilledListener> orderListenerTable;
	private final List<EventAtomEntry> filledEntriesList;
	private final List<EventAtomEntry> sentEntriesList;
	private List<Integer> listToCloseImediatelly;
	private final Hashtable<Integer, OrderImpl> tempOrders, allOrders;
	private Hashtable<Integer, List<EventAtomEntry>> entriesIDTable;
	private EventsPatternStrategy theStrategy;
	// private EventsPatternStrategy strategy;
	// private IExecutionLog _logger;
	private final HtmlUtils hutil = new HtmlUtils();
	private int widgetDim;
	private int[] fTHCount;
	private Hashtable<Integer, Integer> tries;
	private int birthID;
	private boolean[] entriesByScales;
	private boolean sendingBlocked;

	private boolean thereAnewEntry;
	private boolean usingTheMarket = true;
	private List<OrderImpl> manualEntryOrders;

	/**
	 * @return the tHCount
	 */
	public int getTHCount(int scale) {
		return fTHCount[scale];
	}

	public EventsDealer() {
		super();
		// allEvents = new ArrayList<EventAtom>();
		orderListenerTable = new Hashtable<>();
		filledEntriesList = new ArrayList<>();
		sentEntriesList = new ArrayList<>();
		entriesIDTable = new Hashtable<>();
		tempOrders = new Hashtable<>();
		allOrders = new Hashtable<>();
		manualEntryOrders = new ArrayList<>();
	}

	public void begin(EventsPatternStrategy strategy) {
		this.theStrategy = strategy;
		widget = strategy.getIndicator();
		// addAllEventAtoms(strategy.getEventPattern());
		filledEntries = new ArrayList<>();
		widgetDim = widget.getChscalelevels() + 1;
		fTHCount = new int[widgetDim];
		entriesByScales = new boolean[widgetDim];
		eventsThreadsQueue = new ArrayList<>();
		eventsThreadsQueueToAdd = new ArrayList<>();
		listToCloseImediatelly = new ArrayList<>();
		tries = new Hashtable<>();
		birthID = 0;
		longFree = true;
		longOpened = 0;
		shortFree = true;
		shortOpened = 0;
	}

	protected void countEntry(IOrderMfg order) {
		setFree(order.getType(), false);
	}

	protected int getTHIndex(EventAtomEntry entry) {
		return getWidget().getCurrentPivotsCount(entry.getWidgetScale());
	}

	protected int getTHIndex(int widgetScale) {
		return getWidget().getCurrentPivotsCount(widgetScale);
	}

	// private void addAllEventAtoms(EventGeneral aEventPattern) {
	// aEventPattern.addAllEventAtoms(this);
	// }

	public void beginCheck() {
		// currentEvents.clear();
		checkTHs();
	}

	public void initTHsCount() {
		for (int s = widget.getChscalelevels(); s >= widget
				.getStartScaleLevelWidget(); s--) {
			if (widget.isLevelInformationPresent(s))
				fTHCount[s] = widget.getCurrentPivotsCount(s);
			else
				fTHCount[s] = 0;
		}
	}

	private void checkTHs() {
		for (int level = widget.getChscalelevels(); level >= widget
				.getStartScaleLevelWidget(); level--)
			if (widget.isLevelInformationPresent(level)
					&& widget.isThereANewPivot(level)) {
				fTHCount[level] = widget.getCurrentPivotsCount(level);
				entriesByScales[level] = false;
			}
	}

	public void endCheck() {
		thereAnewEntry = false;
		for (Iterator<EventAtomEntry> it = sentEntriesList.iterator(); it
				.hasNext();) {
			EventAtomEntry eventAtom = it.next();
			if (!eventAtom.isOnRightSwing0() && !eventAtom.isFilled()) {
				it.remove();
				cancelOrder(eventAtom.getOrder());
				logDiscarding(eventAtom, "Sw0 restriction");
			}
		}
		flushOrders();
	}

	public boolean isThereAnewEntry() {
		return thereAnewEntry;
	}

	private int getKey(EventAtomEntry entry) {
		return widgetDim * entry.getID() + entry.getWidgetScale();
	}

	private int getKey(int ID, int aWidgetScale) {
		return widgetDim * ID + aWidgetScale;
	}

	public void trackEntryAttempt(EventAtomEntry entry) {
		for (int i = getWidget().getStartScaleLevelWidget(); i < widgetDim; i++) {
			tries.put(Integer.valueOf(getKey(entry.getID(), i)),
					Integer.valueOf(getTHIndex(i)));
		}
	}

	public void trackEntry(EventAtomEntry entry) {
		entriesByScales[entry.getWidgetScale()] = true;
	}

	// public List<EventAtom> getEvents() {
	// return currentEvents;
	// }
	//
	// public boolean isThereAnyNewEvent() {
	// return !currentEvents.isEmpty();
	// }

	// public void addEvent(EventAtom event) {
	// if (!allEvents.contains(event))
	// allEvents.add(event);
	// }

	/**
	 * @return the widget
	 */
	public IIndicator getWidget() {
		return widget;
	}

	/**
	 * @param aWidget
	 *            the widget to set
	 */
	public void setWidget(IIndicator aWidget) {
		widget = aWidget;
	}

	public void addOrder(OrderImpl order, IOrderFilledListener obs) {
		if (obs != null)
			orderListenerTable.put(Integer.valueOf(order.getId()), obs);
		tempOrders.put(Integer.valueOf(order.getId()), order);
		allOrders.put(Integer.valueOf(order.getId()), order);
	}

	public void cancelOrder(IOrderMfg order) {
		theStrategy.getPortfolio().cancelOrder(order.getId());
		logOrderToBeCancelled(order);
	}

	public void cancelOrCloseIfFilled(EventAtomEntry entry) {
		cancelOrder(entry.getOrder());
		sentEntriesList.remove(entry);
		listToCloseImediatelly.add(Integer.valueOf(entry.getOrder().getId()));
	}

	public IOrderMfg getOrderFromID(int aID) {
		return allOrders.get(Integer.valueOf(aID));
	}

	public void addEntryOrder(OrderImpl order, EventAtomEntry obs) {
		setSendingBlocked(true);
		addOrder(order, obs);
		List<EventAtomEntry> a = entriesIDTable
				.get(Integer.valueOf(obs.getID()));
		if (a == null) {
			a = new ArrayList<>();
			entriesIDTable.put(Integer.valueOf(obs.getID()), a);
		}
		a.add(obs);
		sentEntriesList.add(obs);
	}

	public void addManualEntryOrder(OrderImpl order) {
		manualEntryOrders.add(order);
		synchronized (this) {
			manualEntries++;
		}
	}

	public OrderImpl popManualEntryOrder() {
		if (manualEntryOrders.size() > 0)
			return manualEntryOrders.remove(manualEntryOrders.size() - 1);
		return null;
	}

	/**
	 * called when an order is filled.
	 * 
	 * @param aReport
	 *            the report of the execution.
	 */
	public void orderFilled(IExecutionReport aReport) {
		IOrderFilledListener listener = orderListenerTable.get(Integer
				.valueOf(aReport.getOrderId()));
		if (listener != null) {
			listener.orderFilled(aReport);
			if (listener.isTiedToCloseCommand()) {
				/**
				 * now we can send entries because we got a execution
				 * confirmation of an entry order.
				 */
				setSendingBlocked(false);
				OrderImpl order = (OrderImpl) aReport.getOrder();
				filledEntries.add(order);
				sentEntriesList.remove(listener);
				filledEntriesList.add((EventAtomEntry) listener);
				thereAnewEntry = true;
				countEntry(order);
				if (listToCloseImediatelly.contains(Integer.valueOf(order
						.getId())))
					closePosition(order, MY_BOOLEAN.WHATEVER, null);
			}
		} else { // For the simple protections triggered.
			IOrderMfg parent = aReport.getOrder().getParent();
			if (parent != null)
				removeFilledEntry(parent);
		}
	}

	/**
	 * called when an order has been canceled.
	 * 
	 * @param aId
	 *            the ID of the canceled order.
	 */
	public void orderCanceledOrConfirmed(int aId) {
		IOrderFilledListener listener = orderListenerTable.get(Integer
				.valueOf(aId));
		if (listener != null) {
			if (listener.isTiedToCloseCommand()) {
				/**
				 * now we can send entries because we got a cancel confirmation
				 * of an entry order.
				 */
				setSendingBlocked(false);
			}
		}
	}

	public void closePosition(IOrderMfg aOrder, boolean market,
			IOrderFilledListener obs) {
		closePosition(aOrder, market ? MY_BOOLEAN.YES : MY_BOOLEAN.NO, obs);
	}

	public void closeManualPosition(boolean global, boolean market,
			IOrderFilledListener obs) {
		IOrderMfg order = null;
		do {
			order = popManualEntryOrder();
			if (order != null)
				closePosition(order, market ? MY_BOOLEAN.YES : MY_BOOLEAN.NO,
						obs);
		} while (global && order != null);
	}

	public void closePosition(IOrderMfg aOrder, MY_BOOLEAN market,
			IOrderFilledListener obs) {
		int quantity = -aOrder.getQuantity();
		int currentPrice = getWidget().getCurrentPrice();
		OrderImpl child = null;
		ArrayList<IOrderMfg> children = aOrder.getChildren();
		if (children.size() > 0) {
			for (IOrderMfg child1 : children) {
				if (OrderUtils.isMarketFamily(child1.getExecType()) == market
						.isItTrue()) {
					child = (OrderImpl) child1;
					child.turnIntoMarket(currentPrice, getTickSize());
					break;
				}
			}
		}
		if (child == null) {
			ORDER_TYPE type = OrderUtils.getOpposite(aOrder.getType());
			if (market.isItTrue()) {
				child = new MarketOrder(getNextOrderId(), type, quantity);
			} else {
				child = new LimitOrder(getNextOrderId(), type, quantity,
						currentPrice);
			}
			child.setAccountRouting(aOrder.getAccountRouting());
		}
		addOrder(child, obs);
	}

	public int getNextOrderId() {
		return this.theStrategy.getNextOrderId();
	}

	public void removeFilledEntry(IOrderMfg entry) {
		filledEntries.remove(entry);
		EventAtomEntry e = null;
		if (entry != null) {
			e = (EventAtomEntry) orderListenerTable.get(Integer.valueOf(entry
					.getId()));
			if (e != null) {
				filledEntriesList.remove(e);
				List<EventAtomEntry> a = entriesIDTable.get(Integer.valueOf(e
						.getID()));
				a.remove(e);
				setFree(entry.getType(), true);
			}
		}
	}

	protected void flushOrders() {
		for (Iterator<Integer> it = tempOrders.keySet().iterator(); it
				.hasNext();) {
			Integer k = it.next();
			final OrderImpl o = tempOrders.get(k);
			if (usingTheMarket)
				theStrategy.addOrder(o);
			logOrderSent(o);
			// if (!usingTheMarket)
			// orderFilled(new SimulatedOrder(o,
			// getWidget().getCurrentTime()));
		}
		tempOrders.clear();
	}

	private ArrayList<OrderImpl> filledEntries;

	/**
	 * @return the filledEntries
	 */
	public ArrayList<OrderImpl> getFilledEntries() {
		return filledEntries;
	}

	/**
	 * @return the filledEntriesTable
	 */
	public List<EventAtomEntry> getFilledEntriesList() {
		return filledEntriesList;
	}

	public Hashtable<Integer, List<EventAtomEntry>> getEntriesIDTable() {
		return entriesIDTable;
	}

	public void setEntriesIDTable(
			Hashtable<Integer, List<EventAtomEntry>> aEntriesIDTable) {
		this.entriesIDTable = aEntriesIDTable;
	}

	/**
	 * @param aFilledEntries
	 *            the filledEntries to set
	 */
	public void clearFilledEntries() {
		filledEntries.clear();
	}

	/**
	 * @return the _logger
	 */
	// public IExecutionLog getLogger() {
	// return _logger;
	// }

	protected HtmlUtils getUtil() {
		return hutil;
	}

	private List<EventGeneral> eventsThreadsQueue;
	private List<EventGeneral> eventsThreadsQueueToAdd;

	public void addEventThread(EventGeneral event) {
		eventsThreadsQueueToAdd.add(event);
	}

	public void flushEventThreadsToAdd() {
		for (EventGeneral e : eventsThreadsQueueToAdd) {
			eventsThreadsQueue.add(e);
			logThreadInsertion(e);
		}
		eventsThreadsQueueToAdd.clear();
	}

	public void clearEventThreads() {
		eventsThreadsQueue.clear();
	}

	public synchronized void clearMyEventThreads(EventGeneral ev) {
		for (Iterator<EventGeneral> it = eventsThreadsQueue.iterator(); it
				.hasNext();) {
			EventGeneral eventGeneral = it.next();
			if (sameGeneration(eventGeneral, ev)) {
				it.remove();
				logDiscarding(eventGeneral, "Exit arrived");
				eventGeneral.cancelThisEvent();
			}
		}
		for (Iterator<EventGeneral> it = eventsThreadsQueueToAdd.iterator(); it
				.hasNext();) {
			EventGeneral eventGeneral = it.next();
			if (sameGeneration(eventGeneral, ev)) {
				it.remove();
				logDiscarding(eventGeneral, "Exit arrived");
				eventGeneral.cancelThisEvent();
			}
		}
	}

	public synchronized void checkEventThreads() {
		flushEventThreadsToAdd();
		ArrayList<EventGeneral> torem = new ArrayList<>();
		for (Iterator<EventGeneral> it = eventsThreadsQueue.iterator(); it
				.hasNext();) {
			EventGeneral eventGeneral = it.next();
			eventGeneral.checkIFTriggered(this);
			if (eventGeneral.isTriggered()) {
				torem.add(eventGeneral);
				logActivation(eventGeneral);
			}
		}
		for (EventGeneral eventGeneral : torem) {
			eventsThreadsQueue.remove(eventGeneral);
		}
	}

	private static boolean sameGeneration(EventGeneral eventGeneral,
			EventGeneral ev) {
		return eventGeneral.getRoot() == ev.getRoot();
	}

	public boolean isClear(EventAtomEntry entry) {
		int thIndex = getTHIndex(entry);
		Integer k = tries.get(Integer.valueOf(getKey(entry)));
		boolean res = k == null || thIndex > k.intValue();
		// if (!res)
		// System.out.println("--------------------not clear");
		return res;
	}

	public boolean isClear(EventAtomEntry entry, int[] singleEntriesScales) {
		int ID = entry.getID();
		if (singleEntriesScales != null && singleEntriesScales.length > 0) {
			for (int i : singleEntriesScales) {
				int thIndex = getTHIndex(i);
				Integer k = tries.get(Integer.valueOf(getKey(ID, i)));
				if (k != null && thIndex <= k.intValue())
					return false;
			}
		}
		return true;
	}

	public boolean isOkToEnter(EventAtomEntry entry) {
		return !entriesByScales[entry.getWidgetScale()];
	}

	public void setNewBirth() {
		birthID++;
	}

	public int getThisBirthID() {
		return birthID;
	}

	public void logActivation(EventGeneral t) {
		EAccountRouting routing = computeAccountRouting(t);

		TradeMessageType type = TradeMessage.COMMENT;
		String event = "(PatID=" + t.getBirthID() + ") Arrived "
				+ t.getHtmlBody(HtmlUtils.Plain);
		TradeMessageWrapper msg = new TradeMessageWrapper(
				new EventStrategyMessage(type, theStrategy.getStrategyName(),
						event, t, routing));
		msg.setOrderID(0);
		theStrategy.log(msg);

		showPopup(routing, t);
	}

	private EAccountRouting computeAccountRouting(EventGeneral node) {
		EventGeneral root = node.getRoot();
		EAccountRouting result = root.getAccountRouting();
		if (result == null) {
			EventAtomEntry entry = findEntryEvent(node);
			if (entry == null) {
				Utils.debug_id(9438565,
						"Warning: Cannot find an the entry of the event: "
								+ node
								+ ". Probbaly the pattern is not well formed.");
			} else {
				ORDER_TYPE type = entry.getAType(this);
				if (type == ORDER_TYPE.BUY) {
					result = EAccountRouting.LONG_ACCOUNT;
				} else {
					result = EAccountRouting.SHORT_ACCOUNT;
				}
			}
			root.setAccountRouting(result);
		}
		return result;
	}

	private static EventAtomEntry findEntryEvent(EventGeneral node) {
		if (node == null) {
			return null;
		} else if (node instanceof EventAtomEntry) {
			return (EventAtomEntry) node;
		} else if (node instanceof EventCommandContainer) {
			EventCommandContainer cmdCont = (EventCommandContainer) node;
			EventAtomCommand cmd = cmdCont.getCommand();
			if (cmd instanceof EventAtomEntry) {
				return (EventAtomEntry) cmd;
			}
		}
		return findEntryEvent(node.getParentEvent());
	}

	public void logOrderSent(OrderImpl t) {
		TradeMessageWrapper msg = new TradeMessageWrapper(new StrategyMessage(
				TradeMessage.COMMENT, theStrategy.getStrategyName(), "Sent "
						+ t.toString(theStrategy.getTick()),
				t.getRoutedAccount()));
		msg.setOrderID(t.getId());
		theStrategy.log(msg);
	}

	/**
	 * @param t
	 */
	public void logOrderToBeCancelled(IOrderMfg t) {
		// if (_logger.isEnabled(EMessageType.Comment)) {
		// StrategyMessage m = new StrategyMessage(StrategyMessageType.Comment,
		// "<html><body>" + hutil.color(hutil.bold("Cancelling:"),
		// Color.RED.darker()) + "</br>" + t.toString() + "</body></html>",
		// "Automatic");
		// _logger.log(m);
		// }
	}

	public void logThreadInsertion(EventGeneral t) {
		theStrategy.log(TradeMessage.COMMENT, "(PatID=" + t.getBirthID()
				+ ") Add Thread: " + t.getHtmlBody(HtmlUtils.Plain), 0);
	}

	public void logDiscarding(EventGeneral t, String reason) {
		theStrategy.log(TradeMessage.COMMENT, "(PatID=" + t.getBirthID() + ") "
				+ reason + ", Discarded: " + t.getHtmlBody(HtmlUtils.Plain), 0);
	}

	private boolean shortFree, longFree;
	private int shortOpened, longOpened;
	private boolean warmingUp;
	private int manualEntries;
	protected ConfirmationDialog _thPopupDlg;

	private void setFree(ORDER_TYPE oRDER_TYPE, boolean v) {
		switch (oRDER_TYPE) {
		case BUY:
			longFree = v;
			longOpened += (v ? -1 : 1);
			break;
		case SELL:
			shortFree = v;
			shortOpened += (v ? -1 : 1);
			break;
		}
	}

	/**
	 * gets if we are free to enter again at an specific trading direction in
	 * case it is allowed to enter only once in thar direction.
	 * 
	 * @param oRDER_TYPE
	 *            the entry type
	 * @return @code{@code true} iff we are free to enter again in that
	 *         direction.
	 */
	public boolean isFree(ORDER_TYPE oRDER_TYPE) {
		switch (oRDER_TYPE) {
		case BUY:
			return longFree;
		case SELL:
			return shortFree;
		}
		return false;
	}

	public int getLongOpened() {
		return longOpened;
	}

	public int getShortOpened() {
		return shortOpened;
	}

	public int getOpenedEntriesCount(ORDER_TYPE oRDER_TYPE) {
		switch (oRDER_TYPE) {
		case BUY:
			return getLongOpened();
		case SELL:
			return getShortOpened();
		}
		return 0;
	}

	/**
	 * @return if the market is being used to process orders.
	 */
	public boolean isUsingTheMarket() {
		return usingTheMarket;
	}

	/**
	 * @param aUsingTheMarket
	 *            {@code true} if we want the market to process orders.
	 */
	public void setUsingTheMarket(boolean aUsingTheMarket) {
		usingTheMarket = aUsingTheMarket;
	}

	/**
	 * @return {@code true} if the can not send entries.
	 */
	public boolean isSendingBlocked() {
		return sendingBlocked;
	}

	/**
	 * @param aSendingBlocked
	 *            the parameter to set if we can send entries or not.
	 */
	public void setSendingBlocked(boolean aSendingBlocked) {
		sendingBlocked = aSendingBlocked;
	}

	public EventsPatternStrategy getTheStrategy() {
		return theStrategy;
	}

	public void setTheStrategy(EventsPatternStrategy aTheStrategy) {
		theStrategy = aTheStrategy;
	}

	/**
	 * @return the warmingUp
	 */
	public boolean isWarmingUp() {
		return warmingUp;
	}

	public void setWarmingUp(boolean aB) {
		warmingUp = aB;
	}

	public int getTickSize() {
		return (int) theStrategy.getTickSize();
	}

	/**
	 * gets how many manual entries have been triggered
	 * 
	 * @return
	 */
	public int getManualEntries() {
		return manualEntries;
	}

	/**
	 * decreases the manual entries count indicating it already have been
	 * processed by the automatic strategy.
	 */
	public synchronized void decreaseManualEntries() {
		manualEntries--;
	}

	public void removeManualFilledEntry(IOrderMfg parent) {
		manualEntryOrders.remove(parent);
	}

	private void showPopup(final EAccountRouting routing,
			final EventGeneral event) {
		boolean closeDlg = true;
		if (event instanceof EventAtomTH) {
			final EventAtomTH eventTH = (EventAtomTH) event;

			// play sound
			String[] sounds = { null, null, null };
			if (eventTH.isPlaySound()) {
				String sound = eventTH.getSoundPath();
				sound = sound == null ? UIPlugin.SOUND_CHIME_DOWN : sound;
				sounds[0] = sound;
			}
			if (eventTH.isSpeak()) {
				sounds[1] = UIPlugin.SOUND_ARRIVED_TH_SCALE;
				sounds[2] = eventTH.getWidgetScale() + ".wav";
			}
			UIPlugin plugin = UIPlugin.getDefault();
			plugin.playSound(sounds);

			if (eventTH.isRequiresConfirmation()) {
				closeDlg = false;
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {
						if (_thPopupDlg == null) {
							_thPopupDlg = new ConfirmationDialog(Display
									.getDefault().getActiveShell());
						}

						PortfolioStrategy portfolio = getTheStrategy()
								.getPortfolio();
						TradingConfiguration config = portfolio
								.getTradingConfiguration();
						String confName = SymbolsPlugin.getDefault()
								.getFullConfigurationName(config);
						String account = routing == EAccountRouting.LONG_ACCOUNT ? "LONG ACCOUNT"
								: "SHORT ACCOUNT";
						_thPopupDlg.setTitle("TH Arrives");
						_thPopupDlg.setQuestion("TH Arrives");
						_thPopupDlg.setMessage(account + "\n\n"
								+ "CONFIGURATION:\n" + confName
								+ "\n\nEVENT:\nNew TH on scale "
								+ eventTH.getWidgetScale() + " (PatID="
								+ eventTH.getParentEvent().getBirthID() + ")");
						_thPopupDlg.setMessageColor(ConfirmationDialog.GREEN);
						_thPopupDlg.setCreateCancel(false);

						if (!_thPopupDlg.isOpen()) {
							_thPopupDlg.open(null);
						}

						_thPopupDlg.updateUI();
					}
				});
			}
		}
		if (closeDlg && _thPopupDlg != null) {
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					_thPopupDlg.close();
				}
			});
		}
	}

	public void stopTrading() {
		if (_thPopupDlg != null) {
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					_thPopupDlg.close();
				}
			});
		}
	}
}
