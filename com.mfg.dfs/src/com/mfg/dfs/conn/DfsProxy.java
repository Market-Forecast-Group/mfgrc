package com.mfg.dfs.conn;

import static com.mfg.utils.Utils.debug_var;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import com.mfg.common.BarType;
import com.mfg.common.DFSException;
import com.mfg.common.DFSQuote;
import com.mfg.common.DFSSymbolEvent;
import com.mfg.common.IBarCache;
import com.mfg.common.IDataSource;
import com.mfg.common.ISymbolListener;
import com.mfg.common.Maturity;
import com.mfg.dfs.data.DfsSymbolStatus;
import com.mfg.dfs.data.MaturityStats;
import com.mfg.dm.TickDataRequest;
import com.mfg.utils.IMarketConnectionStatusListener.EConnectionStatus;
import com.mfg.utils.IMarketConnectionStatusListener.ETypeOfData;
import com.mfg.utils.U;
import com.mfg.utils.socket.BaseSocketHelper;
import com.mfg.utils.socket.SimpleRemoteCommand;
import com.mfg.utils.socket.SimpleSocketTextClient;
import com.mfg.utils.socket.SimpleTextPushSink;
import com.thoughtworks.xstream.XStream;

/**
 * The proxy (in the client space) class which has the same interface as the
 * {@linkplain IDFS}.
 * 
 * <p>
 * This class is the server in client's space, some methods are translated and
 * others are simply no operations.
 * 
 * <p>
 * This class is the equivalent of the class {@linkplain ESignalBridge} for
 * esignal, it will create a queue and a socket. The reader thread will gather
 * the message from the socket and the class will transform the client message
 * in socket messages (lines of text).
 * 
 * <p>
 * The class implements a protocol which is synchronous but with notifications
 * from the server, the protocol is line oriented and every request has a token
 * which will return back and give the answer to the client.
 * 
 * <p>
 * The protocol used is <b>NOT</b> the same as the bridge protocol, because this
 * should be data feed independent and in any case has messages that the bridge
 * does not understand, for example the login and also the session, get tick
 * size, get symbol information etc...
 * 
 * <p>
 * This class really is used usually in the dfs connector, or, at present
 * moment, in the {@linkplain DfsBridgeConnector} class, which will make a
 * bridge from the eSignal connector to dfs protocol.
 * 
 * <p>
 * This class is a singleton. We can have only one proxy per process space.
 * 
 * @author Sergio
 * 
 */
public class DfsProxy extends BaseService {

	/**
	 * 
	 * this class is used as an helper because java does not allow multiple
	 * inheritance. In reality a {@link DfsProxy} is a {@link BaseService} and a
	 * {@link BaseSocketHelper}.
	 * 
	 * <p>
	 * The helper is static because in this way it is simple to detect
	 * dependencies.
	 */
	static class DfsSocketHelper extends BaseSocketHelper {

		CopyOnWriteArrayList<IDFSListener> _listeners = new CopyOnWriteArrayList<>();
		/**
		 * This is the map which is used to store the watchers for the symbol
		 * changed... the listeners are stored in client's space, of course
		 * because the interface are not automatically remotable.
		 * 
		 * <p>
		 * Every symbol may have a list of listeners.
		 * 
		 * <p>
		 * The {@link #_listeners} field is only used for generic notifications
		 * and it was preferred to have a different map.
		 * 
		 * <p>
		 * These are the listeners for a single symbol. *
		 * 
		 */
		HashMap<String, CopyOnWriteArrayList<IDatabaseChangeListener>> _watchers = new HashMap<>();

		/**
		 * Then there is the list of the "global" database observers. These
		 * observers do not receive the notification about a single symbol, they
		 * receive notifications about "global" events in the database.
		 * 
		 * <p>
		 * There is an exception, an observer will receive a notification about
		 * a newly added symbol, but that is like a global notification.
		 */
		CopyOnWriteArrayList<IDFSObserver> _observers = new CopyOnWriteArrayList<>();

		private static final String DFS_GREETING = "DFS";

		protected DfsSocketHelper(IDFSListener aListener,
				SimpleSocketTextClient aClient) {
			super(DFS_GREETING, aClient);

			/*
			 * I add here the principal listeners. Others may be added later but
			 * this is fixed.
			 */
			_listeners.add(aListener);
		}

