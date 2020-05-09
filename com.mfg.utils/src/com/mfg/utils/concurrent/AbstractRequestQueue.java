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
public abstract class AbstractRequestQueue implements IRequestQueue {
	private Thread thread;
	protected Object mutex = new Object();
	boolean closed;
	private String name;

	public AbstractRequestQueue(String aName) {
		super();
		this.name = aName;
		restart();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.utils.concurrent.IRequestQueue#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.utils.concurrent.IRequestQueue#start()
	 */
	@Override
	public void restart() {
		closed = false;
		thread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (!closed) {
					synchronized (mutex) {
						if (isEmpty()) {
							try {
								mutex.wait();
							} catch (InterruptedException e) {
								// Adding a comment to avoid empty block
								// warning.
							}
						}
					}
					popRequest();
				}
				// process the rest of the queue
				while (!isEmpty()) {
					popRequest();
				}
			}
		}, getName());
		thread.start();
	}

	public abstract boolean isEmpty();

	/**
	 * @return the thread
	 */
	public Thread getThread() {
		return thread;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.utils.concurrent.IRequestQueue#addRequest(java.lang.Runnable)
	 */
	@Override
	public void addRequest(Runnable request) {
		doAddRequest(request);
		synchronized (mutex) {
			mutex.notifyAll();
		}
	}

	/**
	 * @param request
	 */
	protected abstract void doAddRequest(Runnable request);

	/**
	 * @return the closed
	 */
	@Override
	public boolean isClosed() {
		return closed;
	}

	@Override
	public void close() {
		if (!closed) {
			closed = true;

			synchronized (mutex) {
				mutex.notifyAll();
			}

			// XXX: Is this fine?
			thread.interrupt();
		}

		clear();
	}

	protected abstract void clear();

	protected abstract void popRequest();
}
