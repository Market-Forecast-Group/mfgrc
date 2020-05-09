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
package com.mfg.symbols.trading.old;

import java.io.IOException;

import com.mfg.chart.model.mdb.ChartMDBSession;
import com.mfg.chart.model.mdb.TradeMDB;
import com.mfg.chart.model.mdb.TradeMDB.RandomCursor;
import com.mfg.chart.model.mdb.TradeMDB.Record;
import com.mfg.chart.ui.IChartBrowser;

/**
 * @author arian
 * 
 */
public class OpenPositionChartBrowser implements IChartBrowser {
	private final TradeMDB mdb;
	private Record record;
	private int index = 0;

	public OpenPositionChartBrowser(ChartMDBSession session) throws IOException {
		super();
		mdb = session.connectTo_TradeMDB();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.ui.IChartBrowser#isActive()
	 */
	@Override
	public boolean isActive() {
		return record != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.ui.IChartBrowser#hasNext()
	 */
	@Override
	public boolean hasNext() {
		try {
			return index < mdb.size() - 1;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.ui.IChartBrowser#hasPrev()
	 */
	@Override
	public boolean hasPrev() {
		try {
			return index > 0 && mdb.size() > 0;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.ui.IChartBrowser#moveNext()
	 */
	@Override
	public void moveNext() {
		if (record == null) {
			index = 0;
		} else {
			index++;
		}

		try {
			RandomCursor c = mdb.thread_randomCursor();
			record = mdb.record(c, index);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.ui.IChartBrowser#movePrev()
	 */
	@Override
	public void movePrev() {
		index--;
		try {
			RandomCursor c = mdb.thread_randomCursor();
			record = mdb.record(c, index);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.ui.IChartBrowser#getCurrentTime()
	 */
	@Override
	public long getCurrentTime() {
		return record.openTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.ui.IChartBrowser#getCurrentPrice()
	 */
	@Override
	public double getCurrentPrice() {
		return record.openTime;
	}
}
