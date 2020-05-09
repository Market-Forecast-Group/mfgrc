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

package com.mfg.chart.layers;

/**
 * @author arian
 * 
 */
public interface IStippledLayer {
	public int getLayerStippleFactor();

	public void setLayerStippleFactor(int factor);

	public int getDefaultLayerStippleFactor();

	public float getLayerWidth();

	public void setLayerWidth(float width);

	public float getDefaultLayerWidth();
}
