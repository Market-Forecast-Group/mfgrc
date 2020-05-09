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
import java.util.UUID;

/**
 * @author arian
 * 
 */
public interface IStorageObject {
	public static final String PROP_NAME = "name";

	public UUID getUUID();

	public String getName();

	public SimpleStorage<?> getStorage();

	public void setName(String name);

	public boolean allowRename();

	public void addPropertyChangeListener(PropertyChangeListener l);

	public void removePropertyChangeListener(PropertyChangeListener l);

	public void addPropertyChangeListener(String property,
			PropertyChangeListener l);

	public void removePropertyChangeListener(String property,
			PropertyChangeListener l);

	public void firePropertyChange(String property);
}
