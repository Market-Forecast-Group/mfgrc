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

public class EventCreateCommand extends Command {
	private EventModelNode parent;
	private EventModelNode child;
	private int index = -1;

	public EventCreateCommand() {
		super();
		parent = null;
		child = null;
	}

	public void setParent(Object p) {
		this.parent = (EventModelNode) p;
	}

	public void setChild(Object ch) {
		this.child = (EventModelNode) ch;
	}

	@Override
	public boolean canExecute() {
		return !(parent == null || child == null) &&
		parent.canAdd(child);
	}

	@Override
	public void execute() {
		if (index>=0 && index< parent.getChildren().size()){
//			System.out.println("adding at "+index);
			parent.addChild(index,child);
		}
		else{
//			System.out.println("adding at the end");
			parent.addChild(child);
		}
	}

	@Override
	public boolean canUndo() {
		if (parent == null || child == null)
			return false;
		return parent.getChildren().contains(child);
	}
	
	@Override
	public void undo() {
		parent.removeChild(child);
	}

	public void setIndex(int aIdx) {
		index = aIdx;
	}
	
	public int getIndex() {
		return index;
	}

	/**
	 * @return the parent
	 */
	public EventModelNode getParent() {
		return parent;
	}

	/**
	 * @return the child
	 */
	public EventModelNode getChild() {
		return child;
	}
	
}
