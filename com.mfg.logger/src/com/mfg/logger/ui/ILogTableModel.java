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

import com.mfg.logger.ILogRecord;

public interface ILogTableModel {

	public String[] getColumnNames();


	public Object[] recordToArray(ILogRecord record);


	public ILogRecord getRecord(int index);


	public int getRecordCount();
}
