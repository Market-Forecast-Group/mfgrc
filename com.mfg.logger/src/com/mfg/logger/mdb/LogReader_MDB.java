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
import java.util.AbstractList;

import com.mfg.logger.ILogRecord;
import com.mfg.logger.mdb.LogMDB.RandomCursor;
import com.mfg.logger.mdb.LogMDB.Record;
import com.mfg.logger.memory.MemoryLogReader;

/**
 * @author arian
 * 
 */
public class LogReader_MDB extends MemoryLogReader {
	private final LoggerMDBSession session;
	final LogMDB mdb;
	final IRecordConverter converter;

	public LogReader_MDB(LoggerMDBSession aSession, IRecordConverter aConverter)
			throws IOException {
		this.session = aSession;
		this.converter = aConverter;
		mdb = aSession.connectTo_LogMDB("log.mdb");
		setMemory(new AbstractList<ILogRecord>() {

			@Override
			public ILogRecord get(int index) {
				try {
					RandomCursor c = mdb.thread_randomCursor();
					Record r1 = mdb.record(c, index);
					ILogRecord r2 = LogReader_MDB.this.converter
							.getLogRecord(r1);
					return r2;
				} catch (IOException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}

			@Override
			public int size() {
				try {
					return (int) mdb.size();
				} catch (IOException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
		});
	}

	public LoggerMDBSession getSession() {
		return session;
	}

}
