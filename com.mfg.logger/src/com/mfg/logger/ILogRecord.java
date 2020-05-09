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
 * Represent a log record.
 * 
 * @author arian
 * 
 */
public interface ILogRecord {
	/**
	 * Level of the record. It will be used to check if the record can be logged.
	 * 
	 * @return
	 */
	public LogLevel getLevel();


	/**
	 * Get the time (ms) when the record is logged. The logger is in charge of set this time.
	 * 
	 * @return
	 */
	public long getTimeMillis();


	/**
	 * The record message. The message contains the detailed information about the record.
	 * 
	 * @return
	 */
	public Object getMessage();


	/**
	 * The name of the log producer. Possibly this name was inherited from the logger or the manager.
	 * 
	 * @return
	 */
	public String getSource();


	/**
	 * A simple ID that will differentiate the records in a logger scope. It means, the logger is in charge to set the ID of a record.
	 * 
	 * @return
	 */
	public int getID();


	/**
	 * Set the record ID. The logger implementors are in charge of this, logger clients do not need to do it.
	 * 
	 * @param id
	 */
	public void setID(int id);
}
