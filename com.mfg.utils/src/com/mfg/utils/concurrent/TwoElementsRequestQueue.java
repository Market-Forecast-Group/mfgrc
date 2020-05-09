/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 * 
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 * 
 * @version $Revision$: $Date$:
 * $Id$:
 */
package com.mfg.utils.concurrent;

/**
 * @author arian
 * 
 */
public class TwoElementsRequestQueue extends AbstractRequestQueue {
	private Runnable currentRequest;
	private Runnable lastRequest;

	public TwoElementsRequestQueue(String name) {
		super(name);
	}

	@Override
	public void doAddRequest(Runnable request) {
		if (currentRequest == null) {
			currentRequest = request;
			lastRequest = null;
		} else {
			lastRequest = request;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.utils.concurrent.RequestQueue#pickRequest()
	 */
	@Override
	protected void popRequest() {
		if (currentRequest != null) {
			currentRequest.run();
			currentRequest = null;
		}
		if (lastRequest != null) {
			addRequest(lastRequest);
			lastRequest = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.utils.concurrent.RequestQueue#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return currentRequest == null && lastRequest == null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.utils.concurrent.AbstractRequestQueue#clear()
	 */
	@Override
	protected void clear() {
		currentRequest = null;
		lastRequest = null;
	}
}
