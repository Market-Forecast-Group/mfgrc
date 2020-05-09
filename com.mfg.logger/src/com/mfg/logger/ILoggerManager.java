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
 * This interface represents a log manager. The log manager is the root object
 * of the MFG Logging Framework. The main goal of the manager is provide a
 * logger and a log reader. Log implementors are in charge of questions like:
 * <ul>
 * <li>How the log is stored: memory, JSON database, SQL database, etc...</li>
 * <li>How records are dispatched: synchronously, asynchronously.</li>
 * <li>The default log level</li>
 * <li>Some info about the log producer</li>
 * </ul>
 * 
 * @author arian
 * 
 */
public interface ILoggerManager {
	/**
	 * Some info about the log producer.
	 * 
	 * @return
	 */
	public String getManagerName();

	/**
	 * Provide a logger.
	 * 
	 * @return
	 */
	public ILogger createLogger();

	/**
	 * Provide a log reader.
	 * 
	 * @return
	 */
	public ILogReader getReader();

	/**
	 * Close the associated resources.
	 */
	public void close();

	public void addLoggerListener(ILoggerListener listener);

	public void removeLogListener(ILoggerListener listener);

	public void removeLoggerListener(ILoggerListener listener);

	public void fireRecordLogged(ILogger logger, ILogRecord record);

	public void fireLoggerBegin(ILogger logger, String msg);

}
