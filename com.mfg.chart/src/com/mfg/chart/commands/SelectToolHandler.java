package com.mfg.chart.commands;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.State;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.RegistryToggleState;
import org.eclipse.ui.menus.CommandContributionItem;
import org.mfg.opengl.chart.GLChart;
import org.mfg.opengl.chart.interactive.InteractiveTool;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.ui.views.AbstractChartView;

public abstract class SelectToolHandler extends AbstractChartViewHanlder {

	private final Class<?> _toolClass;

	public SelectToolHandler(Class<?> toolClass) {
		super();
		_toolClass = toolClass;
	}

	@Override
	protected Object execute(AbstractChartView view, ExecutionEvent event) {
		Chart chart = view.getChart();
		GLChart glChart = chart.getGLChart();
		InteractiveTool selectedTool = glChart.getSelectedTool();
		InteractiveTool tool = selectedTool;

		if (tool != null && tool.getClass() == _toolClass) {
			// deselect it
			glChart.setSelectedTool(null);
		} else {
			// select it
			for (InteractiveTool tool2 : chart.getTools()) {
				if (tool2.getClass() == _toolClass) {
					glChart.setSelectedTool(tool2);
					break;
				}
			}
		}
		chart.syncRepaint();

		IViewSite site = (IViewSite) HandlerUtil.getActiveSite(event);

		Command currentCmd = event.getCommand();

		for (IContributionItem item : site.getActionBars().getToolBarManager()
				.getItems()) {
			if (item instanceof CommandContributionItem) {
				if (item.isVisible() && !item.isSeparator()) {
					CommandContributionItem cmdItem = (CommandContributionItem) item;
					Command cmd = cmdItem.getCommand().getCommand();
					if (cmd != currentCmd) {
						State state = cmd
								.getState(RegistryToggleState.STATE_ID);
						if (state != null) {
							state.setValue(new Boolean(true));
							state.setValue(new Boolean(false));
						}
					}
				}
			}
		}

		if (!(((Event) event.getTrigger()).widget instanceof ToolItem)) {
			try {
				HandlerUtil.toggleCommandState(currentCmd);
			} catch (org.eclipse.core.commands.ExecutionException e) {
				e.printStackTrace();
			}
		}
		selectedTool = glChart.getSelectedTool();
		if (selectedTool != null) {
			HideLinesHandler.updateName(event, selectedTool);
		}
		return null;
	}
}
