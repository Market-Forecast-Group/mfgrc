package com.mfg.dfs.iqfeed;

import static com.mfg.utils.Utils.debug_var;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import com.mfg.common.DFSException;
import com.mfg.common.DfsNoConnectionException;
import com.mfg.common.UnparsedBar;
import com.mfg.common.UnparsedTick;
import com.mfg.dfs.data.AllAvailableHistoricalData;
import com.mfg.dfs.data.HistoryRequest;
import com.mfg.dfs.data.IHistoryFeedListener;
import com.mfg.dfs.data.IHistoryFeedListener.EEosStatus;
import com.mfg.dfs.data.PartialHistoryRequest;
import com.mfg.dfs.misc.IDataFeed;
import com.mfg.dfs.misc.IDataFeedListener;
import com.mfg.utils.IMarketConnectionStatusListener.EConnectionStatus;
import com.mfg.utils.IMarketConnectionStatusListener.ETypeOfData;
import com.mfg.utils.U;
import com.mfg.utils.socket.ISimpleSocketListener;
import com.mfg.utils.socket.SimpleSocketTextClient;

/**
 * This is the iqfeed client socket towards iqConnect.exe.
 * 
 * <p>
 * The idea is that this client will handle the communications to <b>all</b> the
 * sockets which iqFeed creates (there are 4 of them, but we use only 2: the
 * real time socket and the history socket) (level2 socket and admin socket are
 * not used).
 * 
 * <p>
 * A question which is still to decide is whether this client is able to
 * simulate this two channels of communications in only one.
 * 
 * <p>
 * For example eSignal was able to give incomplete bars and to continue to give
 * bars in real time (apart from the quote). Should this client mimic the same
 * behavior? If this is to be expected than this client should be more complex,
 * because it needs to handle communications between two sockets at the same
 * time and it should be able to build bars given the quotes, which is a rather
 * error prone task (there is the glue with the real time data which is yet to
 * be perfected).
 * 
 * <p>
 * Probably the idea is to let the application handle the two stream of data
 * differently.
 * 
 * <p>
 * DFS is in some way the place where we can do this, because it updates the
 * history tables periodically and then it creates a stream of prices which is
 * then fed to the app.
 * 
 * 
 * <p>
 * I start to watch a symbol and then I can build the bars using the real time
 * data which is given by the streaming prices. The Feed is able to catch up
 * data using the feed and the last "n" seconds of live data (stored in the
 * servers).
 * 
 * 
 * @author Sergio
 * 
 */
public class IqFeedClient implements IDataFeed {

	/**
	 * This enum will lists all the historical requests.
	 * <p>
	 * This enumeration is used to store the correspondence between a string and
	 * a corresponding IqFeed historical request.
	 * 
	 * <p>
	 * Please refer to historical API documentation for in-depth explanation of
	 * each request type
	 * 
	 * @author Sergio
	 * 
	 */
	private enum EIqFeedHistReqs {

		/** tick data points request */
		HTX {

			@Override
			public Date parseIqDate(String aDateTime) throws ParseException {
				return IqDate.parseTickDate(aDateTime);
			}

		},
		/** tick data days request */
		HTD {

			@Override
			public Date parseIqDate(String aDateTime) throws ParseException {
				return IqDate.parseTickDate(aDateTime);
			}

		},
		/** tick data time-interval request */
		HTT {

			@Override
			public Date parseIqDate(String aDateTime) throws ParseException {
				return IqDate.parseTickDate(aDateTime);
			}

		},

		/** Historical data interval # points */
		HIX,
		/** Historical data interval # days */
		HID,
		/** Historical data interval-interval */
		HIT,

		/** number of End-Of-Day Data */
		HDX {

			@Override
			public Date parseIqDate(String aDateTime) throws ParseException {
				return IqDate.parseIqOnlyDate(aDateTime);
			}

		},
		/** Daily data between start/end */
		HDT {

			@Override
			public Date parseIqDate(String aDateTime) throws ParseException {
				return IqDate.parseIqOnlyDate(aDateTime);
			}

		},

		/** Number of week data points */
		HWX {

			@Override
			public Date parseIqDate(String aDateTime) throws ParseException {
				return IqDate.parseIqOnlyDate(aDateTime);
			}

		},
		/** Number of months data points */
		HMX {

			@Override
			public Date parseIqDate(String aDateTime) throws ParseException {
				return IqDate.parseIqOnlyDate(aDateTime);
			}

		};

