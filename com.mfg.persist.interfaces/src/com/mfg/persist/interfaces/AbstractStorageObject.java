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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.UUID;

/**
 * @author arian
 * 
 */
public abstract class AbstractStorageObject implements IStorageObject {

	private final UUID uuid;
	private String name;

	public AbstractStorageObject() {
		uuid = UUID.randomUUID();
	}

	@Override
	public boolean allowRename() {
		return true;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.persist.interfaces.IStorageObject#getUUID()
	 */
	@Override
	public UUID getUUID() {
		return uuid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.persist.interfaces.IStorageObject#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String aName) {
		this.name = aName;

	}

	private transient final PropertyChangeSupport support = new PropertyChangeSupport(
			this);

	@Override
	public void addPropertyChangeListener(PropertyChangeListener l) {
		support.addPropertyChangeListener(l);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener l) {
		support.removePropertyChangeListener(l);
	}

	@Override
	public void addPropertyChangeListener(String property,
			PropertyChangeListener l) {
		support.addPropertyChangeListener(property, l);
	}

	@Override
	public void removePropertyChangeListener(String property,
			PropertyChangeListener l) {
		support.removePropertyChangeListener(property, l);
	}

	@Override
	public void firePropertyChange(String property) {
		support.firePropertyChange(property, true, false);
	}

}
