/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 * 
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 * 
 */
package com.mfg.symbols.dfs.jobs;

import static java.lang.System.out;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

import com.mfg.common.BAR_TYPE;
import com.mfg.common.BarType;
import com.mfg.common.DFSException;
import com.mfg.common.DfsRealSymbol;
import com.mfg.common.QueueTick;
import com.mfg.connector.dfs.DFSHistoricalDataInfo;
import com.mfg.connector.dfs.DFSHistoricalDataInfo.GapFillingType;
import com.mfg.connector.dfs.DFSHistoricalDataInfo.RequestMode;
import com.mfg.connector.dfs.DFSHistoricalDataInfo.Slot;
import com.mfg.connector.dfs.DFSPlugin;
import com.mfg.dfs.conn.DfsDataProvider;
import com.mfg.dm.DataProviderParams;
import com.mfg.dm.SlotParams;
import com.mfg.dm.TickDataRequest;
import com.mfg.dm.TickDataSource;
import com.mfg.dm.UnitsType;
import com.mfg.dm.speedControl.DataSpeedControlState;
import com.mfg.dm.speedControl.DataSpeedModel;
import com.mfg.dm.speedControl.IDelayControl;
import com.mfg.dm.speedControl.ThreadDelayControl;
import com.mfg.dm.symbols.MergeSeriesAlgorithm;
import com.mfg.symbols.dfs.configurations.DFSConfiguration;
import com.mfg.symbols.dfs.configurations.DFSSymbolData;
import com.mfg.symbols.jobs.SymbolJob;
import com.mfg.symbols.jobs.SymbolJobConfig;
import com.mfg.utils.U;

/**
 * @author arian
 * 
 */
public class DFSJob extends SymbolJob<DFSConfiguration> {
	public static final Object SIMULATOR_JOB_KEY = "Simulator-DFS-Job";

	@Override
	protected void canceling() {
		IDelayControl control = fDataSource.getDelayControl();
		if (control != null) {
			DataSpeedModel model = control.getModel();
			model.setState(DataSpeedControlState.STOPPED);
			out.println("Notify speed control on job cancellation.");
			synchronized (model) {
				model.notifyAll();
			}
		}
		U.debug_var(392340, "trying to cancel the job ", this,
				" asking to cancel ", fDataSource);
		this.fDataSource.canceling();
	}

	/**
	 * I can have the layers; the layers are simply data sources which are not
	 * merged but independently they are updated by the ESignal bridge.
	 * 
	 * ARIAN: the layer are in the same order as in the slots of the
	 * {@linkplain DataProviderParams} class. Usually the last slot is the range
	 * slot but please check. So the layers go from the less detailed to the
	 * most detailed.
	 * 
	 */
	private TickDataSource fDataSource;

	private int[] layerScaleMap;

	private final boolean _simulating;

	private Date _lastTime;

	DataSpeedModel _dataSpeedModel;

	/**
	 * @param config
	 * @throws Exception
	 */
	public DFSJob(SymbolJobConfig<DFSConfiguration> config) throws Exception {
		super(config);
		boolean controller = DFSPlugin.getDefault().getDataProvider().getDfs()
				.getController() != null;
		_simulating = controller;
	}

	@Override
	protected void onDataSourceNewTick(IProgressMonitor monitor, QueueTick qt,
			int dataLayer) {
		super.onDataSourceNewTick(monitor, qt, dataLayer);
		if (_simulating) {
			_lastTime = new Date();
		}
	}

	@Override
	protected void onDataSourceWarmUpFinished(int dataLayer, long lastTime,
			boolean allLayersWarmedUp) {
		super.onDataSourceWarmUpFinished(dataLayer, lastTime, allLayersWarmedUp);
		if (allLayersWarmedUp) {
			if (_dataSpeedModel.getState() == DataSpeedControlState.FAST_FORWARDING) {
				_dataSpeedModel.setState(DataSpeedControlState.PLAYING);
			}
		}
	}

	public Date getLastTime() {
		return _lastTime;
	}

	@Override
	public boolean belongsTo(Object family) {
		if (family == SIMULATOR_JOB_KEY) {
			return _simulating;
		}
		return super.belongsTo(family);
	}