		@SuppressWarnings("static-method")
		public Date parseIqDate(String aDateTime) throws ParseException {
			return IqDate.parseIqDate(aDateTime);
		}

		/**
		 * parses a string and returns the corresponding historical request.
		 * 
		 * @param aString
		 * @return
		 */
		public static EIqFeedHistReqs parseString(String aString) {
			if (aString.equals("HTX")) {
				return HTX;
			} else if (aString.equals("HTD")) {
				return HTD;
			} else if (aString.equals("HTT")) {
				return HTT;
			} else if (aString.equals("HIX")) {
				return HIX;
			} else if (aString.equals("HID")) {
				return HID;
			} else if (aString.equals("HIT")) {
				return HIT;
			} else if (aString.equals("HDX")) {
				return HDX;
			} else if (aString.equals("HDT")) {
				return HDT;
			} else if (aString.equals("HWX")) {
				return HWX;
			} else if (aString.equals("HMX")) {
				return HMX;
			}

			throw new IllegalArgumentException("unknown string");
		}

	}

	/**
	 * This is an helper class just to handle the logic for waiting a particular
	 * request to finish.
	 * 
	 * <p>
	 * It is an invariant of this class that we have only one active request at
	 * a time, but the difference is that we may have different
	 * {@linkplain IqFeedClient} objects.
	 * 
	 * @author Sergio
	 * 
	 */
	private final class IqFeedHistRequest {
		/**
		 * A symbolic timeout, 10 minutes should be enough.
		 */
		private static final long TIME_OUT_INTERVAL = 600_000;
		/**
		 * This is the type of request
		 */
		EIqFeedHistReqs request;
		/**
		 * This is the handle for this request, the combination of the type of
		 * request and the handle will create the requestId which is given to
		 * iqFeed.
		 * 
		 * <p>
		 * This handle is unique in this socket, but it is not unique among
		 * different sockets
		 */
		int handle = 0;

		/**
		 * This is the ascending index of the bar (from 0 to N)
		 * 
		 * <p>
		 * It is not really useful as in eSignal, because the bars come from the
		 * feed and are guaranteed in order (or this should be the case), but in
		 * any case could be useful for debug purposes.
		 */
		private int _index = 0;

		private AtomicBoolean curReqActive = new AtomicBoolean();
		/**
		 * This is the listener to the current request.
		 * <p>
		 * We have only one listener because there is only one active request.
		 */
		private IHistoryFeedListener _histListener;
		private UnparsedBar _prevCompleteBar;
		/**
		 * The status of the current request.
		 */
		private EEosStatus _lastStatus;

		public IqFeedHistRequest() {
			// empty just to avoid a warning.
		}

		/**
		 * I have only one request id, this get is ugly, it should not change
		 * state... this is something I have to change.
		 * 
		 * @param aRequest
		 * @return the id of the request.
		 */
		public void setNextReqId(EIqFeedHistReqs aRequest) {
			if (curReqActive.get()) {
				throw new IllegalStateException(
						"Cannot set next request if there is already one request active");
			}
			request = aRequest;
			++handle; // I increment the handle
			// return request + "$" + ++handle; //the handle is incremented each
			// time
		}

		public String getCurReqId() {
			return request + "$" + handle;
		}

		/**
		 * This is the method that keeps the ball rolling until the request is
		 * finished
		 * 
		 * <p>
		 * This method sends the requests and waits atomically. The request is
		 * sent and at the same time I wait.
		 * 
		 * <p>
		 * The calling thread is <b>stolen</b>. DO NOT CALL THIS METHOD UNLESS
		 * YOU ARE SURE WHAT YOU ARE DOING! Deadlock will manifest if nobody
		 * notifies this request
		 * 
		 * @param aListener
		 * @throws IOException
		 */
		public void sendRequestAndWait(String historyRequest,
				IHistoryFeedListener aListener) throws IOException {
			_histListener = aListener; // I set the listener
			_index = 0;
			_prevCompleteBar = null;
			_histSockListener.preHistoryHook();
			sendRequest(historyRequest, true);
		}

