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

import java.math.BigDecimal;

import org.eclipse.core.databinding.conversion.Converter;

/**
 * @author arian
 * 
 */
public class StringToBigDecimalConverter extends Converter {

	/**
	 * @param fromType
	 * @param toType
	 */
	public StringToBigDecimalConverter() {
		super(String.class, BigDecimal.class);
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
		return fromObject == null || "".equals(fromObject) ? null
				: new BigDecimal((String) fromObject);
	}

}
