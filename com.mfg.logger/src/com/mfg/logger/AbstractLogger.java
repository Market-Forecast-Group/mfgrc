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

/**
 * An abstract implementation of the logger.
 * 
 * @author arian
 * 
 */
public abstract class AbstractLogger implements ILogger {
	public static int idCounter = 0;
	private final ILoggerManager manager;
	private final String name;
	private final LogLevel level;
	protected float priority;
	private String source;
	private ILogFilter[] filters;

	public AbstractLogger(ILoggerManager aManager, String aName) {
		this.manager = aManager;
		this.name = aName;
		this.source = aName;
		this.level = LogLevel.ANY;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.logger.ILogger#getFilters()
	 */
	@Override
	public ILogFilter[] getFilters() {
		return filters;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.logger.ILogger#setFilters(com.mfg.logger.ILogFilter[])
	 */
	@Override
	public void setFilters(ILogFilter... aFilters) {
		this.filters = aFilters;
	}

	@Override
	public ILoggerManager getManager() {
		return manager;
	}

	/**
	 * @param record
	 */
	protected void fireRecordLogged(ILogRecord record) {
		manager.fireRecordLogged(this, record);
	}

	@Override
	public void log(ILogRecord record) {
		if (filters != null) {
			for (ILogFilter filter : filters) {
				if (!filter.accept(record)) {
					return;
				}
			}
		}

		float recordPriority = record.getLevel().getPriority();

		if (recordPriority <= priority) {
			record.setID(idCounter++);
			addRecord(record);
			fireRecordLogged(record);
		}
	}

	@Override
	public void log(LogLevel aLevel, Object msg) {
		float recordPriority = aLevel.getPriority();

		if (recordPriority <= priority) {
			return;
		}

		LogRecord record = new LogRecord(idCounter++, aLevel,
				System.currentTimeMillis(), getSource(), msg);

		if (filters != null) {
			for (ILogFilter filter : filters) {
				if (!filter.accept(record)) {
					return;
				}
			}
		}

		addRecord(record);
		fireRecordLogged(record);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.logger.ILogger#begin(java.lang.String)
	 */
	@Override
	public void begin(String msg) {
		manager.fireLoggerBegin(this, msg);
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @param aSource
	 *            the source to set
	 */
	public void setSource(String aSource) {
		this.source = aSource;
	}

	@Override
	public String getLoggerName() {
		return name;
	}

	@Override
	public LogLevel getLevel() {
		return level;
	}

	@Override
	public void setLevel(LogLevel aLevel) {
		priority = aLevel.getPriority();
	}

	@Override
	public boolean isLoggable(LogLevel aLevel) {
		float levelPriority = aLevel.getPriority();
		return levelPriority <= priority;
	}

	protected abstract void addRecord(ILogRecord record);

}
