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
public class BooleanToIndexConverter extends Converter {

	/**
	 * @param fromType
	 * @param toType
	 */
	public BooleanToIndexConverter() {
		super(Boolean.class, Integer.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.databinding.conversion.IConverter#convert(java.lang.
	 * Object)
	 */
	@SuppressWarnings("boxing")
	@Override
	public Object convert(Object fromObject) {
		return ((Boolean) fromObject) ? 0 : 1;
	}
}
