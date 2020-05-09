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

package com.mfg.logger.memory;

import java.util.List;

import com.mfg.logger.AbstractLogger;
import com.mfg.logger.ILogRecord;
import com.mfg.logger.ILoggerManager;

public class MemoryLogger extends AbstractLogger {
	private List<ILogRecord> memory;

	public MemoryLogger(ILoggerManager manager, String name,
			List<ILogRecord> aMemory) {
		super(manager, name);
		this.memory = aMemory;
	}

	@Override
	protected void addRecord(ILogRecord record) {
		memory.add(record);
	}

	@Override
	public void close() {
		memory = null;
	}

	public List<ILogRecord> getMemory() {
		return memory;
	}

	public void setMemory(List<ILogRecord> aMemory) {
		memory = aMemory;
	}

}
