package com.mfg.chart.model;

public interface IAutoTimeLinesModel {
	public IAutoTimeLinesModel EMPTY = new IAutoTimeLinesModel() {

		@Override
		public ITimePriceCollection getAutoTimeLines(int dataLayer) {
			return ITimePriceCollection.EMPTY;
		}
	};

	public ITimePriceCollection getAutoTimeLines(int dataLayer);
}
