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
package com.mfg.chart.model;

/**
 * This exception is send by the chart models when the data source (like MDB
 * files) was deleted or are not available.
 * 
 * @author arian
 * 
 */
public class ChartModelException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ChartModelException(Throwable cause) {
		super(cause);
	}

	@Override
	public String getMessage() {
		return getClass().getSimpleName() + ": " + super.getMessage();
	}
}
