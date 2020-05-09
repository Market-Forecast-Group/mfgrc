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
package com.mfg.symbols.trading.ui.views;

import org.eclipse.ui.IViewPart;

import com.mfg.symbols.trading.configurations.TradingConfiguration;

/**
 * @author arian
 * 
 */
public interface ITradingView {
	public TradingConfiguration getConfiguration();

	public void setConfiguration(TradingConfiguration configuration);

	// TODO: We really dont need this method. We can say that a view without a
	// configuration does not have a configuration set. Then, with the
	// configuration.info.configurationSet path is enough (see the chart case).
	// But let's wait for Giulio to play a little with it.
	public int getConfigurationSet();

	public void setConfigurationSet(int configurationSet);

	public IViewPart getPart();
}
