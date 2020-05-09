package com.mfg.chart.model;

import java.util.Collections;
import java.util.List;

public interface ISyntheticModel {
	ISyntheticModel EMPTY = new ISyntheticModel() {

		@Override
		public int getZZSwings() {
			return 0;
		}

		@Override
		public int getHigherZZScale() {
			return 0;
		}

		@Override
		public void setHigherZZScale(int numberOfScales) {
			//
		}

		@Override
		public void setZZSwings(int numberOfSwings) {
			//
		}

		@Override
		public int getScaleCount() {
			return 0;
		}

		@Override
		public List<List<PivotPoint>> getZigZagDataset() {
			return Collections.EMPTY_LIST;
		}

		@Override
		public long getRealDate(double displayTime) {
			return 0;
		}

		@Override
		public ITimePriceCollection getSecondScalePrices() {
			return ITimePriceCollection.EMPTY;
		}

		@Override
		public int getIndicatorScale(int synthScale) {
			return 0;
		}

		@Override
		public int getDataLayer(int synthScale) {
			return 0;
		}

		@Override
		public void setMatchPivots(boolean matchPivots) {
			//
		}

		@Override
		public boolean isMatchPivots() {
			return false;
		}
	};

	public static class PivotPoint extends ChartPoint implements Cloneable {
		public Boolean downSwing;
		public double thX;
		public double thY;

		public PivotPoint(double aX, double aY, Boolean aDownSwing,
				double aThX, double aThY) {
			super(aX, aY);
			downSwing = aDownSwing;
			thX = aThX;
			thY = aThY;
		}

		@Override
		public PivotPoint clone() {
			PivotPoint c;
			try {
				c = (PivotPoint) super.clone();
				return c;
			} catch (CloneNotSupportedException e) {
				return null;
			}
		}
	}

	public void setMatchPivots(boolean matchPivots);

	public boolean isMatchPivots();

	public int getZZSwings();

	int getScaleCount();

	public int getHigherZZScale();

	void setHigherZZScale(int scale);

	void setZZSwings(int numberOfSwings);

	List<List<PivotPoint>> getZigZagDataset();

	long getRealDate(double displayTime);

	ITimePriceCollection getSecondScalePrices();

	int getIndicatorScale(int synthScale);

	int getDataLayer(int synthScale);
}
