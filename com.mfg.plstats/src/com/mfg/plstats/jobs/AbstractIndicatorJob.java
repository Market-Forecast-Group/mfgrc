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
package com.mfg.plstats.jobs;

import com.mfg.interfaces.symbols.IIndicatorConfiguration;
import com.mfg.plstats.IndicatorManager;
import com.mfg.utils.jobs.MFGJob;

/**
 * @author arian
 * 
 */
public abstract class AbstractIndicatorJob extends MFGJob {
	private final IndicatorManager theManager;
	private final IIndicatorConfiguration configuration;

	public AbstractIndicatorJob(String name,
			IIndicatorConfiguration aConfiguration, IndicatorManager aManager) {
		super(name + " - " + aConfiguration.getName());
		this.theManager = aManager;
		this.configuration = aConfiguration;
	}

	public IndicatorManager getManager() {
		return theManager;
	}

	/**
	 * @return the configuration
	 */
	public IIndicatorConfiguration getConfiguration() {
		return configuration;
	}
}