	private void initLayerScaleMap() {
		DFSHistoricalDataInfo historicalDataInfo = (DFSHistoricalDataInfo) getHistoricalDataInfoToRun();
		List<Slot> slots = historicalDataInfo.getSlots();
		layerScaleMap = new int[slots.size()];
		for (int layer = 0; layer < slots.size(); layer++) {
			Slot slot = slots.get(layer);
			int scale = slot.getScale();
			layerScaleMap[layer] = scale;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.symbols.jobs.SymbolJob#initializeDataSource()
	 */
	@SuppressWarnings("deprecation")
	// Necessary use of setGap and getGap methods.
	@Override
	protected void initializeDataSource() throws DFSException {
		initLayerScaleMap();
		DFSConfiguration configuration = getSymbolConfiguration();

		DFSSymbolData symbolData = configuration.getInfo().getSymbol();

		DFSHistoricalDataInfo historicalDataInfo = (DFSHistoricalDataInfo) getHistoricalDataInfoToRun();

		DataProviderParams historicalParams = new DataProviderParams();

		historicalParams.setXp(historicalDataInfo.getXp());
		historicalParams.setDp(historicalDataInfo.getDp());
		historicalParams
				.setUseDataSeriesMergedAlgorithm(historicalDataInfo
						.getMultipleDataSeriesAlgorithm() == MergeSeriesAlgorithm.MERGE);

		historicalParams.setGapFillingTypeSlidingWindow(historicalDataInfo
				.getGapFillingType() == GapFillingType.SLIDING_WINDOW);

		ArrayList<SlotParams> slots = new ArrayList<>();

		for (DFSHistoricalDataInfo.Slot slot1 : historicalDataInfo.getSlots()) {
			SlotParams slot2 = new SlotParams();
			slot2.setBarType(getBarType(slot1));
			slot2.setUnitsType(slot1.getUnitsType() == com.mfg.dm.UnitsType.BARS ? com.mfg.dm.UnitsType.BARS
					: com.mfg.dm.UnitsType.DAYS);

			if (historicalDataInfo.getRequestMode() == RequestMode.DATABASE) {
				slot2.setStartDate(slot1.getStartDate().getTime());
			}

			slot2.getStartDate();
			slot2.setMultiplicityBar(slot1.getNumbeOfUnits());
			slot2.setNumBars(slot1.getNumberOfBars());
			slot2.setScale(slot2.getScale());
			slot2.setUnitsType(UnitsType.valueOf(slot1.getUnitsType().name()));
			slot2.setGap1(slot1.getGap1());
			slot2.setGap2(slot1.getGap2());
			slot2.setGap(slot1.getGap());
			slots.add(slot2);
		}
		historicalParams.setSlots(slots);
		historicalParams.setExtraData(historicalDataInfo);

		DfsRealSymbol aRealSymbol = new DfsRealSymbol(
				symbolData.getLocalSymbol(), "", symbolData.getTickSize()
						.intValue(), symbolData.getTickScale().intValue(),
				symbolData.getTickValue());

		boolean isRealTime;
		switch (historicalDataInfo.getRequestMode()) {
		case DATABASE:
			isRealTime = false;
			break;
		case MIXED:
			isRealTime = true;
			break;
		default:
			throw new IllegalStateException("unknown mode!");

		}

		boolean isFilterOutOfRangeTicks = historicalDataInfo
				.isFilterOutOfRangeTicks();
		int minGapInTicks = historicalDataInfo.getMinGapInTicks();

		int numberOfPrices = historicalDataInfo.getWarmUpNumberOfPrices();

		/*
		 * The data request has the boolean flag which says if the cache
		 * expander will have to merge the slots together
		 */
		TickDataRequest dataRequest1 = new TickDataRequest(aRealSymbol,
				historicalParams, numberOfPrices, isRealTime,
				isFilterOutOfRangeTicks, minGapInTicks);

		/*
		 * This is the point where the data request is done and with this data
		 * request I am able to create a virtual symbol in the data provider.
		 * 
		 * The data source here will be only a "proxy" data source.
		 */

		fDataSource = _dataProvider.createTickDataSource(dataRequest1,
				configuration.getUUID());

		_dataSpeedModel = new DataSpeedModel();
		_dataSpeedModel.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (getMonitor() != null
						&& _dataSpeedModel.getState() == DataSpeedControlState.STOPPED) {
					getMonitor().setCanceled(true);
				}
			}
		});
		ThreadDelayControl control = new ThreadDelayControl(_dataSpeedModel);
		fDataSource.setDelayControl(control);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		// run the warm-up at full speed if it is indicated to do so
		if (getJobConfig().isRunWarmupFullSpeed()) {
			_dataSpeedModel.setDelay(0);
		}
		return super.run(monitor);
	}

	public DataSpeedModel getDataSpeedModel() {
		return _dataSpeedModel;
	}

	private static BAR_TYPE getBarType(DFSHistoricalDataInfo.Slot slot) {
		BarType barType = slot.getBarType();
		switch (barType) {
		case DAILY:
			return BAR_TYPE.DAILY;
		case MINUTE:
			return BAR_TYPE.MINUTE;
		case RANGE:
			return BAR_TYPE.RANGE;
		}
		return null;
	}

	@Override
	protected int getDataLayerScale(int layer) {
		return layerScaleMap[layer];
	}

	@Override
	public DfsDataProvider getDataProvider() {
		return DFSPlugin.getDefault().getDataProvider();
	}

	@Override
	public TickDataSource getDataSource() {
		return fDataSource;
	}
}
