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

/**
 * Information associated to a configuration.
 * 
 * This interfaces also provides Java bean methods to use in the JFace Data
 * Bindings.
 * 
 * @author arian
 * @see IConfiguration
 */
public interface IConfigurationInfo {
	/**
	 * Owner configuration.
	 * 
	 * @return Configuration.
	 */
	public IConfiguration<?> getConfiguration();

	// -- Java Bean ---

	public void addPropertyChangeListener(PropertyChangeListener l);

	public void removePropertyChangeListener(PropertyChangeListener l);

	public void addPropertyChangeListener(String property,
			PropertyChangeListener l);

	public void removePropertyChangeListener(String property,
			PropertyChangeListener l);

	public void firePropertyChange(String property);

	public void setConfiguration(IConfiguration<?> configuration);
}
