package com.mfg.dfs.conn;

import static com.mfg.utils.Utils.debug_var;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.mfg.common.Bar;
import com.mfg.common.DFSException;
import com.mfg.common.DFSSubscriptionStartEvent;
import com.mfg.common.DFSSymbolEvent;
import com.mfg.common.IBarCache;
import com.mfg.common.RequestParams;
import com.mfg.dfs.data.IRequestStatus;
import com.mfg.dfs.misc.MultiServer;
import com.mfg.utils.U;
import com.mfg.utils.socket.IPushSource;
import com.mfg.utils.socket.SimpleTextPushSource;
import com.mfg.utils.socket.SimpleTextServerStub;

/**
 * The cache push source is a source of push messages to give to the client an
 * entire view of the cache which is local in this process space.
 * 
 * <p>
 * We can think that this object is only a serializer for the bar cache.
 * 
 * <p>
 * but it is something more than that. It is also an object which takes the
 * cache, pools it and if there are new bars it will push them to the client as
 * long as this source is active.
 * 
 * @author Sergio
 * 
 */
class CachePushSource extends SimpleTextPushSource implements IRequestStatus {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4317556464946409302L;

	private transient IBarCache _cache;
	/* this is used to store the last index given to the client */
	private int _maxI = 0;
	/*
	 * Used to know whether the push source needs to finish after the first push
	 * or not
	 */
	private boolean _isOpenRequest;
	private int _curIndex;
	private final RequestParams _pars;
	private boolean _sentEndFirstPush = false;
	/**
	 * This flag is used to have the cache push source an opportunity to wait
	 * until the push sink key is sent to the socket.
	 * <p>
	 * In the former times there was a global lock to the {@linkplain DfsStub}
	 * object but that caused the stub to be not responding during a push, and
	 * if the push lasted for a long time the stub was not able to delete the
	 * push itself.
	 * <p>
	 * This was a nasty condition and this should restore the correct behavior
	 */
	private AtomicBoolean _releasedToPush = new AtomicBoolean(false);

	/**
	 * @param isOpenRequest
	 *            this depends on the request, if the request does not continue
	 *            in real time the push source delete itself after doing the
	 *            first push
	 */
	public CachePushSource(DfsStub aStub, IBarCache aLocalCache,
			boolean isOpenRequest, RequestParams pars) {
		super(aStub);
		_cache = aLocalCache;
		_isOpenRequest = isOpenRequest;
		_pars = pars;
	}

	@Override
	public synchronized void doPush() throws SocketException {
		/*
		 * The cache could be null if the user has deleted the push, so be
		 * careful. The method is synchronized with respect to interrupt
		 * request, so it should not happen
		 */

		// waiting for the release to push
		while (!_releasedToPush.get()) {
			try {
				synchronized (_releasedToPush) {
					_releasedToPush.wait(5000);
				}
			} catch (InterruptedException e) {
				// nothing
			}
		}

		if (_ended) {
			// nothing to do, I wait for recollection
			return;
		}

		// the do push here is simple: just serialize all the bars!
		boolean firstPush = false;
		if (_maxI == 0) {
			firstPush = true;
		}

		int curSize = _cache.size(); // I store it at the beginning because the
										// cache may change size during the push
		if (_maxI == curSize) {
			if (firstPush && !_isOpenRequest) {
				sendToSocket(Reqs.END_HISTORY);
				_ended = true;
			} else {
				if (!_sentEndFirstPush) {
					sendToSocket(Reqs.END_FIRST_PUSH);
					_sentEndFirstPush = true;
				}
			}
			return;
		}
		_curIndex = _maxI; // this is the start of the loop
		_maxI = curSize;
		for (; _curIndex < _maxI; ++_curIndex) {
			try {
				Bar aBar = _cache.getBar(_curIndex);
				sendToSocket(aBar.serialize());
				if (_endRequested.get()) {
					/*
					 * The cache is closed by the interrupt request method.
					 */
					// _cache.close();
					_ended = true;
					return;
				}
			} catch (DFSException e) {
				_ended = true;
				e.printStackTrace();
				break; // end the push
			}
		}

		if (firstPush) {
			// I send the end
			if (_isOpenRequest) {
				if (!_sentEndFirstPush) {
					_sentEndFirstPush = true;
					sendToSocket(Reqs.END_FIRST_PUSH);
				}
			} else {
				sendToSocket(Reqs.END_HISTORY);
				try {
					_cache.close();
				} catch (DFSException e) {
					e.printStackTrace();
					throw new SocketException(e.toString());
				}
				_ended = true;
			}

		}
	}

	@Override
	public RequestParams getRequest() {
		return _pars;
	}

