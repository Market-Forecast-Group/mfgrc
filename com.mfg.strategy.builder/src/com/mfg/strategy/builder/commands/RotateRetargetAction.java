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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.actions.RetargetAction;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.mfg.strategy.builder.StrategyBuilderPlugin;
import com.mfg.strategy.builder.model.EventModelNode;

public class RotateRetargetAction extends RetargetAction {

	public RotateRetargetAction() {
		super(ICommandIds.CMD_ROTATE,"Rotate");
		init();
	}

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

	

	public static RotateCommand CommandCreateRotateCommnand() {
		RotateCommand r = new RotateCommand();
		return r;
	}

	@Override
	public void run() {
		EventModelNode m = getSelectedNode();
		RotateCommand c = CommandCreateRotateCommnand();
		c.setModel(m);
		c.execute();
	}

	// Helper
	private static EventModelNode getSelectedNode() {
		return null;
	}
}