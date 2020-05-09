package com.mfg.utils.socket;

import static com.mfg.utils.Utils.debug_var;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.mfg.common.DFSException;
import com.mfg.utils.U;

/**
 * The base class for all the listeners to the socket.
 * <P>
 * It helps them also to handle the requests.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public abstract class BaseSocketHelper implements ISimpleSocketListener {

	private void _abortPendingRequests(String err) {
		for (SimpleRemoteCommand req : _requests.values()) {
			req.abort(err);
		}

	}

	/**
	 * The map of push sinks, every connection has its own push sinks which are
	 * all instances of classes derived from {@link SimpleTextPushSink}
	 * 
	 * <p>
	 * key = push id, value = push sink associated to this message.
	 */
	private HashMap<String, SimpleTextPushSink> _pushSinks = new HashMap<>();

	/**
	 * This map stores all the requests...
	 */
	private HashMap<Integer, SimpleRemoteCommand> _requests = new HashMap<>();

	private final String GREETING;
	private SimpleSocketTextClient _simpleClient;

	protected BaseSocketHelper(String aGreetingString,
			SimpleSocketTextClient aClient) {
		GREETING = aGreetingString;
		_simpleClient = aClient;
	}

	/**
	 * parses a string from the server.
	 * 
	 * <p>
	 * The string can be a multi-line string, as the underlying socket uses the
	 * convention of ending the strings with a percentage sign. The percentage
	 * sign is not given here, the string has been already "massaged" from the
	 * class {@linkplain SimpleSocketTextClient}
	 * 
	 * @return true if the string has been successfully parsed.
	 */
	@Override
	public final boolean processLine(String line) {
		try {
			if (line == null) {
				return true;
			}

			if (line.startsWith(GREETING)) {
				// this is the greeting message, pass on.
				debug_var(738281, "This is the greeting message... ", line);
				return true;
			}

			// debug_var(738291, "Proxy receives <-- ", line);

			String splits[] = U.commaPattern.split(line, 2);
			if (splits.length < 2) {
				// this is a serious error, the server is not good
				debug_var(399133, "invalid string received from server ", line);
				_simpleClient.writeLine("invalid string received " + line
						+ " forcing close.");
				// _dfsSocket.stop();
				return false;
			}

			String start = splits[0];
			String left = splits[1];

			/*
			 * If line starts with a "a" then it is an answer to a request, if
			 * it starts with a "p" it is a pushed event, if it is a "s" it is a
			 * service message. Otherwise it is an error
			 */
			if (start.compareTo("a") == 0) {

				// Ok, I have to split the left part
				String leftSplits[] = U.commaPattern.split(left, 2);

				_handleAnswerToRequest(leftSplits[0], leftSplits[1]);

			} else if (splits[0].compareTo("p") == 0) {

				// Ok, I have to split the left part
				String leftSplits[] = U.commaPattern.split(left, 2);

				// the key to the push message is the second parameter
				_handlePushMessage(leftSplits[0], leftSplits[1]);

			} else if (splits[0].compareTo("err") == 0) {
				debug_var(839293, "Error from socket, ", line);
				_abortPendingRequests(line); // maybe this is too much...
												// but for now it is OK
				return true;
			} else if (splits[0].compareTo("s") == 0) {
				// this is a service message

				// debug_var(939313, "Service message ", line);

				String statusSplits[] = U.commaPattern.split(left);
				if (statusSplits.length < 2) {
					debug_var(391934, "Unknown status line ", left);
					return false;
				}

				_handleStatusLine(statusSplits);

			} else if (splits[0].compareTo("fat") == 0) {
				/*
				 * A fatal message will end the client, but is not an error from
				 * the server, maybe it is just that the server is shutting
				 * down.
				 */
				debug_var(399133, "FATAL received, I quit ", line);
				return false;
			} else {
				debug_var(399133, "invalid command received ", splits[0]);
				_simpleClient.writeLine("invalid command received " + splits[0]
						+ " forcing close.");
				// _dfsSocket.stop();
				return false;
			}

		} catch (IOException e) {
			// maybe the socket is closed
			debug_var(391033,
					"Caught the socket exception, now I simply go away");
			return false;
		} catch (Exception e) {
			// this is a bad exception, probably a format detail...
			debug_var(938933,
					"Not good! ending of the socket thread, aborting the loop");
			e.printStackTrace();
			return false;
		}
		// }

		return true; // all went well
	}

	/**
	 * Handles a status line, one which starts with "s", this line usually had
	 * only 2 strings, but I have added the possibility to have remote
	 * notifications, so this has become a general purpose
	 * <i>"status & notification"</i> method.
	 * 
	 * <p>
	 * It is guaranteed that the array has at least length 2.
	 * 
	 * @param statuses
	 *            the splits of the status line. It is an unbound array,
	 *            determined only by the number of ',' characters in the input
	 *            string.
	 */
	protected abstract void _handleStatusLine(String[] statuses);

	/**
	 * a method that sends the request through the socket to the other part of
	 * the network where listening there is a {@linkplain DfsStub}. This is in
	 * some way a common pair of objects which are in synchronous communication
	 * (apart from the notifications in real time of quotes and new bar).
	 * 
	 * <p>
	 * This method is package protected because it may be called also by a
	 * {@link DFSProxyDataSource} to control the virtual symbol.
	 * 
	 * @param aRequest
	 * @throws IOException
	 * @throws DFSException
	 */
	@SuppressWarnings("boxing")
	public void _sendRequest(SimpleRemoteCommand aRequest) throws IOException {
		synchronized (_requests) {
			_removeEndRequests();
			_requests.put(aRequest._handle, aRequest);
			_simpleClient.writeLine(aRequest.serialize());
		}

	}

	/**
	 * removes the requests which have ended.
	 * <p>
	 * The requests are created only in one thread, but they are removed here,
	 * just before a new request is done.
	 */
	private void _removeEndRequests() {
		ArrayList<Integer> _toRemove = new ArrayList<>();
		for (Entry<Integer, SimpleRemoteCommand> entry : _requests.entrySet()) {
			SimpleRemoteCommand req = entry.getValue();
			if (req.isEnded()) {
				_toRemove.add(entry.getKey());
			}
		}
		synchronized (_requests) {
			for (Integer i : _toRemove) {
				_requests.remove(i);
			}
		}

	}

	private void _handleAnswerToRequest(String handleS, String payload)
			throws Exception {
		int handle = Integer.parseInt(handleS);
		@SuppressWarnings("boxing")
		SimpleRemoteCommand rq = _requests.get(handle);
		if (rq == null) {
			// this is strange... very strange
			throw new Exception("Unknown request " + handle
					+ " answer received");
		}

		/*
		 * If I am here I should be able to parse the answer, because the answer
		 * has already the correct handle.
		 */
		rq.parseAnswer(payload);

	}

	private void _handlePushMessage(String sinkKey, String payload)
			throws IOException {
		/*
		 * I have to synchronize on the push sinks because suppose I am
		 * subscribing to a symbol... I get the answer and immediately a push
		 * quote... but I cannot handle the push quote until the push sink is
		 * initialized on this side.
		 */
		synchronized (_pushSinks) {
			// take the push sink
			SimpleTextPushSink ps = _pushSinks.get(sinkKey);
			if (ps == null) {
				debug_var(391039, "Cannot find a push sink with key ", sinkKey,
						" I abort it");
				DeletePushCommand dpr = new DeletePushCommand(sinkKey);
				_sendRequest(dpr);
				return;
			}

			// tell the push sink the new message.
			ps.handlePush(payload);
		}
	}

	/**
	 * Adds a subscription sinks to the proxy.
	 * <p>
	 * The sink will be active until it is not removed by the system or by
	 * another command.
	 * 
	 * @param aPushKey
	 *            a unique string which identify this push sink. *
	 * @param ss
	 *            the sink to be added.
	 */
	public void addSink(SimpleTextPushSink ss) {
		String aPushKey = ss._pushId;
		debug_var(381934, "Added the sink ", ss, " with the key ", aPushKey);
		synchronized (_pushSinks) {
			_pushSinks.put(aPushKey, ss);
		}
	}

	/**
	 * simply removes the push id from the sinks.
	 * <p>
	 * This for now is automatic only for the sinks which correspond to closed
	 * histories.
	 * 
	 * @param _hs
	 * @throws IOException
	 * @throws DFSException
	 */
	public void removeSink(SimpleTextPushSink _hs) throws IOException {
		synchronized (_pushSinks) {
			_pushSinks.remove(_hs._pushId);
			DeletePushCommand dpr = new DeletePushCommand(_hs._pushId);
			_sendRequest(dpr);
		}
	}

	public SimpleTextPushSink removeSink(String pushKey) {
		synchronized (_pushSinks) {
			return _pushSinks.remove(pushKey);
		}
	}

}
