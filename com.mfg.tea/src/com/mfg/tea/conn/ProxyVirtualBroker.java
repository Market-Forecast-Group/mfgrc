package com.mfg.tea.conn;

import java.io.IOException;

import com.mfg.broker.IOrderExec;
import com.mfg.broker.IOrderMfg;
import com.mfg.broker.IOrderStatus;
import com.mfg.common.TEAException;
import com.mfg.utils.XmlIdentifier;
import com.mfg.utils.socket.SimpleTextPushSink;

/**
 * The proxy version of a {@link SingleSimulBroker} which will dispatch the
 * messages through the socket.
 * 
 * Of course there will be a stub version of it...
 * 
 * <p>
 * The stub version of a virtual broker is in some way something like a push
 * source, because the virtual broker interface is asynchronous and it will give
 * to the broker the notifications of orders and their executions.
 * 
 * <p>
 * The proxy virtual broker is a sink, because it collects the messages from the
 * server about notifications (asynchronous) in the broker.
 * 
 * <p>
 * In reality we may have a synchronous interface because we may be in
 * simulation, in any case the {@link #placeOrder(TEAOrder)} for example will
 * return only when the order is successfuly in the real broker in the server's
 * side.
 * 
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
class ProxyVirtualBroker implements IVirtualBroker {

	/**
	 * The helper class to handle the push from the outside.
	 * 
	 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
	 * 
	 */
	private class BrokerPushSinkHelper extends SimpleTextPushSink {

		public BrokerPushSinkHelper(String aPushId) {
			super(aPushId);
		}

		// to do

		@Override
		public void handlePush(String payload) throws IOException {
			PushBrokerEnvelope envelope = (PushBrokerEnvelope) XmlIdentifier
					.createFromString(payload);

			switch (envelope.type) {
			case NEW_EXECUTION:
				_listener.newExecutionNew((IOrderExec) envelope.realObject);
				break;
			case ORDER_STATUS:
				_listener.orderStatusNew((IOrderStatus) envelope.realObject);
				break;
			default:
				throw new IllegalStateException("unkown message in envelope "
						+ payload);
			}

		}
	}

	private BrokerPushSinkHelper _helper;

	/**
	 * This is the client's side listener, usually a portfolio or a shell (they
	 * are equivalent).
	 */
	IVirtualBrokerListener _listener;

	private IDuplexStatistics _proxyStats = new ProxyDuplexStats();

	private TEAProxy _proxy;

	public ProxyVirtualBroker(TEAProxy aProxy, String aPushId,
			IVirtualBrokerListener _localListener) {
		// super(aPushId);
		_proxy = aProxy;
		_listener = _localListener;
		_helper = new BrokerPushSinkHelper(aPushId);
	}

	@Override
	public void start() throws TEAException {
		/*
		 * start the broker.
		 */
		StartBrokerCommand sbc = new StartBrokerCommand(_proxy, _helper._pushId);
		_proxy._sendRequest(sbc);
		sbc.join();
	}

	@Override
	public void stop() throws TEAException {
		StopBrokerCommand sbc = new StopBrokerCommand(_proxy, _helper._pushId);
		_proxy._sendRequest(sbc);
		sbc.join();

	}

	@Override
	public void placeOrder(IOrderMfg aOrder, boolean sendImmediately)
			throws TEAException {

		if (!sendImmediately) {
			throw new UnsupportedOperationException();
		}

		/*
		 * to place an order the proxy broker must create a "placeorderCommand"
		 * and send it. The place order command adds the push identifier to the
		 * message, just to make sure that on server's side there is a way to
		 * know to which Virtual broker the order must be sent.
		 */
		PlaceOrderCommand poc = new PlaceOrderCommand(_proxy, _helper._pushId,
				aOrder, sendImmediately);

		/*
		 * I have to send it to the socket.
		 */
		_proxy._sendRequest(poc);

		poc.join();

	}

	@Override
	public void dropOrder(int aOrderId) throws TEAException {
		// TO DO Auto-generated method stub

	}

	@Override
	public IDuplexStatistics getAccountStats() {
		return _proxyStats;
	}

	public SimpleTextPushSink getSink() {
		return _helper;
	}

	@Override
	public void watchAccountStats() {
		// TO DO Auto-generated method stub

	}

	@Override
	public void updateOrder(IOrderMfg newOrder) throws TEAException {
		// TO DO Auto-generated method stub

	}

	// @Override
	// public List<IOrderMfg> getOpenenedOrders(boolean longOpenedOrders) {
	// // TO DO Auto-generated method stub
	// return null;
	// }

	@Override
	public void unwatchAccountStats() {
		// T ODO Auto-generated method stub

	}

	@Override
	public void placeParkedOrder(int aId) throws TEAException {
		// TODO Auto-generated method stub

	}

	@Override
	public void forgetParkedOrder(int aId) throws TEAException {
		// TODO Auto-generated method stub

	}

}
