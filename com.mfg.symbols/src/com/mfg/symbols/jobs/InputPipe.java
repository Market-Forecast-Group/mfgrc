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
package com.mfg.symbols.jobs;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.eclipse.core.runtime.Assert;
import org.mfg.mdb.runtime.MDBSession;
import org.mfg.mdb.runtime.SessionMode;

import com.mfg.chart.model.mdb.ChartMDBSession;
import com.mfg.common.TEAException;
import com.mfg.dm.TickDataSource;
import com.mfg.inputdb.indicator.mdb.IndicatorMDBSession;
import com.mfg.inputdb.prices.mdb.PriceMDBSession;
import com.mfg.logger.application.IAppLogger;
import com.mfg.symbols.SymbolsPlugin;
import com.mfg.symbols.inputs.configurations.InputConfiguration;
import com.mfg.symbols.inputs.configurations.InputConfigurationInfo;
import com.mfg.utils.PropertiesEx;
import com.mfg.widget.arc.gui.IndicatorParamBean;
import com.mfg.widget.arc.strategy.LayeredIndicator;
import com.mfg.widget.arc.strategy.MultiscaleIndicator;
import com.mfg.widget.recorders.AdvancedIndicatorRecorder;

/**
 * @author arian
 * 
 */
public class InputPipe {
	private final SymbolJob<?> symbolJob;
	private final InputConfiguration configuration;
	private final IAppLogger logger;
	private LayeredIndicator indicator;
	IndicatorMDBSession indicatorSession;
	private Recorder[] recorders;
	private boolean _running;
	private Map<Object, Object> _addons;

	public static class Recorder extends AdvancedIndicatorRecorder {

		public Recorder(LayeredIndicator aIndicator,
				IndicatorMDBSession aSession, int layer) throws IOException {
			super(aIndicator, aSession, layer);
		}
	}

	public InputPipe(SymbolJob<?> symbolJob1,
			InputConfiguration aConfiguration, final IAppLogger logger1)
			throws IOException {
		super();
		this.symbolJob = symbolJob1;
		this.configuration = aConfiguration;
		this.logger = logger1;
		_addons = new HashMap<>();
		logger1.logComment("Input pipe %s connected with %s", aConfiguration
				.getName(), symbolJob1.getSymbolConfiguration().getName());

		createSession();
		createIndicatorAndRecorders();

		_running = true;
	}

	private void createIndicatorAndRecorders() throws IOException {
		IndicatorParamBean indicatorParams = configuration.getInfo()
				.getIndicatorParams();
		PropertiesEx extraProperties = SymbolsPlugin.getDefault()
				.getProperties();
		indicatorParams.setProperties(extraProperties);
		indicatorSession.setScalesCount(indicatorParams
				.getIndicatorNumberOfScales());
		indicatorSession.setPolylineDegree(indicatorParams
				.getIndicator_centerLineAlgo().getDegree());

		PriceMDBSession priceSession = symbolJob.getMdbSession();

		indicator = new LayeredIndicator(indicatorSession);

		MultiscaleIndicator indicatorLayer;

		TickDataSource layeredDS = getSymbolJob().getDataSource();

		recorders = new Recorder[layeredDS.getLayersSize()];

		for (int i = 0; i < layeredDS.getLayersSize(); i++) {

			indicatorLayer = new MultiscaleIndicator(indicatorParams,
					priceSession, i);
			indicator.addLayer(indicatorLayer);
			layeredDS.addTickListener(i, indicatorLayer);

			/*
			 * I have not a global listener, for global events so I have to
			 * resort to this hack, the layered indicator need only to know the
			 * end of warm up... and it needs the end of all the warm up.
			 * 
			 * As the warm up events are sent serially, first the layer zero
			 * then the others, the layered indicator needs to subscribe to the
			 * latest.
			 * 
			 * The layered indicator is added after the indicator, because it
			 * has to do the warm up before the layered indicator.
			 */
			if (symbolJob.getDataSource().getRequest().isRealTime()) {
				/*
				 * In case of real time the last layer is the first.
				 */
				if (i == 0) {
					layeredDS.addTickListener(i, indicator);
				}
			} else {
				if (i == layeredDS.getLayersSize() - 1) {
					layeredDS.addTickListener(i, indicator);
				}
			}

			recorders[i] = new Recorder(indicator, indicatorSession, i);
		}

		indicatorSession.saveProperties();
	}

