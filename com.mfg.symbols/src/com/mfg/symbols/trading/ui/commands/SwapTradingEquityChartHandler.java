package com.mfg.symbols.trading.ui.commands;

import org.eclipse.core.commands.ExecutionEvent;

import com.mfg.chart.commands.AbstractChartViewHanlder;
import com.mfg.chart.ui.views.AbstractChartView;
import com.mfg.chart.ui.views.IChartView;
import com.mfg.symbols.trading.ui.adapters.TradingChartAdapater;

public class SwapTradingEquityChartHandler extends AbstractChartViewHanlder {

	@Override
	protected Object execute(AbstractChartView view, ExecutionEvent event) {
		TradingChartAdapater adapter = (TradingChartAdapater) ((IChartView) view)
				.getContentAdapter();
		adapter.swapTradingAndEquity(view);
		return null;
	}
}
