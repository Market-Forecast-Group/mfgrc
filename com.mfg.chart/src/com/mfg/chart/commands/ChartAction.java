package com.mfg.chart.commands;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.bindings.TriggerSequence;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.keys.IBindingService;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.ui.views.ChartContentAdapter;

public class ChartAction extends Action {
	protected final Chart _chart;
	protected ChartContentAdapter _adapter;

	public ChartAction(ChartContentAdapter adapter, String id,
			ImageDescriptor icon, int style) {
		super(null, style);
		_adapter = adapter;
		_chart = adapter.getChart();
		if (icon != null) {
			setImageDescriptor(icon);
		}
		if (id != null) {
			setActionDefinitionId(id);

			ICommandService cmdServ = (ICommandService) PlatformUI
					.getWorkbench().getService(ICommandService.class);
			Command cmd = cmdServ.getCommand(id);
			IBindingService keyServ = (IBindingService) PlatformUI
					.getWorkbench().getService(IBindingService.class);
			TriggerSequence[] keys = keyServ.getActiveBindingsFor(id);
			try {
				String name = cmd.getName();
				if (keys.length > 0) {
					String fmt = keys[0].format();
					// I don't know why sometimes the command name gets the key
					// but other don't.
					if (!name.contains(fmt)) {
						name += " (" + fmt + ")";
					}
				}
				setText(name);
			} catch (NotDefinedException e) {
				e.printStackTrace();
			}
		}
	}
}