		private void sendRequest(String historyRequest, boolean blocking)
				throws IOException {

			synchronized (curReqActive) {
				if (curReqActive.compareAndSet(false, true) == false) {
					// this is a very strange situation! There is a current
					// request active
					debug_var(189393,
							"Very strange situation, current request is active!");
					throw new IllegalStateException(); // it is not safe to
														// continue.
				}

				_histSocket.writeLine(historyRequest);

				if (blocking) {
					waitForCurrentRequest();
				}
			}
		}

		public void waitForCurrentRequest() {
			/* I may recursively enter this lock, but it's not important. */
			long then = System.currentTimeMillis();
			synchronized (curReqActive) {
				while (true) {
					try {
						if (curReqActive.get() == false) {
							debug_var(391933, "The request is finished!");
							return; // OK
						} else if (System.currentTimeMillis() - then >= TIME_OUT_INTERVAL) {
							debug_var(201859,
									"Time out in request, I simulate a generic error.");
							abortCurrentRequest();
							_histListener
									.onEndOfStream(EEosStatus.GENERIC_ERROR);
							return;
						}
						curReqActive.wait(TIME_OUT_INTERVAL);
					} catch (InterruptedException e) {
						// interrupted... this may happen when we shutdown
						debug_var(671294,
								"The thread has been interrupted, I abort the request");
						abortCurrentRequest();
					}
				}
			}
		}

		// ends unconditionally the current request.
		public void abortCurrentRequest() {
			synchronized (curReqActive) {
				if (curReqActive.compareAndSet(true, false) == false) {
					debug_var(
							231347,
							"ABORT CALLED WHILE THERE IS NO REQUEST! Very strange situation, there was no active request...");
					/*
					 * This is actually a good situation because it may be due
					 * to the fact that there has been already an abort which
					 * was called! So this is the second abort of an already
					 * aborted request.
					 */
					// throw new IllegalStateException(); // not safe to
					// continue
				}

				curReqActive.notify(); // the current request is notified!
			}
		}

		/**
		 * @param reqId
		 *            the request which has finished.
		 */
		@SuppressWarnings("boxing")
		public void finishedRequest(String reqId) {
			// To check if the current request is equal to reqId
			synchronized (curReqActive) {
				if (curReqActive.compareAndSet(true, false) == false) {
					debug_var(939313,
							"Very strange situation, there was no active request...");
					// throw new IllegalStateException(); // not safe to
					// continue
					/*
					 * Actually it may happen if the current request has been
					 * aborted.
					 */
					return;
				}

				// NOT ALL REQUESTS GIVE INCOMPLETE BARS!!!!

				if (_prevCompleteBar != null) {

					if (this.request == EIqFeedHistReqs.HDX) {
						debug_var(536362,
								"Giving the last COMPLETE (HDX) bar ",
								_prevCompleteBar, " @ index ", _index);
						_histListener.onNewCompleteBar(_prevCompleteBar);
					} else {
						// debug_var(392030, "Giving the last incomplete bar ",
						// _prevCompleteBar, " @ index ", _index);

						// Probably this request is about minutes.
						long timeOfLastBar = _prevCompleteBar.start;
						long now = System.currentTimeMillis();

						// HORROR, I assume that the request is a minute request
						if (((now - timeOfLastBar) / (60 * 1000)) > 30) {
							debug_var(
									381931,
									"I assume that the last bar is complete because it is old ",
									_prevCompleteBar);
							_histListener.onNewCompleteBar(_prevCompleteBar);
						} else {
							debug_var(
									328953,
									"I assume that this bar is incomplete, because it is new ",
									_prevCompleteBar);
							_histListener.onNewIncompleteBar(_prevCompleteBar);
						}
					}

				}

				_histListener.onEndOfStream(_lastStatus); // this is the end

				curReqActive.notify(); // the current request is notified!
			}
		}

		public void acceptBar(int reqId, UnparsedBar ub) {
			if (reqId != handle) {
				throw new IllegalArgumentException();
			}
			// todo... you have to store the n-1 bar, because the last bar is
			// incomplete, but
			// you have not idea of when there is the last bar!

			if (_prevCompleteBar != null) {
				_histListener.onNewCompleteBar(_prevCompleteBar);
			}

			_prevCompleteBar = ub; // I am one bar delayed...
		}

		/**
		 * some thistorical requests do not give bars, but ticks.
		 * 
		 * @param reqId
		 * @param ut
		 */
		public void acceptTick(int reqId, UnparsedTick ut) {
			if (reqId != handle) {
				throw new IllegalArgumentException("reqId is " + reqId
						+ " I expected " + handle);
			}
			_histListener.onHistoricalTick(ut);
		}

