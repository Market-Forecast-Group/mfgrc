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
package org.mfg.opengl.chart;

import java.util.List;

import javax.media.opengl.GL2;

import org.mfg.opengl.IGLConstants;

/**
 * @author arian
 * 
 */
public interface IGLChartCustomization extends IGLConstants {

	/**
	 * Get the RGBA background color of the y-ticks.
	 * 
	 * @param yTick
	 * @return The RGBA color, or <code>null</code> if no custom background
	 *         should be painted.
	 */
	public float[] getYTickBackgroundColor(double yTick);

	/**
	 * Get the RGBA foreground color of the y-ticks.
	 * 
	 * @param yTick
	 * @return The RGBA color, or <code>null</code> if no custom foreground
	 *         should be painted.
	 */

	public float[] getYTickForegroundColor(double yTick);

	/**
	 * @param tick
	 * @return
	 */
	public String formatXTick(double tick);

	/**
	 * @param tick
	 * @return
	 */
	public String formatYTick(double tick);

	public String getXTooltip(double crossPlotX, double crossPlotY);

	public String getYTooltip(double crossPlotX, double crossPlotY);

	/**
	 * Computes extra y-ticks.
	 * 
	 * @param yrange
	 * @return A list with the y-extra ticks. It allows <code>null</code>.
	 */
	public List<Double> computeExtraYTicks(PlotRange yrange);

	public float[] getYTickGridLineColor(double tick);

	public void paintExtraGrid(GL2 gl, int width, int height);
}
