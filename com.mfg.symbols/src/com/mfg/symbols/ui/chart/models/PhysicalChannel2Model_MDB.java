package com.mfg.symbols.ui.chart.models;

import java.io.IOException;

import com.mfg.chart.model.ChartModelException;
import com.mfg.chart.model.ChartModel_MDB;
import com.mfg.chart.model.IChannel2Collection;
import com.mfg.chart.model.PhysicalPriceModel_MDB;
import com.mfg.inputdb.indicator.mdb.Channel2MDB;
import com.mfg.inputdb.indicator.mdb.Channel2MDB.Record;
import com.mfg.inputdb.indicator.mdb.IndicatorMDBSession;
import com.mfg.inputdb.prices.mdb.PriceMDB;
import com.mfg.inputdb.prices.mdb.PriceMDB.RandomCursor;
import com.mfg.inputdb.prices.mdb.PriceMDBSession;
import com.mfg.utils.collections.TimeMap;

class PhysicalChannel2Collection extends Channel2Collection {
	private final long _lower;
	private final TimeMap _timeMap;

	public PhysicalChannel2Collection(Record[] data, int degree, long lower,
			TimeMap timeMap) {
		super(data, degree);
		_timeMap = timeMap;
		_lower = lower;
	}

	@Override
	public double getStart(int i) {
		return getRealTime(getItem(i).startTime) - _lower;
	}

	@Override
	public double getEnd(int i) {
		return getRealTime(getItem(i).endTime) - _lower;
	}

	long getRealTime(long index) {
		long time = _timeMap.get((int) index);
		return time;
	}

	@Override
	public double evaluateCentarLine(int i, double time) {
		long realTime = (long) (_lower + time);
		long index = _timeMap.indexOf(realTime);
		return super.evaluateCentarLine(i, index);
	}
}

public class PhysicalChannel2Model_MDB extends Channel2Model_MDB {

	private final PriceMDBSession _priceSession;

	public PhysicalChannel2Model_MDB(IndicatorMDBSession session, int level,
			ChartModel_MDB chartModel) {
		super(session, level, chartModel);
		_priceSession = chartModel.getPriceSession();
	}

	@Override
	public IChannel2Collection getChannels2(int dataLayer, long plower,
			long pupper) {
		try {
			PhysicalPriceModel_MDB priceModel = (PhysicalPriceModel_MDB) getChartModel()
					.getPriceModel();

			PriceMDB pricemdb = priceModel.getMDB(dataLayer);
			Channel2MDB channelmdb = getMDB2(dataLayer);

			long lowest = priceModel.getLowerDisplayTime(dataLayer);
			long lower = plower + lowest;
			long upper = pupper + lowest;

			RandomCursor c1 = pricemdb.thread_randomCursor();

			long startFakeTime = pricemdb.indexOfPhysicalTime(c1, lower);
			long stopFakeTime = pricemdb.indexOfPhysicalTime(c1, upper);

			com.mfg.inputdb.indicator.mdb.Channel2MDB.RandomCursor c3 = channelmdb
					.thread_randomCursor();
			com.mfg.inputdb.indicator.mdb.Channel2MDB.Cursor c4 = channelmdb
					.thread_cursor();
			long start = channelmdb.indexOfStartTime(c3, startFakeTime);
			long stop = channelmdb.indexOfEndTime(c3, stopFakeTime);

			Channel2MDB.Record[] data = channelmdb.select_sparse(c3, c4, start,
					stop + 1, maxNumberOfPointsToShow);

			TimeMap timeMap = _priceSession.getTimeMap(dataLayer);
			return new PhysicalChannel2Collection(data, getDegree(), lowest,
					timeMap);
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}

}
