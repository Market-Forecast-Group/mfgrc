package com.mfg.chart.ui.settings.global;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class EmptyEditor extends Composite implements IChartSettingsEditor {

	public EmptyEditor(Composite parent) {
		super(parent, SWT.NONE);
	}

	@Override
	public void applyChanges() {
		// nothing
	}

	@Override
	public Composite getUI() {
		return this;
	}

}