		public void setStatus(EEosStatus aStatus) {
			_lastStatus = aStatus;
		}
	}

	IqFeedHistRequest _curRequest = new IqFeedHistRequest();

	private static final int HISTORICAL_IQFEED_PORT = 9100;

	private static final int REALTIME_IQFEED_PORT = 5009;

	private static final String PROTOCOL_USED = "5.1";

	// private static final int MAX_DATAPOINTS = 7_000_000;

	/**
	 * This is the listener to the data feed, this listener receives the real
	 * time streaming of quotes and errors.
	 * 
	 * <p>
	 * The historical data goes through the {@linkplain HistoryRequest} object.
	 * 
	 */
	IDataFeedListener _listener;

	/**
	 * Creates a connection to the iqFeed giving a concrete listener to attach.
	 * 
	 * <p>
	 * The concrete listener is then used to pass the messages to the outside.
	 * 
	 * @param aListener
	 */
	public IqFeedClient(IDataFeedListener aListener) {
		_listener = aListener;
	}

	SimpleSocketTextClient _histSocket;

	/**
	 * This connects to the real time channel of iqFeed.
	 * <p>
	 * In iqFeed the real time data are sent in another socket and they must be
	 * parsed with another listener.
	 * 
	 */
	SimpleSocketTextClient _realTimeSocket;

	HistorySocketListener _histSockListener = null;

	private RealTimeSocketListener _rtSockListener;

	@Override
	public void subscribeToSymbol(String symbol) throws DFSException {
		if (!isConnected()) {
			throw new DFSException("not connected, cannot subscribe!");
		}
		try {
			_realTimeSocket.writeLine("w" + symbol);
		} catch (IOException e) {
			throw new DFSException(e);
		}
	}

	@Override
	public void unsubscribeSymbol(String symbol) throws DFSException {
		if (!isConnected()) {
			return; // no error message, it is not useful now.
		}
		try {
			_realTimeSocket.writeLine("r" + symbol);
		} catch (IOException e) {
			throw new DFSException(e);
		}
	}

	/**
	 * @param connectionString
	 *            the connection string is used to pass some parameters to
	 *            iqConnect.exe. In our case the iqConnect is started alone, so
	 *            we do not need any connection string. The ports are fixed and
	 *            we don't change them, at least for now
	 * @throws DFSException
	 * 
	 */
	@Override
	public void start(String connectionString) throws DFSException {
		/*
		 * In this method I start the thread which is used to create the main
		 * loop. The main loop is similar to the loop in the former bridge, but
		 * this is different because it is directly linked to iqFeed.
		 */

		_histSockListener = new HistorySocketListener();
		_rtSockListener = new RealTimeSocketListener();

		_histSocket = new SimpleSocketTextClient(false);
		_histSocket.start(_histSockListener, "localhost",
				HISTORICAL_IQFEED_PORT);

		_realTimeSocket = new SimpleSocketTextClient(false);
		_realTimeSocket.start(_rtSockListener, "localhost",
				REALTIME_IQFEED_PORT);
	}

	@SuppressWarnings("static-method")
	void _sendProtocol(SimpleSocketTextClient aSstc) throws IOException {
		String setProtocol = "S,SET PROTOCOL," + PROTOCOL_USED;
		aSstc.writeLine(setProtocol);
	}

	void _sendClientName() throws IOException {
		String setClientName = "S,SET CLIENT NAME," + "client#"
				+ ((int) (Math.random() * 1000));
		_histSocket.writeLine(setClientName);
	}

	void _sendRealTimeFields() throws IOException {
		String selectFields = "S,SELECT UPDATE FIELDS,Most Recent Trade TimeMS,Most Recent Trade Size,Most Recent Trade";
		_realTimeSocket.writeLine(selectFields);
	}

	abstract class IqGenericSocketListener implements ISimpleSocketListener {
		private final ETypeOfData _type;

		public IqGenericSocketListener(ETypeOfData aType) {
			_type = aType;
		}

		@Override
		public void onLostConnection() {
			_listener.onConnectionStatusUpdate(_type,
					EConnectionStatus.DISCONNECTED);
		}

		@Override
		public void onConnectionEstabilished() {
			_listener.onConnectionStatusUpdate(_type,
					EConnectionStatus.CONNECTED);
		}

