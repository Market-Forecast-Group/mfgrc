package com.mfg.chart.ui.settings.global;

import org.eclipse.swt.widgets.Composite;

import com.mfg.chart.backend.opengl.Chart;

public class PolylineToolEditor implements IChartSettingsEditor {

	private final Chart _chart;
	private final Composite _parent;
	private final Object _context;
	private PolylinesToolEditorComp _comp;

	public PolylineToolEditor(Chart chart, Composite parent,
			Object context) {
		_chart = chart;
		_parent = parent;
		_context = context;
	}

	@Override
	public void applyChanges() {
		_comp.applyChanges();
	}

	@Override
	public Composite getUI() {
		if (_comp == null) {
			_comp = new PolylinesToolEditorComp(_parent, _chart, _context);
		}
		return _comp;
	}
}
