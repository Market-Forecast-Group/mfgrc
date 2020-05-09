package com.mfg.tea.conn;

import static com.mfg.utils.Utils.debug_var;

import java.io.IOException;

import com.mfg.broker.IOrderMfg;
import com.mfg.broker.orders.OrderImpl;
import com.mfg.common.DFSException;
import com.mfg.common.TEAException;
import com.mfg.utils.U;
import com.mfg.utils.XmlIdentifier;
import com.mfg.utils.socket.BaseSocketHelper;
import com.mfg.utils.socket.SimpleRemoteCommand;
import com.mfg.utils.socket.SimpleSocketTextClient;
import com.mfg.utils.socket.SimpleTextServerStub;

abstract class TEARemoteCommand extends SimpleRemoteCommand {

	protected TEAProxy _proxy;

	public TEARemoteCommand(int handle, String string, String unparsed_params) {
		super(handle, string, unparsed_params);
		/*
		 * The constructor on the stub side, the unparsed params contains the
		 * serialization of the VirtualBrokerParams structure.
		 */
	}

	public TEARemoteCommand(TEAProxy aProxy, String string) {
		super(string);
		_proxy = aProxy;
	}

	@Override
	public Object getAnswer() throws TEAException {
		join();
		return _answer;
	}

	@Override
	public void join() throws TEAException {
		try {
			super.join();
		} catch (Exception e) {
			if (e instanceof TEAException) {
				throw (TEAException) e;
			}
			throw new IllegalStateException(e);
		}
	}

}

class StopBrokerCommand extends TEARemoteCommand {

	public StopBrokerCommand(int handle, String unparsed_params) {
		super(handle, TEARemoteCommands.stopB.toString(), unparsed_params);
	}

	public StopBrokerCommand(TEAProxy aProxy, String aPushId) {
		super(aProxy, TEARemoteCommands.stopB.toString());
		_unparsedParams = aPushId;
	}

	@Override
	protected void _internalPerform(SimpleTextServerStub aStub)
			throws Exception {
		// only one parameter.
		String pushId = _unparsedParams;
		TEAStub stub = (TEAStub) aStub;

		stub.stopBroker(pushId);
	}

	@Override
	protected int parseAnswerImpl(String payload) throws DFSException {
		// no answer.
		return 0;
	}

}

class StartBrokerCommand extends TEARemoteCommand {

	public StartBrokerCommand(int handle, String unparsed_params) {
		super(handle, TEARemoteCommands.sbc.toString(), unparsed_params);
	}

	public StartBrokerCommand(TEAProxy aProxy, String aPushId) {
		super(aProxy, TEARemoteCommands.sbc.toString());
		_unparsedParams = aPushId;
	}

	@Override
	protected void _internalPerform(SimpleTextServerStub aStub)
			throws Exception {
		// only one parameter.
		String pushId = _unparsedParams;
		TEAStub stub = (TEAStub) aStub;

		stub.startBroker(pushId);
	}

	@Override
	protected int parseAnswerImpl(String payload) throws DFSException {
		// no answer.
		return 0;
	}

}

class PlaceOrderCommand extends TEARemoteCommand {

	// private TEAOrder _order;

	public PlaceOrderCommand(int handle, String unparsed_params) {
		super(handle, TEARemoteCommands.poc.toString(), unparsed_params);

	}

	@SuppressWarnings("boxing")
	public PlaceOrderCommand(TEAProxy aProxy, String aPushKey,
			IOrderMfg aOrder, boolean sendImmediately) {
		super(aProxy, TEARemoteCommands.poc.toString());
		_unparsedParams = U.join(aPushKey,
				((XmlIdentifier) aOrder).serializeToString(), sendImmediately);
	}

	@Override
	protected void _internalPerform(SimpleTextServerStub aStub)
			throws Exception {

		String[] splits = U.commaPattern.split(_unparsedParams, 3);

		String pushkey = splits[0];

		/*
		 * first we build the order back. This is where the order enters the
		 * server space: from now on it will be handled here.
		 * 
		 * The client version of the order may be removed, altered, it does not
		 * matter.
		 * 
		 * We have to take into consideration the fact that TEA may be embedded
		 * and we receive the exact reference from the client.
		 * 
		 * In the past this was handled with a careful modification of the
		 * internal private fields of the order... maybe now this is not any
		 * more necessary... tbd.
		 */
		OrderImpl order = (OrderImpl) XmlIdentifier.createFromString(splits[1]);

		boolean sendImmediately = Boolean.parseBoolean(splits[2]);

		TEAStub stub = (TEAStub) aStub;

		stub.placeOrder(pushkey, order, sendImmediately);

	}

