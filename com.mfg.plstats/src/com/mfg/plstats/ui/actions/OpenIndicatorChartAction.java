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

package com.mfg.plstats.ui.actions;

import com.mfg.interfaces.symbols.IIndicatorConfiguration;
import com.mfg.plstats.PLStatsPlugin;

/**
 * @author arian
 * 
 */
public class OpenIndicatorChartAction extends AbstractIndicatorAction {

	/**
	 * @param text
	 * @param configuration
	 */
	public OpenIndicatorChartAction(IIndicatorConfiguration configuration) {
		super("Open Chart", configuration);
		setImageDescriptor(PLStatsPlugin
				.getBundledImageDescriptor("icons/trade.jpg"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		PLStatsPlugin.getDefault().getIndicatorManager()
				.openIndicatorChart(getConfiguration(), true);
	}
}
