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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

import com.mfg.interfaces.symbols.IIndicatorConfiguration;
import com.mfg.persist.interfaces.PersistInterfacesPlugin;
import com.mfg.plstats.PLStatsPlugin;

/**
 * @author arian
 * 
 */
public class CreateIndicatorAction extends AbstractIndicatorAction {
	/**
	 * 
	 */
	public CreateIndicatorAction(IIndicatorConfiguration configuration) {
		super("Create Indicator", configuration);
		setImageDescriptor(PLStatsPlugin
				.getBundledImageDescriptor("icons/run_indicator.gif"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {

		if (PersistInterfacesPlugin.getDefault().isWorkspaceDirty()) {
			if (MessageDialog
					.openConfirm(
							PlatformUI.getWorkbench()
									.getActiveWorkbenchWindow().getShell(),
							"Create Indicator",
							"The Workspace changes will be saved before create the Indicator. Do you want to continue?")) {
				PersistInterfacesPlugin.getDefault().saveWorkspace();
			} else {
				return;
			}
		}

		try {
			PLStatsPlugin.getDefault().getIndicatorManager()
					.startCreateIndicatorJob(getConfiguration());
		} catch (Exception e) {
			e.printStackTrace();
			setEnabled(true);
		}
	}
}