	@Override
	public void interruptRequest() {
		/*
		 * This method cannot be synchronized, because the doPush method holds
		 * the lock on this, so first of all I interrupt the request, then I
		 * will wait for the doPush method to return
		 */
		super.interruptRequest();
		/*
		 * This synchronize will wait this monitor as the doPush holds it.
		 */
		synchronized (this) {
			try {
				_cache.close();
			} catch (DFSException e) {
				// I cannot throw a checked exception, I will throw a generic
				// runtime exception
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			_cache = null;
			_ended = true;
		}

	}

	/**
	 * This method usually is called by another thread that does the push, It is
	 * not necessary to synchronize the access, as we are talking about a
	 * primitive type
	 */
	@Override
	public int numBarsGiven() {
		return _curIndex;
	}

	public void releasePush() {
		_releasedToPush.set(true);
		synchronized (_releasedToPush) {
			_releasedToPush.notify();
		}

	}

}

/**
 * This is the stub object (in service space) of the dfs object.
 * 
 * <p>
 * There is one object of this class for each client in the system
 * 
 * <p>
 * This class will parse the line from the client and will get the command
 * parameters.
 * <p>
 * The command is then passed to the {@linkplain MultiServer} class which is
 * able to handle the multiclient approach.
 * 
 * <p>
 * When this class receives the notifications it will pass them to the client
 * socket.
 * 
 * @author Sergio
 * 
 */
public class DfsStub extends SimpleTextServerStub {

	/**
	 * This loop is called in the push thread execution space.
	 * <p>
	 * The thread will be able to
	 */
	protected void _pushThreadLoop() {
		// this is a normal pump loop
		while (true) {
			if (Thread.currentThread().isInterrupted()) {
				break; // I end the thread.
			}
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				// this is a normal situation... I am interrupted and I finish
				break;
			}

			/* Ok I can now give to all the sources a mean to do a push */
			// synchronized (this) {
			// I synchronize on me because when I get a request from the
			// outside (for a new cache) I have to first tell the client
			// the push sink and then later I have to start the actual push.
			// If we don't synchronize it may happen that the push starts
			// before the push sink is sent to the proxy

			synchronized (_pushSources) {
				ArrayList<SimpleTextPushSource> toRemove = new ArrayList<>();
				for (IPushSource psT : _pushSources) {
					SimpleTextPushSource ps = (SimpleTextPushSource) psT;
					try {
						ps.doPush();
					} catch (SocketException e) {
						debug_var(381940, "Got an exception while pushing ",
								ps, " I abort it");
						ps.interruptRequest();
					}
					if (ps.isEnded()) {
						toRemove.add(ps);
					}
				}

				for (SimpleTextPushSource ps : toRemove) {
					ps.aboutToBeCollected();
					_pushSources.remove(ps);
				}
			}
			// }

		}

	}

	/**
	 * starts the push thread.
	 * <p>
	 * From the outside the stub is only a passive object, it receives requests
	 * and gives answers. From a practical point of view, instead, we may have
	 * some requests which have not a single answer (for example the
	 * subscriptions) and from another point of view we may have requests which
	 * have a very long answer which <b>also</b> has not an end.
	 * <p>
	 * For example we have a cache request. This request may be very long. We
	 * may also give the payload one at a time... but the problem are the real
	 * time bars.
	 * <p>
	 * Those bars come after some time... and we must be prepared to handle them
	 * as well
	 */
	@Override
	public synchronized void start() {

		preStartHook();

		if (_pushThread != null) {
			return; // already started.
		}
		_pushThread = new Thread(new Runnable() {

			@Override
			public void run() {
				_pushThreadLoop();
			}
		});

		_pushThread.setName("SimpleTextServerPush " + this.hashCode());

		_pushThread.start();
	}

	/**
	 * This adds a normal push source. It is usually a cache push source, one
	 * which does some actual work when we call the do push.
	 * 
	 * @param aPushSource
	 */
	public final void addPushSource(IPushSource aPushSource) {
		_pushSources.add(aPushSource);
	}

	public static final String PUSH_TO_PUSH = "<ptp>";

	/**
	 * A status string for the scheduler.
	 */
	public static final String SCHEDULER_STATUS = "<scheduler>";

	/**
	 * The ended cycle event for the scheduler
	 */
	public static final String SCHEDULER_ENDED_CYCLE_EVENT = "<end_cycle>";

	/**
	 * The started cycle event for the scheduler.
	 */
	public static final String SCHEDULER_STARTED_CYCLE_EVENT = "<start_cycle>";

	/**
	 * Symbol initialization ended event.
	 */
	public static final String SYMBOL_INITIALIZATION_ENDED_EVENT = "<sym_start_event>";

	/**
	 * The event which is sent when the symbol has changed.
	 */
	public static final Object SYMBOL_CHANGED_EVENT = "<sym_changed_event>";

