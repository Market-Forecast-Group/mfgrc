package com.mfg.chart.layers;

import org.mfg.opengl.chart.IDataset;

import com.mfg.chart.model.ITimePriceCollection;

/**
 * Dataset to wrap a price collection.
 *
 * @author arian
 *
 */
public class TimePriceDataset implements IDataset {

	private final ITimePriceCollection collection;

	public TimePriceDataset(final ITimePriceCollection collection1) {
		this.collection = collection1;
	}

	/**
	 * @return the data
	 */
	public ITimePriceCollection getCollection() {
		return collection;
	}

	@Override
	public int getItemCount(final int series) {
		return collection.getSize();
	}

	@Override
	public double getX(final int series, final int item) {
		return collection.getTime(item);
	}

	@Override
	public double getY(final int series, final int item) {
		return collection.getPrice(item);
	}

	@Override
	public int getSeriesCount() {
		return 1;
	}
}
