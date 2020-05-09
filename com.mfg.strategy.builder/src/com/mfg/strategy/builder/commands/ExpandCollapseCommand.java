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

import org.eclipse.gef.commands.Command;

import com.mfg.strategy.builder.model.EventModelNode;
import com.mfg.strategy.builder.model.EventsCanvasModel;
import com.mfg.strategy.builder.model.SimpleEventModel;

public class ExpandCollapseCommand extends Command {
	private EventModelNode model;

	@Override
	public void execute() {
		model.setCollapsed(!model.isCollapsed());
	}

	public void setModel(Object aModel) {
		this.model = (EventModelNode) aModel;
	}

	@Override
	public void undo() {
		execute();
	}
	
	public boolean canExpandCollapse() {
		return !(model instanceof EventsCanvasModel) &&
		!(model instanceof SimpleEventModel);
	}
}