		@Override
		protected void _handleStatusLine(String[] statuses) {

			String status1 = statuses[0];
			String status2 = statuses[1];

			if (status1.equals(DfsStub.SYMBOL_CHANGED_EVENT)) {

				String symbol = status2;
				XStream stream = new XStream();
				MaturityStats ms = (MaturityStats) stream.fromXML(statuses[2]);

				// here I will cycle on the IDatabaseChangedListeners objects.
				// if you get a NPE then there is a race condition on the
				// watching.
				CopyOnWriteArrayList<IDatabaseChangeListener> list;
				synchronized (_watchers) {
					list = _watchers.get(symbol);
				}
				for (IDatabaseChangeListener listener : list) {
					listener.onSymbolChanged(symbol, ms);
				}

				return;

			} else if (status1.equals(DfsStub.SCHEDULER_STATUS)) {
				if (status2.equals(DfsStub.SCHEDULER_ENDED_CYCLE_EVENT)) {
					for (IDFSObserver obs : _observers) {
						obs.onSchedulerEndedCycle();
					}
				} else if (status2
						.equals(DfsStub.SCHEDULER_STARTED_CYCLE_EVENT)) {
					for (IDFSObserver obs : _observers) {
						obs.onSchedulerStartRunning();
					}
				} else if (status2
						.equals(DfsStub.SYMBOL_INITIALIZATION_ENDED_EVENT)) {
					String symbol = statuses[2];
					for (IDFSObserver obs : _observers) {
						obs.onSymbolInitializationEnded(symbol);
					}
				}

				return;
			}

			for (IDFSListener listener : _listeners) {
				listener.onConnectionStatusUpdate(ETypeOfData.valueOf(status1),
						EConnectionStatus.valueOf(status2));
			}

		}

		public void add(IDFSListener aListener) {
			_listeners.add(aListener);
		}

		// /**
		// * @return the principal listener, that which cannot be removed
		// because
		// * it is part of the structure of the helper (it is assigned in
		// * the {@link DfsProxy#start(IDFSListener)} method.
		// */
		// public IDFSQuoteListener getPrincipalListener() {
		// return _listeners.get(0);
		// }

		@Override
		public void onConnectionEstabilished() {
			for (IDFSListener listener : _listeners) {
				listener.onConnectionStatusUpdate(ETypeOfData.DFS_PROXY,
						EConnectionStatus.CONNECTED);
			}

		}

		@Override
		public void onLostConnection() {
			// If I lose the connection to dfs I also lose the connections to
			// the data feed
			for (IDFSListener listener : _listeners) {
				listener.onConnectionStatusUpdate(ETypeOfData.DFS_PROXY,
						EConnectionStatus.DISCONNECTED);
				listener.onConnectionStatusUpdate(ETypeOfData.HISTORICAL,
						EConnectionStatus.DISCONNECTED);
				listener.onConnectionStatusUpdate(ETypeOfData.REAL_TIME,
						EConnectionStatus.DISCONNECTED);
			}

		}

		@Override
		public void onTryingToConnect() {
			for (IDFSListener listener : _listeners) {
				listener.onConnectionStatusUpdate(ETypeOfData.DFS_PROXY,
						EConnectionStatus.CONNECTING);
			}
		}

		public void remove(IDFSListener aListener) {
			_listeners.remove(aListener);
			// you cannot remove the first listener
			assert (_listeners.size() != 0);
		}

		/**
		 * unwatches the given symbol
		 * 
		 * @param aSymbol
		 * @return true if the symbol has now no listener, it is an orphan
		 */
		public boolean unwatchDbSymbol(IDatabaseChangeListener aListener,
				String aSymbol) {
			synchronized (_watchers) {
				CopyOnWriteArrayList<IDatabaseChangeListener> list = _watchers
						.remove(aSymbol);
				if (list == null) {
					return true;
				}
				list.remove(aListener);
				if (list.size() == 0) {
					_watchers.remove(aSymbol);
					return true;
				}
				return false;
			}

		}

		/**
		 * 
		 * @param aListener
		 * @param symbol
		 * @return true if the symbol was already watched.
		 */
		public boolean watchDbSymbol(IDatabaseChangeListener aListener,
				String symbol) {
			synchronized (_watchers) {
				CopyOnWriteArrayList<IDatabaseChangeListener> list = _watchers
						.get(symbol);
				if (list == null) {
					list = new CopyOnWriteArrayList<>();
					list.add(aListener);
					_watchers.put(symbol, list);
					return false;
				}
				// I already watch this symbol, I simply update the listener.
				return !list.addIfAbsent(aListener);
			}
		}

