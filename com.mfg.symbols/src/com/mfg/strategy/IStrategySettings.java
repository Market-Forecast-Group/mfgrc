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
package com.mfg.strategy;

import java.beans.PropertyChangeListener;

/**
 * @author arian
 * 
 */
public interface IStrategySettings {
	public void addPropertyChangeListener(PropertyChangeListener l);

	public void removePropertyChangeListener(PropertyChangeListener l);

	public void addPropertyChangeListener(String property,
			PropertyChangeListener l);

	public void removePropertyChangeListener(String property,
			PropertyChangeListener l);

	public void firePropertyChange(String property);
}