		@Override
		public void onTryingToConnect() {
			_listener.onConnectionStatusUpdate(_type,
					EConnectionStatus.CONNECTING);

		}
	}

	class RealTimeSocketListener extends IqGenericSocketListener {

		public RealTimeSocketListener() {
			super(ETypeOfData.REAL_TIME);
		}

		@Override
		public void onConnectionEstabilished() {
			super.onConnectionEstabilished();

			try {
				_sendProtocol(_realTimeSocket);
				_sendRealTimeFields();
			} catch (IOException e) {
				// If I am here probably I have another disconnection...
				onLostConnection();
			}
		}

		// private final Pattern splitsPattern = Pattern.compile(",");

		@Override
		public boolean processLine(String line) {
			// debug_var(627193, "RT <- received [", line, "]");
			// this is a sample line for the quote:
			// Q,@ES#,06:50:07.909,1,1692.50,

			// I Have to split the pattern.
			String splits[] = U.commaPattern.split(line);

			if (splits[0].equals("Q") || splits[0].equals("P")) {
				if (splits.length != 5) {
					debug_var(739103, "unbelievable line ", line,
							" returning false!");
					return false;
				}
				// Ok, this is a quote message
				String symbol = splits[1];

				Date time = null;
				try {
					time = IqDate.parseOnlyTimeMsToday(splits[2]);
				} catch (ParseException e) {
					debug_var(839293, "Cannot parse ", splits[2]);
					return false;
				}
				int volume = Integer.parseInt(splits[3]);

				String price = splits[4];

				_listener.onNewQuote(symbol, time.getTime(),
						System.currentTimeMillis(), price, volume);
			}

			// I have to give the quote.
			// _listener.onNewQuote(symbol, timestamp, quote);

			return true; // I have processed it!
		}

	}

	/**
	 * This is the listener from the history socket.
	 * <p>
	 * This class is meant to be used only from from the iqfeedclient, so it is
	 * a private inner class.
	 * 
	 * @author Sergio
	 * 
	 */
	class HistorySocketListener extends IqGenericSocketListener {

		public HistorySocketListener() {
			super(ETypeOfData.HISTORICAL);
		}

		@Override
		public void onConnectionEstabilished() {
			super.onConnectionEstabilished();

			try {
				_sendProtocol(_histSocket);
				_sendClientName();
			} catch (IOException e) {
				// If I am here probably I have another disconnection...
				onLostConnection();
			}
		}

		long lastUpdate = System.currentTimeMillis();
		private int _linesProcessed = 0;

		/**
		 * All the messages from the socket are comma separated.
		 */
		private final Pattern splitsPattern = Pattern.compile(",");

		private final Pattern reqSplitPattern = Pattern.compile("\\$");

