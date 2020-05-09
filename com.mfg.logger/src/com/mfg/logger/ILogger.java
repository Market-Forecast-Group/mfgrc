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
 * This interface represents a logger. The logger is the object used by process
 * to emit the log records. Logger implementors must to be in charge of:
 * <ul>
 * <li>Check if the record can be logged in dependence of the logging level</li>
 * <li>Store the record</li>
 * <li>Set the record time and ID</li>
 * <li>Notify to listeners</li>
 * <li>Provide some info about the log producer</li>
 * <li></li>
 * </ul>
 * 
 * @author arian
 * 
 */
public interface ILogger {
	/**
	 * The logger name provide some info about the log producer. By default this
	 * name is inherited from the log manager.
	 * 
	 * @see {@link ILoggerManager#getManagerName()}
	 * @return
	 */
	public String getLoggerName();

	public ILoggerManager getManager();

	/**
	 * If the level is loggable a new record is created and logged. Implementors
	 * must to set the record ID and time, and notify to listeners.
	 * 
	 * @param level
	 * @param msg
	 */
	public void log(LogLevel level, Object msg);

	/**
	 * If the record level is loggable the record logged. Implementors must to
	 * update the record ID and time, and notify to listeners.
	 * 
	 * @param level
	 * @param msg
	 */
	public void log(ILogRecord record);

	/**
	 * The logging level.
	 * 
	 * @return
	 */
	public LogLevel getLevel();

	/**
	 * Set the logging level.
	 * 
	 * @param level
	 */
	public void setLevel(LogLevel level);

	/**
	 * If the level can be logged.
	 * 
	 * @param level
	 * @return
	 */
	public boolean isLoggable(LogLevel level);

	/**
	 * Close the resources associated to the appender. This method must to be
	 * used only by {@link ILoggerManager} implementors.
	 */
	public void close();

	public void begin(String msg);

	public void setFilters(ILogFilter... filters);

	public ILogFilter[] getFilters();
}
