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

import com.mfg.inputdb.indicator.mdb.BandsMDB;
import com.mfg.inputdb.indicator.mdb.BandsMDB.Cursor;
import com.mfg.inputdb.indicator.mdb.BandsMDB.RandomCursor;
import com.mfg.inputdb.indicator.mdb.BandsMDB.Record;
import com.mfg.inputdb.indicator.mdb.IndicatorMDBSession;

class BandsCollection extends ItemCollection<Record> implements
		IBandsCollection {

	public BandsCollection(Record[] data, int start, int len) {
		super(data, start, len);
	}

	public BandsCollection(Record[] data) {
		super(data);
	}

	@Override
	public double getTopPrice(int index) {
		return getItem(index).topPrice;
	}

	@Override
	public double getCenterPrice(int index) {
		return getItem(index).centerPrice;
	}

	@Override
	public double getBottomPrice(int index) {
		return getItem(index).bottomPrice;
	}

	@Override
	public long getTime(int index) {
		return getItem(index).time;
	}

	@Override
	public double getTopRaw(int index) {
		return getItem(index).topRaw;
	}

	@Override
	public double getCenterRaw(int index) {
		return getItem(index).centerRaw;
	}

	@Override
	public double getBottomRaw(int index) {
		return getItem(index).bottomRaw;
	}

}

public class BandsModel_MDB extends Model_MDB implements IBandsModel {
	private final int _level;
	private final IndicatorMDBSession indicatorSession;

	public BandsModel_MDB(IndicatorMDBSession session, int level,
			ChartModel_MDB chartModel) {
		super(session);
		this.indicatorSession = session;
		this._level = level;
		setChartModel(chartModel);
	}

	@Override
	public int getLevel() {
		return _level;
	}

	public BandsMDB getMDB(int dataLayer) throws IOException {
		BandsMDB mdb = indicatorSession.connectTo_BandsMDB(dataLayer, _level);
		return mdb;
	}

	@Override
	public IBandsCollection getBands(int dataLayer, long lowerTime,
			long upperTime) {
		Record[] data;
		try {
			// long s = currentTimeMillis();
			BandsMDB mdb = getMDB(dataLayer);
			if (mdb.size() == 0) {
				return IBandsCollection.EMPTY;
			}
			RandomCursor c1 = mdb.thread_randomCursor();
			Cursor c2 = mdb.thread_cursor();
			Record first = mdb.first(c1);
			Record last = mdb.last(c1);
			long start = Math.max(lowerTime, first.time) - first.time;
			long stop = Math.min(upperTime, last.time) - first.time;
			data = mdb.select_sparse(c1, c2, start - 1, stop + 1,
					maxNumberOfPointsToShow);
			return new BandsCollection(data);
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}

	@Override
	public boolean containsDataIn(int dataLayer, long lower, long upper) {
		try {
			// long s = currentTimeMillis();
			BandsMDB mdb = getMDB(dataLayer);
			if (mdb.size() == 0) {
				return false;
			}
			RandomCursor c1 = mdb.thread_randomCursor();
			Record first = mdb.first(c1);
			Record last = mdb.last(c1);
			if (lower >= first.time && lower <= last.time) {
				return true;
			}
			if (upper >= first.time && upper <= last.time) {
				return true;
			}
			if (lower <= first.time && upper >= last.time) {
				return true;
			}
			return false;
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}

	public Record[] getAllBands() {
		try {
			BandsMDB mdb = getMDB(_level);
			return mdb.selectAll(mdb.thread_cursor());
		} catch (IOException e) {
			e.printStackTrace();
			throw new ChartModelException(e);
		}
	}

}
