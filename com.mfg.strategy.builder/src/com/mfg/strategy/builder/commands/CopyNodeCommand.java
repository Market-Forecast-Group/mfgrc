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
import java.util.Iterator;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.Clipboard;

import com.mfg.strategy.builder.model.EventModelNode;
import com.mfg.strategy.builder.model.EventsCanvasModel;

public class CopyNodeCommand extends Command {
	private ArrayList<EventModelNode> list = new ArrayList<>();


	public boolean addElement(EventModelNode node) {
		if (!list.contains(node)) {
			return list.add(node);
		}
		return false;
	}


	@Override
	public boolean canExecute() {
		if (list == null || list.isEmpty())
			return false;
		Iterator<EventModelNode> it = list.iterator();
		while (it.hasNext()) {
			if (!isCopyableNode(it.next()))
				return false;
		}
		return true;
	}


	@Override
	public void execute() {
		if (canExecute()){
			ArrayList<EventModelNode> newList = new ArrayList<>();
			for (EventModelNode eventModelNode : list) {
				newList.add(eventModelNode);
			}
			Clipboard.getDefault().setContents(newList);
		}
	}


	@Override
	public boolean canUndo() {
		return false;
	}


	public static boolean isCopyableNode(EventModelNode node) {
		return !(node instanceof EventsCanvasModel);
	}
}
