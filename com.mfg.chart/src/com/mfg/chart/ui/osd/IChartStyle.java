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

package com.mfg.chart.ui.osd;

/**
 * @author arian
 * 
 */
public interface IChartStyle {
	public String getName();

	public float[] getColor();

	public void setColor(float[] color);

	public float[] getDefaultColor();

	public boolean hasColor();

	public int getStippleFactor();

	public void setStippleFactor(int factor);

	public int getDefaultStippleFactor();

	public boolean hasStipple();

	public void setWidth(float width);

	public float getWidth();

	public float getDefaultWidth();

	public boolean hasWidth();

}
