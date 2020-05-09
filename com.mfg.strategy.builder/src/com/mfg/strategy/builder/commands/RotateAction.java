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

public class RotateAction extends SelectionAction {

	public RotateAction(IWorkbenchPart part) {
		super(part);
	}


	@Override
	protected void init() {
		setText("Rotate");
		setToolTipText("Changes V/H orientation");
		setId(ICommandIds.CMD_ROTATE);
		ImageDescriptor icon = AbstractUIPlugin.imageDescriptorFromPlugin(
				StrategyBuilderPlugin.PLUGIN_ID, "icons/stepover_co.gif");
		if (icon != null)
			setImageDescriptor(icon);
		setEnabled(false);
	}


	@Override
	protected boolean calculateEnabled() {
		RotateCommand cmd = CommandCreateRotateCommnand();
		EventModelNode selectedNode = getSelectedNode();
		cmd.setModel(selectedNode);
		if (/* cmd == null || */selectedNode == null || // The variable cmd cannot be null at this location
		!cmd.canRotate())
			return false;
		return true;
	}


	public static RotateCommand CommandCreateRotateCommnand() {
		RotateCommand r = new RotateCommand();
		return r;
	}


	@Override
	public void run() {
		EventModelNode m = getSelectedNode();
		RotateCommand c = CommandCreateRotateCommnand();
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
