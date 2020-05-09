package com.mfg.chart.model;

public interface IPriceModel {

	public static class LayerProjection {
		private long _lowerDate;
		private long _upperDate;
		private long _lowerTime;
		private long _upperTime;
		private boolean _isLowestTime;
		private boolean _isBigestTime;
		private boolean _offData;

		public LayerProjection(long lowerDate, long upperDate, long lowerTime,
				long upperTime, boolean isLowestTime, boolean isBigestTime,
				boolean offData) {
			super();
			this._lowerDate = lowerDate;
			this._upperDate = upperDate;
			this._lowerTime = lowerTime;
			this._upperTime = upperTime;
			this._isLowestTime = isLowestTime;
			this._isBigestTime = isBigestTime;
			this._offData = offData;
		}

		public boolean isOffData() {
			return _offData;
		}

		public void setOffData(boolean offData) {
			this._offData = offData;
		}

		public long getLowerDate() {
			return _lowerDate;
		}

		public void setLowerDate(long lowerDate) {
			this._lowerDate = lowerDate;
		}

		public long getUpperDate() {
			return _upperDate;
		}

		public void setUpperDate(long upperDate) {
			this._upperDate = upperDate;
		}

		public long getLowerDisplayTime() {
			return _lowerTime;
		}

		public void setLowerTime(long lowerTime) {
			this._lowerTime = lowerTime;
		}

		public long getUpperDisplayTime() {
			return _upperTime;
		}

		public void setUpperTime(long upperTime) {
			this._upperTime = upperTime;
		}

		public boolean isLowestTime() {
			return _isLowestTime;
		}

		public void setLowestTime(boolean isLowestTime) {
			this._isLowestTime = isLowestTime;
		}

		public boolean isHighestTime() {
			return _isBigestTime;
		}

		public void setBigestTime(boolean isBigestTime) {
			this._isBigestTime = isBigestTime;
		}

		@Override
		public String toString() {
			return "LayerProjection [lowerDate=" + _lowerDate + ", upperDate="
					+ _upperDate + ", lowerTime=" + _lowerTime + ", upperTime="
					+ _upperTime + ", isLowestTime=" + _isLowestTime
					+ ", isBigestTime=" + _isBigestTime + "]";
		}

		public long getDateLength() {
			return _upperDate - _lowerDate;
		}

		public long getTimeLength() {
			return _upperTime - _lowerTime;
		}

	}

	IPriceModel EMPTY = new EmptyPriceModel();

	public long getLowerDisplayTime(int dataLAyer);

	public long getUpperDisplayTime(int dataLayer);

	public long getLowerPhysicalTime(int dataLayer);

	public long getUpperPhysicalTime(int dataLayer);

	public long getDataLayerLowerDisplayTime(int dataLayer);

	public long getDataLayerUpperDisplayTime(int dataLayer);

	public IPriceCollection getPrices(int dataLayer, long lowerTime,
			long upperTime, int maxNumberOfPoints);

	public IPriceCollection getVolumes(int dataLayer, long lowerTime,
			long upperTime, long lowPrice, long upperPrice);

	public long getPhysicalTime_from_FakeTime(int dataLayer, long fakeTime);

	public long getDisplayTime_from_PhysicalTime(int dataLayer,
			long physicalTime);

	public long getDisplayTimeOffset(int dataLayer, long displayTime,
			long distance);

	public double getTickSize();

	public int getTickScale();

	public Integer getLastPrice(int dataLayer);

	public long getPricesDistance(int dataLayer, long lower, long upper);

	public ITimesOfTheDayCollection getTimeOfTheDayCollection(int dataLayer,
			int hh, int mm, int maxNumOfDays, long lowerFakeTime,
			long upperFakeTime);

	public IChartModel getChartModel();

	public void setChartModel(IChartModel chartModel);

	public long getLowerDisplayTime_from_DisplayTime(int dataLayer,
			long displayLower);

	public long getUpperDisplayTime_from_DisplayTime(int dataLayer,
			long displayUpper);

	public long getLowerPhysicalTime_from_DisplayTime(int dataLayer,
			long displayLower);

	public long getUpperPhysicalTime_from_DisplayTime(int dataLayer,
			long displayUpper);

	public long getPhysicalTime_from_DisplayTime(int layer, long displayTime);

	public long getFakeTime_from_PhysicalTime(int layer, long physicalTime);

	public long getFakeTime_from_DisplayTime(int layer, long displayTime);

	public long getDataLayerPricesCount(int layer);

	public LayerProjection getLayerProjection(int fromLayer,
			long fromLowerTime, long fromUpperTime, int toLayer);

	public LayerProjection getLayerProjection(int fromLayer,
			LayerProjection fromProjection, int toLayer);

	public Long getStartRealtime(int dataLayer);

	public int getVolume_from_FakeTime(int dataLayer, long fakeTime);

	public long getLastTime(int dataLayer);
}
