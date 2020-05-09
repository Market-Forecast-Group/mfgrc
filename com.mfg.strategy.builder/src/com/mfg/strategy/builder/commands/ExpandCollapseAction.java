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
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.mfg.strategy.builder.StrategyBuilderPlugin;
import com.mfg.strategy.builder.model.EventModelNode;

public class ExpandCollapseAction extends SelectionAction {

	private ImageDescriptor iconExpand;


	// private ImageDescriptor iconCollapse;

	public ExpandCollapseAction(IWorkbenchPart part) {
		super(part);
		// setLazyEnablementCalculation(false);
	}


	@Override
	protected void init() {
		setText("Expand/Collapse");
		setToolTipText("Expands/Collapses the Event");
		setId(ICommandIds.CMD_EXPANDCOLLAPSE);
		iconExpand = AbstractUIPlugin.imageDescriptorFromPlugin(StrategyBuilderPlugin.PLUGIN_ID, "icons/expanded.gif");
		// iconCollapse = AbstractUIPlugin.imageDescriptorFromPlugin(SymbolsPlugin.PLUGIN_ID, "icons/collapsed.gif");
		if (iconExpand != null)
			setImageDescriptor(iconExpand);
		setEnabled(false);
	}


	@Override
	protected boolean calculateEnabled() {
		ExpandCollapseCommand cmd = CommandCreateCommnand();
		EventModelNode selectedNode = getSelectedNode();
		cmd.setModel(selectedNode);
		if (/* cmd == null || */selectedNode == null || !cmd.canExpandCollapse())// cmd cannot be null at this location.
			return false;
		return true;
	}


	public static ExpandCollapseCommand CommandCreateCommnand() {
		ExpandCollapseCommand r = new ExpandCollapseCommand();
		return r;
	}


	@Override
	public void run() {
		EventModelNode m = getSelectedNode();
		ExpandCollapseCommand c = CommandCreateCommnand();
		c.setModel(m);
		execute(c);
	}


	// Helper
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
}
