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
package com.mfg.interfaces.indicator;

import com.mfg.interfaces.configurations.IConfiguration;
import com.mfg.interfaces.configurations.IConfigurationInfo;

/**
 * Indicator configuration used for real-time.
 * 
 * @author arian
 * @see IConfiguration
 */
public interface IIndicatorConfiguration2 extends
		IConfiguration<IConfigurationInfo> {
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.interfaces.configurations.IConfiguration#getInfo()
	 */
	@Override
	public IIndicatorConfigurationInfo getInfo();
}
