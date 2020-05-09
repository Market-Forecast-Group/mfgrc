package com.mfg.utils.socket;

import static com.mfg.utils.Utils.catch_exception_and_continue;
import static com.mfg.utils.Utils.debug_var;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import com.mfg.utils.U;

/**
 * The simple socket text client is a class that handles the logic to make
 * simple connections to a text server, run the mail loop, ending it
 * 
 * <p>
 * This is a simple class used to easy the work when you have to implement a
 * communication to a socket server.
 * 
 * @author Sergio
 * 
 */
public class SimpleSocketTextClient {

	// / A string which the socket server will never give to us (if it does it
	// has more problem than us!)
	private static final String DEATH = "#@NBV(@c983V(A2hc8h23#";

	private final LinkedBlockingQueue<String> _queueFromSocket = new LinkedBlockingQueue<>(100);

	private Socket _socket;

	private PrintWriter _writer;

	private BufferedReader _reader;

	private Thread _readerTh;

	AtomicBoolean _endRequested = new AtomicBoolean(false);

	private Thread dpThread;

	public int _port;

	public String _host;

	/**
	 * This is the listener which will process messages arriving asynchronously
	 * from the socket.
	 * 
	 * <p>
	 * This listener is only asked to process one message at a time, because the
	 * thread is unique inside this socket client
	 */
	ISimpleSocketListener _listener;

	private final boolean _useDfsFormat;

	/**
	 * creates a SimpleSocketTextClient which will handle the communication to
	 * the real underlying socket.
	 * 
	 * <p>
	 * It needs a listener to process the messages.
	 * 
	 * @param useDfsFormat
	 *            if this is true then every line which is sent using this
	 *            socket will be transformed using this transformation: a single
	 *            percent sign
	 * 
	 *            <code>%</code>
	 * 
	 *            is transformed in two percent signs
	 * 
	 *            <code>%%</code>. The line is sent with a new line appended and
	 *            then another line is added which contains <b>only</b> a
	 *            percent sign and a newline.
	 * 
	 *            When the line is read the inverse transformation is done:
	 *            lines are read until a line with only a percent sign is
	 *            observed, all lines are concatenated and every pair of percent
	 *            signs <code>%%</code> is transformed in a single percent
	 *            <code>%</code>
	 * 
	 * @param aListener
	 *            the listener which is used to process the messages.
	 */
	public SimpleSocketTextClient(boolean useDfsFormat) {
		// _listener = aListener;
		_useDfsFormat = useDfsFormat;
	}

	/**
	 * starts the given socket, the socket is intended in text mode.
	 * 
	 * <p>
	 * The method returns immediately. If you want you can wait for a successful
	 * connection with another method
	 * 
	 * 
	 * @param host
	 * @param port
	 */
	public void start(ISimpleSocketListener aListener, String host, int port) {
		debug_var(259616, "starting the iqfeed thread... Effectively.");
		_listener = aListener;
		dpThread = new Thread(new DPThread(host, port), "simple socket thread");
		dpThread.setDaemon(true);
		dpThread.start();
	}

	/**
	 * waits for a connection, until timeout expires.
	 * 
	 * <p>
	 * if timeout is negative waits forever.
	 * 
	 * @param timeOut
	 *            in milliseconds
	 * 
	 * @return true if the socket is connected.
	 */
	public boolean waitForConnect(long timeOut) {
		for (;;) {
			if (_connected.get()) {
				return true;
			}
			synchronized (_connected) {
				try {
					_connected.wait(timeOut);
					if (!_connected.get()) {
						return false;
					}
					return true;
				} catch (InterruptedException e) {
					return false;
				}
			}
		}
	}

	AtomicBoolean _connected = new AtomicBoolean(false);

	/**
	 * The data provider thread which is used to connect to the bridge and keep
	 * the connection alive.
	 * 
	 * @author Pasqualino
	 */
	private class DPThread implements Runnable {

		public DPThread(String host, int port) {
			_host = host;
			_port = port;
		}

		@Override
		public void run() {

			while (true) {
				_listener.onTryingToConnect();
				try {
					_connect();
				} catch (Exception e) {
					_listener.onLostConnection();
					if (_endRequested.compareAndSet(true, false)) {
						debug_var(101051,
								"End requested from outside... I quit!");
						break;
					}

					try {
						Thread.sleep(5000);
					} catch (InterruptedException e1) {
						debug_var(891513,
								"interrupted eSig thread while sleeping... I quit");
						break;
					}
					continue;
				}
				_connected.set(true);
				_listener.onConnectionEstabilished();
				synchronized (_connected) {
					_connected.notify();
				}
				runMainLoop();
				debug_var(281491, "The main loop has finished. I disconnect");
				disconnect();
				_connected.set(false);
				if (_endRequested.compareAndSet(true, false)) {
					debug_var(101051, "End requested from outside... I quit!");
					break;
				}
			}
			debug_var(201015, "Final end of the dp thread.");
		}

	}

