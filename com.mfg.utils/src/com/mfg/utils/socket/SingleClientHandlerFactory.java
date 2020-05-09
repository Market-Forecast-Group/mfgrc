package com.mfg.utils.socket;

import java.io.IOException;
import java.net.Socket;

import com.mfg.common.DFSException;

/**
 * The factory which is used to create the handlers when a new client opens the
 * communication to the {@linkplain SimpleSocketTextServer}
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public abstract class SingleClientHandlerFactory {

	protected SimpleSocketTextServer _server = null;

	/**
	 * Creates a handler for the socket.
	 * 
	 * <p>
	 * The socket is assumed to have been accepted by a
	 * {@linkplain SimpleSocketTextServer}
	 * 
	 * @param aSocket
	 * @return
	 * @throws DFSException
	 * @throws IOException
	 * @throws Exception
	 */
	public abstract SingleClientHandler createNewHandler(Socket aSocket)
			throws Exception;

	/**
	 * Sets the server.
	 * 
	 * <p>
	 * All the handler
	 * 
	 * @param aServer
	 */
	public void setServer(SimpleSocketTextServer aServer) {
		_server = aServer;
	}

}
