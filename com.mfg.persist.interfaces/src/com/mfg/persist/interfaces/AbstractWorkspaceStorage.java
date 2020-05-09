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

package com.mfg.persist.interfaces;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author arian
 * 
 */
public class AbstractWorkspaceStorage implements IWorkspaceStorage {

	protected ArrayList<IWorkspaceStorageListener> listerners = new ArrayList<>();
	protected final PropertyChangeListener propertyChangeListener;
	protected final Set<Object> nonPersistedObjects;
	private String storageId;
	protected boolean _notifyPropertyChange;

	public AbstractWorkspaceStorage(String aStorageId) {
		this.storageId = aStorageId;
		_notifyPropertyChange = true;
		propertyChangeListener = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (_notifyPropertyChange) {
					fireObjectModified(evt.getSource());
				}
			}
		};
		nonPersistedObjects = new HashSet<>();
	}

	public AbstractWorkspaceStorage() {
		this("");
		storageId = getClass().getCanonicalName();
	}

	/**
	 * @return the storageId
	 */
	@Override
	public String getStorageId() {
		return storageId;
	}

	@Override
	public String createNewName(String prefix, Collection<? extends Object> list) {
		HashSet<Object> set = new HashSet<>();
		for (Object obj : list) {
			String name = getName(obj);
			set.add(name);
		}
		String name = "";
		for (int i = 1; i <= list.size() + 1; i++) {
			name = prefix + " " + i;
			if (!set.contains(name)) {
				break;
			}
		}
		return name;
	}

	@SuppressWarnings("static-method")//The method is overloaded on inner classes
	public String getName(Object obj) {
		return obj.toString();
	}

	@Override
	public void addStorageListener(IWorkspaceStorageListener l) {
		ArrayList<IWorkspaceStorageListener> list = new ArrayList<>(
				listerners);
		list.add(l);
		listerners = list;
	}

	@Override
	public void removeStorageListener(IWorkspaceStorageListener l) {
		ArrayList<IWorkspaceStorageListener> list = new ArrayList<>(
				listerners);
		list.remove(l);
		listerners = list;
	}

	protected void fireListAdded(List<? extends Object> list) {
		for (Object obj : list) {
			nonPersistedObjects.add(obj);
			addChangeListenerToAddedObject(obj);
		}
		for (IWorkspaceStorageListener l : listerners) {
			l.listAdded(this, list);
		}
		fireStorageChanged();
	}

	protected void fireObjectAdded(Object obj) {
		nonPersistedObjects.add(obj);
		addChangeListenerToAddedObject(obj);

		for (IWorkspaceStorageListener l : listerners) {
			l.objectAdded(this, obj);
		}
		fireStorageChanged();
	}

	protected void fireObjectRemoved(Object obj) {
		removeChangeListenerFromDeletedObject(obj);

		for (IWorkspaceStorageListener l : listerners) {
			l.objectRemoved(this, obj);
		}
		fireStorageChanged();
	}

	protected void fireObjectAboutToRemove(Object obj) throws RemoveException {
		for (IWorkspaceStorageListener l : listerners) {
			l.objectAboutToRemove(this, obj);
		}
		fireStorageChanged();
	}

	protected void fireListRemoved(List<? extends Object> list) {
		for (Object obj : list) {
			removeChangeListenerFromDeletedObject(obj);
		}

		for (IWorkspaceStorageListener l : listerners) {
			l.listRemoved(this, list);
		}
		fireStorageChanged();
	}

	protected void fireObjectModified(Object obj) {
		for (IWorkspaceStorageListener l : listerners) {
			l.objectModified(this, obj);
		}
		fireStorageChanged();
	}

	public void fireStorageChanged() {
		PersistInterfacesPlugin.getDefault().setWorkspaceDirty(true);
		for (IWorkspaceStorageListener l : listerners) {
			l.storageChanged(this);
		}
	}

	protected void addChangeListenerToAddedObject(Object obj) {
		try {
			Method method = obj.getClass().getMethod(
					"addPropertyChangeListener", PropertyChangeListener.class);
			method.invoke(obj, propertyChangeListener);
		} catch (Exception e) {
			//DO NOTHING
		}
	}

	protected void addChangeListenerToAddedList(List<? extends Object> list) {
		for (Object obj : list) {
			addChangeListenerToAddedObject(obj);
		}
	}

	protected void removeChangeListenerFromDeletedObject(Object obj) {
		try {
			Method method = obj.getClass().getMethod(
					"removePropertyChangeListener",
					PropertyChangeListener.class);
			method.invoke(obj, propertyChangeListener);
		} catch (Exception e) {
			//DO NOTHING
		}
	}

	@Override
	public void storageLoaded() {
		nonPersistedObjects.clear();
	}

	@Override
	public void storageSaved() {
		nonPersistedObjects.clear();
	}

	@Override
	public boolean isPersisted(Object obj) {
		return !nonPersistedObjects.contains(obj);
	}
}