	/**
	 * The normal main loop of the application
	 */
	void runMainLoop() {
		String str;
		this._queueFromSocket.clear();

		try {
			while (!_endRequested.get()) {
				str = _queueFromSocket.take();
				if (str.equals(DEATH)) {
					debug_var(929125, "Get the DEATH from the socket");
					break;
				}
				// debug_var(319493, "Got from socket ", str);
				if (!_listener.processLine(str)) {
					debug_var(738281,
							"The client has not been able to process ", str,
							" I quit!");
					disconnect();
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			catch_exception_and_continue(e, true);
			disconnect();
		}
		debug_var(191945, "Ending main loop... ");
	}

	public synchronized void disconnect() {
		try {
			if (_socket != null) {
				_socket.close();
				_reader.close();
				_writer.close();
				_socket = null;

				debug_var(321091, "Removing the reader thread...");
				_readerTh.interrupt();
				_readerTh.join();
				_readerTh = null;

			} else {
				U.debug_var(238341,
						"the socket is already null, nothing to disconnect");
			}
		} catch (final IOException e) {
			System.out.println("Cannot connect: " + e.getMessage());
		} catch (InterruptedException e) {
			catch_exception_and_continue(e, true);
			_readerTh = null;
		}
	}

	/**
	 * Connects to the two sockets (historical and real time and creates also
	 * the necessary reader threads).
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	void _connect() throws IOException, InterruptedException {

		// the address is fixed to "localhost" (iqConnect does not accept
		// foreign connections).
		_socket = new Socket(_host, _port);

		_writer = new PrintWriter(new OutputStreamWriter(
				_socket.getOutputStream()), true);
		_reader = new BufferedReader(new InputStreamReader(
				_socket.getInputStream()));
		_endRequested.set(false);

		debug_var(291515, "Start reader thread. the previous reader is ",
				_readerTh, " (should be null!)");

		if (_readerTh != null) {
			debug_var(919031, "There is a reader! Strange... I will stop it");
			_readerTh.interrupt();
			_readerTh.join();
			_readerTh = null;
		}

		_readerTh = new Thread(new Runnable() {
			@Override
			public void run() {
				_readerThread();
			}

		});

		_readerTh.setName("Reader thread for simple socket client to "
				+ this._host + " port " + this._port);

		_readerTh.start();

	}

	void _readerThread() {
		String str;
		StringBuilder sb = new StringBuilder();
		while (true) {
			try {
				while ((str = _reader.readLine()) != null) {
					// debug_var(819519, " IN [bridge] <-- {", str, "}");
					if (_useDfsFormat) {
						if (str.equals("%")) {
							_queueFromSocket.put(sb.toString());
							sb.setLength(0);
							continue;
						}
						if (sb.length() != 0) {
							sb.append(EOL); // this is not the first line
						}
						String massaged = _regexInput.matcher(str).replaceAll(
								"%");
						sb.append(massaged);
					} else {
						_queueFromSocket.put(str);
					}
				}
				// if (str == null) {
				if (_useDfsFormat) {
					_queueFromSocket.put(sb.toString()); // last line
				}
				// }
			} catch (SocketTimeoutException e) {
				if (_socket.isConnected()) {
					debug_var(329252,
							"Timeout in reading the socket... but it is valid. I continue");
					continue;
				}
				_killTheMainLoop();
			} catch (IOException e) {
				debug_var(418935, "----- ioexce ", e.toString());
				_killTheMainLoop();
			} catch (Throwable e) {
				debug_var(168129, "unbelievable exception in reader ", e);
				_killTheMainLoop();
			}
			break;
		}

		debug_var(181895, "Ending the reader thread...");

	}

	private void _killTheMainLoop() {
		try {
			_queueFromSocket.put(DEATH);
		} catch (InterruptedException e) {
			debug_var(399101, "Interrupted while putting DEATH on queue, why?");
		}
	}

	public void stop() {
		try {
			_endDP();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Ends the data thread...
	 * 
	 * @throws InterruptedException
	 */
	private void _endDP() throws InterruptedException {
		if (dpThread == null) {
			return; // nothing to do.
		}
		endMainLoop();
		debug_var(234852, "waiting for iqbridge bridge to stop");
		dpThread.interrupt(); // wake up from the wait.
		dpThread.join();
	}

	private void endMainLoop() {
		debug_var(149155, "endMainLoop requested...");

		_endRequested.set(true);
		_killTheMainLoop();

	}

	public boolean isConnected() {
		return _socket != null ? (_socket.isConnected() && !_socket.isClosed())
				: false;
	}

	private static final char EOL[] = new char[] { '\r', '\n' };

	private static final Pattern _regexOutput = Pattern.compile("%");

	private static final Pattern _regexInput = Pattern.compile("%%");

	/**
	 * Sends a line to the underlying data stream.
	 * <p>
	 * The line should not contain a line feed. the END of Line marker will be
	 * appended to the string automatically.
	 * 
	 * @param line
	 * @throws IOException
	 */
	public synchronized void writeLine(String line) throws IOException {
		if (_useDfsFormat) {
			String modified = _regexOutput.matcher(line).replaceAll("%%");

			_writer.write(modified);
			_writer.write(EOL);
			_writer.write("%");
			_writer.write(EOL);
		} else {
			_writer.println(line);
		}

		if (_writer.checkError()) {
			debug_var(398193, "Error in socket, please what I have to do?");
			throw new IOException("Error while writing " + line);
		}
	}

	// public void setListener(ISimpleSocketListener aListener) {
	// _listener = aListener;
	//
	// }
}
