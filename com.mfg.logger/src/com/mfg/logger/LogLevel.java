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

public class LogLevel {

	public static final LogLevel OFF = new LogLevel(0, "Off");
	public static final LogLevel ANY = new LogLevel(Float.MAX_VALUE, "Any");

	private float priority;
	private String name;

	public LogLevel(float aPriority, String aName) {
		this.priority = aPriority;
		this.name = aName;
		if (LoggerPlugin.getDefault().getLogRecordConverter() != null) {
			LoggerPlugin.getDefault().getLogRecordConverter().addLogLevel(this);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String aName) {
		this.name = aName;
	}

	public float getPriority() {
		return priority;
	}

	public void setPriority(int aPriority) {
		this.priority = aPriority;
	}

}
