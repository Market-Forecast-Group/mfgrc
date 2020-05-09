package com.mfg.chart.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.mfg.opengl.chart.interactive.InteractiveTool;

import com.mfg.chart.ui.interactive.IAnchorTool;
import com.mfg.chart.ui.views.AbstractChartView;

public class DeletePointedAnchorHandler extends AbstractChartViewHanlder {

	@Override
	protected Object execute(AbstractChartView view, ExecutionEvent event) {
		InteractiveTool tool = view.getChart().getGLChart().getSelectedTool();
		if (tool != null && tool instanceof IAnchorTool) {
			((IAnchorTool) tool).deletePointedAnchor();
		}
		return null;
	}

}
