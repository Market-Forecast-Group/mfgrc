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

package com.mfg.strategy.builder.part;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.actions.ActionFactory;

import com.mfg.strategy.builder.commands.ICommandIds;

public class AppContextMenuProvider extends ContextMenuProvider {

	private ActionRegistry actionRegistry;


	public AppContextMenuProvider(EditPartViewer viewer, ActionRegistry registry) {
		super(viewer);
		setActionRegistry(registry);
	}


	@Override
	public void buildContextMenu(IMenuManager menu) {
		IAction action;

		GEFActionConstants.addStandardActionGroups(menu);

		action = getActionRegistry().getAction(ActionFactory.UNDO.getId());
		menu.appendToGroup(GEFActionConstants.GROUP_UNDO, action);

		action = getActionRegistry().getAction(ActionFactory.REDO.getId());
		menu.appendToGroup(GEFActionConstants.GROUP_UNDO, action);

		action = getActionRegistry().getAction(ActionFactory.DELETE.getId());
		menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);

		action = getActionRegistry().getAction(ActionFactory.COPY.getId());
		menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);

		action = getActionRegistry().getAction(ActionFactory.PASTE.getId());
		menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);

		action = getActionRegistry().getAction(ICommandIds.CMD_ROTATE);
		menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);

		action = getActionRegistry().getAction(ICommandIds.CMD_EXPANDCOLLAPSE);
		menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);

		action = getActionRegistry().getAction(ICommandIds.CMD_RUN);
		menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);

		action = getActionRegistry().getAction(ActionFactory.SAVE.getId());
		menu.appendToGroup(GEFActionConstants.GROUP_SAVE, action);
//		System.out.println("toma");
		action = getActionRegistry().getAction(ActionFactory.SAVE_AS.getId());
		menu.appendToGroup(GEFActionConstants.GROUP_SAVE, action);

	}


	private ActionRegistry getActionRegistry() {
		return actionRegistry;
	}


	private void setActionRegistry(ActionRegistry registry) {
		actionRegistry = registry;
	}

}