		public boolean addObserver(IDFSObserver anObserver) {
			return _observers.addIfAbsent(anObserver);
		}

		public boolean removeObserver(IDFSObserver anObserver) {
			return _observers.remove(anObserver);
		}
	}

	private static DfsProxy _instance;

	/**
	 * factory method to create the proxy.
	 * 
	 * @param aListener
	 * @param host
	 * @param port
	 * @return
	 */
	public static DfsProxy createProxy(String host, int port) {
		if (_instance == null) {
			_instance = new DfsProxy(host, port);
		}
		return _instance;
	}

	/**
	 * simple access method to get the proxy instance (used by the requests to
	 * add/remove push sinks).
	 * 
	 * @return
	 */
	public static DfsProxy getInstance() {
		return _instance;
	}

	final AtomicBoolean _endRequested = new AtomicBoolean(false);

	private DfsSocketHelper _dfsSocketHelper;

	private String _host;

	private int _port;

	SimpleSocketTextClient _dfsSocket;

	private boolean _isSimDataFeed;

	@SuppressWarnings("boxing")
	private DfsProxy(String host, int port) {
		debug_var(425322, "DFS will try to connect to ", host, " port ", port);
		_host = host;
		_port = port;
		// _listeners.add(aListener);
	}

	@Override
	protected void _connectToExistingRemoteDataSource(String dataSourceId,
			ISymbolListener aListener) throws DFSException {
		_genericSubscribe(dataSourceId, aListener, true);
	}

	@Override
	protected IDataSource _createDataSourceImpl(TickDataRequest aRequest,
			ISymbolListener aListener) throws DFSException {

		/*
		 * in the proxy side the virtual symbol is not here. Here I only create
		 * the ServerSideDataSource (which is proxy side...) and then the
		 * virtual symbol is created in the server.
		 * 
		 * Here only the source is created and this source is used to collect
		 * the data from the Virtual symbol.
		 * 
		 * So in case of proxy the bars will not be here, here will be fed only
		 * the expansion of them.
		 */

		CreateDataSourceCommand cdsc = new CreateDataSourceCommand(aRequest);
		_sendRequest(cdsc);

		String remoteVirtualSymbol = cdsc.getVirtualSymbolId();

		/*
		 * Create the sink?, no the sink is created only when I start the data
		 * source, the virtual symbol is no different from other symbols.
		 */

		IDataSource dataSource = new DFSProxyDataSource(remoteVirtualSymbol,
				this, aListener, aRequest.getLayersSize());

		return dataSource;
	}

	@Override
	protected void _disconnectFromRemoteDataSource(String dataSourceId,
			ISymbolListener aListener) throws DFSException {
		unsubscribeQuote(dataSourceId);
	}

	private void _genericSubscribe(String symbol, ISymbolListener idfsListener,
			boolean mustSendAck) throws DFSException {
		SubscribeCommand sr = new SubscribeCommand(symbol, idfsListener,
				mustSendAck);
		_sendRequest(sr);
		sr.join();
	}

	void _sendRequest(SimpleRemoteCommand sr) throws DFSException {
		try {
			_dfsSocketHelper._sendRequest(sr);
		} catch (IOException e) {
			e.printStackTrace();
			throw new DFSException(e);
		}
	}

	@Override
	public boolean addGlobalQuoteHook(IQuoteHook aWatcher) {
		/*
		 * The proxy cannot add a global quote hook.
		 */
		return false;
	}

	@Override
	public void addListener(IDFSListener aListener) {
		_dfsSocketHelper.add(aListener);
	}

	@Override
	public boolean addObserver(IDFSObserver anObserver) {
		return _dfsSocketHelper.addObserver(anObserver);
	}

	public void addSink(SimpleTextPushSink ss) {
		_dfsSocketHelper.addSink(ss);

	}

	@SuppressWarnings("boxing")
	@Override
	public int getBarCount(String symbol, BarType aType, int barWidth)
			throws DFSException {
		GetBarCountCommand gbcc = new GetBarCountCommand(symbol, aType,
				barWidth);
		_sendRequest(gbcc);
		return (Integer) gbcc.getAnswer();
	}

