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

import org.eclipse.core.commands.ExecutionEvent;

import com.mfg.chart.commands.AbstractChartViewHanlder;
import com.mfg.chart.ui.views.AbstractChartView;

/**
 * @author arian
 * 
 */
public class BrowseTradeHandler extends AbstractChartViewHanlder {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.chart.commands.AbstractChartViewHanlder#execute(com.mfg.chart
	 * .ui.views.ChartView, org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	protected Object execute(AbstractChartView view, ExecutionEvent event) {
		// TODO: Migrate to new trading
		// if (view instanceof TradingChartView) {
		// IChartBrowser browser = ((TradingChartView) view).getTradeBrowser();
		// boolean forward = event.getCommand().getId()
		// .endsWith("tradeForward");
		// if (forward && browser.hasNext() || !forward && browser.hasPrev()) {
		// if (forward) {
		// browser.moveNext();
		// } else {
		// browser.movePrev();
		// }
		// PriceChart_OpenGL chart = view.getChart();
		// chart.scrollToPoint(browser.getCurrentTime(),
		// browser.getCurrentPrice());
		// }
		// }
		return null;
	}

}
