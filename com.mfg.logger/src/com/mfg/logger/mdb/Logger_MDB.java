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

import com.mfg.logger.AbstractLogger;
import com.mfg.logger.ILogRecord;
import com.mfg.logger.ILoggerManager;
import com.mfg.logger.mdb.LogMDB.Appender;

/**
 * @author arian
 * 
 */
public class Logger_MDB extends AbstractLogger {
	private final LoggerMDBSession session;
	private final IRecordConverter converter;
	private final LogMDB mdb;
	private final Appender appender;

	/**
	 * @param manager
	 * @param name
	 * @throws IOException
	 */
	public Logger_MDB(ILoggerManager manager, String name,
			LoggerMDBSession aSession, IRecordConverter aConverter)
			throws IOException {
		super(manager, name);
		this.session = aSession;
		this.converter = aConverter;
		this.mdb = aSession.connectTo_LogMDB("log.mdb");
		appender = mdb.appender();
	}

	/**
	 * @return the session
	 */
	public LoggerMDBSession getSession() {
		return session;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.logger.ILogger#close()
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
	 * @see com.mfg.logger.AbstractLogger#addRecord(com.mfg.logger.ILogRecord)
	 */
	@Override
	protected void addRecord(ILogRecord record) {
		converter.fillMDBRecord(record, appender.toRecord());
		try {
			appender.append();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}
