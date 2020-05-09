/**
 * 
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 * 
 * $Id: $
 *
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 * 
 * @version $Revision: $ $Date: $
 */

package com.mfg.chart.model;

import com.mfg.inputdb.indicator.mdb.IndicatorMDBSession;

public class ScaledIndicatorModel_MDB extends Model_MDB implements
		IScaledIndicatorModel {
	private final int _scaleCount;
	private final IBandsModel[] _bandsModelMap;
	private final IPivotModel[] _pivotModelMap;
	private final IChannelModel[] _channelModelMap;
	private final IRealTimeZZModel[] _realTimeZZModelMap;
	private final IParallelRealTimeZZModel[] _parallelRealTimeZZModelMap;
	private final IRealTimeChannelModel[] _realTimeChannelModelMap;
	private ITrendLinesModel _trendLinesModel;
	protected IndicatorMDBSession _indicatorSession;
	private final IAutoTimeLinesModel[] _autoTimeLinesModelMap;

	public ScaledIndicatorModel_MDB(IndicatorMDBSession session,
			ChartModel_MDB chartModel) {
		super(session);
		_indicatorSession = session;
		setChartModel(chartModel);

		this._scaleCount = _indicatorSession == null ? 0 : _indicatorSession
				.getScalesCount();

		_bandsModelMap = new IBandsModel[_scaleCount + 1];
		_pivotModelMap = new PivotModel_MDB[_scaleCount + 1];
		_channelModelMap = new ChannelModel_MDB[_scaleCount + 1];

		_realTimeZZModelMap = new IRealTimeZZModel[_scaleCount + 1];
		_parallelRealTimeZZModelMap = new IParallelRealTimeZZModel[_scaleCount + 1];
		_realTimeChannelModelMap = new IRealTimeChannelModel[_scaleCount + 1];
		_autoTimeLinesModelMap = new IAutoTimeLinesModel[_scaleCount + 1];

		for (int level = getFirstScale(); level <= _scaleCount; level++) {
			_bandsModelMap[level] = createBandsmodel(level);
			_pivotModelMap[level] = createPivotModel(level);
			_channelModelMap[level] = createChannelModel(level);
			_autoTimeLinesModelMap[level] = createAutoTimeLinesModel(level);

			_realTimeZZModelMap[level] = IRealTimeZZModel.EMPTY;
			_parallelRealTimeZZModelMap[level] = IParallelRealTimeZZModel.EMPTY;
			_realTimeChannelModelMap[level] = IRealTimeChannelModel.EMPTY;
		}
		_trendLinesModel = ITrendLinesModel.EMPTY;
	}

	@Override
	public ChartModel_MDB getChartModel() {
		return (ChartModel_MDB) super.getChartModel();
	}

	protected IChannelModel createChannelModel(int level) {
		return _indicatorSession == null ? IChannelModel.EMPTY
				: new ChannelModel_MDB(_indicatorSession, level,
						getChartModel());
	}

	protected IPivotModel createPivotModel(int level) {
		return _indicatorSession == null ? IPivotModel.EMPTY
				: new PivotModel_MDB(_indicatorSession, level, getChartModel());
	}

	protected IBandsModel createBandsmodel(int level) {
		return _indicatorSession == null ? IBandsModel.EMPTY
				: new BandsModel_MDB(_indicatorSession, level, getChartModel());
	}

	protected IAutoTimeLinesModel createAutoTimeLinesModel(int level) {
		return _indicatorSession == null ? IAutoTimeLinesModel.EMPTY
				: new AutoTimeLinesModel_MDB(_indicatorSession, level,
						getChartModel());
	}

	@Override
	public int getFirstScale() {
		// TODO: magic number for now
		return 2;
	}

	@Override
	public int getScaleCount() {
		return _scaleCount;
	}

	@Override
	public IBandsModel getBandsModel(int level) {
		return _bandsModelMap[level];
	}

	@Override
	public IPivotModel getPivotModel(int level) {
		return _pivotModelMap[level];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.models.IScaledIndicatorModel#getChannelModel(int)
	 */
	@Override
	public IChannelModel getChannelModel(int level) {
		return _channelModelMap[level];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.IScaledIndicatorModel#getRealTimeZZModel(int)
	 */
	@Override
	public IRealTimeZZModel getRealTimeZZModel(int level) {
		return _realTimeZZModelMap[level];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.IScaledIndicatorModel#setRealTimeZZModel(int,
	 * com.mfg.chart.model.IRealTimeZZModel)
	 */
	@Override
	public void setRealTimeZZModel(int level, IRealTimeZZModel model) {
		_realTimeZZModelMap[level] = model;
	}

	@Override
	public void setParallelRealTimeZZModel(int level,
			IParallelRealTimeZZModel model) {
		_parallelRealTimeZZModelMap[level] = model;
	}

	@Override
	public IParallelRealTimeZZModel getParallelRealTimeZZModel(int level) {
		return _parallelRealTimeZZModelMap[level];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.chart.model.IScaledIndicatorModel#getRealTimeChannelModel(int)
	 */
	@Override
	public IRealTimeChannelModel getRealTimeChannelModel(int level) {
		return _realTimeChannelModelMap[level];
	}

	@Override
	public void setRealTimeChannelModel(int level, IRealTimeChannelModel model) {
		_realTimeChannelModelMap[level] = model;
	}

	public void setTrendLinesModel(ITrendLinesModel trendLinesModel) {
		this._trendLinesModel = trendLinesModel;
	}

	@Override
	public ITrendLinesModel getTrendLinesModel() {
		return _trendLinesModel;
	}

	@Override
	public IAutoTimeLinesModel getAutoTimeLinesModel(int level) {
		return _autoTimeLinesModelMap[level];
	}

}
