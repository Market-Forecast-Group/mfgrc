package com.mfg.chart.ui.settings;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class LineTypeLabelProvider extends LabelProvider {
	@Override
	public String getText(Object element) {
		return "";
	}

	@Override
	public Image getImage(Object element) {
		return SettingsUtils.getLineTypeImage(((Integer) element).intValue());
	}
}
