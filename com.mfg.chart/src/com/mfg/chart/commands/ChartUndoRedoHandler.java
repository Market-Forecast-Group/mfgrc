package com.mfg.chart.commands;

import org.eclipse.core.commands.ExecutionEvent;

import com.mfg.chart.ui.views.AbstractChartView;

public class ChartUndoRedoHandler extends AbstractChartViewHanlder {

	@Override
	protected Object execute(AbstractChartView view, ExecutionEvent event) {
		if (event.getCommand().getId().endsWith("undo")) {
			view.getChart().undo();
		} else if (event.getCommand().getId().endsWith("redo")) {
			view.getChart().redo();
		}
		return null;
	}

}
