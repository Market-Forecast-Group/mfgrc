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
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

import com.mfg.interfaces.symbols.IIndicatorConfiguration;
import com.mfg.persist.interfaces.IWorkspaceStorage;
import com.mfg.persist.interfaces.WorkspaceStorageAdapter;
import com.mfg.plstats.PLStatsPlugin;
import com.mfg.plstats.ProbabilitiesPerpective;
import com.mfg.utils.ObjectListener;
import com.mfg.widget.WidgetPlugin;
import com.mfg.widget.probabilities.DistributionsContainer;
import com.mfg.widget.probabilities.ProbabilityElement;

/**
 * @author gardero
 * 
 */
public class CreateProbabilitiesAction extends AbstractIndicatorAction {
	public CreateProbabilitiesAction(IIndicatorConfiguration configuration) {
		super("Calculate Probabilities", configuration);
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
		setText("Calculate Probabilities" + getAditionalText());
		setImageDescriptor(getIconMode());
	}

	public String getAditionalText() {
		return exists() ? " (Update)" : "";
	}

	public void updateEnabled() {
		setEnabled(null != PLStatsPlugin.getDefault().getIndicatorManager()
				.getFrozenIndicator(getConfiguration()));
	}

	public ImageDescriptor getIconMode() {
		return exists() ? PLStatsPlugin
				.getBundledImageDescriptor("icons/dice.ico") : PLStatsPlugin
				.getBundledImageDescriptor("icons/dice.ico");
	}

	private boolean exists() {
		List<ProbabilityElement> d = WidgetPlugin.getDefault()
				.getProbabilitiesManager().getDistributionsStorate()
				.getDistributions(getConfiguration());
		if (d == null || d.size() == 0)
			return false;
		for (ProbabilityElement probabilityElement : d) {
			if (getConfiguration().getProbabilitiesSettings().equals(
					probabilityElement.getProbabilityConfiguration()))
				return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		try {
			PLStatsPlugin.getDefault().getIndicatorManager()
					.startCreateProbabilitiesJob(getConfiguration());
		} catch (Exception e) {
			e.printStackTrace();
			setEnabled(true);
		}
	}

	public static void whendone() {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				// open the perspective
				try {
					PlatformUI.getWorkbench().showPerspective(
							ProbabilitiesPerpective.ID,
							PlatformUI.getWorkbench()
									.getActiveWorkbenchWindow());
				} catch (WorkbenchException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
