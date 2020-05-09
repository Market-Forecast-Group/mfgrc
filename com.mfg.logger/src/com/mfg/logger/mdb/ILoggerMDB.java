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

import java.io.File;
import java.io.IOException;

/**
 * @author arian
 * 
 */
public interface ILoggerMDB {

	int getOpenCursorCount();

	boolean isAppenderOpen();

	void closeAppender() throws IOException;

	void flushAppender() throws IOException;

	File getFile();

	public boolean deleteFiles();

}
