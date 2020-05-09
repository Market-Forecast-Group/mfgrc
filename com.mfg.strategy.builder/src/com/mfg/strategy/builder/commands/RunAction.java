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

public class RunAction extends SelectionAction {

	public RunAction(IWorkbenchPart part) {
		super(part);
	}


	@Override
	protected void init() {
		setText("Run");
		setToolTipText("Launches a run configuration for this strategy");
		setId(ICommandIds.CMD_RUN);
		ImageDescriptor icon = AbstractUIPlugin.imageDescriptorFromPlugin(StrategyBuilderPlugin.PLUGIN_ID, "icons/run.gif");
		if (icon != null)
			setImageDescriptor(icon);
		setEnabled(false);
	}


	@Override
	protected boolean calculateEnabled() {
		RunCommand cmd = CommandCreateRunCommnand();
		EventModelNode selectedNode = getSelectedNode();
		cmd.setModel(selectedNode);
		if (/* cmd == null || */selectedNode == null || !cmd.canRotate())// The variable cmd cannot be null at this location
			return false;
		return true;
	}


	public static RunCommand CommandCreateRunCommnand() {
		RunCommand r = new RunCommand();
		return r;
	}


	@Override
	public void run() {
		EventModelNode m = getSelectedNode();
		RunCommand c = CommandCreateRunCommnand();
		c.setModel(m);
		execute(c);
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
}