		/**
		 * processes the line which comes from the socket, this line has the
		 * same syntax as the iqFeed document.
		 * 
		 * @return
		 */
		@SuppressWarnings("boxing")
		@Override
		public boolean processLine(String line) {

			// the second is the time stamp or the end of the message.
			try {

				String[] splits = splitsPattern.split(line, 3);

				if (splits[0].equals("S")) {
					// ok, this is a service message!
					if (splits[1].equals("CURRENT PROTOCOL")) {
						if (splits[2].equals("5.1")) {
							return true; // all ok
						}
					}
					debug_var(837198, "Cannot parse ", line);
					return false;
				}

				if (splits.length != 3) {
					// this is another VERY BAD situation, because the format is
					// fixed by iqfeed
					debug_var(391382, "Syntax error receiving line", line);
					return false;
				}

				// the first split is the request
				String requestId = splits[0];
				String timeStampOrError = splits[1];
				String allTheRestLine = splits[2];

				String reqSplits[] = reqSplitPattern.split(requestId);

				if (reqSplits.length != 2) {
					// this is rather bad, even in case of error the first thing
					// which comes from the socket
					// MUST be the request id, which is in any case is
					// parseable.
					debug_var(381993, "unreal message coming from iq ",
							requestId, " cannot parse it, giving up");
					return false;
				}

				String reqCommand = reqSplits[0];
				int reqId = Integer.parseInt(reqSplits[1]);

				// The second part could be an error from the server "E" or a
				// message from the
				// iqConnect Exe, in which case it starts with an exclamation
				// mark.
				if (splits[1].equals("E")) {
					// this is an error message, it must be handled differently
					EEosStatus res = _handleErrorMessage(requestId,
							allTheRestLine);
					_curRequest.setStatus(res);
					if (res == EEosStatus.UNKNOWN_ERROR) {
						_curRequest.abortCurrentRequest();
						return false;
					}
					return true;
				} else if (splits[1].charAt(0) == '!') {
					// this is a service message
					return _handleServiceMessage(splits[1], allTheRestLine);
				}

				EIqFeedHistReqs reqType = EIqFeedHistReqs
						.parseString(reqCommand);

				// the enumeration will allow the user to
				Date timeStamp = reqType.parseIqDate(timeStampOrError);

				// then there are the other fields.
				// but the other fields are dependent on the type of the
				// historical
				// request which has been made, so we have to make a distinction

				UnparsedBar ub = null; // this is the bar which will be parsed
										// by the line.
				UnparsedTick ut = null;
				// HDX, HDT, HWX, and HMX
				switch (reqType) {
				case HDT:
				case HDX:
				case HMX:
				case HWX:
					ub = _getUnparsedBarFromHdxHdtHwxHmxLine(allTheRestLine,
							timeStamp.getTime());
					break;

				case HID:
				case HIT:
				case HIX:
					ub = _getUnparsedBarFromHixHidHitLine(allTheRestLine,
							timeStamp.getTime());
					break;

				case HTD:
				case HTT:
				case HTX:
					ut = _getUnparsedTick(allTheRestLine, timeStamp.getTime());
					break;
				}

				// there are requests which do not send a "bar" but only a
				// trade, this should be changed.

				if (ub != null) {
					_curRequest.acceptBar(reqId, ub);
				} else {
					// if (reqType != EIqFeedHistReqs.HTD && reqType !=
					// EIqFeedHistReqs.HTT && reqType != EIqFeedHistReqs.HTX){
					// throw new IllegalStateException("unparsable bar! @ "+
					// allTheRestLine);
					// }
					_curRequest.acceptTick(reqId, ut);
				}

				long now = System.currentTimeMillis();
				++_linesProcessed;
				if (now - lastUpdate > 1000) {
					debug_var(393913, "HISTORY: [", _linesProcessed,
							"] I am alive @ ", timeStamp, " line: ",
							allTheRestLine);
					lastUpdate = now;
				}

			} catch (Throwable e) {
				// In case of exception the line means that it has not being
				// recognized.
				e.printStackTrace();
				// I artificially end the request
				_curRequest.abortCurrentRequest();
				return false;
			}

			return true; // All OK.

		}

		/**
		 * simple method called when we have a new request that comes from the
		 * outside.
		 */
		public void preHistoryHook() {
			_linesProcessed = 0;
			lastUpdate = System.currentTimeMillis();
			_curRequest.setStatus(EEosStatus.ALL_OK);
		}

		private UnparsedTick _getUnparsedTick(String line, long time) {
			// sample line [1661.50,1,11405,1661.50,1661.75,286789,C,43,01,]
			String splits[] = splitsPattern.split(line, 3);
			UnparsedTick ut = new UnparsedTick(time, splits[0], splits[1]);
			return ut;
		}

		private UnparsedBar _getUnparsedBarFromHdxHdtHwxHmxLine(String line,
				long start) {
			String splits[] = splitsPattern.split(line);
			if (splits.length != 6) {
				throw new IllegalArgumentException();
			}

			int volume = Integer.parseInt(splits[4]);
			// splits[5] is the open interest which for us is not needed.
			UnparsedBar ub = new UnparsedBar(start, splits[2], splits[0],
					splits[1], splits[3], volume);
			return ub;
		}

		/**
		 * returns an unparsed line from hid line. This unparsed line is then
		 * passed to the current request (which will pass it to the
		 * {@linkplain IHistoryFeedListener}
		 * 
		 * @param line
		 *            (should be something like:
		 *            1583.00,1583.00,1583.00,1583.00,9631,1,)
		 * @param start
		 * @return
		 */
		private UnparsedBar _getUnparsedBarFromHixHidHitLine(String line,
				long start) {
			String splits[] = splitsPattern.split(line);
			if (splits.length != 7) {
				throw new IllegalArgumentException("cannot parse" + line);
			}

			int volume = Integer.parseInt(splits[5]);
			// splits[4] is the period volume which for us is not needed.
			UnparsedBar ub = new UnparsedBar(start, splits[2], splits[0],
					splits[1], splits[3], volume);
			return ub;
		}

	}

