package com.mfg.dfs.data;

import java.util.Collection;

import com.mfg.utils.socket.IPushSource;
import com.mfg.utils.socket.ISimpleClient;

/**
 * A client model is the model of a client which is used to store the
 * information about a client, its requests, the connection time, etc...
 * 
 * <p>
 * This is a class used by the GUI to display the state information in a gui way
 * 
 * <p>
 * This is only a query interface, no modification of the state is possible
 * using this interface
 * 
 * @author Sergio
 * 
 */
public interface IClientStatus extends ISimpleClient {

	/**
	 * returns the login of this client, the name with which the client has been
	 * connected to the server.
	 * 
	 * @return
	 */
	public String getLogin();

	public Collection<IPushSource> getActiveRequests();
}
