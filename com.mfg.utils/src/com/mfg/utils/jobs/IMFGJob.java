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
 * Common protocol for all the MFG jobs. Also used to differentiate the MFG jobs
 * with the Eclipse's jobs. Implementers should, if possible, extends the base
 * class {@link MFGJob} or implement the method
 * {@link Job#belongsTo(Object)} in order to return <code>true</code> if the
 * family is equal to <code>IMFGJob.class</code>. See the
 * {@link MFGJob#belongsTo(Object)} as example.
 * 
 * @author arian
 * 
 */
public interface IMFGJob {
	//Adding a comment to avoid empty block warning.
}
