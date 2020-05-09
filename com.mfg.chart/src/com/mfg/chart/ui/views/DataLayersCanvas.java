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
package com.mfg.chart.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.mfg.opengl.chart.PlotRange;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.model.IChartModel;
import com.mfg.chart.model.IPriceModel;

/**
 * @author arian
 * 
 */
public class DataLayersCanvas extends Canvas implements PaintListener {

	public static class LayerInfo {
		private long _startDate;
		private long _endDate;
		private int _layer;
		private boolean _visible;

		public LayerInfo() {

		}

		/**
		 * @return the visible
		 */
		public boolean isVisible() {
			return _visible;
		}

		/**
		 * @param visible
		 *            the visible to set
		 */
		public void setVisible(boolean visible) {
			this._visible = visible;
		}

		public long getStartDate() {
			return _startDate;
		}

		public void setStartDate(long startDate) {
			this._startDate = startDate;
		}

		public long getEndDate() {
			return _endDate;
		}

		public void setEndDate(long endDate) {
			this._endDate = endDate;
		}

		public int getLayer() {
			return _layer;
		}

		public void setLayer(int layer) {
			this._layer = layer;
		}
	}

	private List<LayerInfo> _layersInfo;
	private long minDate;
	private long maxDate;
	private long rangeLowerDate;
	private long rangeUpperDate;

	public DataLayersCanvas(Composite parent) {
		super(parent, SWT.DOUBLE_BUFFERED);
		addPaintListener(this);
	}

	@Override
	public void paintControl(PaintEvent e) {
		e.gc.setBackground(e.display.getSystemColor(SWT.COLOR_WHITE));
		e.gc.fillRectangle(0, 0, e.width, e.height);

		if (_layersInfo != null && _layersInfo.size() > 0) {
			double len = maxDate - minDate;
			double scale = e.width / len;
			int vspace = 10;
			int h = e.height / _layersInfo.size();
			int y = vspace / 2;
			for (int i = 0; i < _layersInfo.size(); i++) {
				LayerInfo info = _layersInfo.get(i);
				int x1 = (int) ((info.getStartDate() - minDate) * scale);
				int x2 = (int) ((info.getEndDate() - minDate) * scale);
				int color = info.isVisible() ? SWT.COLOR_GREEN : SWT.COLOR_CYAN;

				e.gc.setBackground(e.display.getSystemColor(color));
				e.gc.fillRectangle(x1, y, x2 - x1, h - vspace);
				e.gc.drawText(Integer.toString(info.getLayer() + 1), x1
						+ (x2 - x1) / 2, y + h / 2
						- e.gc.getFontMetrics().getHeight());

				if (info.getEndDate() < maxDate) {
					int w = (int) ((maxDate - info.getEndDate()) * scale);
					e.gc.setBackground(e.display.getSystemColor(SWT.COLOR_RED));
					e.gc.fillRectangle(x2, y, w, h - vspace);
				}

				y += h;
			}
			e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_BLACK));
			int x1 = (int) ((rangeLowerDate - minDate) * scale);
			int x2 = (int) ((rangeUpperDate - minDate) * scale);
			int w = x2 - x1;
			w = Math.min(e.width - x1 - 1, w);
			e.gc.drawRectangle(x1, 0, w, e.height - 1);
		}
	}

	/**
	 * @param layersInfo
	 *            the info to set
	 */
	public void setInfo(List<LayerInfo> layersInfo) {
		this._layersInfo = layersInfo;
		minDate = Long.MAX_VALUE;
		maxDate = 0;
		for (LayerInfo info : layersInfo) {
			minDate = Math.min(minDate, info.getStartDate());
			maxDate = Math.max(maxDate, info.getEndDate());
		}
		redraw();
	}

	public void setChart(Chart chart) {
		IChartModel model = chart.getModel();
		PlotRange range = chart.getXRange();
		IPriceModel priceModel = model.getPriceModel();

		rangeLowerDate = priceModel.getPhysicalTime_from_DisplayTime(chart.getDataLayer(),
				(long) range.lower);
		rangeUpperDate = priceModel.getPhysicalTime_from_DisplayTime(chart.getDataLayer(),
				(long) range.upper);

		List<LayerInfo> list = new ArrayList<>();
		int count = model.getDataLayerCount();
		for (int layer = 0; layer < count; layer++) {
			long n = priceModel.getDataLayerPricesCount(layer);
			if (n > 0) {
				LayerInfo info = new LayerInfo();

				info.setVisible(layer == chart.getDataLayer());

				long fake = priceModel.getDataLayerLowerDisplayTime(layer);
				long date = priceModel.getPhysicalTime_from_DisplayTime(layer, fake);
				info.setStartDate(date);

				fake = priceModel.getDataLayerUpperDisplayTime(layer);
				date = priceModel.getPhysicalTime_from_DisplayTime(layer, fake);
				info.setEndDate(date);

				info.setLayer(layer);

				list.add(info);
			}
		}

		setInfo(list);
	}

	/**
	 * @return the info
	 */
	public List<LayerInfo> getInfo() {
		return _layersInfo;
	}

}
