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

import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;

import com.mfg.strategy.builder.model.EventModelNode;

public class PasteNodeAction extends SelectionAction {

	public PasteNodeAction(IWorkbenchPart aPart) {
		super(aPart);
		setLazyEnablementCalculation(true);
	}


	@Override
	protected void init() {
		super.init();
		ISharedImages sharedImages =
				PlatformUI.getWorkbench().getSharedImages();
		setText("Paste");
		setId(ActionFactory.PASTE.getId());

		setHoverImageDescriptor(sharedImages
				.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));

		setImageDescriptor(sharedImages
				.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));

		setDisabledImageDescriptor(sharedImages
				.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE_DISABLED));
		setEnabled(false);
	}


	private Command createPasteCommand() {
		PasteNodeCommand pasteNodeCommand = new PasteNodeCommand();
		pasteNodeCommand.setParent(getSelectedNode());
		return pasteNodeCommand;
	}


	@Override
	protected boolean calculateEnabled() {
		Command command = createPasteCommand();
		return command != null && command.canExecute();
	}


	@SuppressWarnings("rawtypes")
	// TODO: Review warning adding type Object to rawtype List.
	private EventModelNode getSelectedNode() {
		List objects = getSelectedObjects();
		if (objects.isEmpty())
			return null;
		if (!(objects.get(0) instanceof EditPart))
			return null;
		EditPart part = (EditPart) objects.get(0);
		return (EventModelNode) part.getModel();
	}


	@Override
	public void run() {
		Command command = createPasteCommand();
		if (command != null && command.canExecute())
			execute(command);
	}
}
