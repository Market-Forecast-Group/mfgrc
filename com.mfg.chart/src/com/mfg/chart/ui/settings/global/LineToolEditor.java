package com.mfg.chart.ui.settings.global;

import org.eclipse.swt.widgets.Composite;

import com.mfg.chart.backend.opengl.Chart;

public class LineToolEditor implements IChartSettingsEditor {

	private final Chart _chart;
	private final Composite _parent;
	private final Object _context;
	private LineToolEditorComp _comp;

	public LineToolEditor(Chart chart, Composite parent,
			Object context) {
		super();
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
			_comp = new LineToolEditorComp(_parent, _chart, _context);
		}
		return _comp;
	}

}
