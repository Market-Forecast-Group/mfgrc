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
package com.mfg.logger.mdb;

import com.mfg.logger.ILogRecord;

/**
 * Converter used to serialize the log.
 * 
 * @author arian
 * 
 */
public interface IRecordConverter {
	/**
	 * This gets the data from <code>logRecord</code> and puts it into
	 * <code>mdbRecord</code>.
	 * 
	 * @param logRecord
	 * @param mdbRecord
	 */
	public void fillMDBRecord(ILogRecord logRecord, LogMDB.Record mdbRecord);

	/**
	 * Create a new instance of {@link ILogRecord} with the data of
	 * <code>mdbRecord</code>
	 * 
	 * @param mdbRecord
	 * @return
	 */
	public ILogRecord getLogRecord(LogMDB.Record mdbRecord);
}