	@SuppressWarnings("boxing")
	@Override
	public int getBarsBetween(String symbol, BarType aType, int barWidth,
			long startDate, long endDate) throws DFSException {
		RemoteCommand gbbr = new GetBarsBetweenCommand(symbol, aType, 1,
				startDate, endDate);
		_sendRequest(gbbr);
		return (Integer) gbbr.getAnswer();
	}

	@Override
	public IBarCache getCache(String prefixSymbol, Maturity aMaturity,
			BarType aType, int nUnits) throws DFSException {
		throw new UnsupportedOperationException();
	}

	/**
	 * The remote proxy is not able to communicate with the data feed directly,
	 * so this method will always return null.
	 */
	@Override
	public IDataFeedController getController() {
		return null;
	}

	@SuppressWarnings("boxing")
	@Override
	public long getDateAfterXBarsFrom(String symbol, BarType aType,
			int barWidth, long startDate, int numBars) throws DFSException {
		GetDateAfterXBarsReq gdaxbr = new GetDateAfterXBarsReq(symbol, aType,
				barWidth, startDate, numBars);
		_sendRequest(gdaxbr);
		return (long) gdaxbr.getAnswer();
	}

	@SuppressWarnings("boxing")
	@Override
	public long getDateBeforeXBarsFrom(String symbol, BarType aType,
			int barWidth, long endTime, int numBars) throws DFSException {
		GetDateBeforeXBarsReq gdbxbr = new GetDateBeforeXBarsReq(symbol, aType,
				barWidth, endTime, numBars);
		_sendRequest(gdbxbr);
		return (long) gdbxbr.getAnswer();
	}

	@Override
	public DfsSchedulingTimes getSchedulingTimes() {
		throw new UnsupportedOperationException("to be done");
	}

	@Override
	public DfsSymbolStatus getStatusForSymbol(String symbol)
			throws DFSException {

		GetSymbolStatusCommand gssr = new GetSymbolStatusCommand(symbol);
		_sendRequest(gssr);
		return (DfsSymbolStatus) gssr.getAnswer(); // blocking!

	}

	@Override
	public DfsSymbolList getSymbolsList() throws DFSException {
		SymbolsListCommand slr = new SymbolsListCommand();
		_sendRequest(slr);
		return slr.getSymbolsList();
	}

	@Override
	public boolean isConnectedToSimulatedDataFeed() throws DFSException {
		// IsConnectedToSimulatedDataFeedCommand ictsdf = new
		// IsConnectedToSimulatedDataFeedCommand();
		// _sendRequest(ictsdf);
		// return (boolean) ictsdf.getAnswer();
		return _isSimDataFeed;
	}

	@Override
	public boolean isSchedulerRunning() {
		return false; // I do not know.
	}

	@Override
	public void login(String user, String password) throws DFSException {
		LoginCommand lr = new LoginCommand(user, password);
		_sendRequest(lr);
		lr.join();
	}

	@Override
	public void manualScheduling() {
		//
	}

	@Override
	public void removeGlobalQuoteHook(IQuoteHook aHook) {
		// here it is a no-op, as the hook was never added.
	}

	@Override
	public void removeListener(IDFSListener aListener) {
		_dfsSocketHelper.remove(aListener);
	}

	@Override
	public boolean removeObserver(IDFSObserver anObserver) {
		return _dfsSocketHelper.removeObserver(anObserver);
	}

	public void removeSink(SimpleTextPushSink _hs) throws DFSException {
		try {
			_dfsSocketHelper.removeSink(_hs);
		} catch (IOException e) {
			throw new DFSException(e);
		}
	}

	// @Override
	// public IBarCache requestHistory(RequestParams aReq, IBarCacheAsync
	// aMonitor)
	// throws DFSException {
	// RequestHistoryCommand rhr = new RequestHistoryCommand(aReq, aMonitor);
	// _sendRequest(rhr);
	// return rhr.getCache(); // blocking!
	//
	// }

	/**
	 * Sends back to the socket a certain answer which is a push to push answer.
	 * 
	 * @param line
	 * @throws IOException
	 * @throws DFSException
	 */
	public void sendPushToPush(String line) throws IOException {
		_dfsSocket.writeLine(U.join(DfsStub.PUSH_TO_PUSH, line));
	}

