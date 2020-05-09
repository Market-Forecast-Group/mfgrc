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

import org.mfg.mdb.runtime.MDBList;

import com.mfg.chart.model.mdb.ChartMDBSession;
import com.mfg.chart.model.mdb.TradeMDB;
import com.mfg.chart.model.mdb.TradeMDB.Record;
import com.mfg.chart.ui.IChartBrowser;

/**
 * @author arian
 * 
 */
public class ClosePositionChartBrowser implements IChartBrowser {
	private int index = 0;
	// private final TradeMDB mdb;
	private Record record;
	private MDBList<Record> _list;

	public ClosePositionChartBrowser(ChartMDBSession session)
			throws IOException {
		super();
		TradeMDB mdb = session.connectTo_TradeMDB();
		_list = mdb.list(mdb.thread_randomCursor());
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
		return index < _list.size() - 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.ui.IChartBrowser#hasPrev()
	 */
	@Override
	public boolean hasPrev() {
		return index > 0 && _list.size() > 0;
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

		record = _list.get(index);
		long size = _list.size();
		while (!record.isClosed) {
			index++;
			record = _list.get(index);
			if (index >= size) {
				record = null;
				return;
			}
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
		record = _list.get(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.ui.IChartBrowser#getCurrentTime()
	 */
	@Override
	public long getCurrentTime() {
		return record.closeTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.ui.IChartBrowser#getCurrentPrice()
	 */
	@Override
	public double getCurrentPrice() {
		return record.closePrice;
	}
}
