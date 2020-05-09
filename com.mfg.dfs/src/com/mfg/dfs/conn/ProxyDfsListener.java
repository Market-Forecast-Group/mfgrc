package com.mfg.dfs.conn;

import static com.mfg.utils.Utils.debug_var;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketException;
import java.util.HashMap;

import com.mfg.common.DFSException;
import com.mfg.common.DFSSymbolEvent;
import com.mfg.dfs.conn.DfsProxy.DfsSocketHelper;
import com.mfg.dfs.data.MaturityStats;
import com.mfg.dfs.misc.IDFSSynchableQuoteListener;
import com.mfg.dfs.misc.MultiServer;
import com.mfg.utils.U;

/**
 * This is the proxy listener, it is used as a stand alone listener or a
 * dispatcher for the remote listeners of this service.
 * 
 * <p>
 * This is the listener server side which will send the message to a print
 * stream (the stream could be connected to a socket). *
 * 
 * <p>
 * The corresponding class which does the parsing of these messages is
 * {@link DfsSocketHelper} class method
 * 
 * {@code protected void _handleStatusLine(String[] statuses) }
 * 
 * @author Sergio
 * 
 */
public class ProxyDfsListener implements IDFSListener,
		IDFSSynchableQuoteListener, IDatabaseChangeListener, IDFSObserver {

	private PrintWriter _out;

	// A map with key = symbol, value = subscription
	private HashMap<String, SubscriptionPushSource> _subSources = new HashMap<>();

	private ClientSocket _cs;

	public ProxyDfsListener(PrintWriter _writer) {
		_out = _writer;
	}

	public ProxyDfsListener(ClientSocket cs) {
		_out = null;
		_cs = cs;
	}

	@Override
	public void onNewSymbolEvent(DFSSymbolEvent aQuote) {
		String symbol = aQuote.symbol;

		if (_out != null) {
			// This is a console proxy listener, no need to use the push
			// sources.
			StringBuilder sb = new StringBuilder();
			U.join("p", "qc", aQuote.toPayload());
			_out.println(sb.toString());
			_out.flush();
		} else {
			SubscriptionPushSource subSource;
			synchronized (_subSources) {
				subSource = _subSources.get(symbol);
			}
			if (subSource == null) {
				debug_var(738293, "The subscription to symbol ", symbol,
						" is null. THIS IS ANOMALOUS");
				_forceClientSocketStop();
				return;
			}
			try {
				subSource.newQuote(aQuote);
			} catch (SocketException e) {
				debug_var(919839, "got exception while pushing the quote to ",
						subSource.getPushKey(), " with symbol ", symbol,
						"  disconnecting the client");
				_forceClientSocketStop();
				// _subSources.remove(symbol);
			}

		}
	}

	/**
	 * Adds a subscription to this proxy.
	 * 
	 * <p>
	 * This is used to make a container for the proxy which is then used to feed
	 * the proxy client with the subscription key.
	 * 
	 * @param symbol
	 * @param sps
	 */
	public void addSubscriptionSource(String symbol, SubscriptionPushSource sps) {
		synchronized (_subSources) {
			_subSources.put(symbol, sps);
		}
	}

	/**
	 * removes all the subscriptions.
	 * 
	 * @param multiserver
	 */
	public void removeAllSubscriptions(MultiServer multiserver) {
		String[] array;

		/*
		 * In this way I relinquish the lock to the subsources immediately,
		 * because otherwise there may be a deadlock.
		 */
		synchronized (_subSources) {
			array = _subSources.keySet().toArray(new String[0]);
		}

		for (String symbol : array) {
			debug_var(829482, "Removing subscription to ", symbol);
			try {
				multiserver.unsubscribeQuote(this, symbol);
			} catch (DFSException e) {
				e.printStackTrace();
				debug_var(819391, "Wanted to unsubscribe to ", symbol, " got ",
						e);
			}
		}
	}

	/**
	 * gets the push identifier for a given symbol.
	 * <p>
	 * It returns the push identifier if the symbol is subscribed, null
	 * otherwise
	 * 
	 * @param symbol
	 * @return the push identifier, null otherwise
	 */
	public String getPushIdForSymbol(String symbol) {
		synchronized (_subSources) {
			SubscriptionPushSource subSource = _subSources.get(symbol);
			if (subSource == null) {
				return null;
			}
			return subSource.getPushKey();
		}
	}

	public SubscriptionPushSource removeSubscriptionSource(String symbol) {
		U.debug_var(319273, "About to remove the subscritption for ", symbol);
		synchronized (_subSources) {
			return _subSources.remove(symbol);
		}
	}

	@Override
	public void onConnectionStatusUpdate(ETypeOfData aDataType,
			EConnectionStatus aStatus) {
		if (_out != null) {
			// _out.println(U.join("s", aDataType, aStatus));
			// _out.flush();
			return;
		}
		try {
			_cs.printLine(U.join("s", aDataType, aStatus));
		} catch (IOException e) {
			debug_var(637195, "Got exception while updating the socket status");
			e.printStackTrace();
		}

	}

	/**
	 * Receives the push to push message for the given subscription.
	 * 
	 * @param aSymbol
	 *            the subscription symbol (virtual). It has to exist.
	 * 
	 * @param fakeTime
	 *            the last processed time for this subscription.
	 */
	public void pushToPush(String aSymbol, int fakeTime) {
		SubscriptionPushSource source = _subSources.get(aSymbol);
		/*
		 * source maybe null because the client has already removed the
		 * subscription. So there is nothing to signal the push to push to.
		 */
		if (source == null) {
			return;
		}
		source.pushToPush(fakeTime);
	}

	@SuppressWarnings("boxing")
	@Override
	public void waitUntilProcessedTime(String symbol, int aFakeTime) {
		U.debug_var(382139, "symbol ", symbol, " waiting for ptp ", aFakeTime);
		SubscriptionPushSource source = _subSources.get(symbol);
		if (source == null) {
			U.debug_var(
					932005,
					"the source is null, probably the client has crashed, removing subscription to ",
					symbol);
			_forceClientSocketStop();
			return;
		}
		source.waitForTime(aFakeTime);

	}

	@SuppressWarnings("boxing")
	@Override
	public void aboutToBeRemoved() {
		/*
		 * I simulate that a max push to push has come... so it will notify and
		 * return immediately.
		 */
		U.debug_var(338251, "The listener for ", this._cs.getRemoteIp(),
				" is going to be removed, I have ", _subSources.size(),
				" sources to clear");
		for (SubscriptionPushSource source : _subSources.values()) {
			source.pushToPush(Integer.MAX_VALUE);
		}

	}

	@Override
	public void onSchedulerEndedCycle() {
		if (_out != null) {
			return;
		}
		try {
			_cs.printLine(U.join("s", DfsStub.SCHEDULER_STATUS,
					DfsStub.SCHEDULER_ENDED_CYCLE_EVENT));
		} catch (IOException e) {
			_forceClientSocketStop();
		}
	}

	@Override
	public void onSchedulerStartRunning() {
		if (_out != null) {
			return;
		}
		try {
			_cs.printLine(U.join("s", DfsStub.SCHEDULER_STATUS,
					DfsStub.SCHEDULER_STARTED_CYCLE_EVENT));
		} catch (IOException e) {
			_forceClientSocketStop();
		}

	}

	@Override
	public void onSymbolInitializationEnded(String symbol) {
		if (_out != null) {
			return;
		}
		try {
			_cs.printLine(U.join("s", DfsStub.SCHEDULER_STATUS,
					DfsStub.SYMBOL_INITIALIZATION_ENDED_EVENT, symbol));
		} catch (IOException e) {
			_forceClientSocketStop();
		}
	}

	@Override
	public void onSymbolChanged(String aSymbol, MaturityStats newStats) {
		if (_out != null) {
			StringBuilder sb = new StringBuilder();
			U.join("symbol ", aSymbol, " CHANGED", newStats.toString());
			_out.println(sb.toString());
			_out.flush();
		} else {
			try {
				_cs.printLine(U.join("s", DfsStub.SYMBOL_CHANGED_EVENT,
						aSymbol, newStats.toString()));
			} catch (SocketException e) {
				_forceClientSocketStop();
			}
		}

	}

	private void _forceClientSocketStop() {
		U.debug_var(394821, "Forcing client stop ", _cs.getLogin(), " ip ",
				_cs.getRemoteIp());
		_cs.stop();
	}

}
