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
package com.mfg.symbols.trading.old;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.mfg.chart.model.ChartPoint;
import com.mfg.chart.model.mdb.ChartMDBSession;
import com.mfg.chart.ui.IChartBrowser;

/**
 * @author arian
 * 
 */
public class TradeChartBrowser implements IChartBrowser {
	private final OpenPositionChartBrowser openBrowser;
	private final ClosePositionChartBrowser closeBrowser;
	private final List<ChartPoint> list;
	private int index;
	private final Comparator<? super ChartPoint> comp;

	/**
	 * @param session
	 * @throws IOException
	 * 
	 */
	public TradeChartBrowser(ChartMDBSession session) throws IOException {
		openBrowser = new OpenPositionChartBrowser(session);
		closeBrowser = new ClosePositionChartBrowser(session);
		list = new ArrayList<>();
		index = 0;
		comp = new Comparator<ChartPoint>() {

			@Override
			public int compare(ChartPoint o1, ChartPoint o2) {
				return new Double(o1.x).compareTo(new Double(o2.x));
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.ui.IChartBrowser#isActive()
	 */
	@Override
	public boolean isActive() {
		return !list.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.ui.IChartBrowser#hasNext()
	 */
	@Override
	public boolean hasNext() {
		if (list.size() < 2) {
			advanceBrowsers();
		}
		return index < list.size() - 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.ui.IChartBrowser#hasPrev()
	 */
	@Override
	public boolean hasPrev() {
		return index > 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.ui.IChartBrowser#moveNext()
	 */
	@Override
	public void moveNext() {
		advanceBrowsers();
		index++;

	}

	/**
	 * 
	 */
	public void advanceBrowsers() {
		if (openBrowser.hasNext()) {
			openBrowser.moveNext();
			list.add(new ChartPoint(openBrowser.getCurrentTime(), openBrowser
					.getCurrentPrice()));
		}
		if (closeBrowser.hasNext()) {
			closeBrowser.moveNext();
			list.add(new ChartPoint(closeBrowser.getCurrentTime(), closeBrowser
					.getCurrentPrice()));
		}
		Collections.sort(list, comp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.ui.IChartBrowser#movePrev()
	 */
	@Override
	public void movePrev() {
		index--;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.ui.IChartBrowser#getCurrentTime()
	 */
	@Override
	public long getCurrentTime() {
		return (long) list.get(index).x;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.ui.IChartBrowser#getCurrentPrice()
	 */
	@Override
	public double getCurrentPrice() {
		return (long) list.get(index).y;
	}
}
