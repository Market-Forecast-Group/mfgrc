package com.mfg.utils.socket;

import java.net.SocketException;

public interface ICommandFactory {

	/**
	 * Creates a remote command used to perform an operation on server space.
	 * 
	 * @param line
	 * @return
	 * @throws SocketException
	 *             if there is something wrong in the line
	 */
	public SimpleRemoteCommand createCommand(String line)
			throws SocketException;

}
