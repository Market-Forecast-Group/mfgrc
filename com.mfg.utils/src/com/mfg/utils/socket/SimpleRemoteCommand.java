package com.mfg.utils.socket;

import static com.mfg.utils.Utils.debug_var;

import java.lang.reflect.Constructor;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.mfg.common.DFSException;
import com.mfg.utils.U;

/**
 * In our architecture a {@linkplain SingleClientHandler} is able to process
 * commands, which are parsed from a text stream.
 * 
 * <p>
 * Every command has an answer. This is then serialized back to the client.
 * 
 * <p>
 * A command, as the result of its performing, could create a
 * {@linkplain SimpleTextPushSource} which will handle subsequent, pseudo
 * asynchronous, communication to the client.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public abstract class SimpleRemoteCommand {

	/**
	 * gets the splitted params in a safe way.
	 * 
	 * @param numRequested
	 * @return
	 * @throws DFSException
	 */
	protected static String[] _getSplittedParamsSafe(String unsplitted,
			int numRequested) throws SocketException {
		String pars[] = U.commaPattern.split(unsplitted);
		if (pars.length != numRequested) {
			throw new SocketException("Wrong number parameters for "
					+ unsplitted + " got " + pars.length + " requested "
					+ numRequested);
		}
		return pars;
	}

	/**
	 * This is the real result which comes from the server. Zero is OK, any
	 * other value means that answer will contain an exception to be thrown
	 */
	protected int _ansCode;

	/**
	 * This is the parsed answer for the command which is used to get the answer
	 * to the outside (it may also be a complex object, like the cache)
	 */
	protected Object _answer;

	/**
	 * The answer which is given when the command is void.
	 */
	protected static final String VOID = "void";

	protected AtomicBoolean _ended = new AtomicBoolean(false);

	/**
	 * This is the handle of the request. It is used to serialize the request to
	 * the socket.
	 */
	public final int _handle;

	static private AtomicInteger _nextHandle = new AtomicInteger();

	/**
	 * Just a constant which is used to distinguish a parsing error from a
	 * server error.
	 */
	private static final int SERVER_ERROR = -999;

	/**
	 * The string representation of the command's parameters, without the
	 * command string
	 */
	protected String _unparsedParams;

	protected final String _command;

	protected SimpleRemoteCommand(int handle, String aCommandValue,
			String unparsed_params) {
		_handle = handle;
		_command = aCommandValue;
		_unparsedParams = unparsed_params;
	}

	protected SimpleRemoteCommand(String aCommand) {
		_handle = _nextHandle.incrementAndGet();
		_command = aCommand;
	}

	/**
	 * This is the actual method which does the work.
	 * 
	 * <p>
	 * It may throw an exception which is caught by the
	 * {@link #perform(DfsStub)} method. The exception is then serialized to the
	 * socket because that exception is then thrown again in the proxy process.
	 * 
	 * @param aStub
	 *            the stub object which can fulfill the request.
	 * 
	 * @throws Exception
	 *             the internal perform may fail with an exception. That
	 *             exception is serialized back to the client.
	 */
	protected abstract void _internalPerform(SimpleTextServerStub aStub)
			throws Exception;

	/**
	 * serializes the answer for the socket.
	 * <p>
	 * The default implementation is to call the {@link #toString()} method, but
	 * derived classes are free to implement it differently.
	 * <p>
	 * The serialized version of the answer is then returned back from the
	 * method {@link #parseAnswerImpl(String)}.
	 * 
	 * @return the string representation of the answer
	 */
	protected String _serializeAnswerHook() {
		return _answer.toString();
	}

	/**
	 * aborts the current command. The command is aborted in the proxy space in
	 * response to a server error
	 * 
	 * @param err
	 *            the abort message.
	 */
	public final void abort(String err) {
		_ansCode = -1;
		_answer = new DFSException("Aborted with message " + err);
		_ended.set(true);
		synchronized (_ended) {
			_ended.notify();
		}
	}

	/**
	 * This is just an hook to let the command do any house-keeping after it has
	 * been serialized to the socket.
	 * 
	 * <p>
	 * This is for now really useful only for the
	 * {@linkplain RequestHistoryCommand} because it needs a creates a push
	 * cache which is then used to sent the bars to the outside.
	 */
	public void afterSentHook() {
		// void here.
	}

	/**
	 * returns the answer to this command. The answer is unformatted, it is
	 * simply an object, subclasses are free to handle this method differently.
	 * 
	 * <p>
	 * The method is not final because in this way subclasses may specialize the
	 * exception.
	 * 
	 * @return the answer. This method is useful only after the object has been
	 *         joined.
	 * @throws Exception
	 *             the generic exception (maybe a remote exception).
	 */
	public Object getAnswer() throws Exception {
		join();
		return _answer;
	}

	public int getReturnCode() throws Exception {
		if (!isEnded())
			join();
		return _ansCode;
	}

	/**
	 * Simple query method to know if a request has ended.
	 * 
	 * @return true if ended, false otherwise.
	 */
	public final boolean isEnded() {
		return _ended.get();
	}

	/**
	 * This method will block the calling thread until the request has been
	 * finished.
	 * <p>
	 * It may throw an exception if the answer is not valid, the socket is
	 * closed or something like that.
	 * <p>
	 * All the synchronous requests have this method in common.
	 * <p>
	 * This method can also throw an exception, if the request is interrupted or
	 * if the result could not be retrieved for some reasons.
	 * 
	 * <p>
	 * The method has a fixed timeout of one minute.
	 * 
	 * <p>
	 * The method is not final because in this way subclasses may specialize the
	 * exception.
	 * 
	 * @throws Exception
	 */
	public void join() throws Exception {
		int cycle = 0;
		try {
			while (!_ended.get()) {
				synchronized (_ended) {
					_ended.wait(1000);
					// U.debug_var(382951, "Waiting in command ", this,
					// " cycle ",
					// cycle);
					if (++cycle > 60) {
						throw new SocketException("timeout in command "
								+ this._handle + " class " + this);
					}
				}
			}
		} catch (InterruptedException e) {
			throw new SocketException("Interrupted while joining the command "
					+ this._handle);
		}

		if (_ansCode != 0) {
			if (_answer == null || !(_answer instanceof Exception)) {
				// this is really bad
				throw new SocketException(
						"anomaly in the remote command, ans code is "
								+ _ansCode + " and answer is " + _answer);
			}
			/*
			 * the exception may be something more specific, like a TEAException
			 * or a DFSException, but here we are in a generic package and these
			 * classes may be used in other projects so I choose to throw a
			 * generic exception instead.
			 * 
			 * Subclasses may of course specialize this exception.
			 */
			throw (Exception) _answer;
		}
	}

	/**
	 * This method parses the answer from the socket. If the answer is an error,
	 * or if the answer itself is not parsable... then it makes no sense to
	 * throw an exception here, because we are not the thread of control.
	 * 
	 * <p>
	 * The result is instead placed inside the result integer and the waiting
	 * thread, if any, will be notified.
	 * 
	 * <p>
	 * This method will not throw any exception, guaranteed.
	 * 
	 * @param splits
	 * 
	 */
	public final void parseAnswer(String payload) {
		// Ok, we may now have the possibility to check the different types of
		// answers, because I may have different types of them.
		try {

			String reqs[] = U.commaPattern.split(payload, 2);
			if (reqs.length < 2) {
				_ansCode = SERVER_ERROR;
				_answer = new DFSException("Cannot parse the answer " + payload);
			}
			_ansCode = Integer.parseInt(reqs[0]);
			// Only if the answer is OK, it makes sense to get the result.
			if (_ansCode == 0) {
				parseAnswerImpl(reqs[1]);
			} else {
				/*
				 * the answer code is not zero, so I simply have to parse the
				 * exception from the line, the payload is
				 */
				String splits[] = U.commaPattern.split(reqs[1]);
				if (splits.length != 2) {
					// this is really bad, because I do not have an exception to
					// parse
					_answer = new DFSException("");
				}

				String exceptionName = splits[0];
				String remoteMessage = splits[1];

				@SuppressWarnings("unchecked")
				Class<Exception> clazz = (Class<Exception>) Class
						.forName(exceptionName);
				Constructor<Exception> constructor = clazz
						.getConstructor(java.lang.String.class);
				_answer = constructor.newInstance(remoteMessage);

			}

		} catch (Exception e) {
			debug_var(399133, "Caught exception ", e,
					" I will simply end the thread which waits the answer, with a negative result");
			_ansCode = -1;
			_answer = new DFSException(e); // wrap whatever is thrown.
		} finally {
			_ended.set(true);
			synchronized (_ended) {
				_ended.notify();
			}
		}
	}

	/**
	 * This method will parse the answer based on the string which comes from
	 * the socket.
	 * <p>
	 * The "string" could actually be a multiline string, based on the case.
	 * 
	 * @param splits
	 * @return
	 * @throws DFSException
	 */
	protected abstract int parseAnswerImpl(String payload) throws DFSException;

	/**
	 * This method simply performs the command on the real object:
	 * 
	 * <p>
	 * it must not throw any exceptions..., every checked or unchecked exception
	 * must be serialized to the socket and recreated in the proxy space.
	 * 
	 * @param _dfs
	 * 
	 */
	public final void perform(SimpleTextServerStub aStub) {
		try {
			_answer = VOID; // the answer is void for now
			_internalPerform(aStub);
			_ansCode = 0; // anything is OK, the perform did return an answer
		} catch (Throwable e) {
			// I serialize the exception, the substring is to take away the
			// "class " prefix (with the space)
			e.printStackTrace();
			_ansCode = -1;
			_answer = U
					.join(e.getClass().toString().substring(6), e.toString());
		}

	}

	/**
	 * The serialized version of a remote command is
	 * 
	 * <pre>
	 * &quot;r,handle,$payload&quot;
	 * </pre>
	 * 
	 * where $payload is the serialized version of the request (each one has its
	 * own serialized version)
	 * 
	 * @return
	 */
	@SuppressWarnings("boxing")
	public final String serialize() {
		return U.join("r", _handle, _command, _unparsedParams);
	}

	/**
	 * the serialized version of the answer is
	 * <p>
	 * 
	 * <pre>
	 * a,$handle,$ansCode,$serialized_answer
	 * </pre>
	 * 
	 * @return
	 */
	@SuppressWarnings("boxing")
	public final String serializeAnswer() {
		return U.join("a", _handle, _ansCode, _serializeAnswerHook());
	}
}