	// @Override
	// public BigDecimal getTickSizeForSymbol(String symbol) throws DFSException
	// {
	// throw new DFSException("not supported for now.");
	// }

	/**
	 * @param allTheRestLine
	 */
	public boolean _handleServiceMessage(String string, String allTheRestLine) {
		if (string.equals("!ENDMSG!")) {
			debug_var(839193, "The message history has finished");
			_curRequest.finishedRequest(string);
			return true;
		}
		return false;
	}

	/**
	 * @param allTheRestLine
	 *            the remaining line after the "E" character (tbd)
	 */
	@SuppressWarnings("static-method")
	public EEosStatus _handleErrorMessage(String reqId, String allTheRestLine) {
		if (allTheRestLine.compareTo("Invalid symbol.,") == 0) {
			debug_var(381839,
					"The client says: invalid symbol, I fake the ending of the request.");

			return EEosStatus.INVALID_SYMBOL;
		} else if (allTheRestLine.compareTo("!NO_DATA!,,") == 0) {
			debug_var(
					390193,
					"There is no data, I wait for the end of message from the server for reqid ",
					reqId);
			return EEosStatus.NO_DATA;
		} else if (allTheRestLine.compareTo("Invalid symbol format.,") == 0) {
			debug_var(390193,
					"The symbol is invalid, I fake the ending of the request",
					reqId);
			return EEosStatus.INVALID_SYMBOL;
		} else if (allTheRestLine
				.compareTo("Could not connect to History socket.,") == 0) {
			debug_var(390193, "unable to connect, maybe a net problem", reqId);
			return EEosStatus.NOT_CONNECTED;
		} else if (allTheRestLine.compareTo("Invalid start date.,") == 0) {
			debug_var(
					183941,
					"Invalid start date, probably this is a bug in the application ",
					reqId);
			return EEosStatus.INVALID_DATE;
		} else if (allTheRestLine.startsWith("Unknown Server Error code")) {
			debug_var(718401,
					"This is a generic, unspecified, error, I pass it to the client.");
			return EEosStatus.GENERIC_ERROR;
		} else if (allTheRestLine.startsWith("Socket Error:")) {
			debug_var(390193, "Socket error ", allTheRestLine,
					" received, I quit ", reqId);
			return EEosStatus.NOT_CONNECTED;
		}

		debug_var(391039, "Unable to process [", allTheRestLine,
				"]... aborting");
		return EEosStatus.UNKNOWN_ERROR;
	}

