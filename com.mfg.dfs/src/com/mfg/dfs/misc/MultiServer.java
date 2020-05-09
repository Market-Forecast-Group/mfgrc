package com.mfg.dfs.misc;

import static com.mfg.utils.Utils.debug_var;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.marketforescastgroup.logger.LogManager;
import com.mfg.common.BarType;
import com.mfg.common.CsvSymbol;
import com.mfg.common.DFSException;
import com.mfg.common.DFSQuote;
import com.mfg.common.DFSSubscriptionStartEvent;
import com.mfg.common.DFSSymbolEvent;
import com.mfg.common.DfsInvalidRangeException;
import com.mfg.common.DfsSymbol;
import com.mfg.common.IBarCache;
import com.mfg.common.ISymbolListener;
import com.mfg.common.Maturity;
import com.mfg.common.RandomSymbol;
import com.mfg.common.RequestParams;
import com.mfg.dfs.conn.DfsCacheRepo.GetSymbolDataAns;
import com.mfg.dfs.conn.DfsProxy;
import com.mfg.dfs.conn.DfsSchedulingTimes;
import com.mfg.dfs.conn.DfsSymbolList;
import com.mfg.dfs.conn.IDFS;
import com.mfg.dfs.conn.IDFSListener;
import com.mfg.dfs.conn.IDFSObserver;
import com.mfg.dfs.conn.IDataFeedController;
import com.mfg.dfs.conn.IDatabaseChangeListener;
import com.mfg.dfs.conn.IQuoteHook;
import com.mfg.dfs.data.DataModel;
import com.mfg.dfs.data.DfsSymbolStatus;
import com.mfg.dfs.data.MaturityStats;
import com.mfg.dfs.iqfeed.IqFeedClient;
import com.mfg.dm.TickDataRequest;
import com.mfg.utils.FinancialMath;
import com.mfg.utils.U;
import com.mfg.utils.concurrent.LazyWriteArrayList;
import com.mfg.utils.concurrent.LazyWriteArrayList.RunnableItem;

/**
 * The multiserver is a container for the dfs server able to be called from many
 * threads and many clients.
 * 
 * <p>
 * Its interface is multiclient. It has a set of listeners and it is able to
 * manage them.
 * 
 * <p>
 * The request takes the listener. But the listener is then passed to the
 * service itself.
 * 
 * <p>
 * The service is then used as a bridge between the server and the client
 * socket.
 * 
 * <p>
 * The service does not implement the {@linkplain IDFS} interface, because it is
 * not a simple service.
 * 
 * <p>
 * The monoclient server will use the Multiserver to do its work, not the
 * contrary.
 * 
 * @author Sergio
 * 
 */
