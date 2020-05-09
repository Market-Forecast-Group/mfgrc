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
package com.mfg.logger.application.ui;

import com.mfg.logger.ILogReader;
import com.mfg.logger.ILogRecord;
import com.mfg.logger.ui.AbstractLogTableModel;

/**
 * @author arian
 * 
 */
public class AppLogTableModel extends AbstractLogTableModel {

	private static final String[] COLUMN_NAMES = { "Level", "Time", "Message",
			"Source", "Component", "Mem" };

	/**
	 * @param reader
	 */
	public AppLogTableModel(ILogReader reader) {
		super(reader);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.logger.ui.ILogTableModel#getColumnNames()
	 */
	@Override
	public String[] getColumnNames() {
		return COLUMN_NAMES;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.logger.ui.ILogTableModel#recordToArray(com.mfg.logger.ILogRecord)
	 */
	@Override
	public Object[] recordToArray(ILogRecord record) {
		IAppLogMessage msg = (IAppLogMessage) record.getMessage();
		return new Object[] { record.getLevel(),
				String.format("%tT", Long.valueOf(record.getTimeMillis())),
				msg.getMessage(), record.getSource(), msg.getComponent(),
				Long.valueOf(msg.getMem()) };
	}

}
