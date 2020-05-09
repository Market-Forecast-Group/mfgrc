/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 * 
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 * 
 * @version $Revision$: $Date$:
 * $Id$:
 */
package com.mfg.chart.model;

import java.io.IOException;

import com.mfg.inputdb.indicator.mdb.BandsMDB;
import com.mfg.inputdb.indicator.mdb.BandsMDB.Cursor;
import com.mfg.inputdb.indicator.mdb.BandsMDB.RandomCursor;
import com.mfg.inputdb.indicator.mdb.BandsMDB.Record;
import com.mfg.inputdb.indicator.mdb.IndicatorMDBSession;

class PhysicalBandsCollection extends BandsCollection {

	private final long _lower;

	public PhysicalBandsCollection(long lower, Record[] data) {
		super(data);
		this._lower = lower;
	}

	@Override
	public long getTime(int index) {
		return getItem(index).physicalTime - _lower;
	}

}

/**
 * @author arian
 * 
 */
public class PhysicalBandsModel_MDB extends BandsModel_MDB {

	public PhysicalBandsModel_MDB(IndicatorMDBSession indicatorSession,
			int level, ChartModel_MDB chartModel) {
		super(indicatorSession, level, chartModel);
	}

	@Override
	public IBandsCollection getBands(int dataLayer, long aLowerTime,
			long aUpperTime) {
		Record[] data;
		try {
			BandsMDB mdb = getMDB(dataLayer);
			if (mdb.size() == 0) {
				return IBandsCollection.EMPTY;
			}
			RandomCursor c1 = mdb.thread_randomCursor();
			Cursor c2 = mdb.thread_cursor();
			long lower = getLowerDisplayTime(dataLayer);

			long lowerTime = aLowerTime + lower;
			long upperTime = aUpperTime + lower;

			data = mdb.select_sparse__where_PhysicalTime_in(c1, c2, lowerTime,
					upperTime, maxNumberOfPointsToShow);
			return new PhysicalBandsCollection(lower, data);
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}
}
