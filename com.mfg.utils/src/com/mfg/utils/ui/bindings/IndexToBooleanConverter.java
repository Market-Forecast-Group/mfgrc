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
 * Convert 0 to True and 1 to False.
 * 
 * @author arian
 * 
 */
public class IndexToBooleanConverter extends Converter {

	/**
	 * @param fromType
	 * @param toType
	 */
	public IndexToBooleanConverter() {
		super(Integer.class, Boolean.class);
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

		if (fromObject == null) {
			return null;
		}
		return ((Integer) fromObject) == 0;
	}

}
