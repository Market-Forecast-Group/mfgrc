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

import com.mfg.strategy.builder.model.BoundsModel;
import com.mfg.strategy.builder.model.EventModelNode;

public class DeleteCommand extends Command {
	private EventModelNode model;
	private EventModelNode parentModel;

	@Override
	public void execute() {
		this.parentModel.removeChild(model);
	}
	
	@Override
	public boolean canExecute() {
		return !(model instanceof BoundsModel);
	}

	public void setModel(Object aModel) {
		this.model = (EventModelNode) aModel;
	}

	public void setParentModel(Object aModel) {
		parentModel = (EventModelNode) aModel;
	}

	@Override
	public void undo() {
		this.parentModel.addChild(model);
	}
}