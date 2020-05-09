package com.mfg.chart.commands;

import org.eclipse.jface.resource.ImageDescriptor;
import org.mfg.opengl.chart.GLChart;
import org.mfg.opengl.chart.interactive.InteractiveTool;

import com.mfg.chart.ui.views.ChartContentAdapter;

public class SelectToolAction extends ChartAction {
	public static final String TIME_LINES_CMD_ID = "com.mfg.chart.commands.showTimeLines";
	public static final String HARMONIC_LINES_CMD_ID = ShowHarmonyLinesHandler.SHOW_HARMONIC_LINES_COMMAND;
	public static final String POLY_LINES_CMD_ID = "com.mfg.chart.commands.showPolylines";
	public static final String LINES_TOOL_CMD_ID = "com.mfg.chart.commands.lineTool";
	public static final String[] COMMON_CMD_IDS = { TIME_LINES_CMD_ID,
			HARMONIC_LINES_CMD_ID, POLY_LINES_CMD_ID, LINES_TOOL_CMD_ID };

	private final String _toolClassName;
	private boolean _inGroup;

	public SelectToolAction(ChartContentAdapter adapter, String id,
			ImageDescriptor icon, Class<?> toolClass) {
		this(adapter, id, icon, toolClass.getName(), true);
	}

	public SelectToolAction(ChartContentAdapter adapter, String id,
			ImageDescriptor icon, Class<?> toolClass, boolean inGroup) {
		this(adapter, id, icon, toolClass.getName(), inGroup);
	}

	public SelectToolAction(ChartContentAdapter adapter, String id,
			ImageDescriptor icon, String toolClassName, boolean inGroup) {
		super(adapter, id, icon, AS_CHECK_BOX);
		_toolClassName = toolClassName;
		_inGroup = inGroup;
	}

	@Override
	public void run() {
		GLChart glChart = _chart.getGLChart();
		InteractiveTool selectedTool = glChart.getSelectedTool();
		InteractiveTool tool = selectedTool;

		if (tool != null && tool.getClass().getName().equals(_toolClassName)) {
			glChart.setSelectedTool(null);
		} else {
			for (InteractiveTool tool2 : _chart.getTools()) {
				if (tool2.getClass().getName().equals(_toolClassName)) {
					glChart.setSelectedTool(tool2);
					tool2.repaint();
					break;
				}
			}
		}
		selectedTool = glChart.getSelectedTool();
		if (_inGroup) {
			for (ChartAction action : _adapter.getActionMap().values()) {
				if (action instanceof SelectToolAction) {
					SelectToolAction selAction = (SelectToolAction) action;
					if (selAction._inGroup) {
						boolean checked = selectedTool != null
								&& selAction._toolClassName.equals(selectedTool
										.getClass().getName());
						action.setChecked(checked);
					}
				}
			}
		}
	}
}
