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
package com.mfg.chart.commands;

import org.eclipse.core.commands.ExecutionEvent;

import com.mfg.chart.ui.views.AbstractChartView;

/**
 * @author arian
 * 
 */
public class ScrollUpDownHandler extends AbstractChartViewHanlder {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.chart.commands.AbstractChartViewHanlder#execute(com.mfg.chart
	 * .ui.views.AbstractChartView, org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	protected Object execute(AbstractChartView view, ExecutionEvent event) {
		if (event.getCommand().getId().contains("Up")) {
			view.getChart().shiftUp(20);
		} else {
			view.getChart().shiftUp(-20);
		}

		return null;
	}

}
