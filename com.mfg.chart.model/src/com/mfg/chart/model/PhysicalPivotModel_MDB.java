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

class PhysicalPivotCollection extends PivotCollection {

	private final long _lower;

	/**
	 * @param data
	 */
	public PhysicalPivotCollection(long lower, Record[] data) {
		super(data);
		this._lower = lower;
	}

	@Override
	public long getTime(int index) {
		return getItem(index).pivotPhysicalTime - _lower;
	}

	@Override
	public long getTHTime(int index) {
		return getItem(index).confirmPhysicalTime - _lower;
	}
}

public class PhysicalPivotModel_MDB extends PivotModel_MDB {

	public PhysicalPivotModel_MDB(IndicatorMDBSession session, int level,
			ChartModel_MDB chartModel) {
		super(session, level, chartModel);
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
			return new PhysicalPivotCollection(getLowerDisplayTime(dataLayer),
					new Record[] { r });
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}

	@SuppressWarnings("resource")
	@Override
	public IPivotCollection getNegPivots(int dataLayer, long aLowerTime,
			long aUpperTime) {
		try {
			long lower = getLowerDisplayTime(dataLayer);
			long lowerTime = aLowerTime + lower;
			long upperTime = aUpperTime + lower;

			PivotMDB mdb = getMDB(dataLayer);
			if (mdb.size() == 0)
				return IPivotCollection.EMPTY;

			RandomCursor c1 = mdb.thread_randomCursor();
			Cursor c2 = mdb.thread_cursor();
			Record[] data = mdb.select_sparse__where_PivotPhysicalTime_in(c1,
					c2, lowerTime, upperTime, maxNumberOfPointsToShow);

			PhysicalPivotCollection col = new PhysicalPivotCollection(
					getLowerDisplayTime(dataLayer), data);
			return col;
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}

	@Override
	public int countNegPivots(int dataLayer, long aLowerTime, long aUpperTime) {
		try {
			long lower = getLowerDisplayTime(dataLayer);

			long lowerTime = aLowerTime + lower;
			long upperTime = aUpperTime + lower;

			PivotMDB mdb = _indicatorSession.connectTo_PivotMDB(dataLayer,
					_level);
			if (mdb.size() == 0)
				return 0;
			RandomCursor c = mdb.thread_randomCursor();

			long start = mdb.indexOfPivotPhysicalTime(c, lowerTime);
			long stop = mdb.indexOfPivotPhysicalTime(c, upperTime);
			return (int) (stop - start);
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}

}
