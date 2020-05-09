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

import static java.lang.System.out;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * @author arian
 * 
 */
public abstract class BaseConfigurationInfo implements IConfigurationInfo {

	private IConfiguration<?> configuration;
	private transient final PropertyChangeSupport support = new PropertyChangeSupport(
			this);

	/**
	 * 
	 */
	public BaseConfigurationInfo() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.interfaces.configurations.IConfigurationInfo#getConfiguration()
	 */
	@Override
	public IConfiguration<?> getConfiguration() {
		return configuration;
	}

	/**
	 * @param aConfiguration
	 *            the configuration to set
	 */
	@Override
	public void setConfiguration(IConfiguration<?> aConfiguration) {
		this.configuration = aConfiguration;
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener l) {
		support.addPropertyChangeListener(l);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener l) {
		support.removePropertyChangeListener(l);
		out.println(hashCode() + ": remove " + l.hashCode() + " "
				+ support.getPropertyChangeListeners().length);
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
