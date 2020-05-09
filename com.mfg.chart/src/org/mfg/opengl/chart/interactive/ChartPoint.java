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
package org.mfg.opengl.chart.interactive;

/**
 * @author arian
 * 
 */
public class ChartPoint {
	private int screenX;
	private int screenY;
	private double plotX;
	private double plotY;

	public ChartPoint(int screenX1, int screenY1, double plotX1, double plotY1) {
		super();
		this.screenX = screenX1;
		this.screenY = screenY1;
		this.plotX = plotX1;
		this.plotY = plotY1;
	}

	public int getScreenX() {
		return screenX;
	}

	public void setScreenX(int screenX1) {
		this.screenX = screenX1;
	}

	public int getScreenY() {
		return screenY;
	}

	public void setScreenY(int screenY1) {
		this.screenY = screenY1;
	}

	public double getPlotX() {
		return plotX;
	}

	public void setPlotX(double plotX1) {
		this.plotX = plotX1;
	}

	public double getPlotY() {
		return plotY;
	}

	public void setPlotY(double plotY1) {
		this.plotY = plotY1;
	}

	public ChartPoint delta(ChartPoint p) {
		return new ChartPoint(screenX - p.screenX, screenY - p.screenY, plotX
				- p.plotX, plotY - p.plotY);
	}
}
