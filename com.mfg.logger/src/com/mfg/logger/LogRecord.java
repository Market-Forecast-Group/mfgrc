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

/**
 * Default implementation of a {@link ILogRecord}.
 * 
 * @author arian
 * 
 */
public class LogRecord implements ILogRecord {

	private LogLevel level;
	private long millis;
	private Object message;
	private String source;
	private int id;

	public LogRecord(int aId, LogLevel aLevel, long aMillis, String aSource,
			Object aMessage) {
		this.level = aLevel;
		this.millis = aMillis;
		this.message = aMessage;
		this.source = aSource;
		this.id = aId;
	}

	@Override
	public LogLevel getLevel() {
		return level;
	}

	public void setLevel(LogLevel aLevel) {
		this.level = aLevel;
	}

	@Override
	public long getTimeMillis() {
		return millis;
	}

	public void setMillis(long aMillis) {
		this.millis = aMillis;
	}

	@Override
	public Object getMessage() {
		return message;
	}

	public void setMessage(Object aMessage) {
		this.message = aMessage;
	}

	@Override
	public String getSource() {
		return source;
	}

	public void setSource(String aSource) {
		this.source = aSource;
	}

	@Override
	public int getID() {
		return id;
	}

	@Override
	public void setID(int aId) {
		this.id = aId;
	}

	@Override
	public String toString() {
		return "" + message.toString() + System.getProperty("line.separator");
	}

}
