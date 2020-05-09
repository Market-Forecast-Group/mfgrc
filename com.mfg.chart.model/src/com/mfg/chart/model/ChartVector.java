/**
 * 
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 * 
 * $Id: $
 *
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 * 
 * @version $Revision: $ $Date: $
 */
/**
 * 
 */
package com.mfg.chart.model;


/**
 * Useful for chart clients.
 * 
 * @author arian
 * 
 */
public class ChartVector {
	public ChartPoint source;
	public ChartPoint target;

	public ChartVector(ChartPoint source1, ChartPoint target1) {
		super();
		this.source = source1;
		this.target = target1;
	}

	public ChartVector(double x0, double y0, double x1, double y1) {
		this(new ChartPoint(x0, y0), new ChartPoint(x1, y1));
	}
}
