package com.mfg.chart.commands;

import org.mfg.opengl.chart.interactive.InteractiveTool;

import com.mfg.chart.layers.IndicatorLayer;
import com.mfg.chart.ui.interactive.PolylineTool;
import com.mfg.chart.ui.views.ChartContentAdapter;
import com.mfg.utils.ImageUtils;

public class PolylineToolAction extends SelectToolAction {

	public PolylineToolAction(ChartContentAdapter adapter) {
		super(adapter, POLY_LINES_CMD_ID, ImageUtils.getBundledImageDescriptor(
				"com.mfg.chart", "icons/PL_16.png"), PolylineTool.class);
	}

	@Override
	public void run() {
		InteractiveTool selTool = _chart.getGLChart()
				.getSelectedTool();
		PolylineTool tool = selTool instanceof PolylineTool? (PolylineTool) selTool : null;
		if (tool == null) {
			// select the tool
			super.run();
		}

		IndicatorLayer indicatorLayer = _chart.getIndicatorLayer();
		Integer selectedScale = indicatorLayer.getSelectedScale();
		if (selectedScale == null) {
			if (tool != null) {
				super.run(); // de-select the tool
			}
		} else {
			// the tool is selected, get it...
			tool = (PolylineTool) _chart.getGLChart().getSelectedTool();
			// ...and show the equation
			indicatorLayer.setSelectedScale(null);
			int n = selectedScale.intValue();
			tool.setCombination(n);
			tool.repaint();
		}

	}
}
