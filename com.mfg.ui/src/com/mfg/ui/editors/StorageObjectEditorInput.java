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
package com.mfg.ui.editors;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;

import com.mfg.persist.interfaces.IStorageObject;
import com.mfg.persist.interfaces.SimpleStorage;

/**
 * @author arian
 * 
 */
public final class StorageObjectEditorInput<T extends IStorageObject>
		implements IEditorInput, IPersistableElement {

	public static final String KEY_LAST_EDITOR_TAB = "lastTab";
	public static final String OBJECT_UUID_KEY = "object-uuid";
	public static final String OBJECT_STORAGE_ID = "object-storage-id";

	private final T storageObject;
	private final SimpleStorage<?> storage;
	private int _lastTab;
	private Map<Object, Object> _extraData;
	private Function<Void, Integer> _getEditorActivePage = (p) -> Integer
			.valueOf(0);

	public StorageObjectEditorInput(T obj) {
		super();
		Assert.isNotNull(obj);
		Assert.isNotNull(obj.getStorage());
		this.storageObject = obj;
		this.storage = obj.getStorage();
		_extraData = new HashMap<>();
	}

	public Map<Object, Object> getExtraData() {
		return _extraData;
	}

	public SimpleStorage<?> getStorage() {
		return storage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IEditorInput#exists()
	 */
	@Override
	public boolean exists() {
		return getStorage().isPersisted(storageObject);
	}

	/**
	 * @return the configuration
	 */
	public T getStorageObject() {
		return storageObject;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StorageObjectEditorInput<?>) {
			return ((StorageObjectEditorInput<?>) obj).getStorageObject() == this
					.getStorageObject();
		}
		return super.equals(obj);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IEditorInput#getName()
	 */
	@Override
	public String getName() {
		return storageObject.getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IEditorInput#getToolTipText()
	 */
	@Override
	public String getToolTipText() {
		return getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IEditorInput#getPersistable()
	 */
	@Override
	public IPersistableElement getPersistable() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IPersistable#saveState(org.eclipse.ui.IMemento)
	 */
	@Override
	public void saveState(IMemento memento) {
		memento.putString(OBJECT_UUID_KEY, getStorageObject().getUUID()
				.toString());
		memento.putString(OBJECT_STORAGE_ID, getStorage().getStorageId());
		int tab = _getEditorActivePage.apply(null).intValue();
		memento.putInteger(KEY_LAST_EDITOR_TAB, tab);
	}

	public int getLastTab() {
		return _lastTab;
	}

	/**
	 * @param aLastTab
	 *            the lastTab to set
	 */
	public void setLastTab(int aLastTab) {
		this._lastTab = aLastTab;
	}

	public void setGetEditorActivePage(
			Function<Void, Integer> getEditorActivePage) {
		_getEditorActivePage = getEditorActivePage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IPersistableElement#getFactoryId()
	 */
	@Override
	public String getFactoryId() {
		return StorageObjectEditorInputFactory.ID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
	 */
	@Override
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

}
