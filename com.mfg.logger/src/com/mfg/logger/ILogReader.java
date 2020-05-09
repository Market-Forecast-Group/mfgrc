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

package com.mfg.logger;

import java.util.List;

/**
 * This interface represents a log reader. To read the log, log clients do not
 * need to know the nature of the log database to read it, they just have to
 * request a log reader to the log manager.
 * 
 * @see {@link ILoggerManager#getReader()}
 * @author arian
 * 
 */
public interface ILogReader {
	int getRecordCount();

	public List<ILogRecord> read();

	public List<ILogRecord> read(int start, int end);

	public ILogRecord read(int pos);

	/**
	 * Set the filters to the reader. To remove the filters set
	 * <code>null</code>
	 * 
	 * @param filters
	 *            An array of filters. Set <code>null</code> to remove all.
	 */
	public void setFilters(ILogFilter... filters);
}
