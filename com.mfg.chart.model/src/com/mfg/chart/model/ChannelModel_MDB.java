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
/**
 * 
 */

package com.mfg.chart.model;

import java.io.IOException;

import com.mfg.inputdb.indicator.mdb.Channel2MDB;
import com.mfg.inputdb.indicator.mdb.ChannelMDB;
import com.mfg.inputdb.indicator.mdb.ChannelMDB.Cursor;
import com.mfg.inputdb.indicator.mdb.ChannelMDB.RandomCursor;
import com.mfg.inputdb.indicator.mdb.ChannelMDB.Record;
import com.mfg.inputdb.indicator.mdb.IndicatorMDBSession;

class ChannelCollection extends ItemCollection<Record> implements
		IChannelCollection {

	public ChannelCollection(Record[] data) {
		super(data);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.models.IChannelCollection#getStartTime(int)
	 */
	@Override
	public long getStartTime(int index) {
		return getItem(index).startTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.models.IChannelCollection#getStartTopPrice(int)
	 */
	@Override
	public double getStartTopPrice(int index) {
		return getItem(index).topStartPrice;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.models.IChannelCollection#getStartCenterPrice(int)
	 */
	@Override
	public double getStartCenterPrice(int index) {
		return getItem(index).centerStartPrice;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.models.IChannelCollection#getStartBottomPrice(int)
	 */
	@Override
	public double getStartBottomPrice(int index) {
		return getItem(index).bottomStartPrice;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.models.IChannelCollection#getEndTime(int)
	 */
	@Override
	public long getEndTime(int index) {
		return getItem(index).endTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.models.IChannelCollection#getEndTopPrice(int)
	 */
	@Override
	public double getEndTopPrice(int index) {
		return getItem(index).topEndPrice;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.models.IChannelCollection#getEndCenterPrice(int)
	 */
	@Override
	public double getEndCenterPrice(int index) {
		return getItem(index).centerEndPrice;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.models.IChannelCollection#getEndBottomPrice(int)
	 */
	@Override
	public double getEndBottomPrice(int index) {
		return getItem(index).bottomEndPrice;
	}

}

/**
 * @author arian
 * 
 */
public class ChannelModel_MDB extends Model_MDB implements IChannelModel {

	private final int _level;
	private final IndicatorMDBSession indicatorSession;

	public ChannelModel_MDB(IndicatorMDBSession session, int level,
			ChartModel_MDB chartModel) {
		super(session);
		indicatorSession = session;
		setChartModel(chartModel);
		this._level = level;
	}

	public IndicatorMDBSession getIndicatorSession() {
		return indicatorSession;
	}

	protected ChannelMDB getMDB(int dataLayer) throws IOException {
		return indicatorSession.connectTo_ChannelMDB(dataLayer, _level);
	}

	protected Channel2MDB getMDB2(int dataLayer) throws IOException {
		return indicatorSession.connectTo_Channel2MDB(dataLayer, _level);
	}

	@Override
	public IChannelCollection getChannels(int dataLayer, long lowerTime,
			long upperTime) {
		try {
			ChannelMDB mdb = getMDB(dataLayer);
			if (mdb.size() == 0)
				return IChannelCollection.EMPTY;

			RandomCursor c1 = mdb.thread_randomCursor();
			Cursor c2 = mdb.thread_cursor();

			long index = mdb.indexOfStartTime(c1, lowerTime);
			long start = index;
			long stop = index;

			index = mdb.indexOfEndTime(c1, lowerTime);
			if (index < start) {
				start = index;
			}
			if (index > stop) {
				stop = index;
			}

			index = mdb.indexOfStartTime(c1, upperTime);
			if (index < start) {
				start = index;
			}
			if (index > stop) {
				stop = index;
			}

			index = mdb.indexOfEndTime(c1, upperTime);
			if (index < start) {
				start = index;
			}
			if (index > stop) {
				stop = index;
			}
			Record[] data = mdb.select_sparse(c1, c2, start, stop,
					maxNumberOfPointsToShow);
			return new ChannelCollection(data);
		} catch (IOException e) {
			throw new ChartModelException(e);
		}

	}

	@Override
	public IChannel2Collection getChannels2(int dataLayer, long lowerTime,
			long upperTime) {
		return IChannel2Collection.EMPTY;
	}
}
