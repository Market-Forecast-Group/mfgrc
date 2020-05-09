package com.mfg.chart.ui.settings.global;

import org.eclipse.swt.widgets.Composite;

public interface IChartSettingsEditor {
	public void applyChanges();

	public Composite getUI();
}
