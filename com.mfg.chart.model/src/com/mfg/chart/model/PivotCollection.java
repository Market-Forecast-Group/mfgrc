package com.mfg.chart.model;

import com.mfg.inputdb.indicator.mdb.PivotMDB.Record;

class PivotCollection extends ItemCollection<Record> implements
		IPivotCollection {

	public PivotCollection(Record[] data, int start, int len) {
		super(data, start, len);
	}

	/**
	 * @param data
	 */
	public PivotCollection(Record[] data) {
		super(data);
	}

	@Override
	public long getTime(int index) {
		return getItem(index).pivotTime;
	}

	@Override
	public double getPrice(int index) {
		return getItem(index).pivotPrice;
	}

	@Override
	public long getTHTime(int index) {
		return getItem(index).confirmTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.models.IPivotCollection#isUp(int)
	 */
	@Override
	public boolean isUp(int index) {
		return getItem(index).isUp;
	}

	@Override
	public double getTHPrice(int index) {
		return getItem(index).confirmPrice;
	}

}