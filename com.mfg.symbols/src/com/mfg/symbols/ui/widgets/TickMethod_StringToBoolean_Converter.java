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

public class TickMethod_StringToBoolean_Converter extends Converter implements
		ITickMethodConverter {

	public TickMethod_StringToBoolean_Converter() {
		super(String.class, boolean.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.databinding.conversion.IConverter#convert(java.lang
	 * .Object)
	 */
	@Override
	public Object convert(Object fromObject) {
		return Boolean.valueOf(fromObject == AUTOMATIC_TICK_METHOD);
	}

}