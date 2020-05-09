package com.mfg.chart.model;

public class EmptyChartModel implements IChartModel {
	@Override
	public ISyntheticModel getSyntheticModel() {
		return ISyntheticModel.EMPTY;
	}

	@Override
	public IPositionCollection getPendingOrdersModel() {
		return IPositionCollection.EMPTY;
	}

	@Override
	public IScaledIndicatorModel getScaledIndicatorModel() {
		return IScaledIndicatorModel.EMPTY;
	}

	@Override
	public IPriceModel getPriceModel() {
		return IPriceModel.EMPTY;
	}

	@Override
	public ITradingModel getTradingModel() {
		return ITradingModel.EMPTY;
	}

	@Override
	public boolean isAlive() {
		return true;
	}

	@Override
	public ITemporalPricesModel getTemporalPricesModel() {
		return ITemporalPricesModel.EMPTY;
	}

	@Override
	public IDataLayerModel getDataLayerModel() {
		return null;
	}

	@Override
	public void setRangeModel(IDataLayerModel rangeModel) {
		//
	}

	@Override
	public int getDataLayerCount() {
		return 0;
	}

	@Override
	public long getToken() {
		return 0;
	}
}