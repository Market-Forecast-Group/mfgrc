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

import static com.mfg.chart.ui.IChartUtils.EMPTY_DATASET;

import org.mfg.opengl.BitmapData;
import org.mfg.opengl.chart.IDataset;
import org.mfg.opengl.chart.PlotRange;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.backend.opengl.painters.AbstractBitmapMultiColorPainter;
import com.mfg.chart.backend.opengl.painters.LineStripMultiColorPainter;
import com.mfg.chart.model.ITradingModel;
import com.mfg.chart.model.ITimePriceCollection;

/**
 * @author arian
 * 
 */
public class EquityLayer extends FinalLayer {

	private final ITradingModel model;
	final DatasetDelegate dataset;

	/**
	 * @param name
	 * @param chart
	 */
	public EquityLayer(Chart chart) {
		super("Equity", chart);
		model = chart.getModel().getTradingModel();

		dataset = new DatasetDelegate(EMPTY_DATASET);
		chart.addDataset(dataset, new LineStripMultiColorPainter() {
			@Override
			public float[] getColor(int series, int item) {
				double y = dataset.getY(series, item);
				return y < 0 ? COLOR_RED : COLOR_BLUE;
			}
		});
		chart.addDataset(dataset, new AbstractBitmapMultiColorPainter() {

			@Override
			public float[] getColor(IDataset ds, int series, int item) {
				double y = dataset.getY(series, item);
				return y < 0 ? COLOR_RED : COLOR_BLUE;
			}

			@Override
			public BitmapData getBitmap(IDataset ds, int series, int item) {
				return BITMAP_BIG_DOT;
			}
		});
	}

	public IDataset getCrossSnappingDataset() {
		return dataset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.layers.IStippledLayer#getDefaultLayerStippleFactor()
	 */
	@Override
	public int getDefaultLayerStippleFactor() {
		return STIPPLE_FACTOR_NULL;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.layers.FinalLayer#updateDataset()
	 */
	@Override
	public void updateDataset() {
		PlotRange range = _chart.getXRange();
		ITimePriceCollection data = model.getEquity((long) range.lower,
				(long) range.upper);
		dataset.setBase(new TimePriceDataset(data));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.layers.FinalLayer#getAutorangeDataset()
	 */
	@Override
	public IDataset getAutorangeDataset() {
		return dataset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.layers.FinalLayer#clearDatasets()
	 */
	@Override
	public void clearDatasets() {
		dataset.setBase(EMPTY_DATASET);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.layers.FinalLayer#getDefaultLayerColor()
	 */
	@Override
	public float[] getDefaultLayerColor() {
		return COLOR_RED;
	}

	/**
	 * @return
	 */
	public PlotRange getDataTimeRange() {
		return new PlotRange(model.getEquityLowerTime(),
				model.getEquityUpperTime());
	}

}
