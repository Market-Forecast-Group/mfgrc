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
package com.mfg.logger.application.ui;

/**
 * @author arian
 * 
 */
public class AppLogMessage implements IAppLogMessage {
	private String message;
	private String component;
	private long mem;

	public AppLogMessage(String aMessage, String aComponent, long aMem) {
		this.message = aMessage;
		this.component = aComponent;
		this.mem = aMem;
	}

	@Override
	public String getMessage() {
		return message;
	}

	public void setMessage(String aMessage) {
		this.message = aMessage;
	}

	@Override
	public String getComponent() {
		return component;
	}

	public void setComponent(String aComponent) {
		this.component = aComponent;
	}

	@Override
	public long getMem() {
		return mem;
	}

	public void setMem(long aMem) {
		this.mem = aMem;
	}
}
