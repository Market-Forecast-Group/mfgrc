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

import com.mfg.strategy.builder.model.EventModelNode;

public class EventMoveCommand extends EventCreateCommand {
	
	private int prevPos;
	private EventModelNode prevParent;

	public EventMoveCommand() {
		super();
	}

	@Override
	public void execute() {
		prevParent = getChild().getParent();
		prevPos = prevParent.getChildren().indexOf(getChild());
		prevParent.removeChild(getChild());
//		System.out.println("Idx "+getIndex());
		super.execute();
	}
	
	@Override
	public void undo() {
		super.undo();
		if (prevPos>=0 && prevPos< prevParent.getChildren().size()){
//			System.out.println("REDO add at "+prevPos);
			prevParent.addChild(prevPos,getChild());
		}
		else {
//			System.out.println("REDO simple add");
			prevParent.addChild(getChild());
		}
	}

}
