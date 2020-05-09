package com.mfg.chart.ui.settings.global;

import org.eclipse.swt.widgets.Composite;

import com.mfg.chart.backend.opengl.Chart;

public interface IChartSettingsEditorProvider {
	public IChartSettingsEditor createEditor(Composite parent,
			Chart chart, Object node, Object context);
}
