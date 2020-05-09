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
package com.mfg.strategy.builder;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.RetargetAction;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;

import com.mfg.strategy.builder.ui.StrategyBuilderEditor;

public class MultiActionBarContributor extends MultiPageEditorActionBarContributor {
	private ActionRegistry registry = new ActionRegistry();

	private List<RetargetAction> retargetActions = new ArrayList<>();


	@Override
	public void init(IActionBars bars) {
		buildActions();
		super.init(bars);
	}


	protected void addAction(IAction action) {
		getActionRegistry().registerAction(action);
	}


	protected void addRetargetAction(RetargetAction action) {
		addAction(action);
		this.retargetActions.add(action);
		getPage().addPartListener(action);
	}


	protected IAction getAction(String id) {
		return getActionRegistry().getAction(id);
	}


	protected ActionRegistry getActionRegistry() {
		return this.registry;
	}


	protected void buildActions() {
		IWorkbenchWindow workbenchWindow = getPage().getWorkbenchWindow();
		addRetargetAction((RetargetAction) ActionFactory.UNDO.create(workbenchWindow));
		addRetargetAction((RetargetAction) ActionFactory.REDO.create(workbenchWindow));
		addRetargetAction((RetargetAction) ActionFactory.COPY.create(workbenchWindow));
		addRetargetAction((RetargetAction) ActionFactory.PASTE.create(workbenchWindow));
		addRetargetAction((RetargetAction) ActionFactory.DELETE.create(workbenchWindow));
		addRetargetAction((RetargetAction) ActionFactory.SELECT_ALL.create(workbenchWindow));
	}


	@Override
	public void contributeToToolBar(IToolBarManager toolBarManager) {
		toolBarManager.add(new Separator());
		toolBarManager.add(getAction(ActionFactory.UNDO.getId()));
		toolBarManager.add(getAction(ActionFactory.REDO.getId()));
		toolBarManager.add(getAction(ActionFactory.DELETE.getId()));
	}


	@Override
	public void setActivePage(IEditorPart activeEditor) {
		IActionBars bars = getActionBars();
		if ((activeEditor instanceof StrategyBuilderEditor)) {
			ActionRegistry aRegistry = ((StrategyBuilderEditor) activeEditor).getActionRegistry();
			bars.setGlobalActionHandler(ActionFactory.UNDO.getId(), aRegistry.getAction(ActionFactory.UNDO.getId()));
			bars.setGlobalActionHandler(ActionFactory.REDO.getId(), aRegistry.getAction(ActionFactory.REDO.getId()));
			bars.setGlobalActionHandler(ActionFactory.DELETE.getId(), aRegistry.getAction(ActionFactory.DELETE.getId()));
		} 
		bars.updateActionBars();
	}


	@Override
	public void dispose() {
		super.dispose();
		for (int i = 0; i < this.retargetActions.size(); i++) {
			RetargetAction action = this.retargetActions.get(i);
			getPage().removePartListener(action);
			action.dispose();
		}
		this.registry.dispose();
		this.retargetActions = null;
		this.registry = null;
	}
}
