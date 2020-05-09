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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mfg.logger.AbstractLoggerManager;
import com.mfg.logger.AsyncLogger;
import com.mfg.logger.ILogReader;
import com.mfg.logger.ILogRecord;
import com.mfg.logger.ILogger;

public class MemoryLoggerManager extends AbstractLoggerManager {

	protected List<ILogRecord> memory;
	private MemoryLogger memlogger;
	private MemoryLogReader theReader;

	/**
	 * @param async
	 *            Maybe used on inner classes.
	 */
	public MemoryLoggerManager(String name, boolean async) {
		super(name);
		memory = Collections.synchronizedList(new ArrayList<ILogRecord>());
	}

	@Override
	public ILogger createLogger() {
		return new AsyncLogger(memlogger = new MemoryLogger(this,
				getManagerName(), memory));
	}

	@Override
	protected ILogReader createReader() {
		return theReader = new MemoryLogReader(memory);
	}

	@Override
	public void close() {
		// Adding a comment to avoid empty block warning.
	}

	public void changeMemory(List<ILogRecord> newLog) {
		memory = newLog;
		if (theReader != null)
			theReader.setMemory(memory);
		if (memlogger != null)
			memlogger.setMemory(memory);
	}
}
