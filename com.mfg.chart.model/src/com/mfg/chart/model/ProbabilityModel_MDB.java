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

import com.mfg.tradingdb.mdb.ProbabilityMDB;
import com.mfg.tradingdb.mdb.ProbabilityMDB.Cursor;
import com.mfg.tradingdb.mdb.ProbabilityMDB.RandomCursor;
import com.mfg.tradingdb.mdb.TradingMDBSession;

class ProbabilityCollection extends ItemCollection<ProbabilityMDB.Record>
		implements IProbabilityCollection {

	public ProbabilityCollection(ProbabilityMDB.Record[] data) {
		super(data);
	}

	@Override
	public long getTime(int index) {
		return getItem(index).time;
	}

	@Override
	public double getPositivePrice(int index) {
		return getItem(index).posPrice;
	}

	@Override
	public double getNegativePrice(int index) {
		return getItem(index).negPrice;
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
public class ProbabilityModel_MDB extends Model_MDB implements
		IProbabilityModel {

	private ProbabilityMDB mdb;

	public ProbabilityModel_MDB(TradingMDBSession session, int level) {
		super(session);
		try {
			mdb = session.connectTo_ProbabilityMDB(level);
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.IProbabilityModel2#getProbablities(long, long)
	 */
	@Override
	public IProbabilityCollection getProbabilities(long lowerTime,
			long upperTime) {
		try {
			RandomCursor c1 = mdb.thread_randomCursor();
			Cursor c2 = mdb.thread_cursor();
			ProbabilityMDB.Record[] data = mdb.select_sparse__where_Time_in(c1,
					c2, lowerTime, upperTime, maxNumberOfPointsToShow);
			return new ProbabilityCollection(data);
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}

}
