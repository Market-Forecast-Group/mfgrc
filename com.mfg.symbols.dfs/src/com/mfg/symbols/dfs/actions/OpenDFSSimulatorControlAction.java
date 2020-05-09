package com.mfg.symbols.dfs.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Event;

import com.mfg.symbols.dfs.DFSSymbolsPlugin;
import com.mfg.symbols.dfs.ui.DFSSimulatorControlView;
import com.mfg.utils.ImageUtils;
import com.mfg.utils.PartUtils;

public class OpenDFSSimulatorControlAction extends Action {
	public OpenDFSSimulatorControlAction() {
		super("DFS Simulator Control", ImageUtils.getBundledImageDescriptor(
				DFSSymbolsPlugin.PLUGIN_ID, "icons/play.gif"));
	}

	@Override
	public void runWithEvent(Event event) {
		PartUtils.openView(DFSSimulatorControlView.ID);
	}
}
