package com.mfg.chart.commands;

import static java.lang.System.out;

import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.INamedHandleStateIds;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.State;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.CommandContributionItem;
import org.mfg.opengl.chart.interactive.InteractiveTool;

import com.mfg.chart.ui.views.AbstractChartView;

public class HideLinesHandler extends AbstractChartViewHanlder {

	private static final String CMD_ID = "com.mfg.chart.commands.hideLines";

	@Override
	protected Object execute(AbstractChartView view, ExecutionEvent event) {
		String cls = event.getParameter("toolClass");

		for (InteractiveTool tool : view.getChart().getTools()) {
			String name = tool.getClass().getName();
			if (name.equals(cls)) {
				tool.setAlwaysPaint(!tool.isAlwaysPaint());
				updateName(event, tool);
				tool.repaint();
				break;
			}
		}

		return null;
	}

	public static void updateName(ExecutionEvent event, InteractiveTool tool) {
		updateName(event, (tool.isAlwaysPaint() ? "Hide" : "Show") + " Lines",
				tool.getClass().getName());
	}

	private static void updateName(ExecutionEvent event, String name, String cls) {
		IViewSite site = (IViewSite) HandlerUtil.getActiveSite(event);

		IMenuManager menu = site.getActionBars().getMenuManager();
		CommandContributionItem cmdItem = findCommand(menu, CMD_ID, cls);
		State s = cmdItem.getCommand().getCommand()
				.getState(INamedHandleStateIds.NAME);
		if (s == null) {
			s = new State();
			cmdItem.getCommand().getCommand()
					.addState(INamedHandleStateIds.NAME, s);
		}
		s.setValue(name);
		cmdItem.update();

		out.println("rename " + cmdItem.getCommand().getId() + ":" + cls
				+ " to " + name);
	}

	private static CommandContributionItem findCommand(IMenuManager menu,
			String id, String cls) {
		for (IContributionItem i : menu.getItems()) {
			if (i instanceof IMenuManager) {
				CommandContributionItem cmdItem = findCommand((IMenuManager) i,
						id, cls);
				if (cmdItem != null) {
					return cmdItem;
				}
			} else if (i instanceof CommandContributionItem) {
				CommandContributionItem cmdItem = (CommandContributionItem) i;
				ParameterizedCommand cmd2 = cmdItem.getCommand();
				if (cmd2.getId().equals(id)) {
					Map<?, ?> parameterMap = cmd2.getParameterMap();
					Object cls2 = parameterMap.get("toolClass");
					if (cls.equals(cls2)) {
						return cmdItem;
					}
				}
			}
		}
		return null;
	}

}
