package com.mfg.dfs.conn;

import static com.mfg.utils.Utils.debug_var;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import com.mfg.common.DFSException;
import com.mfg.dfs.cache.MfgMdbSession;
import com.mfg.dfs.data.DfsClientsModel;
import com.mfg.utils.CmdLineParser.IllegalOptionValueException;
import com.mfg.utils.CmdLineParser.UnknownOptionException;
import com.mfg.utils.socket.SimpleSocketTextServer;
import com.mfg.utils.socket.SimpleTextClientsModel;
import com.mfg.utils.socket.SingleClientHandler;
import com.mfg.utils.socket.SingleClientHandlerFactory;

/**
 * This is the class which stores the thread of the socket accepter thread.
 * 
 * <p>
 * It will start a socket server and spawn a thread for each client that wants
 * to connect. The socket is a simple text socket, we don't use binary at all,
 * it's too complex and difficult to debug.
 * <p>
 * maybe I could later add a binary interface (I wrote it some time ago) but for
 * now I think that this is not the case.
 * 
 * <p>
 * The socket server is also a clients model because it stores the model of all
 * the clients connected to this socket (and then each client will store all the
 * information about the requests done)
 * 
 * <p>
 * This is a weak singleton class, weak in the sense that it does not enforce
 * its singleness, but it checks it anyway (I could relax this, because in any
 * case you could have different servers on different ports), it simplifies the
 * code but it does not add anything to the clearity of it.
 * 
 * @author Sergio
 * 
 */
public class SocketServer extends SimpleSocketTextServer {

	transient static boolean useSimulator;

	/**
	 * 
	 */
	// private static final long serialVersionUID = -8014570385633256506L;

	transient static boolean offline;

	private static SocketServer _ss;

	@SuppressWarnings("boxing")
	public static void createSocketServer(String[] args,
			boolean createConsoleAndWait) throws Exception {
		MfgCmdLineParser cmdLine;
		try {
			cmdLine = new MfgCmdLineParser(args);
		} catch (IllegalOptionValueException | UnknownOptionException e) {
			throw new RuntimeException(e);
		}

		MfgMdbSession.setSessionRoot(cmdLine.customDfsRoot);

		int port = 8999;
		debug_var(391903, "DFS server started offline? ", cmdLine.offline,
				" accepted connections. on port ", port,
				". Type quit<enter> to finish.");
		_ss = new SocketServer(8999, cmdLine.useSimulator, cmdLine.offline);
		_ss.start();
		if (createConsoleAndWait) {
			try (Scanner sc = new Scanner(System.in)) {
				String line;

				do {
					line = sc.nextLine();
					if (line.compareTo("quit") == 0) {
						break;
					} else if (line.compareTo("status") == 0) {
						System.out.println(_ss.getModel().toString());
					} else if (line.compareTo("fcs") == 0) {
						ServerFactory.getRealService().manualScheduling();
						System.out.println("Booked the manual scheduling...");
					}
					debug_var(393901, "Received [", line, "] quit to exit");

					// processServerCommandLine(line);

				} while (true);

				debug_var(319331, "Stopping the socket server, please wait...");
				_ss.stop();
			}
		}
	}

	public static void main(String args[]) throws Exception {
		createSocketServer(args, true);
	}

	public static void stopRemoteDFS() {
		if (_ss != null)
			_ss.stop();
	}

	/**
	 * Creates a socket server that listens to a particular port.
	 * 
	 * <p>
	 * The constructor is private because this is a singleton.
	 * 
	 * @param port
	 * @throws DFSException
	 *             if something is wrong
	 * @throws IOException
	 */
	SocketServer(int port, final boolean isUseSimulator, boolean isOffline)
			throws DFSException, IOException {
		super(port, new SingleClientHandlerFactory() {

			@Override
			public SingleClientHandler createNewHandler(Socket aSocket)
					throws Exception {
				return new ClientSocket(_server, aSocket);
			}
		});

		// _externalObservable = new DfsClientsModel(this);
		SocketServer.offline = isOffline;
		SocketServer.useSimulator = isUseSimulator;

	}

	@Override
	protected SimpleTextClientsModel createExternalObservable() {
		return new DfsClientsModel(this);
	}

	/**
	 * returns the model to get the information about all the clients which are
	 * connected to this socket server.
	 * 
	 * @return
	 */
	public DfsClientsModel getModel() {
		return (DfsClientsModel) _externalObservable;
	}

	@Override
	protected void postStopHook() {
		if (ServerFactory.getRealService() != null) {
			debug_var(391033, "Stopping the real service, please wait");
			ServerFactory.getRealService().stop();
		}
	}

	@Override
	protected void preStartAcceptingThreadHook() throws DFSException {
		ServerFactory.getMultiServer(useSimulator, offline);
	}
}