	/**
	 * @param configuration1
	 * @return
	 * @throws IOException
	 */
	private void createSession() throws IOException {
		InputConfigurationInfo info = configuration.getInfo();
		String relPath = info.getDatabasePath();
		if (relPath == null) {
			relPath = "ChartDatabases/" + configuration.getName() + "-"
					+ configuration.getUUID() + "-"
					+ System.currentTimeMillis();
			info.setDatabasePath(relPath);

		}
		SymbolsPlugin.getDefault();
		File dbRoot = SymbolsPlugin.getInputDatabaseRoot(configuration);

		// we check if we need to create a new database
		if (dbRoot.exists() && ChartMDBSession.isTemporal(dbRoot)) {
			relPath = "ChartDatabases/" + configuration.getName() + "-"
					+ configuration.getUUID() + "-"
					+ System.currentTimeMillis();
			info.setDatabasePath(relPath);
			SymbolsPlugin.getDefault();
			dbRoot = SymbolsPlugin.getInputDatabaseRoot(configuration);
		}

		MDBSession.delete(dbRoot);
		Assert.isTrue(!dbRoot.exists());

		indicatorSession = new IndicatorMDBSession(configuration.getName(),
				dbRoot, SessionMode.READ_WRITE, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.symbols.inputs.pipes.IPipe#belongsTo(java.lang.Object)
	 */
	public boolean belongsTo(Object familiy) {
		return familiy.equals(configuration)
				|| familiy == configuration.getUUID() || familiy == this;
	}

	public Recorder[] getRecorders() {
		return recorders;
	}
	
	/**
	 * @return the indicator
	 */
	public LayeredIndicator getIndicator() {
		return indicator;
	}

	public InputConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 * Add-ons are used to attach external objects to the input pipe. This can
	 * be used by third-party components who need to do extra computations based
	 * on the indicator. An example of this is the poly-line widget in the
	 * dash-board.
	 * 
	 * @return
	 */
	public Map<Object, Object> getAddons() {
		return _addons;
	}

	public IndicatorMDBSession getMdbSession() {
		return indicatorSession;
	}

	public SymbolJob<?> getSymbolJob() {
		return symbolJob;
	}

	public void close() throws IOException, TimeoutException {
		logger.logComment("Input pipe %s closed", configuration.getName());
		for (int i = 0; i < recorders.length; i++) {
			Recorder recorder = recorders[i];
			recorder.close();
		}
		if (ChartMDBSession.isTemporal(indicatorSession.getRoot())) {
			indicatorSession.closeAndDelete();
		} else {
			indicatorSession.close();
		}
	}

	public void stop() {
		_running = false;
		SymbolJob<?> job = getSymbolJob();
		for (TradingPipe pipe : job.getTradingPipes()) {
			if (pipe.getConfiguration().getInfo().getInputConfiguratioId()
					.equals(getConfiguration().getUUID())) {
				if (pipe.isRunning()) {
					try {
						pipe.stop();
					} catch (TEAException e) {
						e.printStackTrace();
					}
				}
			}
		}
		logger.logComment("Input pipe %s stopped", configuration.getName());
		job.getDataSource().removeTickListener(0, indicator);

		job.removeInputPipe(this);
		SymbolJob.getManager().fireInputPipeStopped(symbolJob, this);

		try {
			close();
		} catch (IOException | TimeoutException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public boolean isRunning() {
		return _running;
	}
}
