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
package com.mfg.interfaces.configurations;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.UUID;

/**
 * @author arian
 * 
 */
public abstract class BaseConfiguration<T extends IConfigurationInfo>
		implements IConfiguration<T> {
	private transient final PropertyChangeSupport support = new PropertyChangeSupport(
			this);
	private String name;
	private UUID uuid;
	private T info;

	/**
	 * 
	 */
	public BaseConfiguration() {
		uuid = UUID.randomUUID();
	}

	@Override
	public boolean allowRename() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.interfaces.configurations.IConfiguration#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.interfaces.configurations.IConfiguration#setName(java.lang.String
	 * )
	 */
	@Override
	public void setName(String aName) {
		this.name = aName;
		firePropertyChange("name");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.interfaces.configurations.IConfiguration#getUUID()
	 */
	@Override
	public UUID getUUID() {
		return uuid;
	}

	/**
	 * @param aUuid
	 *            the uUID to set
	 */
	public void setUUID(UUID aUuid) {
		this.uuid = aUuid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.interfaces.configurations.IConfiguration#getInfo()
	 */
	@Override
	public T getInfo() {
		return info;
	}

	/**
	 * @param aInfo
	 *            the info to set
	 */
	@Override
	public void setInfo(T aInfo) {
		this.info = aInfo;
		aInfo.setConfiguration(this);
		firePropertyChange(PROP_INFO);
	}

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
