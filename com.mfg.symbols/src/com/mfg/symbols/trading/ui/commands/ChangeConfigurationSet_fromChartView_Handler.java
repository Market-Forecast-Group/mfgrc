package com.mfg.symbols.trading.ui.commands;

import org.eclipse.core.commands.ExecutionEvent;

import com.mfg.chart.commands.AbstractChartViewHanlder;
import com.mfg.chart.ui.views.AbstractChartView;
import com.mfg.symbols.trading.ui.actions.ChangeConfigurationSetAction;
import com.mfg.symbols.trading.ui.adapters.ChartViewToITradingViewAdapter;

public class ChangeConfigurationSet_fromChartView_Handler extends
		AbstractChartViewHanlder {

	@Override
	protected Object execute(AbstractChartView view, ExecutionEvent event) {
		ChangeConfigurationSetAction.runAction(true,
				new ChartViewToITradingViewAdapter(view));
		return null;
	}

}
