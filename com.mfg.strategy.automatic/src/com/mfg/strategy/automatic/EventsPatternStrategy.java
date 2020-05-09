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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.mfg.broker.IOrderExec;
import com.mfg.broker.IOrderMfg;
import com.mfg.broker.IOrderStatus;
import com.mfg.broker.events.TradeMessage;
import com.mfg.broker.orders.OrderImpl;
import com.mfg.common.QueueTick;
import com.mfg.interfaces.indicator.IIndicator;
import com.mfg.interfaces.trading.StrategyType;
import com.mfg.strategy.FinalStrategy;
import com.mfg.strategy.MyOrderReport;
import com.mfg.strategy.automatic.eventPatterns.EventAtomEntry;
import com.mfg.strategy.automatic.eventPatterns.EventAtomExit;
import com.mfg.strategy.automatic.eventPatterns.EventAtomProfitLoss;
import com.mfg.strategy.automatic.eventPatterns.EventAtomTH;
import com.mfg.strategy.automatic.eventPatterns.EventCommandContainer;
import com.mfg.strategy.automatic.eventPatterns.EventDaemon;
import com.mfg.strategy.automatic.eventPatterns.EventGeneral;
import com.mfg.strategy.automatic.eventPatterns.EventSortedCollection;
import com.mfg.strategy.automatic.eventPatterns.EventsDaemonsCollection;
import com.mfg.strategy.automatic.eventPatterns.LSFilterType;
import com.mfg.utils.Utils;
import com.mfg.utils.ui.HtmlUtils;
import com.mfg.utils.ui.HtmlUtils.IHtmlStringProvider;