	@Override
	protected int parseAnswerImpl(String payload) throws DFSException {
		// There is not a direct answer. The client does not need to know the
		// internal id of this order.
		return 0;
	}

}

class StopTEACommand extends TEARemoteCommand {

	public StopTEACommand(TEAProxy aProxy) {
		super(aProxy, TEARemoteCommands.stopTEA.toString());
		_unparsedParams = VOID;
	}

	public StopTEACommand(int handle, String unsplitted_params) {
		super(handle, TEARemoteCommands.stopTEA.toString(), unsplitted_params);
	}

	@Override
	protected void _internalPerform(SimpleTextServerStub aStub)
			throws Exception {
		TEAStub stub = (TEAStub) aStub;
		stub.stopTEA(false);
	}

	@Override
	protected int parseAnswerImpl(String payload) throws DFSException {
		// nothing
		return 0;
	}
}

/**
 * This command is used to notify the stub part of TEA of the id.
 * 
 * <p>
 * For now there is not a concept of authentication, probably there will be an
 * authentication at the level of ssh tunnelling (for now it is absent).
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
class StartTEACommand extends TEARemoteCommand {

	public StartTEACommand(TEAProxy aProxy, String teaId) {
		super(aProxy, TEARemoteCommands.start.toString());
		_unparsedParams = teaId;
	}

	public StartTEACommand(int handle, String unsplitted_params) {
		super(handle, TEARemoteCommands.start.toString(), unsplitted_params);
	}

	@Override
	protected void _internalPerform(SimpleTextServerStub aStub)
			throws Exception {
		TEAStub stub = (TEAStub) aStub;
		stub.startTEA(_unparsedParams);
	}

	@Override
	protected int parseAnswerImpl(String payload) throws DFSException {
		// nothing to parse
		return 0;
	}
}

/**
 * This is the command to create a remote virtual broker in a remote TEA.
 * 
 * <P>
 * The broker may be simulated, in this case TEA will instantiate a market
 * simulator connected to its dfs (which can also be remote).
 * 
 * <p>
 * The command uses the {@link VirtualBrokerParams} structure to serialize the
 * broker parameters.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
class CreateVirtualBrokerCommand extends TEARemoteCommand {

	private IVirtualBrokerListener _localListener;

	public CreateVirtualBrokerCommand(int handle, String unparsed_params) {
		super(handle, TEARemoteCommands.cvbc.toString(), unparsed_params);
	}

	/**
	 * constructor on the proxy side...
	 * 
	 * <p>
	 * This has to be done carefully because we have to stop the broker listener
	 * interface here, because it will substituted by a proxy listener on the
	 * server's side.
	 * 
	 * @param aProxy
	 *            the proxy which has created the command.
	 * 
	 * @param aParams
	 */
	public CreateVirtualBrokerCommand(TEAProxy aProxy,
			VirtualBrokerParams aParams) {
		super(aProxy, TEARemoteCommands.cvbc.toString());

		_localListener = aParams.listener;
		aParams.listener = null;

		_unparsedParams = aParams.serializeToString();
	}

	@Override
	protected int parseAnswerImpl(String payload) throws DFSException {
		/*
		 * The answer is simple the push key.
		 * 
		 * Here I should create a "sink" for the virtual broker.
		 */

		ProxyVirtualBroker pvb = new ProxyVirtualBroker(_proxy, payload,
				_localListener);
		_answer = pvb;

		_proxy.addProxyVirtualBroker(pvb);

		return 0;
	}

	@Override
	protected void _internalPerform(SimpleTextServerStub aStub)
			throws DFSException, TEAException {
		/*
		 * stub side... so I have to get the parameters
		 */
		TEAStub stub = (TEAStub) aStub;
		VirtualBrokerParams params = (VirtualBrokerParams) XmlIdentifier
				.createFromString(_unparsedParams);

		if (params.listener != null) {
			throw new IllegalStateException();
		}

		/*
		 * here the listener must be null, because it has been nulled by the
		 * constructor, I have to create here a new listener which is a proxy
		 * listener in the stub (server) part.
		 */
		VirtualBrokerStub vStub = new VirtualBrokerStub(stub);

		params.listener = vStub;

		/*
		 * create the normal broker
		 */
		IVirtualBroker aBroker = stub.createVirtualBroker(params);

		/*
		 * encapsulate it. The broker is actually a real object, not a proxy.
		 * This is important because the stub can make in this way some
		 * simplifications.
		 */
		vStub.encapsulateBroker((VirtualBrokerBase) aBroker);

		/*
		 * The answer is the push key of the virtual broker, it will be sent to
		 * the proxy.
		 */
		_answer = vStub.getPushKey();
	}

}

