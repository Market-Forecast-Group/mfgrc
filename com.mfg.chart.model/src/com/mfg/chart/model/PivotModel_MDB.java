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

package com.mfg.chart.model;

import java.io.IOException;

import com.mfg.inputdb.indicator.mdb.IndicatorMDBSession;
import com.mfg.inputdb.indicator.mdb.PivotMDB;
import com.mfg.inputdb.indicator.mdb.PivotMDB.Cursor;
import com.mfg.inputdb.indicator.mdb.PivotMDB.RandomCursor;
import com.mfg.inputdb.indicator.mdb.PivotMDB.Record;

public class PivotModel_MDB extends Model_MDB implements IPivotModel {

	protected final int _level;
	protected final IndicatorMDBSession _indicatorSession;

	public PivotModel_MDB(IndicatorMDBSession session, int level,
			ChartModel_MDB chartModel) {
		super(session);
		this._indicatorSession = session;
		this._level = level;
		setChartModel(chartModel);
	}

	protected com.mfg.inputdb.indicator.mdb.PivotMDB getMDB(int dataLayer)
			throws IOException {
		return _indicatorSession.connectTo_PivotMDB(dataLayer, _level);
	}

	@Override
	public int getPivotsCount(int dataLayer) {
		try {
			return (int) getMDB(dataLayer).size();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public IPivotCollection getPivotAtIndex(int dataLayer, int index) {
		try {
			PivotMDB mdb = getMDB(dataLayer);
			if (mdb.size() == 0) {
				return IPivotCollection.EMPTY;
			}
			RandomCursor c = mdb.thread_randomCursor();
			Record r = mdb.record(c, index);
			return new PivotCollection(new Record[] { r });
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}

	@SuppressWarnings("resource")
	@Override
	public IPivotCollection getNegPivots(int dataLayer, long lowerTime,
			long upperTime) {
		try {
			PivotMDB mdb = getMDB(dataLayer);
			if (mdb.size() == 0)
				return IPivotCollection.EMPTY;
			RandomCursor c1 = mdb.thread_randomCursor();
			Cursor c2 = mdb.thread_cursor();
			Record[] data = mdb.select_sparse__where_PivotTime_in(c1, c2,
					lowerTime, upperTime, maxNumberOfPointsToShow);
			return new PivotCollection(data);
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}

	@Override
	public int countNegPivots(int dataLayer, long lower, long upper) {
		try {
			PivotMDB mdb = getMDB(dataLayer);
			if (mdb.size() == 0) {
				return 0;
			}
			RandomCursor c = mdb.thread_randomCursor();
			long start = mdb.indexOfPivotTime(c, lower);
			long stop = mdb.indexOfPivotTime(c, upper);
			return (int) (stop - start);
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}

	@Override
	public int getLevel() {
		return _level;
	}
}
