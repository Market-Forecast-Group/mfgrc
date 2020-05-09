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

import com.mfg.tradingdb.mdb.TradeMDB.Record;

/**
 * @author arian
 * 
 */
public class TradeCollection extends ItemCollection<Record> implements
		ITradeCollection {

	/**
	 * @param data
	 */
	public TradeCollection(Record[] data) {
		super(data);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.ITradeCollection#getOpenTime(int)
	 */
	@Override
	public long getOpenTime(int index) {
		return getItem(index).openTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.ITradeCollection#getOpenPrice(int)
	 */
	@Override
	public double getOpenPrice(int index) {
		return getItem(index).openPrice;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.ITradeCollection#getOpenings(int)
	 */
	@Override
	public long[] getOpenings(int index) {
		Record item = getItem(index);
		long[] openings = new long[item.openingCount];
		if (openings.length > 0) {
			openings[0] = item.opening0;
		}
		if (openings.length > 1) {
			openings[1] = item.opening1;
		}
		return openings;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.ITradeCollection#getCloseTime(int)
	 */
	@Override
	public long getCloseTime(int index) {
		return getItem(index).closeTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.ITradeCollection#getClosePrice(int)
	 */
	@Override
	public double getClosePrice(int index) {
		return getItem(index).closePrice;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.ITradeCollection#isGain(int)
	 */
	@Override
	public boolean isGain(int index) {
		return getItem(index).isGain;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.ITradeCollection#isLong(int)
	 */
	@Override
	public boolean isLong(int index) {
		return getItem(index).isLong;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.ITradeCollection#isClosed(int)
	 */
	@Override
	public boolean isClosed(int index) {
		return getItem(index).isClosed;
	}
}
