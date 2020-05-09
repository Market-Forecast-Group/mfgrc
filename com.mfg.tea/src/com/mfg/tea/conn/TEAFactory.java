package com.mfg.tea.conn;

import static com.mfg.utils.Utils.debug_var;

import java.io.IOException;

import com.mfg.common.TEAException;
import com.mfg.dfs.conn.IDFS;

/**
 * creates either a remote or a local tea. This class is the sister of the
 * ServerFactory class which created a multiserver and a local or remote DFS.
 * 
 * <p>
 * But what is the relationship between the {@link TEAFactory} class and the
 * {@link TEAGateway}?
 * 
 * <p>
 * it is simple! Tea Gateway is able to know if we are connecting to a local or
 * remote tea.
 * 
 * <p>
 * It is the same relationship between DFSDataProvider and ServerFactory.
 * 
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class TEAFactory {

	/*
	 * In the simplest case we have dfs and tea embedded, in this case the
	 * multitea will have a direct correspondence to DFS, because the virtual
	 * brokers may have the necessity to have the quotes from the virtual
	 * symbol... I have to define here better the interface, because the
	 * interface should be defined also when the objects are not in the same
	 * process space.
	 */

	private static MultiTEA _multiTea;

	/**
	 * this is the singleton tea, can be local, remote, but not both.
	 */
	private static ITEA _tea;

	private static TeaSocketServer _ss;

	/**
	 * Creates a local tea using a listener.
	 * 
	 * @param aListener
	 * @param aDataProvider
	 *            the data provider which is used to give prices to the virtual
	 *            brokers.
	 * @param aTeaId
	 *            the id of the local tea to be created, it should then be
	 *            registered to the multiserver
	 * @param acceptRemoteClients
	 *            if true we can accept remote clients for this embedded tea,
	 *            this will create the socket server which is then used to
	 *            accept incoming connections routed to the present multi tea
	 *            object.
	 * 
	 * 
	 * @return an interface to a local tea.
	 * @throws TEAException
	 */
	@SuppressWarnings("boxing")
	public static ITEA createLocalTea(ITEAListener aListener,
			IDFS aDataProvider, String aTeaId, boolean acceptRemoteClients)
			throws TEAException {

		if (_tea == null) {

			if (_multiTea == null) {
				createtMultiTea(true, aDataProvider);
			}

			_tea = new LocalTEA(aTeaId, aListener, _multiTea);
			_tea.start();

			if (acceptRemoteClients) {
				try {
					_ss = new TeaSocketServer(TeaSocketServer.DEFAULT_TEA_PORT);
					_ss.start();
				} catch (IOException e) {
					debug_var(381466, "Cannot open port ",
							TeaSocketServer.DEFAULT_TEA_PORT,
							" probably there is another server running");
				} catch (Exception e) {
					e.printStackTrace();
					throw new TEAException(e);
				}

			}
		}

		return _tea;
	}

	public static ITEA createRemoteTea(String aTeaId, ITEAListener aListener,
			String host, int port) throws TEAException {
		if (_tea != null) {
			return _tea;
		}

		_tea = unconditionallyCreateRemoteTea(aTeaId, aListener, host, port);
		return _tea;
	}

	/**
	 * Creates a TEA proxy without registering it here.
	 * <p>
	 * The client is responsible to stopping the proxy whenever it is not needed
	 * any more.
	 * 
	 * @param aTeaId
	 * @param aListener
	 * @param host
	 * @param port
	 * @return
	 * @throws TEAException
	 */
	public static ITEA unconditionallyCreateRemoteTea(String aTeaId,
			ITEAListener aListener, String host, int port) throws TEAException {
		ITEA tea = null;
		try {
			tea = TEAProxy.createProxy(aTeaId, aListener, host, port);
			tea.start();
			// _tea.login(user, password);
		} catch (TEAException e) {
			// something went wrong, I undo the creation7
			debug_var(718391, "Got exception, ", e,
					" I undo the creation of the proxy");
			if (tea != null) {
				tea.stop(); // to stop the thread
			}
			throw e; // rethrow it.
		}

		return tea;
	}

	/**
	 * creates the multi tea object which will have the possibility to connect
	 * to the real world broker.
	 * 
	 * @param useSimulatedBroker
	 * @param aDataProvider
	 * @throws TEAException
	 */
	static synchronized void createtMultiTea(boolean useSimulatedBroker,
			IDFS aDataProvider) throws TEAException {
		if (_multiTea == null) {
			_multiTea = new MultiTEA(useSimulatedBroker, aDataProvider);
		}
	}

	/**
	 * disposes the two servers, local and remote.
	 * 
	 * @throws TEAException
	 */
	public static synchronized void disposeServer() throws TEAException {
		if (_tea != null) {
			_tea.stop();
			_tea = null;
		}

		if (_multiTea != null) {
			_multiTea.stop();
			_multiTea = null;
		}

	}

	public static MultiTEA getMultiServer() {
		return _multiTea;
	}

}
