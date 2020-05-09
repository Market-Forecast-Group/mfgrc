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
/**
 * 
 */
package com.mfg.chart.model.mdb.recorders;

import java.io.File;
import java.io.IOException;
import java.nio.BufferOverflowException;

import com.mfg.inputdb.prices.CommonMDBSession;

/**
 * @author arian
 * 
 */
public abstract class AbstractChartRecorder {
	protected final CommonMDBSession _session;

	public AbstractChartRecorder(CommonMDBSession session1) {
		this._session = session1;
	}

	/**
	 * @return the rootDir
	 */
	public File getRootDir() {
		return _session.getRoot();
	}

	/**
	 * @return the session
	 */
	public CommonMDBSession getSession() {
		return _session;
	}

	protected static void handleAppenderException(BufferOverflowException e) {
		handleAppenderException((Exception) e);
	}

	protected static void handleAppenderException(IOException e) {
		handleAppenderException((Exception) e);
	}

	private static void handleAppenderException(Exception e) {
		e.printStackTrace();
		// err.println("It is trying to write in a closed session: " + e);
	}

	/**
	 * @throws IOException
	 */
	public void close() throws IOException {
		_session.flush();
	}
}
