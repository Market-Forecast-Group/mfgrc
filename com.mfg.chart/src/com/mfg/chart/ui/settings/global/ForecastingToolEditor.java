package com.mfg.chart.ui.settings.global;

import org.eclipse.swt.widgets.Composite;

import com.mfg.chart.backend.opengl.Chart;

public class ForecastingToolEditor implements IChartSettingsEditor {
	private Composite _parent;
	private Chart _chart;
	private ForecastingToolEditorComp _comp;

	public ForecastingToolEditor(Composite parent, Chart chart) {
		super();
		_parent = parent;
		_chart = chart;
	}

	@Override
	public void applyChanges() {
		_comp.applyChanges();
	}

	@Override
	public Composite getUI() {
		if (_comp == null) {
			_comp = new ForecastingToolEditorComp(_parent, _chart);
		}
		return _comp;
	}

}
