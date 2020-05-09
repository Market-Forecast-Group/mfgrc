package com.mfg.chart.model;

import java.io.IOException;

import com.mfg.inputdb.indicator.mdb.IndicatorMDBSession;
import com.mfg.inputdb.indicator.mdb.PivotMDB;
import com.mfg.inputdb.indicator.mdb.PivotMDB.RandomCursor;
import com.mfg.inputdb.indicator.mdb.PivotMDB.Record;

public class AutoTimeLinesModel_MDB extends Model_MDB implements
		IAutoTimeLinesModel {

	private final int _level;
	private final IndicatorMDBSession _indicatorSession;

	public AutoTimeLinesModel_MDB(IndicatorMDBSession indicatorSession,
			int level, ChartModel_MDB chartModel) {
		super(indicatorSession);
		setChartModel(chartModel);
		_level = level;
		_indicatorSession = indicatorSession;
	}

	@Override
	public ITimePriceCollection getAutoTimeLines(int dataLayer) {
		try {
			PivotMDB mdb = _indicatorSession.connectTo_PivotMDB(dataLayer,
					_level);
			long size = mdb.size();
			if (size > 1) {
				RandomCursor c = mdb.thread_randomCursor();
				Record p1 = mdb.record(c, size - 2);
				Record p2 = mdb.last(c);
				return new PivotCollection(new Record[] { p1, p2 });
			}
			return ITimePriceCollection.EMPTY;
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}

}
