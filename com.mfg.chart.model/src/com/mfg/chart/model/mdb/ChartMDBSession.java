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

package com.mfg.chart.model.mdb;

import static java.lang.System.out;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

import org.eclipse.core.runtime.Assert;
import org.mfg.mdb.runtime.SessionMode;

import com.mfg.chart.model.ChartModelPlugin;
import com.mfg.chart.model.MDBPaths;
import com.mfg.logger.LoggerPlugin;
import com.mfg.logger.application.IAppLogger;

/**
 * @author arian
 * 
 */
public class ChartMDBSession extends BaseChartMDBSession {

	private static final String PROP_DATA_LAYERS_COUNT = "dataLayersCount";

	private static final String PROP_TICK_SIZE = "tickSize";

	public static final String PROP_SCALES_COUNT = "scalesCount";

	private static final String PROP_IS_PERCENT_PROB_MODE = "isPercentProbabilityMode";

	private static final String PROP_IS_CONDITIONAL_PROB_LINES_ONLY = "isConditionalProbabilityLinesOnly";

	private static final String PROP_TICK_SCALE = "tickScale";

	private static final String PROP_DATA_LAYERS_SCALE = "dataLayerScales";

	private static final String PROP_START_REALTIME = "startRealtime";

	private final PropertyChangeSupport support = new PropertyChangeSupport(
			this);

	private int tickSize;

	private IAppLogger appLogger;

	private int tickScale;

	private int dataLayersCount;

	private int[] dataLayerScaleMap;

	private Long[] startRealtimes;

	public static boolean isTemporal(File root) {
		return new File(root, "temporal").exists();
	}

	public ChartMDBSession(String sessionName, File root, SessionMode mode)
			throws IOException {
		this(sessionName, root, mode, false);
	}

	public ChartMDBSession(String sessionName, File root, SessionMode mode,
			boolean temporal) throws IOException {
		super(sessionName, root, mode);

		out.println("Creating " + (temporal ? "temporal " : "")
				+ "Chart DB session " + root);
		if (temporal) {
			File temp = new File(root, "temporal");
			temp.createNewFile();
			Assert.isTrue(isTemporal(root));
		}

		readProperties();
	}

	@Override
	@SuppressWarnings("boxing")
	protected void readProperties() {
		LoggerPlugin loggerPlugin = LoggerPlugin.getDefault();
		appLogger = loggerPlugin == null ? IAppLogger.SYSTEM_PRINT_LOGGER
				: loggerPlugin.getAppLogger(
						ChartModelPlugin.LOGGER_COMPONENT_ID, getSessionName());
		appLogger.logComment("Created session %s at %s", getSessionName(),
				getRoot().getPath());

		if (getProperties().containsKey(PROP_TICK_SIZE)) {
			tickSize = Integer.parseInt(getProperties().getProperty(
					PROP_TICK_SIZE));
			appLogger
					.logComment("Read tick size (=%d) from databse.", tickSize);
		} else {
			tickSize = 25; // default tick size
			appLogger.logComment("Set default tick size (=%d).", tickSize);
		}

		if (getProperties().containsKey(PROP_DATA_LAYERS_COUNT)) {
			dataLayersCount = Integer.parseInt(getProperties().getProperty(
					PROP_DATA_LAYERS_COUNT));
		}

		if (getProperties().containsKey(PROP_DATA_LAYERS_SCALE)) {
			String str = getProperties().getProperty(PROP_DATA_LAYERS_SCALE);
			str = str.substring(1, str.length() - 1);
			String[] split = str.split(",");
			dataLayerScaleMap = new int[split.length];
			for (int i = 0; i < dataLayerScaleMap.length; i++) {
				dataLayerScaleMap[i] = Integer.parseInt(split[i].trim());
			}
		}

		if (getProperties().containsKey(PROP_START_REALTIME)) {
			String str = getProperties().getProperty(PROP_START_REALTIME);
			str = str.substring(1, str.length() - 1);
			String[] split = str.split(",");
			startRealtimes = new Long[split.length];
			for (int i = 0; i < dataLayerScaleMap.length; i++) {
				startRealtimes[i] = Long.parseLong(split[i].trim());
			}
		}
	}

	/**
	 * 
	 * @return the seriesFilter
	 */
	public int[] getDataLayerScales() {
		return dataLayerScaleMap;
	}

	/**
	 * @param dataLayerScale
	 *            the dataLayerScale to set
	 */
	public void setDataLayerScales(int[] dataLayerScale) {
		this.dataLayerScaleMap = dataLayerScale;
		getProperties().setProperty(PROP_DATA_LAYERS_SCALE,
				Arrays.toString(dataLayerScale));
	}

	public void setTickSize(int tickSize1) {
		this.tickSize = tickSize1;
		getProperties()
				.setProperty(PROP_TICK_SIZE, Integer.toString(tickSize1));
		appLogger
				.logComment("Set tick size (=%d).", Integer.valueOf(tickSize1));
	}

	public int getTickSize() {
		return tickSize;
	}

	public int getTickScale() {
		return tickScale;
	}

	public void setTickScale(int tickScale1) {
		this.tickScale = tickScale1;
		getProperties().setProperty(PROP_TICK_SCALE,
				Integer.toString(tickScale1));
		appLogger.logComment("Set tick scale (=%d).",
				Integer.valueOf(tickScale1));
	}

	public int getScalesCount() {
		try {
			return Integer.parseInt(getProperties().getProperty(
					PROP_SCALES_COUNT));
		} catch (Exception e) {
			return 0;
		}
	}

