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

import static java.lang.System.out;

import java.util.Date;

/**
 * A log listener that will print the records to {@link System#out}.
 * 
 * @author arian
 * 
 */
public class PrintLoggerListener implements ILoggerListener {

	@Override
	public void logged(ILogger logger, ILogRecord record) {
		long millis = record.getTimeMillis();
		out.println(millis + "ms " + new Date(millis) + ": "
				+ record.getSource() + " [" + record.getLevel().getName()
				+ "] ->  ID(" + record.getID() + ") " + record.getMessage());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.logger.ILoggerListener#begin(com.mfg.logger.ILogger,
	 * java.lang.String)
	 */
	@Override
	public void begin(ILogger logger, String msg) {
		out.println("Begin: " + msg);
	}
}
