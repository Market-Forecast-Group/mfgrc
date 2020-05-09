package com.mfg.chart.model;


/**
 * Represents a collection of prices
 * 
 * @author arian
 * 
 */
public interface IPriceCollection extends ITimePriceCollection {
	IPriceCollection EMPTY = new IPriceCollection() {

		@Override
		public int getSize() {
			return 0;
		}

		@Override
		public long getTime(int index) {
			return 0;
		}

		@Override
		public double getPrice(int index) {
			return 0;
		}

		@Override
		public boolean isReal(int index) {
			return false;
		}
	};

	public boolean isReal(int index);

}
