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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.mfg.interfaces.symbols.IIndicatorConfiguration;
import com.mfg.plstats.IndicatorManager;
import com.mfg.plstats.PLStatsPlugin;

/**
 * @author gardero
 * 
 */
public class CreateIndexAction extends AbstractIndicatorAction {
	private IWorkbench workbench;
	private IWorkbenchWindow window;

	/**
	 * 
	 */
	public CreateIndexAction(IIndicatorConfiguration configuration) {
		super("Calculate Indexing", configuration);
		updateEnabled();
		updateText();
		setImageDescriptor(getIconMode());
	}

	public void updateText() {
		setText("Calculate Indexing" + getAditionalText());
		setImageDescriptor(getIconMode());
	}

	public static String getAditionalText() {
		return exists() ? " (Update)" : "";
	}

	public static ImageDescriptor getIconMode() {
		return exists()
				? PLStatsPlugin
						.getBundledImageDescriptor("icons/IndexIco2.png")
				: PLStatsPlugin
						.getBundledImageDescriptor("icons/IndexIco2.png");
	}

	private static boolean exists() {
		return false;
	}

	public void updateEnabled() {
		setEnabled(null != PLStatsPlugin.getDefault().getIndicatorManager()
				.getFrozenIndicator(getConfiguration()));
	}

	@Override
	public void run() {
		workbench = PlatformUI.getWorkbench();
		window = workbench.getActiveWorkbenchWindow();
		try {
			PLStatsPlugin.getDefault().getIndicatorManager();
			IndicatorManager
					.startCreateIndexJob(getConfiguration());
		} catch (Exception e) {
			e.printStackTrace();
			setEnabled(true);
		}
	}

	public void whendone() {
		window.getShell().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				// TODO open the Index perspective
				// try {
				// workbench.showPerspective(ProbabilitiesPerpective.ID,
				// window);
				// } catch (WorkbenchException e) {
				// e.printStackTrace();
				// }
			}
		});
	}
}
