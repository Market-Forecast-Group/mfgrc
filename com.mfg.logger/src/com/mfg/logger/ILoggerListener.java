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
 * Interface that represents a log listener. Log listeners are notified every
 * time a record is logged. In this way, log implementors can do any kind of
 * tasks associated to log like print the log, update a log GUI, show a progress
 * bar, etc...
 * 
 * @author arian
 * 
 */
public interface ILoggerListener {
	/**
	 * This method is called every time a record is logged
	 * 
	 * @param logger
	 *            The logger who logs the record
	 * @param record
	 *            The record logged
	 */
	public void logged(ILogger logger, ILogRecord record);

	public void begin(ILogger logger, String msg);
}
