package com.mfg.common;


public class TEAException extends Exception {

	/**
	 * Builds an exception with an error message.
	 * 
	 * @param string
	 */
	public TEAException(String string) {
		super(string);
	}

	public TEAException(Exception e) {
		super(e);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 45246962904578L;

}
