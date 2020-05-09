package com.mfg.chart.model;

public class EmptyPriceModel implements IPriceModel {
	@Override
	public long getLowerDisplayTime(int dataLAyer) {
		return 0;
	}

	@Override
	public long getUpperDisplayTime(int dataLayer) {
		return 0;
	}

	@Override
	public long getLowerPhysicalTime(int dataLayer) {
		return 0;
	}

	@Override
	public long getUpperPhysicalTime(int dataLayer) {
		return 0;
	}

	@Override
	public long getDataLayerLowerDisplayTime(int dataLayer) {
		return 0;
	}

	@Override
	public long getDataLayerUpperDisplayTime(int dataLayer) {
		return 0;
	}

	@Override
	public IPriceCollection getPrices(int dataLayer, long lowerTime,
			long upperTime, int maxNumberOfPoints) {
		return IPriceCollection.EMPTY;
	}

	@Override
	public IPriceCollection getVolumes(int dataLayer, long lowerTime,
			long upperTime, long lowPrice, long upperPrice) {
		return IPriceCollection.EMPTY;
	}

	@Override
	public int getVolume_from_FakeTime(int dataLayer, long fakeTime) {
		return 0;
	}

	@Override
	public long getPhysicalTime_from_FakeTime(int dataLayer, long fakeTime) {
		return 0;
	}

	@Override
	public long getDisplayTime_from_PhysicalTime(int dataLayer,
			long physicalTime) {
		return 0;
	}

	@Override
	public long getDisplayTimeOffset(int dataLayer, long displayTime,
			long distance) {
		return 0;
	}

	@Override
	public double getTickSize() {
		return 0;
	}

	@Override
	public int getTickScale() {
		return 0;
	}

	@Override
	public Integer getLastPrice(int dataLayer) {
		return null;
	}

	@Override
	public long getPricesDistance(int dataLayer, long lower, long upper) {
		return 0;
	}

	@Override
	public ITimesOfTheDayCollection getTimeOfTheDayCollection(int dataLayer,
			int hh, int mm, int maxNumOfDays, long lowerFakeTime,
			long upperFakeTime) {
		return ITimesOfTheDayCollection.EMPTY;
	}

	@Override
	public IChartModel getChartModel() {
		return IChartModel.EMPTY;
	}

	@Override
	public void setChartModel(IChartModel chartModel) {
		//
	}

	@Override
	public long getLowerDisplayTime_from_DisplayTime(int dataLayer,
			long displayLower) {
		return 0;
	}

	@Override
	public long getUpperDisplayTime_from_DisplayTime(int dataLayer,
			long displayUpper) {
		return 0;
	}

	@Override
	public long getLowerPhysicalTime_from_DisplayTime(int dataLayer,
			long displayLower) {
		return 0;
	}

	@Override
	public long getUpperPhysicalTime_from_DisplayTime(int dataLayer,
			long displayUpper) {
		return 0;
	}

	@Override
	public long getPhysicalTime_from_DisplayTime(int layer, long displayTime) {
		return 0;
	}

	@Override
	public long getFakeTime_from_PhysicalTime(int layer, long physicalTime) {
		return 0;
	}

	@Override
	public long getFakeTime_from_DisplayTime(int layer, long displayTime) {
		return 0;
	}

	@Override
	public long getDataLayerPricesCount(int layer) {
		return 0;
	}

	@Override
	public LayerProjection getLayerProjection(int fromLayer,
			long fromLowerTime, long fromUpperTime, int toLayer) {
		return null;
	}

	@Override
	public LayerProjection getLayerProjection(int fromLayer,
			LayerProjection fromProjection, int toLayer) {
		return null;
	}

	@Override
	public Long getStartRealtime(int dataLayer) {
		return null;
	}

	@Override
	public long getLastTime(int dataLayer) {
		return 0;
	}
}