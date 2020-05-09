package com.mfg.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

import com.mfg.ui.widgets.LicenseDialog;

public class RegisterLicKeyHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		LicenseDialog dlg = new LicenseDialog(HandlerUtil.getActiveShell(event));
		dlg.open();
		return null;
	}

}
