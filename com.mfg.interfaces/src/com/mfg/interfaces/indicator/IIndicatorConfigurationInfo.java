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

import com.mfg.interfaces.configurations.IConfigurationInfo;
import com.mfg.interfaces.symbols.AbstractIndicatorParamBean;
import com.mfg.interfaces.trading.Configuration;

/**
 * Indicator configuration info.
 * 
 * @author arian
 * @see IConfigurationInfo
 */
public interface IIndicatorConfigurationInfo extends IConfigurationInfo {
	public final static String PROP_PROBABILITY_SETTINGS = "probabilitiesSettings";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.interfaces.configurations.IConfigurationInfo#getConfiguration()
	 */
	@Override
	public IIndicatorConfiguration2 getConfiguration();

	public AbstractIndicatorParamBean getIndicatorParamBean();

	public Configuration getProbabilitiesSettings();

	public void setProbabilitiesSettings(Configuration aConfiguration);
}
