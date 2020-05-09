package com.mfg.chart.ui.settings;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class LineWidthLabelProvider extends LabelProvider {
	@Override
	public String getText(Object element) {
		return " " + element + " px";
	}

	@Override
	public Image getImage(Object element) {
		return SettingsUtils.getLineWidthImage(((Integer) element).intValue());
	}
}