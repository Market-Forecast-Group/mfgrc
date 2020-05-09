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
package com.mfg.symbols.ui.widgets;

import org.eclipse.core.databinding.conversion.Converter;

public class TickMethod_BooleanToString_Converter extends Converter implements
		ITickMethodConverter {
	public TickMethod_BooleanToString_Converter() {
		super(boolean.class, String.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.databinding.conversion.IConverter#convert(java.lang
	 * .Object)
	 */
	@Override
	public Object convert(Object fromObject) {
		if (((Boolean) fromObject).booleanValue()) {
			return AUTOMATIC_TICK_METHOD;
		}
		return MANUAL_TICK_METHOD;
	}

}