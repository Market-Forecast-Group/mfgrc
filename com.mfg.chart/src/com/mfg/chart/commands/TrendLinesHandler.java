package com.mfg.chart.commands;

import com.mfg.chart.ui.interactive.TrendLinesTool;

public class TrendLinesHandler extends SelectToolHandler {

	public static final String CMD_ID = "com.mfg.chart.commands.trendLines";

	public TrendLinesHandler() {
		super(TrendLinesTool.class);
	}

}
