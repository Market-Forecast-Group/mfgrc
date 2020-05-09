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

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.mfg.logger.AbstractLoggerManager;
import com.mfg.logger.ILogReader;
import com.mfg.logger.ILogger;

/**
 * @author arian
 * 
 */
public class LoggerManager_MDB extends AbstractLoggerManager {

	private final LoggerMDBSession session;
	private final IRecordConverter converter;

	/**
	 * @param name
	 */
	public LoggerManager_MDB(String name, LoggerMDBSession aSession,
			IRecordConverter aConverter) {
		super(name);
		this.session = aSession;
		this.converter = aConverter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.logger.ILoggerManager#createLogger()
	 */
	@Override
	public ILogger createLogger() {
		try {
			return new Logger_MDB(this, getManagerName(), null, null);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.logger.ILoggerManager#close()
	 */
	@Override
	public void close() {
		try {
			session.close();
		} catch (IOException | TimeoutException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.logger.AbstractLoggerManager#createReader()
	 */
	@Override
	protected ILogReader createReader() {
		try {
			return new LogReader_MDB(session, converter);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}
