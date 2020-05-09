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

public class ExpandCollapseRetargetAction extends RetargetAction {

	private ImageDescriptor iconExpand;
//	private ImageDescriptor iconCollapse;


	public ExpandCollapseRetargetAction() {
		super(ICommandIds.CMD_EXPANDCOLLAPSE, "Expand/Collapse");
		init();
	}


	protected void init() {
		setText("Expand/Collapse");
		setToolTipText("Expands/Collapses the Event");

		setId(ICommandIds.CMD_EXPANDCOLLAPSE);

		iconExpand = AbstractUIPlugin.imageDescriptorFromPlugin(StrategyBuilderPlugin.PLUGIN_ID, "icons/expanded.gif");
//		iconCollapse = AbstractUIPlugin.imageDescriptorFromPlugin(SymbolsPlugin.PLUGIN_ID, "icons/collapsed.gif");
		if (iconExpand != null)
			setImageDescriptor(iconExpand);
		setEnabled(false);
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
		c.execute();
	}


	// Helper
	private static EventModelNode getSelectedNode() {
		return null;
	}
}
