package com.mfg.chart.layers;

import javax.media.opengl.GL2;

import org.mfg.opengl.chart.IDataset;
import org.mfg.opengl.chart.ISeriesPainter;
import org.mfg.opengl.chart.PlotRange;

import com.mfg.chart.layers.IndicatorLayer.PivotReference;
import com.mfg.chart.model.IRealTimeZZModel;
import com.mfg.chart.ui.IChartUtils;
import com.mfg.chart.ui.interactive.Polyline;
import com.mfg.chart.ui.interactive.PolylineTool;
import com.mfg.chart.ui.interactive.PolylineTool.EquationType;

public class PolylineLayer extends FinalScaleElementLayer {
	public static final String LAYER_NAME = "AutoPolyline";
	private DatasetDelegate _dataset;
	private IRealTimeZZModel _rtZZModel;
	private Polyline _line;
	boolean[] _visibility;

	public PolylineLayer(ScaleLayer scale1) {
		super(LAYER_NAME, "N", scale1);
		_dataset = new DatasetDelegate(EMPTY_DATASET);

		_rtZZModel = getChart().getModel().getScaledIndicatorModel()
				.getRealTimeZZModel(getLevel());

		_chart.addDataset(_dataset, new ISeriesPainter() {

			@Override
			public void paint(GL2 gl, IDataset ds, PlotRange xrange,
					PlotRange yrange) {

				if (_visibility == null) {
					return;
				}

				boolean visible = false;
				for (boolean b : _visibility) {
					if (b) {
						visible = true;
						break;
					}
				}
				if (!visible) {
					return;
				}

				if (ds.getSeriesCount() > 0) {
					@SuppressWarnings("unchecked")
					Polyline line = ((WrapperDataset<Polyline>) ((DatasetDelegate) ds)
							.getBase()).getModel();

					PivotReference anchor1 = line.getAnchor1();
					PivotReference anchor2 = line.getAnchor2();

					if (anchor1 != null) {
						for (EquationType type : line.types) {
							if (_visibility[type.ordinal()]) {
								PolylineTool.paintEcuation(_chart, line, type,
										gl);
							}
						}

						PolylineTool.paintAnchor(_chart, anchor1, null,
								getLayerColor(), gl);

						if (anchor2 != null) {
							PolylineTool.paintAnchor(_chart, anchor2, null,
									getLayerColor(), gl);
						}

					}
				}
			}
		});

		setEnabled(false);
	}

	public Polyline getLine() {
		return _line;
	}

	public void setLine(Polyline line, boolean[] visibility) {
		_visibility = visibility;
		_line = line;
		_line.colorsMap.put(EquationType.AVG, getLayerColor());
	}

	@Override
	public void updateDataset() {
		int dataLayer = _chart.getDataLayer();
		if (_line != null

		&& _visibility != null

		&& _chart.getDataLayer() == 0

		&& _rtZZModel.isCompleted(dataLayer)) {

			_dataset.setBase(IChartUtils.createWrapperDataset(_line));

		} else {

			_dataset.setBase(EMPTY_DATASET);

		}
	}

	@Override
	public void clearDatasets() {
		_dataset.setBase(EMPTY_DATASET);
	}

	@Override
	public IDataset getAutorangeDataset() {
		return EMPTY_DATASET;
	}

}
