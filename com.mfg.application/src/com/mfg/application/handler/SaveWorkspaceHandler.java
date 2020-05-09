package com.mfg.application.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.mfg.persist.interfaces.PersistInterfacesPlugin;

public class SaveWorkspaceHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		PersistInterfacesPlugin.getDefault().saveWorkspace();
		return null;
	}

}
