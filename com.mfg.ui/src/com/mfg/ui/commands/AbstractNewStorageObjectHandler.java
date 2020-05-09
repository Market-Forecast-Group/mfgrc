/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 * 
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 * 
 * @version $Revision$: $Date$:
 * $Id$:
 */
package com.mfg.ui.commands;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PartInitException;

import com.mfg.persist.interfaces.IStorageObject;
import com.mfg.persist.interfaces.SimpleStorage;
import com.mfg.ui.UIPlugin;

/**
 * @author arian
 * 
 */
public abstract class AbstractNewStorageObjectHandler<T extends IStorageObject>
		extends AbstractHandler {
	protected abstract SimpleStorage<T> getStorage();

	protected abstract String getInitialObjectName();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
	 * ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		SimpleStorage<T> storage = getStorage();
		T obj = createObject(event);
		String name = createNewName(storage);
		if (name != null) {
			obj.setName(name);
		}
		getStorage().add(obj);
		try {
			UIPlugin.openEditor(obj);
		} catch (PartInitException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return obj;
	}

	/**
	 * @param storage
	 * @return
	 */
	protected String createNewName(SimpleStorage<T> storage) {
		String name = getInitialObjectName();
		List<T> list = storage.getObjects();
		return storage.createNewName(name, list);
	}

	/**
	 * Create the object.
	 * 
	 * @param event
	 * 
	 * @return
	 */
	protected T createObject(ExecutionEvent event) {
		return getStorage().createDefaultObject();
	}
}
