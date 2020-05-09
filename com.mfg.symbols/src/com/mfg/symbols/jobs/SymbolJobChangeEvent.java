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
package com.mfg.symbols.jobs;

/**
 * @author arian
 * 
 */
public class SymbolJobChangeEvent {
	private final SymbolJob<?> job;

	public SymbolJobChangeEvent(SymbolJob<?> aJob) {
		super();
		this.job = aJob;
	}

	/**
	 * @return the job
	 */
	public SymbolJob<?> getJob() {
		return job;
	}

}
