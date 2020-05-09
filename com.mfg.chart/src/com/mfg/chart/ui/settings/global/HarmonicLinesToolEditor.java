package com.mfg.chart.ui.settings.global;

import org.eclipse.swt.widgets.Composite;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.ui.interactive.HarmonicLinesToolEditorComp;

public class HarmonicLinesToolEditor implements IChartSettingsEditor {
	private final Chart _chart;
	private final Composite _parent;
	private HarmonicLinesToolEditorComp _comp;

	public HarmonicLinesToolEditor(Composite parent, Chart chart) {
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
			_comp = new HarmonicLinesToolEditorComp(_parent, _chart);
		}
		return _comp;
	}
}
