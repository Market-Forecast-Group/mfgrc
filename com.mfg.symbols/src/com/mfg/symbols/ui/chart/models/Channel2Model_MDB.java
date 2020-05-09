package com.mfg.symbols.ui.chart.models;

import java.io.IOException;

import com.mfg.chart.model.ChannelModel_MDB;
import com.mfg.chart.model.ChartModelException;
import com.mfg.chart.model.ChartModel_MDB;
import com.mfg.chart.model.IChannel2Collection;
import com.mfg.inputdb.indicator.mdb.Channel2MDB;
import com.mfg.inputdb.indicator.mdb.Channel2MDB.Cursor;
import com.mfg.inputdb.indicator.mdb.Channel2MDB.RandomCursor;
import com.mfg.inputdb.indicator.mdb.IndicatorMDBSession;

public class Channel2Model_MDB extends ChannelModel_MDB {

	private final int _degree;

	public Channel2Model_MDB(IndicatorMDBSession session, int level,
			ChartModel_MDB chartModel) {
		super(session, level, chartModel);
		_degree = session.getPolylineDegree();
	}

	public int getDegree() {
		return _degree;
	}

	@Override
	public IChannel2Collection getChannels2(int dataLayer, long lowerTime,
			long upperTime) {
		try {
			Channel2MDB mdb = getMDB2(dataLayer);
			if (mdb.size() == 0)
				return IChannel2Collection.EMPTY;
			
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
			Channel2MDB.Record[] data = mdb.select_sparse(c1, c2, start, stop,
					maxNumberOfPointsToShow);
			return new Channel2Collection(data, _degree);
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}

}
