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
import com.mfg.inputdb.prices.mdb.PriceMDBSession;
import com.mfg.tradingdb.mdb.TradingMDBSession;

public class ChartModel_MDB implements IChartModel {

	private ScaledIndicatorModel_MDB _indicatorModel;
	private final IPriceModel _priceModel;
	private ITradingModel _executionModel;
	private IPositionCollection _pendingOrdersModel;
	private final ITemporalPricesModel _temporalPricesModel;
	private IDataLayerModel _rangeModel;
	private final PriceMDBSession _priceSession;
	private final IndicatorMDBSession _indicatorSession;
	private final TradingMDBSession _tradingSession;

	public ChartModel_MDB(PriceMDBSession priceSession,
			IndicatorMDBSession indicatorSession,
			TradingMDBSession tradingSession) {
		this(priceSession, indicatorSession, tradingSession,
				new PriceModel_MDB(priceSession), ITemporalPricesModel.EMPTY,
				null, false);
	}

	public ChartModel_MDB(PriceMDBSession priceSession,
			IndicatorMDBSession indicatorSession,
			TradingMDBSession tradingSession, IPriceModel priceModel,
			ITemporalPricesModel temporalPricesModel,
			ITradingModel executionModel, boolean physicalTimes) {
		this._priceSession = priceSession;
		this._indicatorSession = indicatorSession;
		this._tradingSession = tradingSession;

		_indicatorModel = physicalTimes ? createPhysicalIndicatorModel(indicatorSession)
				: createIndicatorModel(indicatorSession);
		_indicatorModel.setChartModel(this);

		this._priceModel = priceModel;
		priceModel.setChartModel(this);

		if (tradingSession == null) {
			this._executionModel = ITradingModel.EMPTY;
		} else {
			this._executionModel = executionModel == null ? (physicalTimes ? new PhysicalTradingModel_MDB(
					priceSession.getTimeMap(0), _tradingSession, priceModel)
					: new TradingModel_MDB(tradingSession, priceModel))
					: ITradingModel.EMPTY;
		}
		if (_executionModel instanceof Model_MDB) {
			((Model_MDB) _executionModel).setChartModel(this);
		}
		_pendingOrdersModel = IPositionCollection.EMPTY;
		this._temporalPricesModel = temporalPricesModel;
	}

	protected ScaledIndicatorModel_MDB createIndicatorModel(
			IndicatorMDBSession indicatorSession) {
		return new ScaledIndicatorModel_MDB(indicatorSession, this);
	}

	protected PhysicalScaledIndicatorModel_MDB createPhysicalIndicatorModel(
			IndicatorMDBSession indicatorSession) {
		return new PhysicalScaledIndicatorModel_MDB(indicatorSession, this);
	}

	@Override
	public long getToken() {
		long priceToken = _priceSession.getModificatonToken();

		long indicatorToken = _indicatorSession == null ? 0 : _indicatorSession
				.getModificatonToken();

		long tradingToken = _tradingSession == null ? 0 : _tradingSession
				.getModificatonToken();

		int ordersToken = _pendingOrdersModel.getSize();

		long tempPricesToken = _temporalPricesModel.getModificationToken();

		long token = priceToken + indicatorToken + tradingToken + ordersToken
				+ tempPricesToken;

		return token;
	}

	public PriceMDBSession getPriceSession() {
		return _priceSession;
	}

	public IndicatorMDBSession getIndicatorSession() {
		return _indicatorSession;
	}

	public TradingMDBSession getTradingSession() {
		if (_executionModel instanceof TradingModel_MDB) {
			TradingMDBSession session = ((TradingModel_MDB) _executionModel)
					.getTradingSession();
			if (session != null) {
				return session;
			}
		}
		return _tradingSession;
	}

	@Override
	public int getDataLayerCount() {
		return _priceSession.getDataLayersCount();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.IChartModel#getRangeModel()
	 */
	@Override
	public IDataLayerModel getDataLayerModel() {
		return _rangeModel;
	}

	/**
	 * @param rangeModel
	 *            the rangeModel to set
	 */
	@Override
	public void setRangeModel(IDataLayerModel rangeModel) {
		this._rangeModel = rangeModel;
	}

	@Override
	public IPriceModel getPriceModel() {
		return _priceModel;
	}

	@Override
	public ScaledIndicatorModel_MDB getScaledIndicatorModel() {
		return _indicatorModel;
	}

	public void setScaledIndicatorModel(ScaledIndicatorModel_MDB indicatorModel) {
		_indicatorModel = indicatorModel;
	}

	@Override
	public ITradingModel getTradingModel() {
		return _executionModel;
	}

	/**
	 * @param executionModel
	 *            the executionModel to set
	 */
	public void setExecutionModel(ITradingModel executionModel) {
		this._executionModel = executionModel;
		if (executionModel instanceof Model_MDB) {
			((Model_MDB) executionModel).setChartModel(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.IChartModel#getPendingOrdersModel()
	 */
	@Override
	public IPositionCollection getPendingOrdersModel() {
		return _pendingOrdersModel;
	}

	/**
	 * @param pendingOrdersModel
	 *            the pendingOrdersModel to set
	 */
	public void setPendingOrdersModel(IPositionCollection pendingOrdersModel) {
		this._pendingOrdersModel = pendingOrdersModel;
	}

	@Override
	public boolean isAlive() {
		return _priceSession.isOpen();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.IChartModel#getTemporalPricesModel()
	 */
	@Override
	public ITemporalPricesModel getTemporalPricesModel() {
		return _temporalPricesModel;
	}

	@Override
	public ISyntheticModel getSyntheticModel() {
		return ISyntheticModel.EMPTY;
	}
}