	@Override
	public void setSchedulingTimes(DfsSchedulingTimes aSchedulingTimes) {
		throw new UnsupportedOperationException("to be done");
	}

	/**
	 * This is the equivalent of the startDp method.
	 */
	@SuppressWarnings("boxing")
	@Override
	public void start(IDFSListener aListener) throws DFSException {
		_dfsSocket = new SimpleSocketTextClient(true);
		_dfsSocketHelper = new DfsSocketHelper(aListener, _dfsSocket);
		_dfsSocket.start(_dfsSocketHelper, _host, _port);
		if (!_dfsSocket.waitForConnect(1000)) {
			throw new DFSException("Cannot connect");
		}

		/*
		 * I set here the flag because it will be asked by MultiTEA during
		 * construction, and that could cause a deadlock.
		 * 
		 * I have preferred to ask the question here and this question is then
		 * put into the field.
		 */
		IsConnectedToSimulatedDataFeedCommand ictsdf = new IsConnectedToSimulatedDataFeedCommand();
		_sendRequest(ictsdf);
		_isSimDataFeed = (boolean) ictsdf.getAnswer();
	}

	@Override
	public void stop() {
		_dfsSocket.stop();
		_dfsSocketHelper = null;
	}

	// @Override
	// public void subscribeQuote(String symbol) throws DFSException {
	// /*
	// * The "normal" way of creating a subscription, with an external
	// * subscribers and without the push notifications, because this is not a
	// * virtual symbol.
	// */
	// if (VirtualSymbolBase.isVirtual(symbol)) {
	// /*
	// * it is not possible to subscribe to a virtual symbol, use the
	// * connectToExistingDataSource method, instead.
	// */
	// throw new IllegalArgumentException();
	// }
	//
	// _genericSubscribe(symbol, _dfsSocketHelper.getPrincipalListener(),
	// false);
	//
	// }

	/**
	 * This is the generic subscription to a virtual symbol.
	 * 
	 * <p>
	 * The subscription to a virtual symbol is different because it has a
	 * different listener, it does not use the "global" listener (which is the
	 * {@link DfsDataProvider}).
	 * 
	 * @param virtualSymbolId
	 *            the id of the virtual symbol.
	 * @param aQuoteListener
	 * @param mustSendAck
	 * @throws DFSException
	 */
	void subscribeVirtualSymbol(String virtualSymbolId,
			ISymbolListener aQuoteListener, boolean mustSendAck)
			throws DFSException {
		_genericSubscribe(virtualSymbolId, aQuoteListener, mustSendAck);

	}

	@Override
	public void truncateMaturity(String aPassword, String aSymbol,
			BarType aType, long truncateDate) {
		//
	}

	@Override
	public void unsubscribeQuote(String symbol) throws DFSException {
		/*
		 * There is no difference unsubscribing a virtual symbol?
		 */
		UnsubscribeRequest ur = new UnsubscribeRequest(symbol);
		_sendRequest(ur);
		ur.join();
	}

	@Override
	public void unWatchDbSymbol(IDatabaseChangeListener aListener,
			String aSymbol) throws DFSException {
		boolean orphanSymbol = _dfsSocketHelper.unwatchDbSymbol(aListener,
				aSymbol);
		if (orphanSymbol) {
			// I must really unwatch!
			UnwatchMaturityCommand uwc = new UnwatchMaturityCommand(aSymbol);
			_sendRequest(uwc);
			uwc.join();
		}

	}

	@Override
	public void watchDbSymbol(IDatabaseChangeListener aListener, String symbol)
			throws DFSException {
		boolean alreadyWatched = _dfsSocketHelper.watchDbSymbol(aListener,
				symbol);
		try {
			if (!alreadyWatched) {
				WatchMaturityCommand wmc = new WatchMaturityCommand(symbol);
				_sendRequest(wmc);
				wmc.join();
			}
		} catch (DFSException e) {
			_dfsSocketHelper.unwatchDbSymbol(aListener, symbol);
			throw e;
		}
	}

	public SimpleTextPushSink removeSink(String payload) {
		return _dfsSocketHelper.removeSink(payload);
	}

}

