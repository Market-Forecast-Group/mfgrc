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

/**
 * @author arian
 * 
 */
public class StrategyFactoryToIdConverter extends Converter {

	/**
	 * @param fromType
	 * @param toType
	 */
	public StrategyFactoryToIdConverter() {
		super(IStrategyFactory.class, String.class);
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
		return ((IStrategyFactory) fromObject).getId();
	}

}
