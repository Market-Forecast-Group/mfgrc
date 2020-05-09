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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.mfg.common.CsvSymbol;
import com.mfg.common.MfgSymbol;
import com.mfg.common.QueueTick;
import com.mfg.connector.csv.CsvCompositeDataSource;
import com.mfg.dm.TickAdapter;
import com.mfg.dm.TickDataSource;
import com.mfg.interfaces.symbols.IIndicatorConfiguration;
import com.mfg.plstats.IndicatorManager;
import com.mfg.plstats.PLStatsPlugin;
import com.mfg.plstats.SessionInfo;
import com.mfg.plstats.charts.IndicatorChartView;
import com.mfg.utils.io.IO;
import com.mfg.widget.WidgetPlugin;
import com.mfg.widget.arc.gui.IndicatorParamBean;
import com.mfg.widget.arc.strategy.ChannelIndicator;
import com.mfg.widget.arc.strategy.LayeredIndicator;
import com.mfg.widget.arc.strategy.MultiscaleIndicator;
import com.mfg.widget.recorders.AdvancedIndicatorRecorder;
import com.mfg.widget.recorders.PriceRecorder;

/**
 * @author arian
 * 
 */
public class CreateIndicatorJob extends AbstractIndicatorJob {

	SessionInfo sessionInfo;

	private AdvancedIndicatorRecorder indicatorRecorder;

	private PriceRecorder priceRecorder;

	ChannelIndicator indicator;

	/**
	 * @param aManager
	 * @param name
	 */
	public CreateIndicatorJob(IIndicatorConfiguration configuration,
			IndicatorManager aManager) {
		super("Create Indicator", configuration, aManager);
		try {
			// create chart recorder
			sessionInfo = getManager().getCreatedMDBSession(getConfiguration());
			if (sessionInfo != null) {
				sessionInfo.getPriceSession().closeAndDelete();
				sessionInfo.getIndicatorSession().closeAndDelete();
			}

			File rootDir = getDatabaseDir();
			IO.deleteFile(rootDir);
			rootDir.mkdirs();

			sessionInfo = getManager().createNewSession(getConfiguration(),
					rootDir);
		} catch (IOException | TimeoutException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		IStatus status = Status.OK_STATUS;
		// create indicator
		indicator = WidgetPlugin.getIndicator(
				(IndicatorParamBean) getConfiguration().getIndicatorSettings(),
				sessionInfo.getPriceSession());
		try {
			priceRecorder = new PriceRecorder(sessionInfo.getPriceSession());
			LayeredIndicator auxIndicator = new LayeredIndicator(
					sessionInfo.getIndicatorSession());
			auxIndicator.addLayer((MultiscaleIndicator) indicator);
			indicatorRecorder = new AdvancedIndicatorRecorder(auxIndicator,
					sessionInfo.getIndicatorSession(), 0);

			// read csv
			MfgSymbol aSymbol = new CsvSymbol(getConfiguration().getSymbol()
					.getFile().getAbsolutePath());

			// Arian: this is a simple modification to use the csv file
			final CsvCompositeDataSource csvds = new CsvCompositeDataSource(
					aSymbol);

			monitor.beginTask("Giving ticks", IProgressMonitor.UNKNOWN);

			csvds.addTickListener(TickDataSource.CSV_LAYER, new TickAdapter() {

				@Override
				public void onStarting(int tick, int scale) {
					sessionInfo.getPriceSession().setTickSize(tick);
					sessionInfo.getPriceSession().setTickScale(scale);

					try {
						sessionInfo.getPriceSession().saveProperties();
						sessionInfo.getIndicatorSession().saveProperties();
					} catch (IOException e) {
						e.printStackTrace();
					}

					indicator.begin(tick);
					indicator.onStarting(tick, scale);
				}

				@Override
				public void onNewTick(QueueTick qt) {
					try {
						newTick(qt);
					} catch (IOException e) {
						e.printStackTrace();
						throw new RuntimeException(e);
					}
				}

				@Override
				public void onWarmUpFinished() {
					indicator.onWarmUpFinished();
				}
			});
			restartCharts();
			csvds.playTicks(monitor);

			priceRecorder.close();
			indicatorRecorder.close();

		} catch (Exception e) {
			e.printStackTrace();
			monitor.done();
			return new Status(IStatus.ERROR, PLStatsPlugin.PLUGIN_ID,
					e.getMessage());
		}

		if (monitor.isCanceled()) {
			getManager().cancelIndicatorJob(this);
		}

		monitor.done();

		return status;
	}

	/**
	 * 
	 */
	private void restartCharts() {
		List<IndicatorChartView> views = PLStatsPlugin.getDefault()
				.getIndicatorManager().getChartViews();
		for (IndicatorChartView view : views) {
			if (view.getConfiguration() == getConfiguration()) {
				view.setConfiguration(getConfiguration());
			}
		}
	}

	/**
	 * @param qTick
	 * @throws IOException
	 */
	void newTick(QueueTick qTick) throws IOException {
		PLStatsPlugin plugin = PLStatsPlugin.getDefault();
		if (plugin != null) {
			priceRecorder.addPrice(qTick);
			indicator.onNewTick(qTick);

			List<IndicatorChartView> views = new ArrayList<>(plugin
					.getIndicatorManager().getChartViews());
			for (IndicatorChartView view : views) {
				if (view.getConfiguration() == getConfiguration()) {
					view.updateChartOnTick();
				}
			}
		}
	}

	/**
	 * @return
	 */
	private File getDatabaseDir() {
		PLStatsPlugin.getDefault().getIndicatorManager();
		return IndicatorManager.getIndicatorDatabaseDir(getConfiguration());
	}

	/**
	 * @return the session
	 */
	public SessionInfo getSession() {
		return sessionInfo;
	}

}
