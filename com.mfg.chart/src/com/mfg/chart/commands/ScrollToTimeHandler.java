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
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.ui.views.AbstractChartView;

/**
 * @author arian
 * 
 */
public class ScrollToTimeHandler extends AbstractChartViewHanlder {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.chart.commands.AbstractChartViewHanlder#execute(com.mfg.chart
	 * .ui.views.ChartView, org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	protected Object execute(AbstractChartView view, ExecutionEvent event) {
		final Chart chart = view.getChart();
		InputDialog dialog = new InputDialog(view.getSite().getShell(),
				"Scroll to Time", "Enter a time to show in the chart",
				Long.toString((long) chart.getXRange().getMiddle()),
				new IInputValidator() {

					@Override
					public String isValid(String newText) {
						try {
							long time = Long.parseLong(newText);
							if (time < 0) {
								return "Enter a positive time";
							}
						} catch (Exception e) {
							return "Invalid number format";
						}
						return null;
					}
				});

		if (dialog.open() == Window.OK) {
			String result = dialog.getValue();
			long time = Long.parseLong(result);
			chart.scrollToPoint(time, chart.getYRange().getMiddle());
		}

		return null;
	}

}