public class MultiServer implements IDataFeedListener, IDFSObserver,
		ISymbolListener, IDatabaseChangeListener {

	/**
	 * This record holds a subscription.
	 * <p>
	 * It is used to have the same listener subscribed more than one time.
	 * 
	 * 
	 * @author Sergio
	 * 
	 */
	private static final class Subscription {

		/**
		 * This is the symbol with which this subscription is known from the
		 * outside.
		 */
		final String _mfgSymbol;

		final IDFSSynchableQuoteListener _listener;

		/**
		 * The constructor takes also the symbol with which this listener has
		 * subscribed to the symbol. This because the listener may have
		 * subscribed to the symbol with the continuous contract, which is
		 * different.
		 * <p>
		 * When we notify the listener of the new price we should give the
		 * continuous contract instead.
		 * 
		 * @param aListener
		 * @param mfgSymbol
		 */
		public Subscription(IDFSSynchableQuoteListener aListener,
				String mfgSymbol) {
			_listener = aListener;
			_mfgSymbol = mfgSymbol;
		}
	}

	/**
	 * the record which holds all the data relative to a subscription.
	 * 
	 * @author Sergio
	 * 
	 */
	private static final class SubscriptionData {
		CopyOnWriteArrayList<Subscription> _subs = new CopyOnWriteArrayList<>();

		// private int _lastQuote;

		/**
		 * The symbol which is associated to this subscription data.
		 */
		private final DfsSymbol _symbol;

		public SubscriptionData(DfsSymbol subSymbol) {
			_symbol = subSymbol;
		}

		private void _waitForAck(DFSSymbolEvent aQuote) {
			if (aQuote instanceof DFSQuote) {
				DFSQuote quote = (DFSQuote) aQuote;
				if (quote.warmUpTick) {
					return;
				}
				/*
				 * 
				 * /* Every sub has to get the ack for the last quote.
				 */
				for (Subscription sub : _subs) {
					sub._listener.waitUntilProcessedTime(quote.symbol,
							quote.tick.getFakeTime());
				}
			}

		}

		/**
		 * adds a subscription, if the subscription is virtual then it sends
		 * also the start sub event.
		 */
		public void addSubscription(Subscription newSub, boolean isVirtual) {
			if (isVirtual) {
				DFSSymbolEvent startEvent = new DFSSubscriptionStartEvent(
						newSub._mfgSymbol, this._symbol.tick,
						this._symbol.scale);
				newSub._listener.onNewSymbolEvent(startEvent);
			}
			_subs.add(newSub);
		}

		public boolean contains(ISymbolListener aListener) {
			for (Subscription sub : _subs) {
				if (sub._listener == aListener) {
					return true;
				}
			}
			return false;
		}

		public void newRealSymbolPrice(long datetime, int price, int volume) {
			// boolean quoteChanged = true;
			// if (price == _lastQuote) {
			// /*
			// * If the price is equal to the last quote I simply update the
			// * volume.
			// */
			// quoteChanged = false;
			// }

			// _lastQuote = price;

			/*
			 * The symbol is the real symbol, I have to convert it to the mfg
			 * symbol.
			 */
			for (Subscription sub : _subs) {
				DFSSymbolEvent dfsquote;
				dfsquote = new DFSQuote(sub._mfgSymbol, datetime, price, volume);
				sub._listener.onNewSymbolEvent(dfsquote);
			}

		}

		/**
		 * 
		 * @param aListener
		 * @return the new size of the subs array, -1 if it has not been found.
		 */
		@SuppressWarnings("null")
		public int remove(ISymbolListener aListener) {
			Subscription subToRemove = null;
			int index = 0;
			IDFSSynchableQuoteListener listenerToRemove = null;
			for (Subscription sub : _subs) {
				if (aListener instanceof IDFSSynchableQuoteListener) {
					if (sub._listener == aListener) {
						// ok, I have found it
						subToRemove = sub;
						listenerToRemove = (IDFSSynchableQuoteListener) aListener;
						break;
					}
				} else {
					// is it in the wrapper?
					SynchableWrapper sw = (SynchableWrapper) sub._listener;
					if (sw._listener == aListener) {
						subToRemove = sub;
						listenerToRemove = sw;
						break;
					}
				}

				++index;
			}

			if (subToRemove == null) {
				return -1; // not found
			}

			/*
			 * The listener to be removed may be waiting for the push to push
			 * and this may cause a deadlock, so before removing I make sure
			 * that it will no more block the push to push.
			 */
			listenerToRemove.aboutToBeRemoved();

			/*
			 * The subs should be freed now by the push to push infinite wait by
			 * the preceding call. This is not so much tested and I am not sure
			 * if this solves all possible deadlocks.
			 * 
			 * 
			 * There was another possible deadlock, because there was a race
			 * condition on the _subs array with the method onNewQuote.
			 * 
			 * I have set these two methods as synchronized but I do not know if
			 * this is really risolutive of all the problems.
			 * 
			 * If not probably the lock must be put directly on the MultiServer
			 * object.
			 */
			final int finalIndex = index;
			/*
			 * This thread may be stuck but I free the creator thread which
			 * closes actually the socket.
			 */
			int eventualSize = _subs.size() - 1;
			_subs.remove(finalIndex);

			// I return the eventual size because the actual size may not be
			// updated in time.
			return eventualSize;
		}

		/**
		 * Sends a new quote to the outside, this is a virtual symbol.
		 * 
		 * <P>
		 * The method is synchronized because there may be a race condition with
		 * the {@link #remove(ISymbolListener)} method. This may be called by
		 * the normal virtual symbol pump and the other may be called by the
		 * ending thread which removes all the subscription.
		 * 
		 * @param aQuote
		 */
		public void sendNewQuote(DFSSymbolEvent aQuote) {
			for (Subscription sub : _subs) {
				sub._listener.onNewSymbolEvent(aQuote);
			}
			/*
			 * The subscription will wait for ack for all the subs for this
			 * virtual symbol.
			 */
			_waitForAck(aQuote);
		}
	}

	static class SymbolChangedIterator extends
			RunnableItem<IDatabaseChangeListener> {

		private final String _symbol;
		private final MaturityStats _stats;

		public SymbolChangedIterator(String aSymbol, MaturityStats someStats) {
			_symbol = aSymbol;
			_stats = someStats;
		}

		@Override
		public void run(IDatabaseChangeListener aItem) {
			aItem.onSymbolChanged(_symbol, _stats);
		}

	}

	private static final class SynchableWrapper implements
			IDFSSynchableQuoteListener {

		ISymbolListener _listener;

		public SynchableWrapper(ISymbolListener aListener) {
			_listener = aListener;
		}

		@Override
		public void aboutToBeRemoved() {
			// nop

		}

		@Override
		public void onNewSymbolEvent(DFSSymbolEvent anEvent) {
			_listener.onNewSymbolEvent(anEvent);

		}

		@Override
		public void waitUntilProcessedTime(String symbol, int aFakeTime) {
			// noop, this is only a wrapper.
		}

	}

	/**
	 * The data model contains everything: cache short term and also the
	 * connection to the long term data
	 */
	private DataModel _dm;

	/**
	 * The controller has also the data feed, which is used for both the real
	 * time and the historical part (the real implementation of the data feed
	 * will hide the complexity that may be present in the data feed itself).
	 */
	private IDataFeed _dataFeed;

	/**
	 * This is the array list of listeners, used to dispatch messages to the
	 * outside.
	 * 
	 * <p>
	 * Of these listeners only one is direct, the others are instances of
	 * {@linkplain DfsProxy}
	 */
	private CopyOnWriteArrayList<IDFSListener> _listeners = new CopyOnWriteArrayList<>();

	/**
	 * This is the subscription map.
	 * <P>
	 * key = symbol (this is the real symbol, not the mfg symbol, or a virtual
	 * symbol), value = a list of all the subscription(s).
	 * <p>
	 * The map is used to avoid to subscribe more than once to the given symbol.
	 */
	private HashMap<String, SubscriptionData> _subscriptions = new HashMap<>();

	private boolean _offline;

	private EConnectionStatus _lastHistoricalStatus = EConnectionStatus.DISCONNECTED;

	private EConnectionStatus _lastRealTimeStatus = EConnectionStatus.DISCONNECTED;

	private CopyOnWriteArrayList<IDFSObserver> _observers = new CopyOnWriteArrayList<>();

	/**
	 * The list which contains the hooks linked to the real quotes.
	 */
	private CopyOnWriteArrayList<IQuoteHook> _quoteHooks = new CopyOnWriteArrayList<>();

	private boolean _schedulerRunning;

	/**
	 * This is the map of the listeners which are watching a particular symbol.
	 * 
	 * <p>
	 * Key is the symbol (prefix + maturity) and value is the list of all the
	 * listeners which are watching it.
	 */
	private HashMap<String, LazyWriteArrayList<IDatabaseChangeListener>> _watchers = new HashMap<>();

	/**
	 * The virtual symbols are stored here, in a map. The map entry is created
	 * by the object which creates the virtual symbol.
	 * 
	 * <p>
	 * It is NOT an error to create a virtual symbol twice, because each
	 * creation gives rise to a new symbol altogether. This is by design because
	 * in this way different clients may have the same tick data request but
	 * they need independently created tick streams.
	 * 
	 * <p>
	 * The key is a <b>unique</b> combination of the request hash id and a time
	 * stamp.
	 */
	private HashMap<String, IVirtualSymbol> _virtualSymbols = new HashMap<>();

	public MultiServer() {

	}

	/**
	 * This method is called when there is a reconnection.
	 * 
	 * <p>
	 * The current subscriptions are then re-scheduled.
	 */
	private void _redoSubscriptions() {
		for (String subSymbol : _subscriptions.keySet()) {
			try {
				_dataFeed.subscribeToSymbol(subSymbol);
			} catch (DFSException e) {
				/*
				 * Something has gone wrong, what can it be? It is safer to
				 * declare the data feed a bit "gone". In any case it will try
				 * again to reconnect, so I will have another option to
				 * subscribe again.
				 */
				e.printStackTrace();
			}
		}
	}

	/**
	 * Adds the quote hook.
	 * 
	 * @param aHook
	 */
	public boolean addGlobalQuoteHook(IQuoteHook aHook) {
		if (_quoteHooks.contains(aHook)) {
			return false;
		}
		return _quoteHooks.add(aHook);
	}

	/**
	 * adds a listener to this multiserver. This listener is stored to dispatch
	 * {@link #onConnectionStatusUpdate(com.mfg.dfs.serv.IDataFeedListener.ETypeOfData, com.mfg.dfs.serv.IDataFeedListener.EConnectionStatus)}
	 * messages
	 * 
	 * <p>
	 * Because this object is a singleton and all the listeners of this server
	 * will stay here.
	 * 
	 * <p>
	 * If this Multiserver is embedded it will have also a direct listener, but
	 * for this object there is no difference between a real listener or a
	 * proxy.
	 * 
	 * @param _listener
	 */
	public void addListener(IDFSListener _listener) {
		synchronized (_listeners) {
			_listeners.add(_listener);
		}
		// I will notify the listeners of the last status.
		_listener.onConnectionStatusUpdate(ETypeOfData.HISTORICAL,
				_lastHistoricalStatus);
		_listener.onConnectionStatusUpdate(ETypeOfData.REAL_TIME,
				_lastRealTimeStatus);
	}

	public boolean addObserver(IDFSObserver anObserver) {
		return _observers.addIfAbsent(anObserver);
	}

	/**
	 * Adds a watcher for the particular symbol passed as a parameter.
	 * 
	 * @param aListener
	 * @param symbol
	 * @throws DFSException
	 *             if anything is wrong. This means that the symbol is probably
	 *             invalid.
	 */
	public void addWatcher(IDatabaseChangeListener aListener, String symbol)
			throws DFSException {
		LazyWriteArrayList<IDatabaseChangeListener> list;
		synchronized (_watchers) {
			list = _watchers.get(symbol);
		}
		if (list == null) {
			list = new LazyWriteArrayList<>();
			_dm.watchSymbol(symbol, this);
			synchronized (_watchers) {
				_watchers.put(symbol, list);
			}

		}
		list.add(aListener);

	}

	/**
	 * Creates a virtual symbol in server's space.
	 * <p>
	 * As all the methods in this class, they may be called by a real service or
	 * a stub in server's space.
	 * <p>
	 * This means that the returned object usually is only a primitive, a string
	 * or a POD, but not a <i>real</i> java object, with state, just to be
	 * clear.
	 * 
	 * <p>
	 * This means, in particular here, that the virtual symbol is not
	 * controllable using an object, but only using the unique identifier which
	 * is returned by this method.
	 * 
	 * @param aRequest
	 * @return the unique identifier for the symbol, used to
	 *         subscribe/unsubscribe to it, and then to
	 * @throws DFSException
	 *             if anything is wrong
	 */
	public synchronized String createVirtualSymbol(TickDataRequest aRequest)
			throws DFSException {

		/*
		 * The request may be of a csv symbol which is local... in this way I
		 * will create another virtual symbol, not connected to DFS but to CSV
		 */

		IVirtualSymbol vs;

		if (aRequest._symbol instanceof CsvSymbol) {
			vs = new CSVVirtualSymbol(this, aRequest);
		} else if (aRequest._symbol instanceof RandomSymbol) {
			vs = new VirtualRandomSymbol(this, aRequest);
		} else {
			/*
			 * The symbol is always created, because an identical request will
			 * be treated as a different tick stream by the server.
			 * 
			 * The request has the external symbol, but to be safe I get the
			 * tick from the data model which has of course the updated data
			 * (this may change in future, so that the tick data request will
			 * not have the tick any more).
			 */
			GetSymbolDataAns dataAns = _dm.getCache().getSymbolDataSafe(
					aRequest.getLocalSymbol());

			U.debug_var(388293, "the symbol ",
					aRequest.getSymbol().getSymbol(), " is translated to ",
					dataAns.f1.parsedMaturity, " with remainder: ",
					dataAns.f1.unparsedString);

			vs = new VirtualSymbol(this, aRequest, dataAns.f2.getSymbol().tick);
		}

		_virtualSymbols.put(vs.getId(), vs);
		return vs.getId();

	}

	public int getBarCount(String symbol, BarType aType, int barWidth)
			throws DFSException {
		return _dm.getBarCount(symbol, aType, barWidth);
	}

	/**
	 * this is a simple pass method that implements the
	 * {@linkplain IDFS#getBarsBetween(String, long, long)}.
	 * 
	 * @param symbol
	 *            the symbol queried
	 * @param aType
	 * @param barWidth
	 *            the width of the bar used to ask the statistic
	 * @param startDate
	 *            the start date from which we start computation
	 * @param endDate
	 *            the end date to arrive to
	 * @return the number of bars contained in the interval
	 * @throws DFSException
	 *             if symbol is not valid or if the interval is outside the
	 *             available range
	 */
	public int getBarsBetween(String symbol, BarType aType, int barWidth,
			long startDate, long endDate) throws DFSException {

		return _dm.getBarsBetween(symbol, aType, barWidth, startDate, endDate);
	}

	/**
	 * Creates a instantaneous snapshot of a history table, either real or
	 * continuous.
	 * 
	 * @param aReq
	 * @return
	 * @throws DFSException
	 */
	public IBarCache getCache(RequestParams aReq) throws DFSException {
		return _dm.getCache(aReq);
	}

	public IBarCache getCache(String prefixSymbol, Maturity aMaturity,
			BarType aType, int nUnits) throws DFSException {
		return _dm.getCache(prefixSymbol, aMaturity, aType, nUnits);
	}

	public IDataFeedController getController() {
		if (_dataFeed instanceof IDataFeedController) {
			return (IDataFeedController) _dataFeed;
		}
		return null;
	}

	public long getDateAfterXBarsFrom(String symbol, BarType aType,
			int barWidth, long startDate, int numBars) throws DFSException {
		if (numBars < 0) {
			throw new DfsInvalidRangeException("negative num bars given");
		}
		return _dm.getDateAfterXBarsFrom(symbol, aType, barWidth, startDate,
				numBars);
	}

	public long getDateBeforeXBarsFrom(String symbol, BarType aType,
			int barWidth, long endTime, int numBars) throws DFSException {
		if (numBars < 0) {
			throw new DfsInvalidRangeException("negative num bars given");
		}
		return _dm.getDateBeforeXBarsFrom(symbol, aType, barWidth, endTime,
				numBars);
	}

	public IDataFeed getFeed() {
		return _dataFeed;
	}

	public DataModel getModel() {
		return _dm;
	}

	public DfsSchedulingTimes getSchedulingTimes() {
		return _dm.getSchedulingTimes();
	}

	/**
	 * @param symbol
	 * @throws DFSException
	 *             not used, for now.
	 */
	public DfsSymbolStatus getStatusForSymbol(String symbol, boolean forceCheck)
			throws DFSException {
		return _dm.getStatusForSymbol(symbol, forceCheck);
	}

	public DfsSymbolList getSymbolsList() {
		return _dm.getSymbolsList();
	}

	/**
	 * Gets the virtual symbol associated to a particular id.
	 * 
	 * <p>
	 * The method is called not by the external world, so if there is a Null
	 * Pointer Exception then there is some error in the handling of the
	 * subscriptions, or a race condition, or something like that.
	 * 
	 * <p>
	 * This method should be package protected.
	 * 
	 * @param _symbol
	 *            the unique id of the symbol.
	 * @return
	 */
	public synchronized IVirtualSymbol getVirtualSymbol(String _symbol) {
		return _virtualSymbols.get(_symbol);
	}

	/**
	 * returns true if we are connected to a simulated data feed.
	 * 
	 * @return true if this is a simulated data feed.
	 */
	public boolean isConnectedToSimulatedDataFeed() {
		return (_dataFeed instanceof PseudoRandomDataFeed);
	}

	public boolean isSchedulerRunning() {
		return _schedulerRunning;
	}

	/**
	 * @param user
	 * @param password
	 * @throws DFSException
	 */
	@SuppressWarnings({ "static-method" })
	public void login(String user, String password) throws DFSException {
		debug_var(393935, "stub login for user ", user, " and password ",
				password);
	}

	/**
	 * this method forces the manual scheduling which will check consistency,
	 * fix holes, etc...
	 */
	public void manualScheduling() {
		_dm.manualScheduling();
	}

	/**
	 * The multiserver is able to store multiple listeners, so it is sensible
	 * that the
	 * {@link #onConnectionStatusUpdate(com.mfg.dfs.serv.IMarketConnectionStatusListener.ETypeOfData, com.mfg.dfs.serv.IMarketConnectionStatusListener.EConnectionStatus)}
	 * stays here. The problem is for the
	 * {@link #onNewQuote(String, long, String)} method, because we may have the
	 * continuous contract, but the multiserver is not able to know anything
	 * about it.
	 * 
	 * <p>
	 * We have to inject a transparent mapping between the multiserver and the
	 * 
	 */
	@Override
	public void onConnectionStatusUpdate(ETypeOfData aDataType,
			EConnectionStatus aStatus) {

		// debug_var(381039, "*** status of ", aDataType, " is ", aStatus);

		switch (aDataType) {
		case HISTORICAL:
			_lastHistoricalStatus = aStatus;
			break;
		case REAL_TIME:
			_lastRealTimeStatus = aStatus;

			/*
			 * reconnection capability, If I connect to the real time I will
			 * have the possibility to add all the symbols which were subscribed
			 * before.
			 */
			if (aStatus == EConnectionStatus.CONNECTED) {
				_redoSubscriptions();
			}
			break;
		case DFS_PROXY:
			// should not happen, so go with the assert.
		default:
			assert (false);
			break;

		}
		synchronized (_listeners) {
			for (IDFSListener lis : _listeners) {
				lis.onConnectionStatusUpdate(aDataType, aStatus);
			}
		}
	}

	@Override
	public void onNewSymbolEvent(DFSSymbolEvent aQuote) {
		/*
		 * This is of course a quote from a virtual symbol
		 */

		String symbol = aQuote.symbol;

		SubscriptionData subData = _subscriptions.get(symbol);

		if (subData == null) {
			/*
			 * This should not happen, if it happens there is a race condition
			 * for the virtual symbol.
			 */
			assert (false);
			return;
		}

		/*
		 * The quote could be the same (in price) usually because a final tick
		 * follows a not final tick.
		 */

		subData.sendNewQuote(aQuote);

	}

	@Override
	public void onNewQuote(String symbol, long datetime, long timeStampLocal,
			String quote, int volume) {

		/*
		 * this may come ONLY from the real data feed, because the virtual
		 * symbol will call only the onQuote (DFSQuote) which has the quote
		 * already massaged.
		 */

		for (IQuoteHook hook : _quoteHooks) {
			hook.onNewQuote(symbol, datetime, timeStampLocal, quote);
		}

		SubscriptionData subData = _subscriptions.get(symbol);

		if (subData == null) {
			debug_var(938922, "bogus quote for symbol ", symbol,
					" I unsubscribe to it");
			try {
				_dataFeed.unsubscribeSymbol(symbol);
			} catch (DFSException e) {
				// Something is very wrong, probably the dfs is down
				debug_var(829931,
						"something is wrong! I force the stopping of the server");
				stop();
				onConnectionStatusUpdate(ETypeOfData.HISTORICAL,
						EConnectionStatus.DISCONNECTED);
				onConnectionStatusUpdate(ETypeOfData.REAL_TIME,
						EConnectionStatus.DISCONNECTED);
			}
			return;
		}

		try {
			int scale = _dm.getScaleForSymbol(symbol);
			int price = FinancialMath.stringPriceToInt(quote, scale);

			subData.newRealSymbolPrice(datetime, price, volume);

		} catch (DFSException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onSchedulerEndedCycle() {
		LogManager.getInstance().INFO("Ended SCHEDULING...");
		_schedulerRunning = false;

		for (IDFSObserver obs : _observers) {
			obs.onSchedulerEndedCycle();
		}

	}

	@Override
	public void onSchedulerStartRunning() {
		LogManager.getInstance().INFO("START SCHEDULING...");
		_schedulerRunning = true;

		for (IDFSObserver obs : _observers) {
			obs.onSchedulerStartRunning();
		}

	}

	@Override
	public void onSymbolChanged(String aSymbol, MaturityStats newStats) {
		LazyWriteArrayList<IDatabaseChangeListener> list;
		synchronized (_watchers) {
			list = _watchers.get(aSymbol);
		}
		// If you get a NPE then there is a bug, because there are no more
		// listeners.
		SymbolChangedIterator sci = new SymbolChangedIterator(aSymbol, newStats);
		list.iterateCode(sci);
	}

	@Override
	public void onSymbolInitializationEnded(String symbol) {

		for (IDFSObserver obs : _observers) {
			obs.onSymbolInitializationEnded(symbol);
		}

	}

	/**
	 * Halts the current thread until the given symbol has been updated.
	 * 
	 * @param symbol
	 *            the symbol to refresh.
	 * @throws DFSException
	 */
	public void refreshSynchSymbol(String symbol) throws DFSException {
		_dm.refreshSynchSymbol(symbol);
	}

	/**
	 * @param aHook
	 */
	public void removeGlobalQuoteHook(IQuoteHook aHook) {
		_quoteHooks.remove(aHook);
	}

	public boolean removeListener(IDFSListener _listener) {
		synchronized (_listeners) {
			return _listeners.remove(_listener);
		}
	}

	public boolean removeObserver(IDFSObserver anObserver) {
		return _observers.remove(anObserver);
	}

	/**
	 * @param aListener
	 * @param aSymbol
	 * @throws DFSException
	 */
	public void removeWatcher(IDatabaseChangeListener aListener, String aSymbol)
			throws DFSException {
		LazyWriteArrayList<IDatabaseChangeListener> list;
		synchronized (_watchers) {
			list = _watchers.get(aSymbol);
		}
		// If you get an NPE then it could be the caller's fault.
		if (list == null) {
			throw new DFSException("symbol " + aSymbol + " Was not watched");
		}
		list.remove(aListener);
		if (list.size() == 0) {
			_dm.unwatchSymbol(aSymbol);
			synchronized (_watchers) {
				_watchers.remove(aSymbol);
			}
		}
	}

	public void setSchedulingTimes(DfsSchedulingTimes aSchedulingTimes)
			throws DFSException {
		_dm.setSchedulingTimes(aSchedulingTimes);
	}

	/**
	 * @param useSimulator
	 *            true if you want to use the simulator (this will create the
	 *            pseudo random data feed)
	 */
	public void start(boolean useSimulator, boolean offline)
			throws DFSException {
		_offline = offline;
		if (_dm != null) {
			return; // I am already started
		}

		if (useSimulator) {
			_dataFeed = new PseudoRandomDataFeed(this);
			_offline = false;
		} else {
			if (!offline) {
				_dataFeed = new IqFeedClient(this);
			} else {
				_dataFeed = new OfflineDataFeed(this);
			}
		}

		_dataFeed.start("nothing"); // This will start the data feed thread!

		try {
			_dm = new DataModel(_dataFeed); // this will start the data model
											// thread.
		} catch (Exception e) {
			throw new DFSException(e);
		}
	}

	public void stop() {
		// service stops and it disconnects from db.
		if (_dm == null) {
			// already stopped
			return;
		}
		/*
		 * Stop all the virtual symbols.
		 */
		for (IVirtualSymbol virtSymbol : _virtualSymbols.values()) {
			virtSymbol.stop();
		}

		try {
			_dm.stop(_offline);
		} catch (IOException e) {
			debug_var(391933, "Exception ", e, " while stopping the data model");
		}

		_dataFeed.stop();

		_dataFeed = null;
		_dm = null; // stopped.

	}

	/**
	 * subscribes the given listener to the symbol given.
	 * <p>
	 * The symbol is <b>NOT</b> a prefix, it is a complete symbol which is
	 * directly passed (no more massaging) to the data feed.
	 * 
	 * <p>
	 * This means that the listener is responsible to subscribe to the symbol
	 * and then to unsubscribe from it.
	 * 
	 * <p>
	 * subscribing more than once to the given symbol is a no-op, but how to
	 * inform the client?
	 * 
	 * <p>
	 * This is a thing to be considered, for now we simply return a boolean
	 * 
	 * 
	 * @param aListener
	 *            the listener which is tied to this subscription
	 * 
	 * @return true if we have subscribed to the symbol effectively, false if we
	 *         were already subscribed to it.
	 * @throws DFSException
	 */
	public synchronized boolean subscribeQuote(ISymbolListener aListener,
			String mfgSymbol) throws DFSException {

		/*
		 * I have to create a wrapper which will simulate a wait for the given
		 * quote.
		 */
		SynchableWrapper sw = new SynchableWrapper(aListener);

		return this.subscribeSynchableQuote(sw, mfgSymbol);
	}

	/**
	 * The subscription is to a synchable quote listener
	 * 
	 * @param aListener
	 * @param mfgSymbol
	 * @return
	 * @throws DFSException
	 */
	public synchronized boolean subscribeSynchableQuote(
			IDFSSynchableQuoteListener aListener, String mfgSymbol)
			throws DFSException {

		String symbol;
		boolean isVirtual = false;

		if (VirtualSymbolBase.isVirtual(mfgSymbol)) {
			symbol = mfgSymbol;
			isVirtual = true;
		} else {
			symbol = _dm.translateMfgSymbol(mfgSymbol);
			debug_var(817834, "The symbol ", mfgSymbol, " is translated to ",
					symbol);
		}

		SubscriptionData subData = _subscriptions.get(symbol);
		if (subData == null) {

			DfsSymbol subSymbol;
			if (isVirtual) {

				/*
				 * maybe this can be changed, because the dfs symbol is used
				 * only to get the tick and the scale.
				 */
				IVirtualSymbol virtSymbol = getVirtualSymbol(symbol);
				if (virtSymbol == null) {
					/*
					 * you are trying to subscribe to a missing symbol
					 */
					throw new DFSException("unknown virtual symbol " + symbol);
				}

				subSymbol = virtSymbol.getDfsSymbol();
			} else {
				subSymbol = _dm.getCache().getSymbolDataSafe(mfgSymbol).f2
						.getSymbol();
			}

			subData = new SubscriptionData(subSymbol);

			Subscription newSub = new Subscription(aListener, mfgSymbol);
			subData.addSubscription(newSub, isVirtual);
			_subscriptions.put(symbol, subData);

			if (!isVirtual) {
				try {
					_dataFeed.subscribeToSymbol(symbol);
				} catch (DFSException e) {
					// something is wrong, I force the removing of this symbol
					_subscriptions.remove(symbol);
					throw e;
				}
			} else {

				/*
				 * starts the virtual symbol. this start is in another thread.
				 * The other thread may subscribe itself to the real symbol
				 * which is related to this virtual symbol.
				 * 
				 * The virtual symbol may immediately start to give prices, that
				 * is the onNewQuote can be called immediately, but this is OK,
				 * because in any case it will be called from another thread
				 * which will be the virtual symbol's thread.
				 */
				_virtualSymbols.get(symbol).start();
			}
			return true; // this is a new subscription altogether

		}
		/*
		 * we are already subscribed to this symbol, but maybe from other
		 * clients.
		 */

		// Let's see if this listener is already subscribed...
		if (subData.contains(aListener)) {
			return false;
		}

		// If I am here I have not found the subscription so I create a new one
		Subscription newSub = new Subscription(aListener, mfgSymbol);
		subData.addSubscription(newSub, isVirtual);

		return true;

	}

	public void truncateMaturity(String aPassword, String aSymbol,
			BarType aType, long truncateLength) throws DFSException {
		if (aPassword.equals("testpp")) {
			_dm.truncateMaturity(aSymbol, aType, truncateLength);
		}
	}

	/**
	 * @param aListener
	 *            the listener that wants to unsubscribe to the quote.
	 * 
	 * @return true if the unsubscription is successful, false if the listener
	 *         was not subscribed to the symbol
	 * @throws DFSException
	 */
	public synchronized boolean unsubscribeQuote(ISymbolListener aListener,
			String mfgSymbol) throws DFSException {

		String iqSymbol;
		boolean isVirtual = false;
		if (VirtualSymbolBase.isVirtual(mfgSymbol)) {
			iqSymbol = mfgSymbol;
			isVirtual = true;

			/*
			 * It is an error to unsubscribe to a missing virtual symbol
			 */
			if (!_virtualSymbols.containsKey(iqSymbol)) {
				// U.debug_var(498520, "missing virtual symbol ", iqSymbol);
				// return false;
				throw new DFSException("missing virtual symbol " + iqSymbol);
			}

		} else {
			iqSymbol = _dm.translateMfgSymbol(mfgSymbol);
		}

		SubscriptionData subData = _subscriptions.get(iqSymbol);

		if (subData == null) {
			// I am already unsubscribed to this symbol.
			return false;
		}

		int newSize = subData.remove(aListener);
		if (newSize == 0) {
			/*
			 * found and it was the last!
			 */
			if (isVirtual) {
				_virtualSymbols.get(iqSymbol).stop();
				_virtualSymbols.remove(iqSymbol);
			} else {
				_dataFeed.unsubscribeSymbol(iqSymbol);
			}
			_subscriptions.remove(iqSymbol);
		}

		return true;

	}

}
