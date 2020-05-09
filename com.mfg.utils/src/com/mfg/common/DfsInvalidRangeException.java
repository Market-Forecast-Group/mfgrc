package com.mfg.common;

public class DfsInvalidRangeException extends DFSException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3422400910747352010L;

	public DfsInvalidRangeException(Exception e) {
		super(e);
		
	}

	public DfsInvalidRangeException(String string) {
		super(string);
	}

}
