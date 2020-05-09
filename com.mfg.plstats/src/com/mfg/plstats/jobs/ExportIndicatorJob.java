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

package com.mfg.plstats.jobs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.mfg.common.CsvSymbol;
import com.mfg.common.DFSException;
import com.mfg.common.MfgSymbol;
import com.mfg.connector.csv.CsvCompositeDataSource;
import com.mfg.dm.TickDataSource;
import com.mfg.interfaces.indicator.IIndicator;
import com.mfg.interfaces.symbols.IIndicatorConfiguration;
import com.mfg.plstats.PLStatsPlugin;
import com.mfg.strategy.automatic.exportIndicator.ExportIndicatorWizard;
import com.mfg.strategy.automatic.exportIndicator.IndicatorExportingConfiguration;
import com.mfg.strategy.automatic.exportIndicator.StrategyExportIndicator;
import com.mfg.utils.jobs.MFGJob;
import com.mfg.widget.probabilities.IIndicatorRunner;
import com.mfg.widget.probabilities.IndicatorRunner;

/**
 * @author gardero
 * 
 */
public class ExportIndicatorJob extends MFGJob implements IndicatorRunner {
	private IIndicatorConfiguration configuration;
	private IIndicator indicator;
	private IProgressMonitor monitor;
	private CsvCompositeDataSource csvds;
	private IStatus status;
	IndicatorExportingConfiguration eConfig;
	IWorkbenchWindow activeWorkbenchWindow;
	WizardDialog dialog;
	ExportIndicatorWizard wizard;

	/**
	 * @param name
	 */
	public ExportIndicatorJob(IIndicatorConfiguration aConfiguration) {
		super("Export Indicator: " + aConfiguration.getName());
		this.configuration = aConfiguration;
		activeWorkbenchWindow = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.plstats.jobs.IIndicatorJob#getConfiguration()
	 */
	public IIndicatorConfiguration getConfiguration() {
		return configuration;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	@Override
	protected IStatus run(final IProgressMonitor monitor1) {
		this.monitor = monitor1;
		monitor1.beginTask("Exporting Indicator", 6);
		status = Status.OK_STATUS;
		try {
			wizard = new ExportIndicatorWizard();
			Runnable runnable = new Runnable() {

				@Override
				public void run() {
					dialog = new WizardDialog(activeWorkbenchWindow.getShell(),
							wizard);
					dialog.create();
					dialog.setTitle("Export Indicator");
					dialog.setMessage("Export Indicator events to a csv");
					if (dialog.open() == Window.OK) {
						eConfig = wizard.getConfiguration();
					} else
						eConfig = null;
				}
			};
			Display.getDefault().syncExec(runnable);
			// runnable.wait();
			// SwingUtilities.invokeAndWait(runnable);
			if (eConfig == null)
				return status = getCancelStatus();
			StrategyExportIndicator s = new StrategyExportIndicator(eConfig);
			buildIndicator();
			s.setIndicator(indicator);
			// s.begin();
			run(s);
			if (!s.isStopped()) {
				// TODO did the job
			}
		} catch (Exception e) {
			e.printStackTrace();
			status = new Status(IStatus.ERROR, PLStatsPlugin.PLUGIN_ID,
					"Export Indicator job error", e);
		}
		if (monitor.isCanceled()) {
			status = getCancelStatus();
		}
		monitor1.done();
		return status;
	}

	private static Status getCancelStatus() {
		return new Status(IStatus.CANCEL, PLStatsPlugin.PLUGIN_ID,
				"Canceled the Indicator export.");
	}

	@Override
	public void run(final IIndicatorRunner aTs) throws DFSException {
		aTs.setIndicator(indicator);
		// create chart recorder
		// Arian: this is a simple modification to use the csv file
		MfgSymbol aSymbol = new CsvSymbol(configuration.getSymbol().getFile()
				.getAbsolutePath());
		csvds = new CsvCompositeDataSource(aSymbol);
		csvds.addTickListener(TickDataSource.CSV_LAYER, aTs);
		csvds.playTicks(monitor);
		if (monitor.isCanceled()) {
			monitor.done();
			aTs.stop();
			status = getCancelStatus();
			return;
		}
		monitor.worked(1);
	}

	@Override
	public void buildIndicator() {
		indicator = PLStatsPlugin.getDefault().getIndicatorManager()
				.getFrozenIndicator(configuration);
	}

	@Override
	public IIndicator getIndicator() {
		return indicator;
	}

}
