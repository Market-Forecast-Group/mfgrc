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
package com.mfg.logger.application;

import com.mfg.logger.memory.MemoryLoggerManager;

/**
 * @author arian
 * 
 */
public class AppLoggerManager extends MemoryLoggerManager {

	/**
	 * @param name
	 * @param async
	 */
	public AppLoggerManager(String name) {
		super(name, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.logger.AbstractLoggerManager#getLogger()
	 */
	@Override
	public AppLogger createLogger() {
		return new AppLogger(this, memory);
	}

}
