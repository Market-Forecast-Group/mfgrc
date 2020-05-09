package com.mfg.utils.socket;

import com.mfg.common.DFSException;

/**
 * This is the simple interface which is the base for all the push sources in
 * the system.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public interface IPushSource {

	/**
	 * returns the push key of this push source. It is a read only string which
	 * is used to identify the push source towards the client.
	 * 
	 * @return the push key. It could be the empty string, but it cannot be
	 *         null.
	 */
	public String getPushKey();

	/**
	 * asks this push source to interrupt the request.
	 * 
	 * @throws DFSException
	 */
	public void interruptRequest();

}
