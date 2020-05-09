package com.mfg.utils.socket;

import static com.mfg.utils.Utils.debug_var;
import static com.mfg.utils.Utils.join_thread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import com.mfg.common.DFSException;

/**
 * This is complementary class of the {@linkplain SimpleSocketTextClient}, which
 * is used to accept a connection to a socket port which is opened in this class
 * from the server side.
 * 
 * <p>
 * This class models either passive and not passive servers, passive when all
 * the communication is done from client to server (with the server responding
 * to each request), active means that the server is also able to push strings
 * towards the client without
 * 
 * 
 * @author Pasqualino
 * @since 2013-11-27
 */
public abstract class SimpleSocketTextServer {

	/**
	 * The factory used to create all the handlers.
	 */
	private SingleClientHandlerFactory _handlersFactory;

	private transient Thread _ssThread = null;

	/**
	 * This object is used to send the notifications of this object to the
	 * outside (usually a GUI), it is not really required, but it simplifies the
	 * GUI creation
	 */
	protected final Observable _externalObservable;

	// the clients are copy on write because the GUI may access the list on
	// another thread.
	CopyOnWriteArrayList<ISimpleClient> _clients = new CopyOnWriteArrayList<>();

	private transient ServerSocket _serverSocket;

	private transient AtomicBoolean _endRequested = new AtomicBoolean(false);

	protected SimpleSocketTextServer(int port,
			SingleClientHandlerFactory aFactory) throws IOException,
			DFSException {
		_handlersFactory = aFactory;
		_handlersFactory.setServer(this);

		_externalObservable = createExternalObservable();

		try {
			_serverSocket = new ServerSocket(port);
			_serverSocket.setSoTimeout(3000);
		} catch (SocketException e) {
			throw new DFSException(e);
		}
	}

	protected abstract SimpleTextClientsModel createExternalObservable();

	public List<ISimpleClient> getClients() {
		return java.util.Collections.unmodifiableList(_clients);
	}

	public void killClient(String remoteIp) {
		for (ISimpleClient client : _clients) {
			if (client.getRemoteIp().equals(remoteIp)) {
				((SingleClientHandler) client).stop();
			}
		}
	}

	protected void postStopHook() {
		// nothing
	}

	/**
	 * called before the accepting thread is going to be started. The client
	 * here can do some housekeeping.
	 * 
	 * @throws DFSException
	 * @throws Exception
	 */
	protected void preStartAcceptingThreadHook() throws Exception {
		// nothing to do here, override to add behavior
	}

	public final void setModified() {
		((SimpleTextClientsModel) _externalObservable).setChanged();
		_externalObservable.notifyObservers();
	}

	/**
	 * This is the never ending thread of the socket server. *
	 * <p>
	 * It creates the client and mantains updated the clients state.
	 * 
	 * <p>
	 * This is the background thread that waits for connections and, when a
	 * client connects, it builds a {@linkplain SingleClientHandler} able to
	 * process it.
	 */
	void ss_thread() {

		_clients.clear(); // I have to clear the clients.

		boolean newClient = false;
		while (!_endRequested.get()) {
			try {

				ArrayList<SingleClientHandler> toRemove = new ArrayList<>();
				Iterator<ISimpleClient> it = _clients.iterator();
				while (it.hasNext()) {
					SingleClientHandler acs = (SingleClientHandler) it.next();
					if (acs.isZombie()) {
						debug_var(389335, "Collect zombie client finished...",
								acs);
						toRemove.add(acs);
					}
				}

				for (SingleClientHandler cs1 : toRemove) {
					_clients.remove(cs1);
				}
				if (toRemove.size() != 0 || newClient) {
					newClient = false;
					((SimpleTextClientsModel) _externalObservable).setChanged();
					_externalObservable.notifyObservers();
				}

				/*
				 * If there is a timeout the flow goes directly to the catch
				 * part, for this I have put the control to the zombies before.
				 */
				@SuppressWarnings("resource")
				Socket socket = _serverSocket.accept();
				SingleClientHandler sch = _handlersFactory
						.createNewHandler(socket);
				// ClientSocket cs = new ClientSocket(this, socket, offline);
				sch.start();

				_clients.add(sch);

				newClient = true;

			} catch (java.net.SocketTimeoutException e) {
				continue;
			} catch (IOException e) {
				debug_var(381930, "Exception thrown in the socket, I quit.");
				return;
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		try {
			debug_var(993913, "Trying to close the socket server...");
			_serverSocket.close();

			Iterator<ISimpleClient> it = _clients.iterator();
			while (it.hasNext()) {
				((SingleClientHandler) it.next()).stop();
			}

		} catch (IOException e) {
			debug_var(391905,
					"Got exception while trying to close the client socket [",
					e.getMessage(), "] force quit");
		}
		debug_var(839201, "Socket server, End normal! I quit");
	}

	public void start() throws Exception {
		if (_ssThread != null) {
			return; // already started.
		}

		_ssThread = new Thread(new Runnable() {
			@Override
			public void run() {
				ss_thread();
			}
		});

		preStartAcceptingThreadHook();

		_ssThread.setName("SimpleSocketTextServer start ");
		_ssThread.start();
	}

	public void stop() {
		boolean changed = _endRequested.compareAndSet(false, true);
		assert (changed) : "something wrong! have you called me twice?";
		join_thread(_ssThread);

		postStopHook();

	}

}
