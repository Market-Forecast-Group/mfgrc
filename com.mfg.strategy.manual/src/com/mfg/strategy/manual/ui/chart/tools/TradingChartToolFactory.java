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
package com.mfg.strategy.manual.ui.chart.tools;

import org.mfg.opengl.chart.interactive.InteractiveTool;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.ui.osd.tools.InteractiveToolFactory;

/**
 * @author arian
 * 
 */
public class TradingChartToolFactory extends InteractiveToolFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.chart.ui.osd.tools.InteractiveToolFactory#createTool(com.mfg.
	 * chart.backend.opengl.PriceChart_OpenGL)
	 */
	@Override
	public InteractiveTool createTool(Chart priceChart) {
		if (priceChart.getType().hasExecutions()) {
			return new TradingChartTool(priceChart);
		}
		return null;
	}

}