	public void setScalesCount(int scalesCount) {
		getProperties().setProperty(PROP_SCALES_COUNT,
				Integer.toString(scalesCount));
	}

	public void setDataLayersCount(int dataLayersCount1) {
		this.dataLayersCount = dataLayersCount1;
		if (startRealtimes == null) {
			setStartRealtimes(new Long[dataLayersCount1]);
		}
		getProperties().setProperty(PROP_DATA_LAYERS_COUNT,
				Long.toString(dataLayersCount1));
	}

	public int getDataLayersCount() {
		return dataLayersCount;
	}

	public Long[] getStartRealtimes() {
		return startRealtimes;
	}

	public void setStartRealtimes(Long[] startRealtime) {
		this.startRealtimes = startRealtime;
		getProperties().setProperty(PROP_START_REALTIME,
				Arrays.toString(startRealtime));
	}

	public boolean isPercentProbabilityMode() {
		return Boolean.parseBoolean(getProperties().getProperty(
				PROP_IS_PERCENT_PROB_MODE, Boolean.toString(false)));
	}

	public void setPercentProbabilityMode(boolean percentProbabilityMode) {
		getProperties().setProperty(PROP_IS_PERCENT_PROB_MODE,
				Boolean.toString(percentProbabilityMode));
	}

	public boolean isConditionalProbabilitiesOnly() {
		return Boolean.parseBoolean(getProperties().getProperty(
				PROP_IS_CONDITIONAL_PROB_LINES_ONLY, Boolean.toString(false)));
	}

	public void setConditionalProbabilitiesOnly(
			boolean conditionalProbabilitiesOnly) {
		getProperties().setProperty(PROP_IS_CONDITIONAL_PROB_LINES_ONLY,
				Boolean.toString(conditionalProbabilitiesOnly));
	}

	/**
	 * @deprecated Use {@link #connectTo_PriceMDB(int)}.
	 * @return
	 * @throws IOException
	 */
	@Deprecated
	public PriceMDB connectTo_PriceMDB() throws IOException {
		return super.connectTo_PriceMDB(MDBPaths.getPriceMDB());
	}

	public PriceMDB connectTo_PriceMDB(int layer) throws IOException {
		return super.connectTo_PriceMDB(MDBPaths.getPriceMDB(layer));
	}

	public TradeMDB connectTo_TradeMDB() throws IOException {
		return connectTo_TradeMDB(MDBPaths.getTradeMDB());
	}

	public EquityMDB connectTo_EquityMDB() throws IOException {
		return connectTo_EquityMDB(MDBPaths.getEquityMDB());
	}

	/**
	 * @return the appLogger
	 */
	public IAppLogger getAppLogger() {
		return appLogger;
	}

	/**
	 * @param level
	 * @return
	 * @throws IOException
	 */
	public PivotMDB connectTo_PivotMDB(int layer, int level) throws IOException {
		return connectTo_PivotMDB(MDBPaths.getPivotMDB(layer, level));
	}

	public ProbabilityMDB connectTo_ProbabilityMDB(int level)
			throws IOException {
		return connectTo_ProbabilityMDB(MDBPaths.getProbabilityMDB(level));
	}

	/**
	 * @param level
	 * @return
	 * @throws IOException
	 */
	public ProbabilityPercentMDB connectTo_ProbabilityPercentMDB(int level)
			throws IOException {
		return connectTo_ProbabilityPercentMDB(MDBPaths
				.getProbabilityPercentMDB(level));
	}

	/**
	 * @param level
	 * @return
	 * @throws IOException
	 */
	public BandsMDB connectTo_BandsMDB(int layer, int level) throws IOException {
		return connectTo_BandsMDB(MDBPaths.getBandsMDB(layer, level));
	}

	/**
	 * @param level
	 * @return
	 * @throws IOException
	 */
	public BandsMDB connectTo_CompressedBandsMDB(int layer, int level)
			throws IOException {
		return connectTo_BandsMDB(MDBPaths.getCompressedBandsMDB(layer, level));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.mdb.MDBSession#close()
	 */
	@Override
	public void close() throws IOException {
		try {
			super.close();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
		firePropertyChange("close");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.mdb.MDBSession#closeAndDelete()
	 */
	@Override
	public int closeAndDelete() throws IOException, TimeoutException {
		int fail = super.closeAndDelete();
		out.println("Closing and deleting " + getSessionName() + ", fail: "
				+ fail);
		appLogger.logComment("Close session %s. Number of failures is %d.",
				getSessionName(), Integer.valueOf(fail));

		firePropertyChange("closeAndDelete");

		Assert.isTrue(fail == 0);

		return fail;
	}

	/**
	 * @param level
	 * @return
	 * @throws IOException
	 */
	public ChannelMDB connectTo_ChannelMDB(int layer, int level)
			throws IOException {
		return connectTo_ChannelMDB(MDBPaths.getChannelMDB(layer, level));
	}

	public ChannelInfoMDB connectTo_ChannelInfoMDB(int layer, int level)
			throws IOException {
		return connectTo_ChannelInfoMDB(MDBPaths
				.getChannelInfoMDB(layer, level));
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		support.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		support.removePropertyChangeListener(l);
	}

	public void addPropertyChangeListener(String property,
			PropertyChangeListener l) {
		support.addPropertyChangeListener(property, l);
	}

	public void removePropertyChangeListener(String property,
			PropertyChangeListener l) {
		support.removePropertyChangeListener(property, l);
	}

	public void firePropertyChange(String property) {
		support.firePropertyChange(property, true, false);
	}
}
