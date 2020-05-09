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

import com.mfg.inputdb.indicator.mdb.ChannelMDB;
import com.mfg.inputdb.indicator.mdb.ChannelMDB.Cursor;
import com.mfg.inputdb.indicator.mdb.ChannelMDB.RandomCursor;
import com.mfg.inputdb.indicator.mdb.ChannelMDB.Record;
import com.mfg.inputdb.indicator.mdb.IndicatorMDBSession;

class PhysicalChannelCollection extends ChannelCollection {

	private final long _lower;

	public PhysicalChannelCollection(long lower, Record[] data) {
		super(data);
		this._lower = lower;
	}

	@Override
	public long getStartTime(int index) {
		return getItem(index).startPhysicalTime - _lower;
	}

	@Override
	public long getEndTime(int index) {
		return getItem(index).endPhysicalTime - _lower;
	}

}

/**
 * @author arian
 * 
 */
public class PhysicalChannelModel_MDB extends ChannelModel_MDB {

	public PhysicalChannelModel_MDB(IndicatorMDBSession session, int level,
			ChartModel_MDB chartModel) {
		super(session, level, chartModel);
	}

	@Override
	public IChannelCollection getChannels(int dataLayer, long aLowerTime,
			long aUpperTime) {
		try {
			long lower = getLowerDisplayTime(dataLayer);
			long lowerTime = aLowerTime + lower;
			long upperTime = aUpperTime + lower;

			ChannelMDB mdb = getMDB(dataLayer);
			RandomCursor c1 = mdb.thread_randomCursor();
			Cursor c2 = mdb.thread_cursor();

			long index = mdb.indexOfStartPhysicalTime(c1, lowerTime);
			long start = index;
			long stop = index;

			index = mdb.indexOfEndPhysicalTime(c1, lowerTime);
			if (index < start) {
				start = index;
			}
			if (index > stop) {
				stop = index;
			}

			index = mdb.indexOfStartPhysicalTime(c1, upperTime);
			if (index < start) {
				start = index;
			}
			if (index > stop) {
				stop = index;
			}

			index = mdb.indexOfEndPhysicalTime(c1, upperTime);
			if (index < start) {
				start = index;
			}
			if (index > stop) {
				stop = index;
			}
			Record[] data = mdb.select_sparse(c1, c2, start, stop,
					maxNumberOfPointsToShow);
			return new PhysicalChannelCollection(lower, data);
		} catch (IOException e) {
			throw new ChartModelException(e);
		}

	}

}
