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

import org.mfg.opengl.chart.GLChart;

/**
 * @author arian
 * 
 */
public class ChartMouseEvent {
	public static final int LEFT_BUTTON = 1;
	public static final int CENTER_BUTTON = 2;
	public static final int RIGHT_BUTTON = 3;
	private ChartPoint _position;
	private int _scrollAmount;
	private int _button;

	public ChartMouseEvent(int x, int y, GLChart chart, int button) {
		this(new ChartPoint(x, y, chart.convertScreenToPlot_X(x),
				chart.convertScreenToPlot_Y(y)), 0, button);
	}

	public ChartMouseEvent(ChartPoint position1, int scrollAmount1, int button) {
		_position = position1;
		_scrollAmount = scrollAmount1;
		_button = button;
	}

	public int getButton() {
		return _button;
	}

	public void setButton(int button) {
		_button = button;
	}

	public ChartPoint getPosition() {
		return _position;
	}

	public void setPosition(ChartPoint position1) {
		this._position = position1;
	}

	public int getScrollAmount() {
		return _scrollAmount;
	}

	public void setScrollAmount(int scrollAmount1) {
		this._scrollAmount = scrollAmount1;
	}

}
