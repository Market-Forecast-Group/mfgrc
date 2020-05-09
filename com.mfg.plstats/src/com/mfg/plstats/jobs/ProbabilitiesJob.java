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

import com.mfg.common.CsvSymbol;
import com.mfg.common.DFSException;
import com.mfg.common.MfgSymbol;
import com.mfg.connector.csv.CsvCompositeDataSource;
import com.mfg.dm.TickDataSource;
import com.mfg.interfaces.indicator.IIndicator;
import com.mfg.interfaces.symbols.IIndicatorConfiguration;
import com.mfg.interfaces.trading.Configuration;
import com.mfg.plstats.PLStatsPlugin;
import com.mfg.utils.jobs.MFGJob;
import com.mfg.widget.IndicatorConfiguration;
import com.mfg.widget.WidgetPlugin;
import com.mfg.widget.probabilities.DistributionsContainer;
import com.mfg.widget.probabilities.IIndicatorRunner;
import com.mfg.widget.probabilities.IndicatorRunner;
import com.mfg.widget.probabilities.SimpleTickListener;
import com.mfg.widget.probabilities.SwingsPopulation;

/**
 * @author gardero
 * 
 */
public class ProbabilitiesJob extends MFGJob implements IndicatorRunner {
	private IIndicatorConfiguration configuration;
	private IIndicator indicator;
	private IProgressMonitor monitor;
	private CsvCompositeDataSource csvds;
	private IStatus status;

	/**
	 * @param name
	 */
	public ProbabilitiesJob(IIndicatorConfiguration aConfiguration) {
		super("Probabilities Calculation: " + aConfiguration.getName());
		this.configuration = aConfiguration;
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
		monitor1.beginTask("Creating Probabilities", 6);
		status = Status.OK_STATUS;
		try {
			SwingsPopulation p = new SwingsPopulation();
			Configuration config = configuration.getProbabilitiesSettings();
			p.setConfiguration(config);
			buildIndicator();
			p.begin(indicator);
			SimpleTickListener ts = new SimpleTickListener(p);
			run(ts);
			if (!ts.isStopped()) {
				p.printStats();
				DistributionsContainer dist = new DistributionsContainer(
						p.getPopulation(), config, this);
				if (!monitor.isCanceled()) {
					dist.setIndicatorConfiguration((IndicatorConfiguration) configuration);
					WidgetPlugin.getDefault().getProbabilitiesManager()
							.setDistributionsContainer(dist);
					WidgetPlugin.getDefault().getProbabilitiesManager()
							.getDistributionsStorate().add(configuration, dist);
					configuration.firePropertyChange(null);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			status = new Status(IStatus.ERROR, PLStatsPlugin.PLUGIN_ID,
					"Create probabilities job error", e);
		}
		if (monitor.isCanceled()) {
			status = new Status(IStatus.CANCEL, PLStatsPlugin.PLUGIN_ID,
					"Canceled the probabilities creation.");
		}
		monitor1.done();
		return status;
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
			status = new Status(IStatus.CANCEL, PLStatsPlugin.PLUGIN_ID,
					"Canceled the probabilities creation.");
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
