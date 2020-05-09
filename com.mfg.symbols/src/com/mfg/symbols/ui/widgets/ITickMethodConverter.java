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

/**
 * @author arian
 * 
 */
public interface ITickMethodConverter {
	public static final String MANUAL_TICK_METHOD = "Manual";
	public static final String AUTOMATIC_TICK_METHOD = "Automatic";
	public static final String[] METHODS = { AUTOMATIC_TICK_METHOD,
			MANUAL_TICK_METHOD };
}
