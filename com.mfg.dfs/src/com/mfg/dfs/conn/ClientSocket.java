package com.mfg.dfs.conn;

import java.net.Socket;
import java.util.Collection;

import com.mfg.dfs.data.IClientStatus;
import com.mfg.utils.socket.IPushSource;
import com.mfg.utils.socket.SimpleSocketTextServer;
import com.mfg.utils.socket.SimpleTextServerStub;
import com.mfg.utils.socket.SingleClientHandler;

/**
 * This is a class which is used to handle all the communications to a client of
 * dfs.
 * 
 * <p>
 * The DFS client will not see the socket, it will simply talk to a proxy which
 * will connect to this class and will listen to this class.
 * 
 * <p>
 * Nevertheless this is the main bridge between the external world and dfs
 * itself.
 * 
 * <p>
 * The protocol is simple, they are simple text lines. I have decided not to use
 * Json or other complex protocols for ease of operation and debugging.
 * 
 * <p>
 * This is the client as seen from the server side.
 * 
 * <p>
 * The client will get the server from the server factory.
 * 
 * 
 * @author Sergio
 * 
 */
public class ClientSocket extends SingleClientHandler implements IClientStatus {

	private String _login;

	public ClientSocket(SimpleSocketTextServer _server, Socket so)
			throws Exception {
		super(_server, so, "DFS system v.1.0 ready.");

	}

	@Override
	public Collection<IPushSource> getActiveRequests() {
		return _stub.getActiveRequests();
	}

	@Override
	public String getLogin() {
		return _login;
	}

	public void setLogin(String user) {
		_login = user;
		_ss.setModified();
	}

	@Override
	protected SimpleTextServerStub _createStub() {
		return new DfsStub(ServerFactory.giveExistingMultiserver(), this);
	}

}
