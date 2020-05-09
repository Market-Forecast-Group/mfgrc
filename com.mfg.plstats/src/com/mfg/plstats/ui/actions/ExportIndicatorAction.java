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

import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import com.mfg.interfaces.symbols.IIndicatorConfiguration;
import com.mfg.persist.interfaces.IWorkspaceStorage;
import com.mfg.persist.interfaces.WorkspaceStorageAdapter;
import com.mfg.plstats.PLStatsPlugin;
import com.mfg.utils.ObjectListener;
import com.mfg.widget.WidgetPlugin;
import com.mfg.widget.probabilities.DistributionsContainer;

/**
 * @author gardero
 * 
 */
public class ExportIndicatorAction extends AbstractIndicatorAction {
	public ExportIndicatorAction(IIndicatorConfiguration configuration) {
		super("Export Indicator", configuration);
		updateText();
		updateEnabled();
		setImageDescriptor(getIconMode());
		WidgetPlugin
				.getDefault()
				.getProbabilitiesManager()
				.getDistributionsContainerSelection()
				.addObjectListener(
						new ObjectListener<DistributionsContainer>() {

							@Override
							public void handle(DistributionsContainer aObject) {
								updateText();
							}
						});
		WidgetPlugin.getDefault().getProbabilitiesManager()
				.getDistributionsStorate()
				.addStorageListener(new WorkspaceStorageAdapter() {
					@Override
					public void objectRemoved(IWorkspaceStorage aStorage,
							Object aObj) {
						updateText();
					}

					@Override
					public void listRemoved(IWorkspaceStorage aStorage,
							List<? extends Object> aList) {
						updateText();
					}
				});
	}

	public void updateText() {
		setText("Export Indicator");
		setImageDescriptor(getIconMode());
	}

	public void updateEnabled() {
		setEnabled(null != PLStatsPlugin.getDefault().getIndicatorManager()
				.getFrozenIndicator(getConfiguration()));
	}

	public static ImageDescriptor getIconMode() {
		return PLStatsPlugin
				.getBundledImageDescriptor("icons/export.gif");
	}

//	private boolean exists() {
//		List<ProbabilityElement> d = WidgetPlugin.getDefault()
//				.getProbabilitiesManager().getDistributionsStorate()
//				.getDistributions(getConfiguration());
//		if (d == null || d.size() == 0)
//			return false;
//		for (ProbabilityElement probabilityElement : d) {
//			if (getConfiguration().getProbabilitiesSettings().equals(
//					probabilityElement.getProbabilityConfiguration()))
//				return true;
//		}
//		return false;
//	}
//
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		try {
			PLStatsPlugin.getDefault().getIndicatorManager()
					.startExportIndicatorJob(getConfiguration());
		} catch (Exception e) {
			e.printStackTrace();
			setEnabled(true);
		}
	}

	public void whendone() {
//		Display.getDefault().asyncExec(new Runnable() {
//			@Override
//			public void run() {
//				// open the perspective
//				try {
//					PlatformUI.getWorkbench().showPerspective(
//							ProbabilitiesPerpective.ID,
//							PlatformUI.getWorkbench()
//									.getActiveWorkbenchWindow());
//				} catch (WorkbenchException e) {
//					e.printStackTrace();
//				}
//			}
//		});
	}
}
