package com.mfg.tea.conn;

import java.net.Socket;

import com.mfg.utils.socket.SimpleSocketTextServer;
import com.mfg.utils.socket.SimpleTextServerStub;
import com.mfg.utils.socket.SingleClientHandler;

/**
 * The concrete class which is in charge of handling the connection to a remote
 * tea client.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class TeaClientSocket extends SingleClientHandler {

	/**
	 * @param _server
	 * @param aSocket
	 * @throws Exception
	 */
	public TeaClientSocket(SimpleSocketTextServer _server, Socket aSocket)
			throws Exception {
		super(_server, aSocket, "TEA system, v. 1.0 ready");
	}

	@Override
	protected SimpleTextServerStub _createStub() throws Exception {
		return new TEAStub(TEAFactory.getMultiServer(), this);
	}

}
