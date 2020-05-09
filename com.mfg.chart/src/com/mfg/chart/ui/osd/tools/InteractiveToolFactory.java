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
package com.mfg.chart.ui.osd.tools;

import org.mfg.opengl.chart.interactive.InteractiveTool;

import com.mfg.chart.backend.opengl.Chart;

/**
 * @author arian
 * 
 */
public abstract class InteractiveToolFactory {

	/**
	 * Creates the tool. Returns <code>null</code> if the given price chart is
	 * not allowed by the tool.
	 * 
	 * @param priceChart
	 * @return
	 */
	public abstract InteractiveTool createTool(Chart chart);
}
