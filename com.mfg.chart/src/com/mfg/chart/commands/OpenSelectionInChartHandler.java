package com.mfg.chart.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.mfg.chart.ui.actions.ShowObjectInChart;

public class OpenSelectionInChartHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		StructuredSelection sel = (StructuredSelection) HandlerUtil
				.getCurrentSelection(event);
		for (Object obj : sel.toArray()) {
			new ShowObjectInChart(obj).run();
		}
		return null;
	}

}
