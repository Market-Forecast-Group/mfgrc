package com.marketforecastgroup.dfsa.app.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.marketforecastgroup.dfsa.DFSAPlugin;

public class SaveWorkspaceHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		DFSAPlugin.saveWorkspace();
		return null;
	}

}
