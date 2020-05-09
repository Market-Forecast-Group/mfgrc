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

import com.mfg.tradingdb.mdb.ProbabilityPercentMDB.Cursor;
import com.mfg.tradingdb.mdb.ProbabilityPercentMDB.RandomCursor;
import com.mfg.tradingdb.mdb.ProbabilityPercentMDB.Record;
import com.mfg.tradingdb.mdb.TradingMDBSession;

class ProbabilityPercentCollection extends ItemCollection<Record> implements
		IProbabilityCollection {

	private final boolean th;

	public ProbabilityPercentCollection(Record[] data, boolean th1) {
		super(data);
		this.th = th1;
	}

	@Override
	public long getTime(int index) {
		return getItem(index).time;
	}

	@Override
	public double getPositivePrice(int index) {
		Record item = getItem(index);
		return th ? item.posTHPrice : item.posCurrentPrice;
	}

	@Override
	public double getNegativePrice(int index) {
		Record item = getItem(index);
		return th ? item.negTHPrice : item.negCurrentPrice;
	}

	@Override
	public boolean isPositiveTradeDireaction(int index) {
		return getItem(index).posTradeDirection;
	}

}

/**
 * @author arian
 * 
 */
public class ProbabilityPercentModel_MDB extends Model_MDB implements
		IProbabilityModel {

	private com.mfg.tradingdb.mdb.ProbabilityPercentMDB mdb;
	private final boolean thProbs;

	/**
	 * 
	 */
	public ProbabilityPercentModel_MDB(TradingMDBSession session, int level,
			boolean thProbs1) {
		super(session);
		this.thProbs = thProbs1;
		try {
			mdb = session.connectTo_ProbabilityPercentMDB(level);
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.IProbabilityModel#getProbabilities(long, long)
	 */
	@Override
	public IProbabilityCollection getProbabilities(long lowerTime,
			long upperTime) {
		try {
			RandomCursor c1 = mdb.thread_randomCursor();
			Cursor c2 = mdb.thread_cursor();
			Record[] data = mdb.select_sparse__where_Time_in(c1, c2, lowerTime,
					upperTime, maxNumberOfPointsToShow);
			return new ProbabilityPercentCollection(data, thProbs);
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}

}