/**
 * The remote proxy part of TEA.
 * 
 * <p>
 * The object simply sends the commands to a TeaStub at the other end of the
 * socket.
 * 
 * <p>
 * This object is a singleton which is used to
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
class TEAProxy extends BaseTEA {

	static class TeaSocketHelper extends BaseSocketHelper {

		protected TeaSocketHelper(SimpleSocketTextClient aClient) {
			super("TEA", aClient);
		}

		@Override
		public void onConnectionEstabilished() {
			//

		}

		@Override
		public void onLostConnection() {
			//

		}

		@Override
		public void onTryingToConnect() {
			//

		}

		@Override
		protected void _handleStatusLine(String[] statuses) {
			//
		}

	}

	/**
	 * This is the only instance. The proxy is a singleton in the application.
	 */
	// private static TEAProxy _instance;

	/**
	 * @param aTeaId
	 * @param aListener
	 * @param host
	 * @param port
	 * @throws TEAException
	 */
	public static ITEA createProxy(String aTeaId, ITEAListener aListener,
			String host, int port) throws TEAException {

		TEAProxy proxy = new TEAProxy(aTeaId, aListener, host, port);
		return proxy;

		// if (_instance == null) {
		// _instance = new TEAProxy(aTeaId, aListener, host, port);
		// }
		// return _instance;
	}

	/**
	 * Maybe this is not useful, just in case. I do not yet know if the proxy
	 * broker should be added in this realm.
	 * 
	 * @param pvb
	 */
	public void addProxyVirtualBroker(ProxyVirtualBroker pvb) {
		/*
		 * Should I add this to the sinks? probably yes, because the server part
		 * will communicate to me about the notifications.
		 */
		_teaSocketHelper.addSink(pvb.getSink());

	}

	// public static TEAProxy getInstance() {
	// return _instance;
	// }

	private String _host;
	private int _port;
	@SuppressWarnings("unused")
	private ITEAListener _listener;

	SimpleSocketTextClient _teaSocket;

	private BaseSocketHelper _teaSocketHelper;
	/**
	 * This flag is used to know whether the proxy has been created or not, in
	 * this case the stop is not issued.
	 */
	private boolean _started = false;

	protected TEAProxy(String aId) {
		super(aId);
	}

	/**
	 * @param aListener
	 * @param host
	 * @param port
	 */
	@SuppressWarnings("boxing")
	public TEAProxy(String aTeaId, ITEAListener aListener, String host, int port) {
		super(aTeaId);

		debug_var(425322, "TEA ", aTeaId, " will try to connect to ", host,
				" port ", port);
		_host = host;
		_port = port;
		_listener = aListener;
	}

	@Override
	public IVirtualBroker createVirtualBroker(VirtualBrokerParams aParams)
			throws TEAException {
		/*
		 * Create the command and send it to the wire.
		 */

		aParams.teaId = _id;

		CreateVirtualBrokerCommand cvbc = new CreateVirtualBrokerCommand(this,
				aParams);

		// _teaSocketListener._sendRequest(cvbc);
		_sendRequest(cvbc);

		return (IVirtualBroker) cvbc.getAnswer();
	}

	/**
	 * Helper method to send the request.
	 * 
	 * <p>
	 * It simply will translate the {@link IOException} to a
	 * {@link TEAException} in case of error.
	 * 
	 * @param aCommand
	 *            the command to send
	 * @throws TEAException
	 *             if something is wrong.
	 */
	void _sendRequest(SimpleRemoteCommand aCommand) throws TEAException {
		try {
			_teaSocketHelper._sendRequest(aCommand);
		} catch (IOException e) {
			throw new TEAException(e);
		}
	}

	@Override
	public void start() throws TEAException {

		_teaSocket = new SimpleSocketTextClient(true);
		_teaSocketHelper = new TeaSocketHelper(_teaSocket);
		_teaSocket.start(_teaSocketHelper, _host, _port);

		if (!_teaSocket.waitForConnect(1000)) {
			throw new TEAException("Cannot connect");
		}

		StartTEACommand stc = new StartTEACommand(this, _id);
		_sendRequest(stc);
		stc.join();
		_started = true;

	}

	@Override
	public synchronized void stop() throws TEAException {
		/*
		 * This will cleanly close the session to TEA.
		 */
		if (_started && _teaSocketHelper != null) {
			StopTEACommand stc = new StopTEACommand(this);
			_sendRequest(stc);
			stc.join();
			_started = false;
		}
		_teaSocket.stop();
		_teaSocketHelper = null;
	}

	// @Override
	// public IInventory getRootStockHolder() {
	// return null;
	// }

	@Override
	public ITEAQuery getQueryTea() {
		return null;
	}

}
