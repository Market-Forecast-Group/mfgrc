package com.mfg.chart.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.mfg.chart.ui.views.AbstractChartView;

public abstract class AbstractChartViewHanlder extends AbstractHandler {
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		IWorkbenchPart part = HandlerUtil.getActivePart(event);
		if (part != null && part instanceof AbstractChartView) {
			final AbstractChartView view = (AbstractChartView) part;
			return execute(view, event);
		}
		return null;
	}

	protected abstract Object execute(AbstractChartView view,
			ExecutionEvent event);

}
