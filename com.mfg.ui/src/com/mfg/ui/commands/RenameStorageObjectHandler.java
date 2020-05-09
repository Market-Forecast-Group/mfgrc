/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 * 
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 * 
 * @version $Revision$: $Date$:
 * $Id$:
 */
package com.mfg.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.handlers.HandlerUtil;

import com.mfg.persist.interfaces.IStorageObject;

/**
 * @author arian
 * 
 */
public class RenameStorageObjectHandler extends AbstractHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
	 * ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		StructuredSelection sel = (StructuredSelection) HandlerUtil
				.getCurrentSelection(event);
		if (sel != null) {
			Object elem = sel.getFirstElement();
			if (elem instanceof IStorageObject) {
				IStorageObject obj = (IStorageObject) elem;
				if (obj.allowRename()) {
					String name = obj.getName();
					InputDialog dialog = new InputDialog(
							HandlerUtil.getActiveShell(event), "Rename",
							"New name:", name, new IInputValidator() {

								@Override
								public String isValid(String newText) {
									return newText.length() > 0 ? null
											: "Invalid name.";
								}
							});
					if (dialog.open() == Window.OK) {
						name = dialog.getValue();
						obj.setName(name);
					}
				}
			}
		}
		return null;
	}

}
