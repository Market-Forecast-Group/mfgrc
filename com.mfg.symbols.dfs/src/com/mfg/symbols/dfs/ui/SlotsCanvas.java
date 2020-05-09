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
package com.mfg.symbols.dfs.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import com.mfg.common.BarType;

/**
 * @author arian
 * 
 */
public class SlotsCanvas extends Canvas implements PaintListener {

	public static interface ILayersModel {
		public int size();

		public long getStartDate(int i);

		public long getEndDate(int i);

		public long getAvailableStartDate(int i);

		public long getAvailableEndDate(int i);

		public BarType getBarType(int i);
	}

	private ILayersModel _model;
	Color[] _colors;

	public SlotsCanvas(Composite parent) {
		super(parent, SWT.DOUBLE_BUFFERED);
		_colors = new Color[3];
		_colors[0] = new Color(getDisplay(), 255, 255, 0);
		_colors[1] = new Color(getDisplay(), 146, 208, 80);
		_colors[2] = new Color(getDisplay(), 0, 176, 240);
		addPaintListener(this);
		addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				for (Color c : _colors) {
					c.dispose();
				}
			}
		});
	}

	public void setModel(ILayersModel model) {
		_model = model;
	}

	public ILayersModel getModel() {
		return _model;
	}

	@Override
	public void paintControl(PaintEvent e) {
		e.gc.setBackground(e.display.getSystemColor(SWT.COLOR_WHITE));
		e.gc.fillRectangle(0, 0, e.width, e.height);

		if (_model != null && _model.size() > 0) {

			long minDate = Long.MAX_VALUE;
			long maxDate = 0;

			for (int i = 0; i < _model.size(); i++) {
				minDate = Math.min(minDate, _model.getStartDate(i));
				minDate = Math.min(minDate, _model.getAvailableStartDate(i));
				maxDate = Math.max(maxDate, _model.getEndDate(i));
				maxDate = Math.max(maxDate, _model.getAvailableEndDate(i));
			}

			double len = maxDate - minDate;
			double scale = (e.width - 10) / len;

			// paint available data

			int vspace = 10;
			int h = e.height / _model.size();
			int y = vspace / 2;

			e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_GRAY));

			for (int i = 0; i < _model.size(); i++) {
				long startDate = _model.getAvailableStartDate(i);
				long endDate = _model.getAvailableEndDate(i);

				int x1 = 5 + (int) ((startDate - minDate) * scale);
				int x2 = 5 + (int) ((endDate - minDate) * scale);

				e.gc.drawRectangle(x1, y, x2 - x1, h - vspace);

				y += h;
			}

			// paint request data
			vspace = 10;
			h = e.height / _model.size();
			y = vspace / 2;
			int stretch = 5;

			e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_BLACK));

			for (int i = 0; i < _model.size(); i++) {
				long startDate = _model.getStartDate(i);
				long endDate = _model.getEndDate(i);

				int x1 = 5 + (int) ((startDate - minDate) * scale);
				int x2 = 5 + (int) ((endDate - minDate) * scale);

				int icolor = _model.getBarType(i).ordinal();
				e.gc.setBackground(_colors[icolor]);
				e.gc.fillRectangle(x1, y + stretch, x2 - x1, h - vspace
						- stretch * 2);
				e.gc.drawRectangle(x1, y + stretch, x2 - x1, h - vspace
						- stretch * 2);

				// TODO: paint the error
				// // paint error
				// long maxLayerDate = _model.getAvailableEndDate(i);
				// if (endDate < maxLayerDate) {
				// int w = (int) ((maxLayerDate - endDate) * scale);
				// e.gc.setBackground(e.display.getSystemColor(SWT.COLOR_RED));
				// e.gc.fillRectangle(x2, y + stretch, w, h - vspace - stretch
				// * 2);
				// }

				y += h;
			}
		}
	}
}