	/**
	 * this is the normal entry point to the request history method.
	 * <p>
	 * In this data feed all the requests are serialized, so the blocking
	 * parameter <b>must</b> be always true.
	 * 
	 * <p>
	 * This because the {@linkplain IqFeedClient} class is able only to do
	 * serialized requests.
	 * 
	 * @throws IOException
	 * 
	 * @throws UnsupportedOperationException
	 *             if blocking is false.
	 */
	@Override
	public void requestHistory(HistoryRequest aRequest) throws DFSException {

		if (!_histSocket.isConnected()) {
			throw new DfsNoConnectionException("Historical data not available");
		}

		// if (!blocking) {
		// throw new UnsupportedOperationException();
		// // iqFeed is only able to do blocking requests,
		// }

		String reqSocket = "#EIE"; // just to test, this will be changed later.
		// String reqId;

		// I get the listener from the history request
		// (this is just a temporary mock up used to get the ball rolling).
		IHistoryFeedListener aListener = aRequest.getListener();

		if (aRequest instanceof AllAvailableHistoricalData) {
			AllAvailableHistoricalData ahd = (AllAvailableHistoricalData) aRequest;

			switch (ahd.getType()) {
			case DAILY:
				// reqId = _curRequest.getRequestId(EIqFeedHistReqs.HDX);
				_curRequest.setNextReqId(EIqFeedHistReqs.HDX);
				// HDX,[Symbol],[MaxDatapoints],[DataDirection],[RequestID],[DatapointsPerSend]<CR><LF>
				reqSocket = EIqFeedHistReqs.HDX + "," + ahd._symbol
						+ ",5000,1," + _curRequest.getCurReqId();
				break;
			case MINUTE:
				_curRequest.setNextReqId(EIqFeedHistReqs.HID);
				// the request to get the historical minute data is this (I get
				// 5000 days)
				reqSocket = EIqFeedHistReqs.HID + "," + ahd._symbol
						+ ",60,5000,,,,1," + _curRequest.getCurReqId();
				break;
			case RANGE:
				_curRequest.setNextReqId(EIqFeedHistReqs.HTD);
				// the request to get the historical minute data is this (I get
				// 5000 days)
				reqSocket = EIqFeedHistReqs.HTD + "," + ahd._symbol
						+ ",5000,,,,1," + _curRequest.getCurReqId();
				break;
			}
		} else if (aRequest instanceof PartialHistoryRequest) {
			// Ok, this is a partial request, so I simply have to
			PartialHistoryRequest phr = (PartialHistoryRequest) aRequest;
			switch (phr.getType()) {
			case DAILY:
				_curRequest.setNextReqId(EIqFeedHistReqs.HDT); // the daily
																// request is
																// the same
				// reqSocket = EIqFeedHistReqs.HDX + "," + phr._symbol + "," +
				// phr.getNumUnits() + ",1," + _curRequest.getCurReqId();
				// HDT,@ESU13,20130722,,,1,PUWEFWI
				reqSocket = EIqFeedHistReqs.HDT
						+ ","
						+ phr._symbol
						+ ","
						+ IqDate.formatToHistoryDate(new Date(phr
								.getBeginDate())) + ",,,1,"
						+ _curRequest.getCurReqId();
				break;
			case MINUTE:
				_curRequest.setNextReqId(EIqFeedHistReqs.HIT); // I ask a
																// certain
																// number of
																// minute bar
				// reqSocket = EIqFeedHistReqs.HIX + "," + phr._symbol + ",60,"
				// + phr.getNumUnits() + ",1," + _curRequest.getCurReqId();
				// HIT,@ESU13,60,20130726 090000,,,,,1,FWPEOWI

				reqSocket = EIqFeedHistReqs.HIT + "," + phr._symbol + ",60,"
						+ IqDate.formatToHistory(new Date(phr.getBeginDate()))
						+ ",,,,,1," + _curRequest.getCurReqId();
				break;
			case RANGE:
				_curRequest.setNextReqId(EIqFeedHistReqs.HTT);
				// the request to get the historical minute data is this (I get
				// 5000 days)
				// reqSocket = EIqFeedHistReqs.HTD + "," + phr._symbol + "," +
				// phr.getNumUnits() + ",,,,1," + _curRequest.getCurReqId();

				// HTT,@ESH14,20130725 180000,20130726 070000,,,,1,34234
				reqSocket = EIqFeedHistReqs.HTT + "," + phr._symbol + ","
						+ IqDate.formatToHistory(new Date(phr.getBeginDate()))
						+ ",,,,,1," + _curRequest.getCurReqId();
				break;
			}

		} else {
			_curRequest.setNextReqId(EIqFeedHistReqs.HTX);
			reqSocket = EIqFeedHistReqs.HTX + "," + aRequest._symbol
					+ ",3000,1," + _curRequest.getCurReqId() + ",2";
		}

		// Ok, now I should build the historical request, decide which is
		// suitable between the various types
		// of historical requests. When the real type of request has been found
		// than
		// we put in the map the history and the prefix.

		/*
		 * This will synchronize all the requests, because all the requests have
		 * to pass from this method.
		 */
		synchronized (_curRequest) {
			debug_var(882392, "Sending request ", reqSocket, " to the socket");
			try {
				_curRequest.sendRequestAndWait(reqSocket, aListener);
			} catch (IOException e) {
				throw new DFSException(e);
			}
		}

	}

	@Override
	public boolean isConnected() {
		return this._histSocket.isConnected() && _realTimeSocket.isConnected();
	}

	/**
	 * waits for the current request to finish.
	 * 
	 * <p>
	 * Probably this method is not useful for the outside and should be made
	 * private, but I really don't know now.
	 * 
	 * <p>
	 * If there is not a current request probably this method will not work, but
	 * it should also not do any harm, because the boolean of the finished
	 * request should be set.
	 */
	public void waitForCurrentRequest() {
		_curRequest.waitForCurrentRequest();
	}

	@Override
	public void stop() {
		debug_var(398291, "Stopping the historical socket");
		_histSocket.stop();
		_histSockListener = null;
		debug_var(839183, "Stopping the real time socket");
		_realTimeSocket.stop();
		_rtSockListener = null;
	}

}
