package com.mfg.chart.ui.settings.global;

import org.eclipse.swt.widgets.Composite;

import com.mfg.chart.backend.opengl.Chart;

final class TimeLineToolEditor implements IChartSettingsEditor {
	/**
	 * 
	 */
	private final Chart _chart;
	private final Composite _parent;
	private final Object _context;
	private TimeLineToolEditorComp _comp;

	TimeLineToolEditor(Chart chart, Composite parent, Object context) {
		_chart = chart;
		_parent = parent;
		_context = context;
	}

	@Override
	public Composite getUI() {
		if (_comp == null) {
			_comp = new TimeLineToolEditorComp(_parent, _chart, _context);
		}
		return _comp;
	}

	@Override
	public void applyChanges() {
		_comp.applyChanges();
	}
}