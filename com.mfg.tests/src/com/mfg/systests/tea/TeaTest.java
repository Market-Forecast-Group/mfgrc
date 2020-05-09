package com.mfg.systests.tea;

import static com.mfg.utils.Utils.debug_var;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

import com.mfg.common.RandomSymbol;
import com.mfg.common.TEAException;
import com.mfg.dfs.conn.IDFS;
import com.mfg.dfs.conn.ProxyDfsListener;
import com.mfg.dfs.conn.ServerFactory;
import com.mfg.dfs.conn.SocketServer;
import com.mfg.tea.conn.ITEA;
import com.mfg.tea.conn.ITEAListener;
import com.mfg.tea.conn.TEAFactory;
import com.mfg.tea.conn.TeaSocketServer;
import com.mfg.tea.db.Db;
import com.mfg.utils.U;

/**
 * A TEA system test, which will try to put on stress all TEA functions, and
 * expecially the global accounting nature of tea, which takes track of the
 * inventories, etc...
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class TeaTest {

	private static final class SyntaxError extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = -99555328986797012L;

		public SyntaxError(String string) {
			super(string);
		}

	}

	private static final String SYMBOL = "Symbol";

	static AtomicBoolean _endRequested = new AtomicBoolean();

	private static ArrayList<TeaTest> _testers = new ArrayList<>();

	/**
	 * Creates the thread which will listen to console and if requested it will
	 * set the flag to stop the application.
	 */
	private static void _createConsoleThread() {

		Thread th = new Thread(new Runnable() {

			@Override
			public void run() {
				try (Scanner sc = new Scanner(System.in)) {
					String line;

					do {
						line = sc.nextLine();
						if (line.compareTo("quit") == 0) {
							U.debug_var(281751,
									"end requested to the app, please wait the shutdown...");
							_endRequested.set(true);
							break;
						} else if (line.compareTo("dd") == 0) {
							Db.i().dump();
							continue;
						}
						debug_var(393901, "Received [", line, "] quit to exit");
					} while (true);
				}
			}
		});
		th.setDaemon(true);
		th.start();

	}

	/**
	 * A main which calls another main... just to start the random dfs.
	 * 
	 * @throws Exception
	 */
	private static void _createRemoteRandomDFS() throws Exception {

		U.debug_var(910581, "Creating a custom server, listening on port 8999");

		String home = System.getProperty("user.home");

		String customRoot = home + "\\RandRoot";

		SocketServer.createSocketServer(new String[] {
				"--customDfsRoot=" + customRoot, "--isOffline" }, false);

	}

	/**
	 * This will call the main to create a remote TEA.
	 * 
	 * @throws Exception
	 */
	private static void _createRemoteTEA() throws Exception {
		com.mfg.tea.conn.TeaSocketServer
				.createTeaServer(new String[] {}, false);
	}

	/**
	 * Returns the string at a position pos. It is "safe" because if the
	 * position is not valid a {@link SyntaxError} is thrown instead of an
	 * {@link IndexOutOfBoundsException}.
	 * 
	 * @param splits
	 *            the parsed line as it came from the user.
	 * @param pos
	 *            the position in which the string is to be retrieved. The
	 *            position zero is the command itself.
	 */
	private static String _getStringSafe(String[] splits, int pos)
			throws SyntaxError {

		if (pos >= splits.length) {
			throw new SyntaxError("This command needs at least " + (pos)
					+ " argumetns. You supplied " + (splits.length - 1));
		}
		return splits[pos];
	}

	static void _stopShell(TestedShell shell) {
		shell.stop();
		try {
			shell.join();
			U.debug_var(299183, "The shell has been joined");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * entry point for the tea tester. It needs a TEA, either local or remote,
	 * and also a DFS, even if the latter could be an empty dfs because it will
	 * use random symbols to test it.
	 * 
	 * @param args
	 *            For now unused.
	 * @throws Exception
	 */
	@SuppressWarnings({ "boxing", "resource" })
	public static void main(String args[]) throws Exception {

		_createRemoteRandomDFS();

		_createRemoteTEA();

		// String home = System.getProperty("user.home");
		// String testingHome = home + "\\RandRoot";
		// U.debug_var(298106, "Setting testing home to " + testingHome);
		// MfgMdbSession.setSessionRoot(testingHome);
		// IDFS aDFS = ServerFactory.createLocalServer(new ProxyDfsListener(
		// new PrintWriter(System.out)), false, false, false);

		U.debug_var(191389, "CONNECTING TO REMOTE DFS, WAIT");
		IDFS aDFS = ServerFactory.createRemoteServer(new ProxyDfsListener(
				new PrintWriter(System.out)), "localhost", 8999, "scott",
				"tiger");

		/*
		 * Creates the console thread used to quit the application.
		 */
		_createConsoleThread();

		/*
		 * create the socket to accept connections to tea test...
		 */
		try (ServerSocket ss = new ServerSocket(5555)) {

			/*
			 * This is the local tea which will be the server for different
			 * clients all in this process space (eventually we might also have
			 * a remote tea connected with a remote DFS).
			 */
			// ITEA aTea = TEAFactory.createLocalTea(teaListener, aDFS, "TEST",
			// false);
			// ITEA aTea = TEAFactory.createRemoteTea("TEST", teaListener,
			// "localhost", 8998);

			ss.setSoTimeout(2000);

			U.debug_var(280259, "waiting for connection on port ", 5555);

			while (true) {

				Socket socket;
				try {
					socket = ss.accept();
					// _sockets.add(socket);
				} catch (SocketTimeoutException ste) {
					if (_endRequested.get()) {
						for (TeaTest teaTest : _testers) {
							teaTest.close();
						}
						break;
					}
					continue;
				}
				/*
				 * After creating the tea we are able to create the testing
				 * environment which will allow the user to create shells at
				 * command and to know the result.
				 */
				TeaTest tt = new TeaTest(aDFS/* , aTea */);
				_testers.add(tt);
				tt._doTest(socket);
			}

		} finally {
			U.debug_var(920942, "Disposing the servers, please wait.");
			TEAFactory.disposeServer();
			ServerFactory.disposingServer();

			TeaSocketServer.stopRemoteTEA();
			SocketServer.stopRemoteDFS();
		}

	}

	private final IDFS _dfs;

	private ITEA _tea;

	HashMap<String, TestedShell> _shells = new HashMap<>();

	PrintStream _printStream;

	private Socket _socket;

	/**
	 * Tea is not sent because this is like an entirely new client which is
	 * connected.
	 * 
	 * @param aDFS
	 * @throws TEAException
	 */
	public TeaTest(IDFS aDFS/* , ITEA aTea */) throws TEAException {
		_dfs = aDFS;
		// _tea = aTea;

	}

	private void _checkTEAPresence() throws SyntaxError {
		if (_tea == null) {
			throw new SyntaxError(
					"TEA is not connected, issue a 'tea' command first.");
		}

	}

	synchronized void _close() throws IOException, TEAException {
		if (_tea != null) {
			_tea.stop();
		}
		if (_socket == null) {
			return;
		}
		_printStream.close();
		_socket.close();
		_socket = null;
	}

	private void _doCreateTeaProxyCommand(String[] splits) throws SyntaxError,
			TEAException {

		if (_tea != null) {
			throw new TEAException("Tea is already been created.");
		}

		String teaName = _getStringSafe(splits, 1);
		ITEAListener teaListener = new ITEAListener() {

			@Override
			public void onConnectionStatusUpdate(ETypeOfConnection aDataType,
					EConnectionStatus aStatus) {
				// TO DO Auto-generated method stub
			}

		};
		_tea = TEAFactory.unconditionallyCreateRemoteTea(teaName, teaListener,
				"localhost", 8998);
	}

	private void _doNewShellCommand(String[] splits) throws SyntaxError {

		_checkTEAPresence();

		if (splits.length < 2 || splits[1].length() == 0) {
			_printStream.println("I need the name of the shell.");
			return;
		}

		if (_shells.containsKey(splits[1])) {
			_printStream.println("The shell " + splits[1]
					+ " is already existing");
			return;
		}

		/*
		 * I have to create a new shell, this will connect to tea and to dfs
		 * with a random symbol
		 */
		RandomSymbol rs = new RandomSymbol(SYMBOL, 5, 0, 1);

		TestedShellParams tsp = new TestedShellParams(splits[1], rs);

		TestedShell testedShell = new TestedShell(_dfs, _tea, tsp);
		testedShell.start();

		_shells.put(splits[1], testedShell);

		// U.sleep(10_000);

	}

	/**
	 * Stop the given shell.
	 * 
	 * @param splits
	 */
	private void _doStopShellCommand(String[] splits) {
		if (splits.length < 2 || splits[1].length() == 0) {
			_printStream.println("I need the name of the shell.");
			return;
		}
		if (!_shells.containsKey(splits[1])) {
			_printStream.println("The shell " + splits[1] + " is not existing");
			return;
		}
		TestedShell testedShell = _shells.get(splits[1]);

		_stopShell(testedShell);

		_shells.remove(splits[1]);
	}

	/**
	 * This is the never ending test method. It ends only when the user types
	 * quit on the console.
	 * 
	 * @param socket
	 * 
	 * @param aDFS
	 *            a ready dfs to be tested (it usually is a void dfs because we
	 *            use only random symbols).
	 * @param aTea
	 *            a ready Tea which is already connected to dfs.
	 * @throws IOException
	 */
	private void _doTest(final Socket socket) throws IOException {

		_socket = socket;

		Thread th = new Thread(new Runnable() {

			@Override
			public void run() {
				try (Scanner sc = new Scanner(socket.getInputStream(), "UTF-8")) {
					String line;

					_printStream = new PrintStream(socket.getOutputStream(),
							true, "UTF-8");
					_printStream.println("Welcome to tea shell, quit to exit");

					do {
						line = sc.nextLine();
						if (line.compareTo("quit") == 0) {
							break;
						}

						try {
							processServerCommandLine(line);
						} catch (SyntaxError e) {
							_printStream.println(e.getMessage());
							continue;
						} catch (TEAException e) {
							_printStream.println(e.getMessage());
							continue;
						} catch (Exception e) {
							e.printStackTrace();
							break;
						}

					} while (true);
				} catch (NoSuchElementException ex) {
					U.debug_var(298185, "OK, I will quit");
				} catch (UnsupportedEncodingException e1) {
					// TO DO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TO DO Auto-generated catch block
					e1.printStackTrace();
				} finally {
					// // forcing close of the allocated shells.
					// try {
					// socket.close();
					// } catch (IOException e) {
					// // TO DO Auto-generated catch block
					// e.printStackTrace();
					// }
					for (TestedShell shell : _shells.values()) {
						_stopShell(shell);
					}
					try {
						_close();
					} catch (IOException | TEAException e) {
						U.debug_var(239851,
								"Got exception while closing the socket");
					}

				}

			}
		});

		th.start();

	}

	// private void _syntaxError(String string) {
	// throw new SyntaxError(string);
	//
	// }

	void close() throws IOException, TEAException {
		_printStream.println("Server is shutting down.");
		for (TestedShell shell : _shells.values()) {
			_stopShell(shell);
		}
		_close();
	}

	/**
	 * Processes a line for the server which usually the user has written on
	 * console (but it may come also from a batch file to have an automatic
	 * testing).
	 * 
	 * @param line
	 * @throws SyntaxError
	 * @throws TEAException
	 */
	void processServerCommandLine(String line) throws SyntaxError, TEAException {
		String splits[] = line.split(",");
		if (splits.length < 2) {
			throw new SyntaxError(
					"I need a command followed by a comma. I received [" + line
							+ "] instead, type 'quit' alone to exit");
			// _printStream
			// .println("I need a command which you prefer! I received ["
			// + line + "] instead, type 'quit' to exit");
			// return;
		}
		String splits1[] = line.split(",", 2);
		@SuppressWarnings("unused")
		String allTheRestLine;
		if (splits1.length == 2) {
			allTheRestLine = splits1[1];
		} else {
			allTheRestLine = null;
		}

		if (splits[0].compareTo("ns") == 0) {
			_doNewShellCommand(splits);
		} else if (splits[0].compareTo("ss") == 0) {
			_doStopShellCommand(splits);
		} else if (splits[0].compareTo("tea") == 0) {
			_doCreateTeaProxyCommand(splits);
		} else {
			_printStream.println("unrecognized command [" + splits[0] + "]");
		}
	}
}
