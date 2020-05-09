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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.mfg.persist.interfaces.IStorageObject;
import com.mfg.persist.interfaces.RemoveException;
import com.mfg.persist.interfaces.SimpleStorage;

/**
 * @author arian
 * 
 */
public class DeleteStorageObjectHandler<T extends IStorageObject> extends
		AbstractHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
	 * ExecutionEvent)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		StructuredSelection sel = (StructuredSelection) HandlerUtil
				.getCurrentSelection(event);
		if (sel != null) {
			Object[] array = sel.toArray();
			String msg = "You are to delete "
					+ (array.length == 0 ? ((IStorageObject) array[0])
							.getName() + "." : array.length + " objects.");
			if (MessageDialog.openConfirm(HandlerUtil.getActiveShell(event),
					"Delete", msg)) {
				for (Object obj : array) {
					try {
						T storageObj = (T) obj;
						SimpleStorage<T> storage = (SimpleStorage<T>) storageObj
								.getStorage();
						if (storage != null) {
							storage.remove((T) obj);
						}
					} catch (RemoveException e) {
						MessageDialog.openInformation(
								HandlerUtil.getActiveShell(event), "Delete",
								e.getMessage());
					}
				}
			}
		}
		return null;
	}
}
