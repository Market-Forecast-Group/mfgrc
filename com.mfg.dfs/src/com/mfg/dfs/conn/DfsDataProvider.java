package com.mfg.dfs.conn;

import static com.mfg.utils.Utils.debug_var;
import static com.mfg.utils.Utils.warn;

import java.io.File;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.mfg.common.DFSException;
import com.mfg.common.IDataSource;
import com.mfg.common.ISymbolListener;
import com.mfg.dm.DataProvider;
import com.mfg.dm.TickDataRequest;
import com.mfg.utils.U;

public abstract class DfsDataProvider extends DataProvider implements
		IDFSListener {

	@Override
	public IDataSource createDataSource(ISymbolListener aListener,
			TickDataRequest aRequest) throws DFSException {

		/*
		 * A local server will create a local data source (directly connected to
		 * the virtual symbol), a remote server will create a proxy to a remote
		 * virtual symbol in server's space.
		 */
		IDataSource ds = _dfs.createDataSource(aRequest, aListener);

		/*
		 * Maybe I have to know if this data source is stopped, to take it
		 * away...
		 */
		// _dataSources.put(ds.getId(), ds);

		return ds;
	}

	@Override
	public void onConnectionStatusUpdate(ETypeOfData aDataType,
			EConnectionStatus aStatus) {
		// TO DO Auto-generated method stub

	}

	private IDFS _dfs;

	// key=symbol value=subscription
	// protected Hashtable<String, Subscription> fSubscriptions = new
	// Hashtable<>();

	// protected JobLogModel fState = new JobLogModel();

	/**
	 * A bootstrap constructor used to have the possibility to create a virtual
	 * symbol server side.
	 */
	protected DfsDataProvider(IDFS aDfs) {
		_dfs = aDfs;
	}

	/**
	 * builds a data provider.
	 * 
	 * <p>
	 * It implies that the plugin is existing, because it asks the plugin all
	 * the data which is used to build it.
	 */
	protected DfsDataProvider(boolean useSimulator, boolean dfsRemote,
			String remoteAddress, boolean isMixedMode) {
		boolean isBridgeOffline = false;
		final String realRemote = remoteAddress;
		// Get user and password, for now we simply use the system property

		String workspaceName = Platform.getInstanceLocation() == null ? ""
				: ("["
						+ new File(Platform.getInstanceLocation().getURL()
								.getFile()).getName() + "]");

		String user = System.getProperty("user.name") + workspaceName;
		String pass = "test password";
		_buildBridge(useSimulator, isBridgeOffline, dfsRemote, isMixedMode,
				realRemote, user, pass);
	}

	/**
	 * this is a simple constructor for debug purposes, it is called by the
	 * {@linkplain DfsTester}.
	 * 
	 * @param isBridgeOffline
	 */
	public DfsDataProvider(boolean useSimulator, boolean isBridgeOffline) {
		_buildBridge(useSimulator, isBridgeOffline, false, false, "", "user",
				"pass");
	}

	/**
	 * builds really the "bridge".
	 * 
	 * <p>
	 * In reality a bridge is not existing any more, but it is used in this way
	 * just for legacy matters.
	 * 
	 * <p>
	 * In the current situation a "bridge" is only the real connection to the
	 * data provider.
	 * 
	 * 
	 * <p>
	 * This function does not throw an exception, it simply exits the virtual
	 * machine if the bridge cannot be created, because this is a requirement.
	 * 
	 * @param isBridgeOffline
	 * @param dfsRemote
	 * @param isMixedMode
	 * @param remoteAddress
	 * @param user
	 * @param pass
	 */
	protected void _buildBridge(boolean useSimulator, boolean isBridgeOffline,
			boolean dfsRemote, boolean isMixedMode, final String remoteAddress,
			String user, String pass) {

		try {
			if (dfsRemote) {
				_dfs = ServerFactory.createRemoteServer(this, remoteAddress,
						8999, user, pass);
			} else {
				/* This will start the embedded server */
				_dfs = ServerFactory.createLocalServer(this, useSimulator,
						isBridgeOffline, isMixedMode);
			}
		} catch (DFSException e) {
			/*
			 * You could now try to connect locally, but if you change here, in
			 * the ServerFactory the proxy is already created and you should
			 * undo the singleton.
			 */
			debug_var(293153, "Cannot create the bridge connector reason ", e);

			if (dfsRemote) {
				try {
					// debug_var(293153, "Trying to connect locally...");

					Display.getDefault().syncExec(new Runnable() {
						@Override
						public void run() {
							// the shell is null, so I simply open the
							MessageDialog
									.openInformation(
											null,
											"INFO",
											"DFS remote at address "
													+ remoteAddress
													+ " is not available, I will connect locally");
						}
					});

					/*
					 * When I have tried to create the service I have already
					 * set the data source. That is the "undo" of that setting,
					 * because the DFSProxy has already set its base service.
					 * 
					 * This is not so good, probably it has to change, because
					 * this static setting is error prone. In the local case
					 * this is not important because the AppLock determines that
					 * the database is locked PRIOR to create the service.
					 */
					BaseDataSource.setService(null);

					_dfs = ServerFactory.createLocalServer(this, useSimulator,
							isBridgeOffline, isMixedMode);
				} catch (DFSException e1) {
					e1.printStackTrace();
					U.messageBox("I cannot lock the database locally, I cannot continue (Is there another local mfg opened?)");
					System.exit(-1); // without a dfs I cannot continue
				}
			} else { // !dfsRemote, try remote.
				try {
					// debug_var(293153, "Trying to connect locally...");

					Display.getDefault().syncExec(new Runnable() {
						@Override
						public void run() {
							// the shell is null, so I simply open the
							MessageDialog
									.openInformation(null, "INFO",
											"DFS local is not available, I will connect remotely");
						}
					});

					_dfs = ServerFactory.createRemoteServer(this, "localhost",
							8999, user, pass);
				} catch (DFSException e1) {
					e1.printStackTrace();
					U.messageBox("There is not a mixed mode application opened in this computer. I cannot go on.");
					// System.exit(-1); // without a dfs I cannot continue
				}
			}
			return;
		}
	}

	public IDFS getDfs() {
		return _dfs;
	}

	@Override
	public String getName() {
		return "DFS data provider!";
	}

	@Override
	public void switchOff() throws DFSException {
		super.switchOff();
	}

	/**
	 * switches on the data provider;
	 * 
	 * <p>
	 * The listener is an object that is able to listen to connection events.
	 * 
	 * <p>
	 * It can be null, in that case the method will create a fake event listener
	 * 
	 * @param aListener
	 * @param userId
	 * @param password
	 * @return
	 */
	@Override
	public boolean switchOn() {
		return true;
	}

	void unsubscribeToSymbol(String symbol) {
		try {
			_dfs.unsubscribeQuote(symbol);
		} catch (DFSException e) {
			// e.printStackTrace();
			warn("Something bad happened while unsubscribing to " + symbol
					+ " msg: " + e.toString());
		}
	}

}