public class EventsPatternStrategy extends FinalStrategy implements
		IHtmlStringProvider {

	protected IIndicator widget;
	private int[] thcount, thInitials;
	private final EventsDealer _dealer;
	private EventGeneral eventPatternModel;
	private EventsDaemonsCollection daemons;
	private EventGeneral averagingLogic;
	private final List<EventGeneral> patternsQueue;
	private boolean flag;
	private EventGeneral _eventToAdd;
	private boolean warmed;
	private int bigScale;
	private boolean onBigScaleTH;
	private String _strategyName;

	public EventsPatternStrategy() {
		super();
		_dealer = new EventsDealer();
		eventPatternModel = createInitial();
		patternsQueue = new ArrayList<>();
		daemons = new EventsDaemonsCollection();
		_strategyName = "EventsPatternStrategy";
	}

	@Override
	public String getStrategyName() {
		return _strategyName;
	}

	public void setStrategyName(String name) {
		_strategyName = name;
	}

	private static EventGeneral createInitial() {
		EventCommandContainer c = new EventCommandContainer();
		c.setPrecondition(new EventAtomTH());
		EventAtomEntry entry = new EventAtomEntry();
		entry.setSimpleProtectionScale(3);
		c.setCommand(entry);
		EventCommandContainer c1 = new EventCommandContainer();
		c1.setPrecondition(new EventAtomTH());
		c1.setCommand(new EventAtomExit());
		EventSortedCollection c2 = new EventSortedCollection();
		c2.addEvent(c);
		c2.addEvent(c1);
		return c2;
	}

	public EventsPatternStrategy(EventGeneral aEventPatternModel) {
		this();
		setEventPattern(aEventPatternModel);
		setAveraging();
		setEnabledAveraging(false);
	}

	@Override
	public void begin(int tickSize1) {
		super.begin(tickSize1);
		widget = getIndicator();
		int dim = widget.getChscalelevels();
		thcount = new int[dim + 1];
		thInitials = new int[dim + 1];
		relScales = new boolean[dim + 1];
		eventPatternModel.setPresentScales(relScales);
		String msg = "Running " + this.getHtmlBody(HtmlUtils.Plain);
		logWarn(msg);
		Utils.debug_var(574325, msg);
		_dealer.begin(this);
		flag = true;
		patternsQueue.clear();
		eventPatternModel.preinit(_dealer);
		eventPatternModel.getDelays(thInitials);
		firstTick = true;
		warmed = false;
		beginWarmUp();
	}

	@Override
	public StrategyType getStrategyType() {
		return StrategyType.AUTOMATIC;
	}

	public void beginWarmUp() {
		_dealer.setWarmingUp(true);
	}

	@Override
	public void endWarmUp() {
		super.endWarmUp();
		_dealer.setWarmingUp(false);
	}

	private void initTHsCount() {
		for (int s = widget.getChscalelevels(); s >= widget
				.getStartScaleLevelWidget(); s--) {
			if (widget.isLevelInformationPresent(s))
				thcount[s] = widget.getCurrentPivotsCount(s);
			else
				thcount[s] = 0;
			onBigScaleTH = s == bigScale;

		}
		_dealer.initTHsCount();
	}

	long lastTime = -1;
	boolean firstTick;

	private boolean pivotCheck() {
		if (firstTick) {
			initTHsCount();
			firstTick = false;
		}
		onBigScaleTH = false;
		long ctime = widget.getCurrentTime();
		if (ctime <= lastTime) {
			return true; // nothing to check...
		}

		lastTime = ctime;
		for (int s = widget.getChscalelevels(); s >= widget
				.getStartScaleLevelWidget(); s--) {
			if (widget.isLevelInformationPresent(s)
					&& widget.isThereANewPivot(s) && isARelevantScale(s)) {
				thcount[s] = widget.getCurrentPivotsCount(s);
				onBigScaleTH = s == bigScale;
				if (onBigScaleTH) {
					cleanQueue();
				}
				// logth(s);
			}
		}
		return false;
	}

	@Override
	public boolean isARelevantScale(int aS) {
		return relScales[aS];
	}

	private boolean[] relScales;

	@Override
	public void newTickImpl(QueueTick aTick) {
		newTick();
	}

	public void newTick() {
		if (pivotCheck()) {
			return;
		}
		_dealer.beginCheck();
		if (!warmedUp()) {
			return;
		}
		if (flag) {
			eventPatternModel.preinit(_dealer);
			eventPatternModel.init(_dealer);
			int dim = widget.getChscalelevels();
			relScales = new boolean[dim + 1];
			eventPatternModel.setPresentScales(relScales);
			Utils.debug_var(12345,
					"--------Scales " + Arrays.toString(relScales));
			bigScale = eventPatternModel.getBigEntryScale();
			daemons.setParentEvent(eventPatternModel);
			daemons.preinit(_dealer);
			daemons.init(_dealer);
			chargeEvent();
			flag = false;
		}
		checkQueue();
		boolean got = _eventToAdd.checkIFTriggered(_dealer);
		if (got) {
			enqueueAPattern();
		}
		if (daemons != null) {
			daemons.checkIFTriggered(_dealer);
		}
		_dealer.checkEventThreads();
		_dealer.endCheck();
	}

	/**
	 * @param reqId
	 */
	public void manualExecution(int reqId, IOrderExec anExec) {
		OrderImpl order = (OrderImpl) getPortfolio().getOrdersMap().get(
				Integer.valueOf(anExec.getOrderId()));
		if (!order.isChild())
			_dealer.addManualEntryOrder(order);
		else
			_dealer.removeManualFilledEntry(order.getParent());
	}

	private boolean warmedUp() {
		if (!warmed) {
			for (int i = 0; i < thcount.length; i++) {
				if (thcount[i] < thInitials[i]) {
					return false;
				}
			}
			warmed = true;
		}
		return true;
	}

	private void logCreation(int i) {
		log(TradeMessage.COMMENT, "Creating new Pattern [" + i + "]", 0);
	}

	/**
	 * @param msg
	 */
	private void logWarn(String msg) {
		// DO NOTHING
	}

	private boolean checkQueue() {
		for (Iterator<EventGeneral> iterator = patternsQueue.iterator(); iterator
				.hasNext();) {
			EventGeneral e = iterator.next();
			e.checkIFTriggered(_dealer);
			if (e.isTriggered()) {
				logMatch();
				iterator.remove();
			}
			if (e.isDiscarded()) {
				// dealer.logDiscarding(e, "Died ");
				e.cancelThisEvent();
				iterator.remove();
			}
		}
		return false;
	}

	private void cleanQueue() {
		ArrayList<Integer> d = new ArrayList<>();
		for (Iterator<EventGeneral> iterator = patternsQueue.iterator(); iterator
				.hasNext();) {
			EventGeneral e = iterator.next();
			if (!e.gotEntry()) {
				d.add(Integer.valueOf(e.getBirthID()));
				iterator.remove();
				e.cancelThisEvent();
			}
		}
		d.add(Integer.valueOf(_eventToAdd.getBirthID()));
		if (d.size() > 0) {
			logDisc(" Patterns " + d.toString());
		}
		chargeEvent();
	}

	private void logMatch() {
		//
	}

	/**
	 * @param m
	 */
	private void logDisc(String m) {
		//
	}

	private void enqueueAPattern() {
		if (!_eventToAdd.isTriggered()) {
			patternsQueue.add(_eventToAdd);
		}
		chargeEvent();
	}

	private void chargeEvent() {
		_eventToAdd = eventPatternModel.clone();
		_dealer.setNewBirth();
		_eventToAdd.preinit(_dealer);
		_eventToAdd.init(_dealer);
		logCreation(_eventToAdd.getBirthID());
	}

	@Override
	public void newExecution(IOrderExec aAnExec) {
		super.newExecution(aAnExec);
		if (getOrdersMap().containsKey(Integer.valueOf(aAnExec.getOrderId())))

			/*
			 * To do, does the strategy needs the fake time?
			 */
			_dealer.orderFilled(new MyOrderReport(getPortfolio().getOrdersMap()
					.get(Integer.valueOf(aAnExec.getOrderId())), aAnExec
					.getExecutionPrice(), aAnExec.getExecutionTime(), aAnExec
					.getExecutionTime()));
	}

	@Override
	public void orderStatus(IOrderStatus aStatus) {
		super.orderStatus(aStatus);
		switch (aStatus.getStatus()) {
		case CANCELLED:
			orderCanceled(aStatus.getOrderId());
			break;
		case ACCEPTED:
			_dealer.orderCanceledOrConfirmed(aStatus.getOrderId());
			break;
		// $CASES-OMITTED$
		default:
			// TODO see what to do...
			break;
		}
	}

	public void orderCanceled(int aId) {
		_dealer.orderCanceledOrConfirmed(aId);
		log(TradeMessage.CANCELED, "Confirm Cancelled "
				+ getPortfolio().getOrdersMap().get(Integer.valueOf(aId)), aId);
	}

	ArrayList<Integer> reqIDs = new ArrayList<>();

	@Override
	public String toString() {
		return getHtmlBody(HtmlUtils.Plain);
	}

	@Override
	public String getHtmlBody(HtmlUtils aUtil) {
		return eventPatternModel.getHtmlBody(aUtil);
	}

	/**
	 * @return the eventPatten
	 */
	// @JSON
	public EventGeneral getEventPattern() {
		return eventPatternModel;
	}

	/**
	 * @param aEventPatten
	 *            the eventPatten to set
	 */
	public void setEventPattern(EventGeneral aEventPatten) {
		eventPatternModel = aEventPatten;
	}

	// @JSON
	public EventsDaemonsCollection getDaemons() {
		return daemons;
	}

	public void setDaemons(EventsDaemonsCollection aDaemons) {
		this.daemons = aDaemons;
	}

	public void addEventPattern(EventGeneral aEventPatten) {
		eventPatternModel = aEventPatten;
	}

	/**
	 * @param daemon
	 */
	public void addDaemon(EventDaemon daemon) {
		// daemons.addEvent(daemon);
	}

	// @JSON
	public EventGeneral getAveragingLogic() {
		return averagingLogic;
	}

	public void setAveragingLogic(EventGeneral aAveragingLogic) {
		this.averagingLogic = aAveragingLogic;
	}

	protected boolean enabledAveraging;

	// @JSON
	public boolean isEnabledAveraging() {
		return enabledAveraging;
	}

	public void setEnabledAveraging(boolean aEnabledAveraging) {
		this.enabledAveraging = aEnabledAveraging;
	}

	private void setAveraging() {
		EventsDaemonsCollection t = new EventsDaemonsCollection();
		t.addEvent(getAveragingConfig(LSFilterType.Long));
		t.addEvent(getAveragingConfig(LSFilterType.Short));
		averagingLogic = t;
	}

	protected static EventCommandContainer getAveragingConfig(
			LSFilterType filter) {
		EventCommandContainer cc = new EventCommandContainer();
		cc.setPrecondition(new EventAtomProfitLoss(
				EventAtomProfitLoss.ProfitLoss.Profit, true, true, 2, filter));
		cc.setCommand(new EventAtomExit(true, true, filter));
		return cc;
	}

	@Override
	public void stopTrading() {
		if (_dealer != null) {
			_dealer.stopTrading();
		}
	}

	/**
	 * checks whether the entry is a pure entry or a pure exit.
	 * 
	 * @param entry
	 *            true to check if it is a pure entry.
	 * @return
	 */
	public boolean isPure(boolean entry) {
		return eventPatternModel.isPure(entry);
	}

	public boolean isPureEntry() {
		return isPure(true);
	}

	public boolean isPureExit() {
		return isPure(false);
	}

	@Override
	public void orderConfirmedByUser(IOrderMfg order) {
		_dealer.orderCanceledOrConfirmed(order.getId());
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
