package com.mfg.tea.conn;

import static com.mfg.utils.Utils.debug_var;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import com.mfg.common.DFSException;
import com.mfg.dfs.conn.IDFS;
import com.mfg.dfs.conn.IDFSListener;
import com.mfg.dfs.conn.ServerFactory;
import com.mfg.utils.socket.SimpleSocketTextServer;
import com.mfg.utils.socket.SimpleTextClientsModel;
import com.mfg.utils.socket.SingleClientHandler;
import com.mfg.utils.socket.SingleClientHandlerFactory;

/**
 * This is the socket server used to collect the orders from the tea clients
 * which are then routed to the {@link MultiTEA} object.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class TeaSocketServer extends SimpleSocketTextServer {

	class DFSTeaListener implements IDFSListener {

		@Override
		public void onConnectionStatusUpdate(ETypeOfData aDataType,
				EConnectionStatus aStatus) {
			//
		}

		// @Override
		// public void onNewQuote(DFSSymbolEvent anEvent) {
		// //
		// }

	}

	private static TeaSocketServer _ss;

	public static final int DEFAULT_TEA_PORT = 8998;

	/**
	 * @param args
	 */
	@SuppressWarnings("boxing")
	public static void createTeaServer(String[] args,
			boolean createConsoleAndWait) throws Exception {
		int port = DEFAULT_TEA_PORT;
		debug_var(391903, "TEA server started @ port ", port);
		_ss = new TeaSocketServer(port);
		_ss.start();
		if (createConsoleAndWait) {
			try (Scanner sc = new Scanner(System.in)) {
				String line;

				do {
					line = sc.nextLine();
					if (line.compareTo("quit") == 0) {
						break;
					}
					debug_var(393901, "Received [", line, "] quit to exit");

				} while (true);

				debug_var(319331, "Stopping the socket server, please wait...");
				_ss.stop();
			}
		}
	}

	/**
	 * This main will build the independent tea, free from eclipse plugin, as a
	 * stand alone application, ready to get the clients connections
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String args[]) throws Exception {
		createTeaServer(args, true);
	}

	public static void stopRemoteTEA() {
		_ss.stop();
	}

	protected TeaSocketServer(int port) throws IOException, DFSException {
		super(port, new SingleClientHandlerFactory() {

			@Override
			public SingleClientHandler createNewHandler(Socket aSocket)
					throws Exception {
				return new TeaClientSocket(_server, aSocket);
			}
		});
	}

	@Override
	protected SimpleTextClientsModel createExternalObservable() {
		return new SimpleTextClientsModel();
	}

	@Override
	protected void preStartAcceptingThreadHook() throws Exception {

		IDFS idfs = ServerFactory.createRemoteServer(new DFSTeaListener(),
				"localhost", 8999, "scott", "tiger");
		TEAFactory.createtMultiTea(true, idfs);
	}

}
