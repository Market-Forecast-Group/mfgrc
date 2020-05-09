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
package com.mfg.symbols.inputs.configurations;

import com.mfg.interfaces.configurations.BaseConfiguration;
import com.mfg.persist.interfaces.SimpleStorage;
import com.mfg.symbols.SymbolsPlugin;

/**
 * @author arian
 * 
 */
public class InputConfiguration extends
		BaseConfiguration<InputConfigurationInfo> {

	/**
	 * 
	 */
	public InputConfiguration() {
		setInfo(new InputConfigurationInfo());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.persist.interfaces.IStorageObject#getStorage()
	 */
	@Override
	public SimpleStorage<?> getStorage() {
		return SymbolsPlugin.getDefault().getInputsStorage();
	}
}
