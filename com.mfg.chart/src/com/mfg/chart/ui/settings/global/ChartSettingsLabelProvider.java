package com.mfg.chart.ui.settings.global;

import org.eclipse.jface.viewers.LabelProvider;
import org.mfg.opengl.chart.interactive.InteractiveTool;

public class ChartSettingsLabelProvider extends LabelProvider {
	@Override
	public String getText(Object element) {
		if (element == ChartSettingsContentProvider.MAIN_CHART_NODE) {
			return "Main Chart";
		}
		if (element == ChartSettingsContentProvider.PRICES_NODE) {
			return "Prices";
		}
		if (element == ChartSettingsContentProvider.ARC_INDICATOR_NODE) {
			return "ARC Indicator";
		}
		if (element == ChartSettingsContentProvider.ADDITIONAL_INDICATOR_NODE) {
			return "Additional Indicator";
		}
		if (element == ChartSettingsContentProvider.TRADING_NODE) {
			return "Trading";
		}
		if (element == ChartSettingsContentProvider.DRAWING_TOOLS_NODE) {
			return "Drawing Tools";
		}
		if (element == ChartSettingsContentProvider.AUTO_TIME_LINES_NODE) {
			return "Auto Time Lines";
		}
		if (element == ChartSettingsContentProvider.TIMES_OF_THE_DAY_NODE) {
			return "Times Of The Day";
		}
		if (element instanceof InteractiveTool) {
			return ((InteractiveTool) element).getName();
		}
		if (element == ChartSettingsContentProvider.INDICATOR_SCALES_NODE) {
			return "Scales";
		}

		return super.getText(element);
	}
}
