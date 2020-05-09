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
package com.mfg.strategy.ui;

import org.eclipse.core.databinding.conversion.Converter;

import com.mfg.strategy.IStrategyFactory;
import com.mfg.symbols.SymbolsPlugin;

/**
 * @author arian
 * 
 */
public class IdToStrategyFactoryConverter extends Converter {

	/**
	 * @param fromType
	 * @param toType
	 */
	public IdToStrategyFactoryConverter() {
		super(String.class, IStrategyFactory.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.databinding.conversion.IConverter#convert(java.lang.
	 * Object)
	 */
	@Override
	public Object convert(Object fromObject) {
		String id = (String) fromObject;
		return SymbolsPlugin.getDefault().getStrategyFactory(id);
	}

}
