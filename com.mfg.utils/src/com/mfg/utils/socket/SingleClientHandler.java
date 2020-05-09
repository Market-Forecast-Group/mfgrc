package com.mfg.utils.socket;

import static com.mfg.utils.Utils.debug_var;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.mfg.utils.U;

/**
 * A server's spin off class which is able to impersonate the server for a
 * single client.
 * 
 * <p>
 * It has the logic to handle the communication (server side) to the given
 * client
 * 
 * @author Sergio
 * 
 */
public abstract class SingleClientHandler implements ISimpleClient {

	private static final int MAX_CYCLES = 6;

	private final String _remoteEndPoint;

	protected final SimpleSocketTextServer _ss;

	public SingleClientHandler(SimpleSocketTextServer _server, Socket so,
			String aGreeting) throws Exception {
		_ss = _server;
		_remoteEndPoint = so.getRemoteSocketAddress().toString();
		_stub = _createStub();
		GREETING = aGreeting;
		_tcp = new VSTcp();
		_tcp.init(so);
	}

	protected abstract SimpleTextServerStub _createStub() throws Exception;

	@Override
	public final String getRemoteIp() {
		return _remoteEndPoint;
	}

	protected transient AtomicBoolean _endRequested = new AtomicBoolean(false);

	private transient Thread _clientTh;

	protected transient VSTcp _tcp;

	protected final SimpleTextServerStub _stub;

	transient AtomicBoolean _zombieClient = new AtomicBoolean();

	private final String GREETING;

	/**
	 * 
	 * @return true if this client has to be recollected, because has finished
	 *         its work.
	 */
	public boolean isZombie() {
		return _zombieClient.get();
	}

	/**
	 * This is the generic send line method which sends line through the socket.
	 * The line will be then parsed by the payload at the other end of the
	 * channel.
	 * 
	 * <p>
	 * This method is synchronized because I do want that the line is sent
	 * atomically in the socket. Several other lines are sent one after another.
	 * 
	 * @param string
	 *            the line which is sent
	 * @throws IOException
	 *             if the socket has been closed from client side (is the client
	 *             dead?)
	 */
	public final synchronized void printLine(String string)
			throws SocketException {
		debug_var(637282, "-----------------------> socket sends : ", string);
		if (_tcp.sendString(string) != 0) {
			_endRequested.set(true);
			throw new SocketException("socket not more existing, forcing close");
		}
	}

	/**
	 * This is the handler method which will handle the communication between
	 * the server and the client. This is the server's view.
	 * 
	 * <p>
	 * As the name implies it is a never ending loop. The server will use this
	 * loop to handle all the communication towards the client: but this is not
	 * the only mean to communicate, because the server has also the
	 * {@linkplain SimpleTextPushSource} object which will handle the "push"
	 * communication towards the client.
	 * 
	 * @throws SocketException
	 */
	protected final void runMainLoop() {
		try {

			printLine(GREETING);

			// this will start the stub push thread (used by the
			// push sources).
			_stub.start();

			while (!_endRequested.get()) {
				// this call will block forever, it is interrupted by the stop
				// method of this class
				// or if the client will close the connection by brute force.
				String line = _tcp.readString();
				if (line == null || line.length() == 0) {
					debug_var(390103, "end of file in socket reached, I quit");
					return;
				}

				debug_var(381939, " IN <--- ", line);

				if (line.equals("quit")) {
					_endRequested.set(true);
					stop();
					break;
				}

				try {
					_stub.parseLine(line);
				} catch (Throwable e) {
					e.printStackTrace();
					_tcp.sendString("err,generic exception after parsing line ["
							+ line + "]. Additional message," + e.toString());
				}
			}
		} catch (SocketException e1) {
			e1.printStackTrace();
			debug_var(301395, "exception in Client socket ", this,
					" Something is very wrong here, I quit");
		} finally {
			_stub.stop(); // this will wait until the stub push thread exits.
		}
	}

	public final void setModified() {
		_ss.setModified();
	}

	/**
	 * starts the client socket thread to receive commands from the outside and
	 * give to the socket the answers.
	 * 
	 * <p>
	 * The client socket is not complex like the {@linkplain DfsProxy} because
	 * it does not have to
	 */
	public void start() {

		_clientTh = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					runMainLoop();
				} finally {
					_zombieClient.set(true);
					synchronized (_zombieClient) {
						_zombieClient.notifyAll();
					}
				}
			}
		});
		_clientTh.setName("client handler for " + this.getRemoteIp());
		_clientTh.start();

	}

	public void stop() {
		_endRequested.set(true);
		try {
			printLine("fat,Server is shutting down... force close of connection.");
			_tcp.disconnect();
		} catch (SocketException e) {
			/*
			 * this is the last chance to write the message. It does not matter
			 * if it does not arrive to destination. In any case the client is
			 * going to stop.
			 */
			U.debug_var(399355, "ignoring exception ", e.toString(),
					" while stopping the client ", this.getRemoteIp());
		}

		int cycle = 0;
		while (!_zombieClient.get()) {
			synchronized (_zombieClient) {
				try {
					_zombieClient.wait(4_000);
					cycle++;
					if (cycle > MAX_CYCLES) {
						break;
					}
				} catch (InterruptedException e) {
					U.debug_var(399315,
							"The client has been interrupted while trying to stop the main loop");

				}
			}
		}
		if (!_zombieClient.get()) {
			U.debug_var(929394, "client is not dead... I interrupt forcily");
			_clientTh.interrupt();
		}

		try {
			_clientTh.join(5_000);
		} catch (InterruptedException e) {
			U.debug_var(929394,
					"Could not join the thread " + _clientTh.getName()
							+ " I force the zombie status. interrupted ",
					Thread.currentThread().getName());
			_zombieClient.set(true);
			// e.printStackTrace();
		}

	}

}
