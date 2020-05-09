/**
 * 
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 * 
 * $Id: $
 *
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 * 
 * @version $Revision: $ $Date: $
 */

package com.mfg.logger;

import java.util.ArrayList;

/**
 * An abstract implementation of a manager.
 * 
 * @author arian
 * 
 */
public abstract class AbstractLoggerManager implements ILoggerManager {

	private final String name;
	protected ILogReader reader;
	private final ArrayList<ILoggerListener> listeners;

	protected abstract ILogReader createReader();

	public AbstractLoggerManager(String aName) {
		this.name = aName;
		listeners = new ArrayList<>();
	}

	@Override
	public String getManagerName() {
		return name;
	}

	@Override
	public ILogReader getReader() {
		if (reader == null) {
			reader = createReader();
		}
		return reader;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.logger.ILoggerManager#addListener(com.mfg.logger.ILoggerListener)
	 */
	@Override
	public void addLoggerListener(ILoggerListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.logger.ILoggerManager#removeLogListener(com.mfg.logger.
	 * ILoggerListener)
	 */
	@Override
	public void removeLogListener(ILoggerListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.logger.ILoggerManager#removeListener(com.mfg.logger.ILoggerListener
	 * )
	 */
	@Override
	public void removeLoggerListener(ILoggerListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}

	@Override
	public void fireRecordLogged(ILogger logger, ILogRecord record) {
		synchronized (listeners) {
			for (ILoggerListener l : listeners) {
				l.logged(logger, record);
			}
		}
	}

	@Override
	public void fireLoggerBegin(ILogger logger, String msg) {
		synchronized (listeners) {
			for (ILoggerListener l : listeners) {
				l.begin(logger, msg);
			}
		}
	}
}
