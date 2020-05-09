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

package com.mfg.logger.ui;

import com.mfg.logger.ILogReader;
import com.mfg.logger.ILogRecord;

public class LogTableModel extends AbstractLogTableModel {
	protected static String[] COLUMN_NAMES = { "Time", "ID", "Level", "Source",
			"Message" };

	public LogTableModel(ILogReader reader) {
		super(reader);
	}

	@Override
	public String[] getColumnNames() {
		return COLUMN_NAMES;
	}

	@Override
	public Object[] recordToArray(ILogRecord record) {
		return new Object[] { Long.valueOf(record.getTimeMillis()),
				Integer.valueOf(record.getID()), record.getLevel().getName(),
				record.getSource(), record.getMessage() };
	}
}
