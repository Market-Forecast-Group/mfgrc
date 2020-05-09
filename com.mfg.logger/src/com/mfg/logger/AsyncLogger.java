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

package com.mfg.logger;

/**
 * @author arian
 * 
 */
public class AsyncLogger implements ILogger {
	private final ILogger logger;

	public AsyncLogger(ILogger aLogger) {
		this.logger = aLogger;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.logger.ILogger#getManager()
	 */
	@Override
	public ILoggerManager getManager() {
		return logger.getManager();
	}

	@Override
	public void log(final LogLevel level, final Object msg) {
		logger.log(level, msg);
	}

	@Override
	public void log(final ILogRecord record) {
		logger.log(record);
	}

	@Override
	public String getLoggerName() {
		return logger.getLoggerName();
	}

	@Override
	public LogLevel getLevel() {
		return logger.getLevel();
	}

	@Override
	public void setLevel(LogLevel level) {
		logger.setLevel(level);
	}

	@Override
	public boolean isLoggable(LogLevel level) {
		return logger.isLoggable(level);
	}

	@Override
	public void close() {
		// Adding a comment to avoid empty block warning.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.logger.ILogger#setFilters(com.mfg.logger.ILogFilter[])
	 */
	@Override
	public void setFilters(ILogFilter... filters) {
		logger.setFilters(filters);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.logger.ILogger#getFilters()
	 */
	@Override
	public ILogFilter[] getFilters() {
		return logger.getFilters();
	}

	@Override
	public void begin(String aString) {
		logger.begin(aString);
	}

}
