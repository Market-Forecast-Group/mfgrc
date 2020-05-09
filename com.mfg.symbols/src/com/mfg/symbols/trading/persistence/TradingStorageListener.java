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
package com.mfg.symbols.trading.persistence;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.progress.WorkbenchJob;

import com.mfg.persist.interfaces.IWorkspaceStorage;
import com.mfg.persist.interfaces.WorkspaceStorageAdapter;
import com.mfg.symbols.SymbolsPlugin;
import com.mfg.symbols.trading.ui.views.ITradingView;

/**
 * @author arian
 * 
 */
public class TradingStorageListener extends WorkspaceStorageAdapter {
	@Override
	public void objectRemoved(IWorkspaceStorage storage, final Object obj) {
		WorkbenchJob job = new WorkbenchJob(
				"Disconnect trading views connected to " + obj) {

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				List<ITradingView> views = SymbolsPlugin.getDefault()
						.getOpenTradingViews();
				monitor.beginTask(getName(), views.size());
				for (ITradingView view : views) {
					Assert.isNotNull(view);
					if (view.getConfiguration() == obj) {
						view.setConfiguration(null);
						monitor.worked(1);
					}
				}
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}
}
