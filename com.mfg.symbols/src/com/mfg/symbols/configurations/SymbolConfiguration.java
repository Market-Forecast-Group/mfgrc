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
package com.mfg.symbols.configurations;

import com.mfg.dm.symbols.SymbolData2;
import com.mfg.interfaces.configurations.BaseConfiguration;

/**
 * @author arian
 * 
 */
public abstract class SymbolConfiguration<TSymbolData extends SymbolData2, TSymbolInfo extends SymbolConfigurationInfo<TSymbolData>>
		extends BaseConfiguration<TSymbolInfo> implements ISymbolConfigurationAdaptable{

	//public abstract IContract toContract();

	public abstract String getFullName();
}
