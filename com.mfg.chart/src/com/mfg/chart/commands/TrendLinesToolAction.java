package com.mfg.chart.commands;

import com.mfg.chart.ui.interactive.TrendLinesTool;
import com.mfg.chart.ui.views.ChartContentAdapter;
import com.mfg.utils.ImageUtils;

public class TrendLinesToolAction extends SelectToolAction {

	public TrendLinesToolAction(ChartContentAdapter adapter) {
		super(adapter, TrendLinesHandler.CMD_ID, ImageUtils
				.getBundledImageDescriptor("com.mfg.chart",
						"icons/trend-tool.png"), TrendLinesTool.class);
	}

}
