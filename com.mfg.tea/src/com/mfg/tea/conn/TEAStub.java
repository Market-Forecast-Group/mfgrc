package com.mfg.tea.conn;

import java.util.HashMap;

import com.mfg.broker.IOrderMfg;
import com.mfg.common.TEAException;
import com.mfg.tea.accounting.MixedInventoriesFolder;
import com.mfg.utils.socket.SimpleTextServerStub;
import com.mfg.utils.socket.SingleClientHandler;

/**
 * The TEA object in server's space which is the corresponding object for the
 * {@link TEAProxy}.
 * 
 * <p>
 * It connects to the multiTEA
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
class TEAStub extends SimpleTextServerStub implements IServerSideTea {

	private MultiTEA _tea;

	private String _teaId;

	private SingleTeaHelper _helper;

	/**
	 * I have a map of all the virtual brokers created for this particular
	 * client, remember that the stub is not a singleton, but it is created for
	 * each connected client.
	 */
	private HashMap<String, VirtualBrokerStub> _brokers = new HashMap<>();

	/**
	 * The registration of TEA can be wrong. In this case the
	 * {@link #postStopHook()} method must not call the unregistered call
	 * automatically.
	 */
	private boolean _registered = false;

	protected TEAStub(MultiTEA aTea, SingleClientHandler aHandler) {
		super(new TeaCommandFactory(), aHandler);
		_tea = aTea;
		// _teaId = "A remote tea";
		@SuppressWarnings("unused")
		String remoteAddress = aHandler.getRemoteIp();

	}

	/**
	 * Adds the given virtual broker stub to this stub object.
	 * 
	 * <p>
	 * This stub object is then used to communicate to the client the virtual
	 * broker's notifcations.
	 * 
	 * 
	 * @param virtualBrokerStub
	 */
	public void addVirtualBroker(VirtualBrokerStub virtualBrokerStub) {
		_brokers.put(virtualBrokerStub.getPushKey(), virtualBrokerStub);
	}

	public IVirtualBroker createVirtualBroker(VirtualBrokerParams params)
			throws TEAException {
		params.teaId = _teaId;
		return _tea.createVirtualBroker(params);
	}

	@Override
	public MixedInventoriesFolder getMixedFolder() {
		return _helper._mixedRealTradingSymbols;
	}

	public MultiTEA getServer() {
		return _tea;
	}

	public void placeOrder(String pushkey, IOrderMfg order,
			boolean sendImmediately) throws TEAException {
		VirtualBrokerStub broker = _brokers.get(pushkey);

		/*
		 * This may happen if TEA is connected remotely, in this case a thread
		 * may have already closed the broker.
		 */
		if (broker == null) {
			throw new TEAException(
					"Broker is not more existing, probably client has requested the shutdown");
		}

		broker.placeOrder(order, sendImmediately);

	}

	@Override
	protected void postStopHook() {
		if (_registered) {
			try {
				_tea.unregisterTea(_teaId, true);
			} catch (TEAException e) {
				e.printStackTrace();
				throw new Error(e);
			}
		}
	}

	public void startBroker(String pushId) throws TEAException {
		_brokers.get(pushId).start();
	}

	// @Override
	// public void setMixedFolder(MixedInventoriesFolder aFolder) {
	// // TO DO Auto-generated method stub
	//
	// }

	public void startTEA(String aTeaId) throws TEAException {
		_teaId = aTeaId;
		_helper = _tea.registerTea(_teaId, this, false);
		_registered = true;
	}

	public void stopBroker(String pushId) throws TEAException {
		_brokers.get(pushId).stop();
		// no need of the broker, any more.
		_brokers.remove(pushId);
	}

	public void stopTEA(boolean allowRepeatedClose) throws TEAException {
		_tea.unregisterTea(_teaId, allowRepeatedClose);
	}
}
