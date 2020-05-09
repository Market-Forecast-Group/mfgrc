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

package com.mfg.strategy.builder;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.UUID;

import com.mfg.strategy.IStrategySettings;

/**
 * @author arian
 * 
 */
public class AutomaticStrategySettings implements IStrategySettings {
	private static final String PROP_STRATEGY_INFO_ID = "strategyInfoId";
	private static final String PROP_PATTERN_FILE = "patternFile";
	private File patternFile;
	private UUID _strategyInfoId;

	
	public UUID getStrategyInfoId() {
		return _strategyInfoId;
	}
	
	public void setStrategyInfoId(UUID strategyInfoId) {
		_strategyInfoId = strategyInfoId;
		firePropertyChange(PROP_STRATEGY_INFO_ID);
	}

	@Deprecated
	public File getPatternFile() {
		return patternFile;
	}


	@Deprecated
	public void setPatternFile(File aPatternFile) {
		this.patternFile = aPatternFile;
		firePropertyChange(PROP_PATTERN_FILE);
	}

	private transient final PropertyChangeSupport support = new PropertyChangeSupport(this);


	@Override
	public void addPropertyChangeListener(PropertyChangeListener l) {
		support.addPropertyChangeListener(l);
	}


	@Override
	public void removePropertyChangeListener(PropertyChangeListener l) {
		support.removePropertyChangeListener(l);
	}


	@Override
	public void addPropertyChangeListener(String property, PropertyChangeListener l) {
		support.addPropertyChangeListener(property, l);
	}


	@Override
	public void removePropertyChangeListener(String property, PropertyChangeListener l) {
		support.removePropertyChangeListener(property, l);
	}


	@Override
	public void firePropertyChange(String property) {
		support.firePropertyChange(property, true, false);
	}
}
