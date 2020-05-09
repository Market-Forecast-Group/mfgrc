package com.mfg.utils.socket;

import static com.mfg.utils.Utils.debug_var;

import java.io.IOException;
import java.net.SocketException;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

import com.mfg.common.DFSException;

/**
 * This is a stub view of the server (from server's space, of course).
 * 
 * <p>
 * The stub is also able to start an independent thread which is used for the
 * push sources.
 * 
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public abstract class SimpleTextServerStub {

	protected SimpleTextServerStub(ICommandFactory aFactory,
			SingleClientHandler aCH) {
		_factory = aFactory;
		_cs = aCH;
	}

	/**
	 * The stub has the possibility to create the remote commands from a line of
	 * text.
	 */
	private final ICommandFactory _factory;

	protected final transient SingleClientHandler _cs;

	/**
	 * This is the list of all the push sources in the stub. Maybe the push
	 * sources could be active, also the sources which are tied to the cache. I
	 * have to think more about it.
	 * 
	 * 
	 * <p>
	 * Note: probably this array should be moved to the DFS stub class because
	 * only there it is used, the other push sources are not used in that way,
	 * like the history source, but are simply asynchronous sources which are
	 * not commanded by the doPush method.
	 */
	protected CopyOnWriteArrayList<IPushSource> _pushSources = new CopyOnWriteArrayList<>();

	/**
	 * This is the thread which will give the push sources a pool mechanism to
	 * push to the corresponding proxy a message.
	 */
	protected transient Thread _pushThread;

	/**
	 * Deletes the push source identified by the key, return false if the key
	 * was not found.
	 * 
	 * <p>
	 * the push source is a history source, because the subscription sources are
	 * put inside the ProxyDfsListener _subSources map.
	 * 
	 * @param pushKey
	 * @return true if the push source with that key has been found.
	 */
	public final boolean _deletePushSource(String pushKey) {
		// int i = -1;
		boolean found = false;
		for (IPushSource ps : _pushSources) {
			// i++;
			if (ps.getPushKey().equals(pushKey)) {
				ps.interruptRequest();
				found = true;
				break;
			}
		}

		/*
		 * Even if I have found it I do not have to remove it because it will
		 * remove itself by exiting from the doPush method.
		 */
		if (found) {
			// _pushSources.remove(i);
			postDeletePushSourceHook();
			return true;
		}
		return false;
	}

	/**
	 * this returns only a copy of the requests list.
	 * 
	 * @return
	 */
	public Collection<IPushSource> getActiveRequests() {
		return Collections.unmodifiableList(_pushSources);
	}

	/**
	 * parses a single line from a client. The behavior is of course server
	 * specific.
	 * 
	 * <p>
	 * The line has already being merged from the socket. No more post
	 * processing is needed, the new lines are chopped.
	 * 
	 * @param line
	 *            the line from the client
	 * @throws IOException
	 */
	public final void parseLine(String line) throws IOException {

		if (_preParseHook(line)) {
			return;
		}

		// First of all I take the command which is requested.
		SimpleRemoteCommand remoteCommand = _factory.createCommand(line);

		remoteCommand.perform(this);

		_cs.printLine(remoteCommand.serializeAnswer());

		remoteCommand.afterSentHook();
	}

	/**
	 * Called before the actual line is parsed by the generic stub.
	 * <p>
	 * The real stub can bypass the generic parsing and if returns false the
	 * line has already been parsed and the stub returns.
	 * 
	 * @param line
	 * @return
	 * @throws DFSException
	 */
	@SuppressWarnings("static-method")
	protected boolean _preParseHook(String line) {
		return false;
	}

	protected void postDeletePushSourceHook() {
		// nothing
	}

	protected void postStopHook() {
		// nothing here
	}

	/**
	 * method called before the actual start is done. The stub can do any house
	 * keeping, the push thread is not yet started.
	 */
	protected void preStartHook() {
		// nothing here
	}

	/**
	 * Called just before the push thread dies.
	 * 
	 * <p>
	 * It can overridden to add behavior.
	 */
	protected void preStopHook() {
		// nothing here
	}

	public void stop() {
		debug_var(839193, "I am about to stop the push thread");

		preStopHook();

		if (_pushThread != null) {
			_pushThread.interrupt();
			try {
				_pushThread.join();
			} catch (InterruptedException e) {
				// very bad situation
				assert (false) : "Why?";
			}

			synchronized (_pushSources) {
				/*
				 * Help recollect garbage. When a client exits the stub remains
				 * active until the socket client is recollected. Clearing the
				 * push sources helps the GC to collect the CachePushSources
				 * which have been instantiated by this push.
				 */
				_pushSources.clear();
			}
		}

		postStopHook();

		debug_var(381934, "push thread destroyed, now I exit");
	}

	public void printLine(String sent) throws SocketException {
		_cs.printLine(sent);
	}

	public void setModified() {
		_cs.setModified();
	}

	public void start() {
		// here it is void.
	}
}
