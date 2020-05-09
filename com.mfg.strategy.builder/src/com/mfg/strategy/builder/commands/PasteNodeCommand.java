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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.Clipboard;

import com.mfg.strategy.builder.model.EventModelNode;

public class PasteNodeCommand extends Command {
	private HashMap<EventModelNode, EventModelNode> list =
			new HashMap<>();

	private EventModelNode parent;


	@Override
	public boolean canExecute() {
		@SuppressWarnings("unchecked")
		ArrayList<EventModelNode> bList =
				(ArrayList<EventModelNode>) Clipboard
						.getDefault()
						.getContents();
		if (bList == null || bList.isEmpty())
			return false;

		Iterator<EventModelNode> it = bList.iterator();
		while (it.hasNext()) {
			EventModelNode node = it.next();
			if (isPastableNode(node)) {
				list.put(node, null);
			}
		}
		return true;
	}


	@Override
	public void execute() {
		if (!canExecute())
			return;
		Iterator<EventModelNode> it = list.keySet().iterator();
		while (it.hasNext()) {
			EventModelNode node = it.next();
			list.put(node, node.clone());
		}
		redo();
	}


	@Override
	public void redo() {
		Iterator<EventModelNode> it = list.values().iterator();
		EventModelNode dest = null;
		while (it.hasNext()) {
			EventModelNode node = it.next();
			if (isPastableNode(node)) {
				if (parent != null)
					dest = parent;
				else
					dest = node.getParent();
				dest.addChild(node.clone());
			}
		}
	}


	@Override
	public boolean canUndo() {
		return !(list.isEmpty());
	}


	@Override
	public void undo() {
		Iterator<EventModelNode> it = list.values().iterator();
		while (it.hasNext()) {
			EventModelNode node = it.next();
			if (isPastableNode(node)) {
				node.getParent().removeChild(node);
			}
		}
	}


	/**
	 * @return the parent
	 */
	public EventModelNode getParent() {
		return parent;
	}


	/**
	 * @param aParent
	 *            the parent to set
	 */
	public void setParent(EventModelNode aParent) {
		parent = aParent;
	}


	/**
	 * @param node  
	 */
	public static boolean isPastableNode(EventModelNode node) {
		return true;
	}
}