	transient MultiServer _dfs;
	transient ProxyDfsListener _proxyListener;

	/**
	 * Builds a stub which is used to receive commands from the outside.
	 * <p>
	 * The server passed to this constructor should be a multiserver, because it
	 * will receive many connections (potentially).
	 * 
	 * @param server
	 *            the server to which we will send the commands.
	 * 
	 * @param cs
	 *            the client socket (it is used to pass errors or handles);
	 */
	public DfsStub(MultiServer server, ClientSocket cs) {
		super(new DfsCommandFactory(), cs);
		_dfs = server;

		/*
		 * the listener is created here because it needs access to the stub to
		 * access the push sources.
		 */
		_proxyListener = new ProxyDfsListener(cs);
	}

	/**
	 * gets the server, this is only used by the remote commands to perform the
	 * action.
	 * 
	 * @return
	 */
	public MultiServer getServer() {
		return _dfs;
	}

	@Override
	public boolean _preParseHook(String line) {

		/*
		 * If the line starts with a string "ptp" then it is a push=to=push
		 * line, that is an answer which is really an answer to a push done to
		 * the client. This push=to=push protocol may be used to synchronize the
		 * push prices.
		 */
		if (line.startsWith(PUSH_TO_PUSH)) {
			String[] splits = U.commaPattern.split(line);
			String aSymbol = splits[1];
			int fakeTime = Integer.parseInt(splits[2]);
			_proxyListener.pushToPush(aSymbol, fakeTime);
			return true;
		}

		return false;

	}

	@Override
	protected void postDeletePushSourceHook() {
		setModified();
	}

	@Override
	protected void postStopHook() {
		// I have to remove all the subscriptions, in theory I have to remove
		// also all the bar cache handles but this is not so important, as the
		// close for a bar cache is only a no op.
		_proxyListener.removeAllSubscriptions(_dfs);
	}

	// lock the thread.

	@Override
	protected void preStartHook() {
		// this will also trigger the sending of the status update.
		_dfs.addListener(_proxyListener);
		boolean res = _dfs.addObserver(_proxyListener);
		assert (res);
	}

	@Override
	protected void preStopHook() {
		_dfs.removeListener(_proxyListener);
	}

	public void login(String user, String password) throws DFSException {
		_dfs.login(user, password);
		((ClientSocket) _cs).setLogin(user);
	}

}

/**
 * The push source is an object which is able to send messages to the proxy
 * client about new quotes for the given symbol.
 * 
 * <p>
 * The quotes come either from a raw subscription or from a virtual symbol. In
 * any case they are not filtered.
 * 
 * @author Sergio
 * 
 */
class SubscriptionPushSource extends SimpleTextPushSource {

	private final String _symbol;
	private DFSSymbolEvent _startSub;

	/**
	 * The last value got from the other side of the socket.
	 */
	private AtomicInteger _lastPushToPush = new AtomicInteger();

	public SubscriptionPushSource(String symbol, DfsStub aStub) {
		super(aStub);
		_symbol = symbol;
	}

	@Override
	public void doPush() {
		/*
		 * I do not push in response to some internal status, but if a new quote
		 * comes from the market.
		 */
		assert (false);
	}

	public void newQuote(DFSSymbolEvent aQuote) throws SocketException {
		/*
		 * The start sub event is saved, and it is sent in the afterSendHook...
		 */
		if (aQuote instanceof DFSSubscriptionStartEvent) {
			_startSub = aQuote;
			return;
		} else if (_startSub != null) {
			/*
			 * normal quote and there is a start sub waiting
			 */
			sendStartSubEvent();
		}

		assert (_symbol.compareTo(aQuote.symbol) == 0);

		aQuote.setPushToPushId(55 /* to do here */);
		String payload = aQuote.toPayload();
		super.sendToSocket(payload);
	}

	public void sendStartSubEvent() throws SocketException {

		if (_startSub != null) {
			String payload = _startSub.toPayload();
			super.sendToSocket(payload);
			_startSub = null;
		}

	}

	/**
	 * Receives the notification that the fake time has being processed.
	 * 
	 * @param fakeTime
	 *            the last fake time processed.
	 */
	public void pushToPush(int fakeTime) {
		_lastPushToPush.set(fakeTime);
		synchronized (_lastPushToPush) {
			_lastPushToPush.notify();
		}
	}

	public void waitForTime(int aFakeTime) {
		while (_lastPushToPush.get() < aFakeTime) {
			synchronized (_lastPushToPush) {
				try {
					/*
					 * The timeout is not really necessary, but it happens that
					 * sometimes the notify which is in the method pushToPush is
					 * not received in time and the wait here takes for ever.
					 */
					_lastPushToPush.wait(1_500);
				} catch (InterruptedException e) {
					// nothing
				}
			}
		}

	}
}
