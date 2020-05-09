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
import org.eclipse.core.runtime.Assert;

/**
 * @author arian
 * 
 */
public class StringToBooleanConverter extends Converter {

	private String trueString;

	/**
	 * @param fromType
	 * @param toType
	 */
	public StringToBooleanConverter() {
		super(String.class, Boolean.class);
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
		Assert.isNotNull(getTrueString());

		if (fromObject == null) {
			return null;
		}

		return getTrueString().equals(fromObject);
	}

	public String getTrueString() {
		return trueString;
	}

	public void setTrueString(String aTrueString) {
		this.trueString = aTrueString;
	}
}
