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
package com.mfg.utils.jobs;

import org.eclipse.core.runtime.jobs.Job;

/**
 * Basic implementation of the {@link IMFGJob}. We recommend to use this class
 * (when possible) instead of the interface {@link IMFGJob}. The only feature
 * implemented here is the {@link #belongsTo(Object)} method. A MFG job always
 * belongs to the {@link IMFGJob} family.
 * 
 * @author arian
 * 
 */
public abstract class MFGJob extends Job implements IMFGJob {
	private boolean _canceledByAppShutdown;

	public MFGJob(String name) {
		super(name);
		_canceledByAppShutdown = false;
	}

	/**
	 * An MFG job always belongs to the <code>{@link IMFGJob}.class</code>
	 * family.
	 */
	@Override
	public boolean belongsTo(Object family) {
		return family == IMFGJob.class || family == getClass();
	}

	public boolean isCanceledByAppShutdown() {
		return _canceledByAppShutdown;
	}

	public void setCanceledByAppShutdown(boolean canceledByAppShutdown) {
		_canceledByAppShutdown = canceledByAppShutdown;
	}

}
