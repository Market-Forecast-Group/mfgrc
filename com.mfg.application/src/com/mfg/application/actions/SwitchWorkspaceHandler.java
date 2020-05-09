package com.mfg.application.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import com.mfg.application.WorkspaceLauncher;

public class SwitchWorkspaceHandler extends AbstractHandler {

	public SwitchWorkspaceHandler() {
	}

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		WorkspaceLauncher pwd = new WorkspaceLauncher(true, null);
		int pick = pwd.open();
		if (pick == Window.CANCEL)
			return null;

		MessageDialog.openInformation(Display.getDefault().getActiveShell(),
				"Switch Workspace",
				"The client will now restart with the new workspace");
		return Boolean.valueOf(PlatformUI.getWorkbench().restart());
	}
}
