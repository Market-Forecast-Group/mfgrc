package com.mfg.common;

/**
 * Exception thrown when 
 * @author Sergio
 *
 */
public class DfsNoConnectionException extends DFSException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8668026546562406258L;

	public DfsNoConnectionException(Exception e) {
		super(e);
	}

	public DfsNoConnectionException(String string) {
		super(string);
	}

}
