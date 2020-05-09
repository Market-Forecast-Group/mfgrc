package com.mfg.chart.ui.settings.global;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.dialogs.PatternFilter;
import org.mfg.opengl.chart.interactive.InteractiveTool;

public class ChartSettingsPatternFilter extends PatternFilter {
	@Override
	protected boolean isLeafMatch(Viewer viewer, Object element) {
		StringBuilder text = new StringBuilder();
		if (element == ChartSettingsContentProvider.MAIN_CHART_NODE) {
			text.append(" background color text grid crosshair label snap prices tick autorange range probabilities enabled");
		} else if (element == ChartSettingsContentProvider.PRICES_NODE) {
			text.append(" color line width type compression prices zz enabled");
		} else if (element == ChartSettingsContentProvider.ARC_INDICATOR_NODE) {
			text.append(" zig zag zz line width type shape enabled size markers parallel real time top bottom center channel th band");
		} else if (element == ChartSettingsContentProvider.INDICATOR_SCALES_NODE) {
			text.append(" filter max number visible color");
		} else if (element == ChartSettingsContentProvider.ADDITIONAL_INDICATOR_NODE) {
			text.append(" probabilities probability enabled profit loss auto trend lines width line type");
		} else if (element == ChartSettingsContentProvider.AUTO_TIME_LINES_NODE) {
			text.append(" anchor color ratio lines line width type enabled ");
		} else if (element == ChartSettingsContentProvider.TIMES_OF_THE_DAY_NODE) {
			text.append(" max number of days only show labels close crosshair never always hour minutes minute color add remove");
		} else if (element == ChartSettingsContentProvider.TRADING_NODE) {
			text.append(" show close position color shape size width long short pending order open gain lose loss ");
		} else if (element instanceof InteractiveTool) {
			InteractiveTool tool = (InteractiveTool) element;
			text.append(" " + tool.getKeywords());
		}

		if (wordMatches(text.toString())) {
			return true;
		}

		return super.isLeafMatch(viewer, element);
	}
}
