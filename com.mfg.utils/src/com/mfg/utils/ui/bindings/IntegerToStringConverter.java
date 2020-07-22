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
 * @author arian
 * 
 */
public class IntegerToStringConverter extends Converter {

	/**
	 * @param fromType
	 * @param toType
	 */
	public IntegerToStringConverter() {
		super(Integer.class, String.class);
	}

	@Override
	public Object convert(Object fromObject) {
		if (fromObject == null) {
			return null;
		}
		return ((Integer) fromObject).toString();
	}

}