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

import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.mfg.interfaces.symbols.IIndicatorConfiguration;
import com.mfg.plstats.PLStatsPlugin;

/**
 * @author arian
 * 
 */
public class LoadIndicatorAction extends AbstractIndicatorAction {
	/**
	 * 
	 */
	public LoadIndicatorAction(IIndicatorConfiguration configuration) {
		super("Load Indicator", configuration);
		setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_UP));
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		try {
			PLStatsPlugin.getDefault().getIndicatorManager().startLoadIndicatorJob(getConfiguration());
		} catch (Exception e) {
			e.printStackTrace();
			setEnabled(true);
		}
	}
}
