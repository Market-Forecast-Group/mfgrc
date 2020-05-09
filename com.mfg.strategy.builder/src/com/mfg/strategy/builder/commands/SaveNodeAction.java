/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 *
 * $Id: $
 */
/**
 * @author <a href="mailto:gardero@gmail.com">Enrique Matos</a>, MFG
 *
 * @version $Revision: $ $Date: $
 */

package com.mfg.strategy.builder.commands;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;

import com.mfg.utils.Utils;

public class SaveNodeAction extends Action {

	public SaveNodeAction() {
		// force calculateEnabled() to be called in every context
		init();
	}


	protected void init() {
		ISharedImages sharedImages =
				PlatformUI.getWorkbench().getSharedImages();
		setText("Save Strategy");
		setId(ActionFactory.SAVE.getId());
		// setActionDefinitionId(ActionFactory.SAVE.getId());
		setHoverImageDescriptor(sharedImages
				.getImageDescriptor(ISharedImages.IMG_ETOOL_SAVE_EDIT));
		setImageDescriptor(sharedImages
				.getImageDescriptor(ISharedImages.IMG_ETOOL_SAVE_EDIT));
		setDisabledImageDescriptor(sharedImages
				.getImageDescriptor(ISharedImages.IMG_ETOOL_SAVE_EDIT_DISABLED));
	}


	@Override
	public boolean isEnabled() {
		IWorkbenchPage page =
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		boolean dirty = page.getActiveEditor().isDirty();
		Utils.debug_var(12345, "me " + dirty);
		return dirty;
	}


	@Override
	public void run() {
		IWorkbenchPage page =
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		page.getActiveEditor().doSave(null);
		Utils.debug_var(12345, "mio save");
	}
}
