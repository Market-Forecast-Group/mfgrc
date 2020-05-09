package com.mfg.utils.socket;

/**
 * inteface used to have from the outside notifications of clients connected to
 * the {@linkplain SimpleSocketTextServer}
 * 
 * @author Sergio
 * 
 */
public interface ISimpleClient {

	/**
	 * gets the remote address of this client.
	 * 
	 * @return
	 */
	public String getRemoteIp();

}
