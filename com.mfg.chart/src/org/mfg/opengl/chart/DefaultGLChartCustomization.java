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

import java.text.Format;
import java.text.NumberFormat;
import java.util.List;

import javax.media.opengl.GL2;

/**
 * @author arian
 * 
 */
public class DefaultGLChartCustomization implements IGLChartCustomization {
	private Format formatXValues;
	private Format formatYValues;

	public DefaultGLChartCustomization() {
		formatXValues = NumberFormat.getIntegerInstance();
		formatYValues = NumberFormat.getIntegerInstance();
	}

	public Format getFormatXValues() {
		return formatXValues;
	}

	public void setFormatXValues(Format formatXValues1) {
		this.formatXValues = formatXValues1;
	}

	public Format getFormatYValues() {
		return formatYValues;
	}

	public void setFormatYValues(Format formatYValues1) {
		this.formatYValues = formatYValues1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mfg.opengl.chart.IGLChartCustomization#getYTickBackgroundColor(double
	 * )
	 */
	@Override
	public float[] getYTickBackgroundColor(double yTick) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mfg.opengl.chart.IGLChartCustomization#getYTickForegroundColor(double
	 * )
	 */
	@Override
	public float[] getYTickForegroundColor(double yTick) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.chart.IGLChartCustomization#formatXTick(double)
	 */
	@Override
	public String formatXTick(double tick) {
		return formatXValues.format(Double.valueOf(tick));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.chart.IGLChartCustomization#formatYTick(double)
	 */
	@Override
	public String formatYTick(double tick) {
		return formatYValues.format(Long.valueOf((long) tick));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.chart.IGLChartCustomization#getXTooltip(int, int,
	 * double, double)
	 */
	@Override
	public String getXTooltip(double crossPlotX, double crossPlotY) {
		return formatXTick(crossPlotX);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.chart.IGLChartCustomization#getYTooltip(int, int,
	 * double, double)
	 */
	@Override
	public String getYTooltip(double crossPlotX, double crossPlotY) {
		return formatYTick(crossPlotY);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mfg.opengl.chart.IGLChartCustomization#computeExtraYTicks(org.mfg
	 * .opengl.chart.PlotRange)
	 */
	@Override
	public List<Double> computeExtraYTicks(PlotRange yrange) {
		return null;
	}

	@Override
	public float[] getYTickGridLineColor(double tick) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mfg.opengl.chart.IGLChartCustomization#paintExtraGrid(javax.media
	 * .opengl.GL2, int, int)
	 */
	@Override
	public void paintExtraGrid(GL2 gl, int width, int height) {
		//Adding a comment to avoid empty block warning.
	}
}
