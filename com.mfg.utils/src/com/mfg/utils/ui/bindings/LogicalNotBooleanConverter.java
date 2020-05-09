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
package com.mfg.utils.ui.bindings;

import org.eclipse.core.databinding.conversion.Converter;

/**
 * Convert true to false and false to true.
 * 
 * @author arian
 * 
 */
public class LogicalNotBooleanConverter extends Converter {

	public LogicalNotBooleanConverter() {
		super(Boolean.class, Boolean.class);
	}

	@SuppressWarnings("boxing")
	@Override
	public Object convert(Object fromObject) {
		return !((boolean) fromObject);
	}

}
