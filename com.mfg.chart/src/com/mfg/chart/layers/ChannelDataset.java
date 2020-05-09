package com.mfg.chart.layers;

import org.mfg.opengl.chart.IDataset;

import com.mfg.chart.model.IChannelCollection;

/**
 * @author arian
 *
 */
class ChannelDataset implements IDataset {

	private final IChannelCollection data;


	public ChannelDataset(final IChannelCollection data1) {
		this.data = data1;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see org.mfg.opengl.chart.IDataset#getSeriesCount()
	 */
	@Override
	public int getSeriesCount() {
		return 1;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see org.mfg.opengl.chart.IDataset#getItemCount(int)
	 */
	@Override
	public int getItemCount(final int series) {
		// 0:start-top, 1:end-top,
		// 2:start-center, 3:end-center
		// 4:start-bottom, 5:end-bottom

		return data.getSize() * 6;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see org.mfg.opengl.chart.IDataset#getX(int, int)
	 */
	@Override
	public double getX(final int series, final int item) {
		final int field = item % 6;
		final int i = item / 6;

		// 0:start-top, 1:end-top,
		// 2:start-center, 3:end-center
		// 4:start-bottom, 5:end-bottom

		if (field % 2 == 0) {
			return data.getStartTime(i);
		}
		return data.getEndTime(i);
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see org.mfg.opengl.chart.IDataset#getY(int, int)
	 */
	@Override
	public double getY(final int series, final int item) {

		// 0:start-top, 1:end-top,
		// 2:start-center, 3:end-center
		// 4:start-bottom, 5:end-bottom

		final int i = item / 6;
		final int field = item % 6;

		switch (field) {
		case 0:
			return data.getStartTopPrice(i);
		case 1:
			return data.getEndTopPrice(i);
		case 2:
			return data.getStartCenterPrice(i);
		case 3:
			return data.getEndCenterPrice(i);
		case 4:
			return data.getStartBottomPrice(i);
		default:// case 5:
			return data.getEndBottomPrice(i);
		}
	}
}