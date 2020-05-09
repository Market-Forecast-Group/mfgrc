/**
 *
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 *
 * $Id: $
 *
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 *
 * @version $Revision: $ $Date: $
 */

package com.mfg.chart.layers;

import org.mfg.opengl.chart.IDataset;

public class ScaleLayer extends MergedLayer<IElementScaleLayer> implements
		IColoredLayer {
	public static final int DEFAULT_FILTER_NUMBER = 50;
	private final int _level;
	private final BandsLayer _bandsLayer;
	private final ZZLayer _zzLayer;
	private final PivotLayer _pivotLayer;
	private final ChannelLayer _channelLayer;
	private ProbabilityLayer probsLayer;
	private int _filterNumber = 50;
	private TrendLayer _trendLayer;
	private AutoTimeLinesLayer _autoLinesLayer;
	private Bands2Layer _bands2Layer;
	private PolylineLayer _polylineLayer;

	public ScaleLayer(final String name, final IndicatorLayer indicatorLayer,
			final int level) {
		super(name, indicatorLayer.getChart());
		this._level = level;

		addLayer(_bandsLayer = new BandsLayer(this));
		addLayer(_bands2Layer = new Bands2Layer(this));
		addLayer(_channelLayer = new ChannelLayer(this));
		addLayer(_zzLayer = new ZZLayer(this));
		addLayer(_pivotLayer = new PivotLayer(this));
		addLayer(_trendLayer = new TrendLayer(this));
		addLayer(_autoLinesLayer = new AutoTimeLinesLayer(this));
		addLayer(_polylineLayer = new PolylineLayer(this));

		if (_chart.getType().hasProbs()) {
			addLayer(probsLayer = new ProbabilityLayer(this));
		}
	}

	@Override
	public float[] getLayerColor() {
		return _chart.getIndicatorLayer().getScalesSettings().scalesColors[getLevel()];
	}

	@Override
	@Deprecated
	public void setLayerColor(final float[] color) {
		// deprecated
	}

	@Override
	public float[] getDefaultLayerColor() {
		return IndicatorLayer.DEFAULT_COLORS[getLevel()];
	}

	public boolean computeFilterVisible() {
		return _zzLayer.countObjects() < getFilterNumber();
	}

	public void updateDataset(boolean updateBands) {
		if (getChart().getIndicatorLayer().isFiltersEnabled()) {
			Boolean visible = null;
			for (final IElementScaleLayer layer : getLayers()) {
				if (!updateBands
						&& (layer == getBandsLayer() || layer == getBands2Layer())) {
					continue;
				}
				if (layer.isEnabled()) {
					if (visible == null) {
						visible = Boolean.valueOf(computeFilterVisible());
					}
					layer.setVisible(visible.booleanValue());
				}
			}
		} else {
			for (final IElementScaleLayer layer : getLayers()) {
				if (layer.isEnabled()) {
					if (layer.isVisible()) {
						layer.updateDataset();
					} else {
						layer.clearDatasets();
					}
				}
			}
		}
	}

	@Override
	public void updateDataset() {
		updateDataset(true);
	}

	public int getFilterNumber() {
		return _filterNumber;
	}

	public void setFilterNumber(int filterNumber) {
		this._filterNumber = filterNumber;
	}

	/**
	 * @return the pivotLayer
	 */
	public PivotLayer getPivotLayer() {
		return _pivotLayer;
	}

	/**
	 * @return the trendLayer
	 */
	public TrendLayer getTrendLayer() {
		return _trendLayer;
	}

	/**
	 * @return the probsLayer
	 */
	public ProbabilityLayer getProbsLayer() {
		return probsLayer;
	}

	/**
	 * @return the bandsLayer
	 */
	public BandsLayer getBandsLayer() {
		return _bandsLayer;
	}

	public Bands2Layer getBands2Layer() {
		return _bands2Layer;
	}

	/**
	 * @return the zzLayer
	 */
	public ZZLayer getZzLayer() {
		return _zzLayer;
	}

	/**
	 * @return the channelLayer
	 */
	public ChannelLayer getChannelLayer() {
		return _channelLayer;
	}

	public AutoTimeLinesLayer getAutoLinesLayer() {
		return _autoLinesLayer;
	}

	@Override
	public IDataset getAutorangeDataset() {
		return _bandsLayer.getAutorangeDataset();
	}

	public PolylineLayer getPolylineLayer() {
		return _polylineLayer;
	}

	/**
	 * @return the level
	 */
	public int getLevel() {
		return _level;
	}

	@Override
	public void clearDatasets() {
		for (IElementScaleLayer layer : getLayers()) {
			layer.clearDatasets();
		}
	}
}
