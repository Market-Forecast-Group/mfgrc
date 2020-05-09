package com.mfg.common;

/**
 * This is the generic exception thrown by DFS.
 * 
 * <p>All synchronous methods can potentially throw this kind of
 * exception.
 * 
 * @author Sergio
 *
 */
public class DFSException extends Exception {

	public DFSException(Exception e) {
		super(e);
	}

	public DFSException(String string) {
		super(string);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 2196588633595301369L;
	//nothing here.
}