// /**
// * a sink which will listen to all the push lines from the stub and builds the
// * bar cache one bar at a time.
// *
// * <p>
// * The sink can end if the history is not open, in this case the sink delete
// * itself from the list of sinks which are in the proxy.
// *
// * @author Sergio
// *
// */
// class HistorySink extends SimpleTextPushSink {
//
// // The history sink has the cache which is being built.
// // private ProxyBarCache _cache = new ProxyBarCache(this);
//
// // this is used to know if the first push has ended;
// // when the first push ends, the cache is given to the outside
// private AtomicBoolean _endFirstPush = new AtomicBoolean();
//
// // private boolean _aborted = false;
//
// // private IBarCacheAsync _async;
//
// public HistorySink(String aPushId,
// @SuppressWarnings("unused") IBarCacheAsync aAsync) {
// super(aPushId);
// // _async = aAsync;
// }
//
// @SuppressWarnings("static-method")
// public IBarCache getCache() {
// return null;
// }
//
// @Override
// public void handlePush(String payload) {
// // if (_aborted) {
// // return;
// // }
// // // debug_var(939921, "Handle the payload [", payload, "]");
// // if (payload.equals(Reqs.END_FIRST_PUSH)) {
// // // Ok the first push is finished
// // _endFirstPush.set(true);
// // synchronized (_endFirstPush) {
// // _endFirstPush.notify();
// // }
// // } else if (payload.equals(Reqs.END_HISTORY)) {
// // // Ok the request is finished.
// // _endFirstPush.set(true);
// // synchronized (_endFirstPush) {
// // _endFirstPush.notify();
// // }
// // _sinkOver = true; // the sink is over, please recollect it.
// // } else {
// // // this is a bar... let's add it
// // Bar aBar = Bar.parseFromString(payload);
// // _cache.add(aBar);
// // _async.doneOneStep();
// //
// // if (_async.isCanceled()) {
// //
// // // abort the cache (?)
// // try {
// // _cache.close();
// // } catch (DFSException e) {
// // e.printStackTrace();
// // throw new RuntimeException(e);
// // }
// // _aborted = true;
// //
// // // signal the end of the push
// // synchronized (_endFirstPush) {
// // _endFirstPush.notify();
// // }
// // }
// // }
// }
//
// public boolean joinFirstPush() throws InterruptedException {
// synchronized (_endFirstPush) {
// _endFirstPush.wait();
// }
// return _endFirstPush.get();
// }
//
// }

/**
 * This is the sink used to catch the quotes from the remote server.
 * 
 * <p>
 * The quotes may be normal or from a virtual symbol, in that case the quotes
 * are not passed to the normal listener (which is usually the
 * {@link DfsDataProvider}, but to the local proxy virtual symbol listener which
 * is an instance of {@link DFSProxyDataSource}.
 * 
 * @author Sergio
 * 
 */
class SubscriptionSink extends SimpleTextPushSink {

	private final ISymbolListener _listener;
	private final boolean _sendPushAck;

	/**
	 * 
	 * @param aPushId
	 * @param aListener
	 * @param sendPushAck
	 *            some subscriptions are synchronized between different clients
	 *            and this flag is used to signal that we want an acknowledge.
	 */
	public SubscriptionSink(String aPushId, ISymbolListener aListener,
			boolean sendPushAck) {
		super(aPushId);
		_listener = aListener;
		_sendPushAck = sendPushAck;
	}

	@SuppressWarnings("boxing")
	@Override
	public void handlePush(String payload) throws IOException {
		// debug_var(738389, "handle the push subscription [" , payload, "]");
		DFSSymbolEvent anEvent = DFSSymbolEvent.fromPayload(payload);
		try {
			/*
			 * This call must block the calling thread until the client has
			 * finished processing the quote, in this way we are able, if
			 * needed, to send the acknowledge when all the listeners (attached
			 * to this global listener) have finished processing the quote.
			 */
			_listener.onNewSymbolEvent(anEvent);
		} finally {
			/*
			 * Here if the subscription is synchronous I can inform the server
			 * that I have processed this tick.
			 * 
			 * All the work is OK only if this is enforced, that is if the
			 * onNewQuote is synchronous... this is to be checked.
			 */
			if (_sendPushAck && anEvent instanceof DFSQuote) {
				DFSQuote quote = (DFSQuote) anEvent;
				if (quote.warmUpTick == false) {
					/*
					 * If this is a real time tick I acknowledge the fact that I
					 * have processed it.
					 */
					DfsProxy.getInstance().sendPushToPush(
							U.join(quote.symbol, quote.tick.getFakeTime()));
				}

			}
		}

	}

}
