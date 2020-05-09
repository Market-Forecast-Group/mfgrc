package com.mfg.tea.conn;

import java.net.SocketException;

import com.mfg.broker.IOrderExec;
import com.mfg.broker.IOrderMfg;
import com.mfg.broker.IOrderStatus;
import com.mfg.common.TEAException;
import com.mfg.utils.socket.SimpleTextPushSource;

/**
 * The stub for a virtual broker is simple a wrapper to the messages from a real
 * virtual broker and a stub from a proxy broker remote.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
class VirtualBrokerStub extends SimpleTextPushSource implements
		IVirtualBrokerListener {

	/**
	 * The broker encapsulated, this receives the commands.
	 */
	private VirtualBrokerBase _broker;

	/**
	 * Creates the stub object of a virtual stub, one which simulates a client
	 * in server's space.
	 * 
	 * @param aBroker
	 *            this is the broker in server's space which is used to really
	 *            sends the messages from the proxy.
	 */
	public VirtualBrokerStub(TEAStub aStub) {
		super(aStub);
		aStub.addVirtualBroker(this);
	}

	@Override
	public void orderStatusNew(IOrderStatus aStatus) {
		PushBrokerEnvelope pbe = new PushBrokerEnvelope(aStatus);
		try {
			sendToSocket(pbe.serializeToString());
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void newExecutionNew(IOrderExec anExec) {
		PushBrokerEnvelope pbe = new PushBrokerEnvelope(anExec);
		try {
			this.sendToSocket(pbe.serializeToString());
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void doPush() {
		assert (false);
	}

	/**
	 * @param aBroker
	 *            encapsulate the broker, which is the real (virtual) broker on
	 *            the server's side.
	 */
	public void encapsulateBroker(VirtualBrokerBase aBroker) {
		_broker = aBroker;
	}

	public void placeOrder(IOrderMfg order, boolean sendImmediately)
			throws TEAException {
		/*
		 * The cloning here it is not necessary because the order has already
		 * been decoupled from MFG as it has been created by the socket.
		 */
		_broker._placeOrderInternal(order, sendImmediately, false);
	}

	public void start() throws TEAException {
		_broker.start();
	}

	public void stop() throws TEAException {
		_broker.stop();
	}

}
