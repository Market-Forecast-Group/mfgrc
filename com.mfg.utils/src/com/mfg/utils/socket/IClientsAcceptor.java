package com.mfg.utils.socket;

/**
 * 
 * @author Sergio
 * 
 */
public interface IClientsAcceptor {

	/**
	 * this method is called when a new client connects to the server.
	 * 
	 * <p>
	 * The client is ready to get the messages. It does already run the message
	 * loop.
	 */
	public void onNewClient(IClientProxy aProxy);

	/**
	 * Processes a line from the client.
	 * 
	 * <p>
	 * The "client" here is not really a <i>client</i>. It is the server's end
	 * point of the client's view of the server itself.
	 * 
	 * @param line
	 */
	public void processLine(String line);

}
