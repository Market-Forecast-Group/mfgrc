package com.mfg.tea.conn;

import com.mfg.common.TEAException;
import com.mfg.tea.accounting.MixedInventoriesFolder;
import com.mfg.tea.conn.ITEAListener.EConnectionStatus;
import com.mfg.tea.conn.ITEAListener.ETypeOfConnection;

/**
 * The object which implements the TEA interface locally, that is without using
 * a proxy, like the embedded DFS.
 * 
 * <p>
 * There is only one tea in a particular mfg application, either a local tea or
 * a remote tea.
 * 
 * <p>
 * All the trading configurations share the same tea, and all the strategies
 * inside the trading configuration either.
 * 
 * <p>
 * This because the TEA could also be remote and in this sense we have only one
 * TEA per client.
 * 
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class LocalTEA extends BaseTEA implements IServerSideTea {

	private ITEAListener _listener;
	private MultiTEA _server;

	// /**
	// * A server side tea holds this object to store all the things common to
	// all
	// * the server side teas, the local tea and the tea stub.
	// */
	// @SuppressWarnings("unused")
	// private SingleTeaHelper _helper;

	/**
	 * 
	 * @param aListener
	 * @param aServer
	 * @throws TEAException
	 */
	public LocalTEA(String aId, ITEAListener aListener, MultiTEA aServer)
			throws TEAException {
		super(aId);
		_listener = aListener;
		_listener.onConnectionStatusUpdate(ETypeOfConnection.TEA_PROXY,
				EConnectionStatus.CONNECTED);
		_server = aServer;

	}

	@Override
	public IVirtualBroker createVirtualBroker(VirtualBrokerParams params)
			throws TEAException {

		/*
		 * This is the same mechanism as when I create a virtual symbol... so I
		 * simply have to ask the virtual server to create a virtual broker for
		 * me.
		 * 
		 * I add my id to the parameters.
		 */
		params.teaId = _id;
		return _server.createVirtualBroker(params);
	}

	@Override
	public void stop() throws TEAException {
		_server.unregisterTea(_id, false);
	}

	@Override
	public void start() throws TEAException {
		// _helper =

		_server.registerTea(_id, this, true);
	}

	// @Override
	// public IInventory getRootStockHolder() {
	// return _helper._mixedRealTradingSymbols;
	// }

	// @Override
	// public void setMixedFolder(MixedInventoriesFolder aFolder) {
	// // TODO Auto-generated method stub
	//
	// }

	@Override
	public MixedInventoriesFolder getMixedFolder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITEAQuery getQueryTea() {
		return null;
	}

}
