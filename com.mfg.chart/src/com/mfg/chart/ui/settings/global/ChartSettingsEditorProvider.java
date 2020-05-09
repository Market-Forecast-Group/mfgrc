package com.mfg.chart.ui.settings.global;

import org.eclipse.swt.widgets.Composite;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.ui.interactive.ForecastingTool;
import com.mfg.chart.ui.interactive.HarmonicLinesTool;
import com.mfg.chart.ui.interactive.LineTool;
import com.mfg.chart.ui.interactive.PolylineTool;
import com.mfg.chart.ui.interactive.TimeLinesTool;
import com.mfg.chart.ui.interactive.TrendLinesTool;

public class ChartSettingsEditorProvider implements
		IChartSettingsEditorProvider {

	@Override
	public IChartSettingsEditor createEditor(final Composite parent,
			final Chart chart, Object node, final Object context) {

		if (node == ChartSettingsContentProvider.MAIN_CHART_NODE) {
			return new MainSettingsEditor(parent, chart);
		}
		if (node == ChartSettingsContentProvider.PRICES_NODE) {
			return new PriceEditor(chart, parent);
		}
		if (node == ChartSettingsContentProvider.ARC_INDICATOR_NODE) {
			return new ARCIndicatorEditor(parent, chart);
		}
		if (node == ChartSettingsContentProvider.INDICATOR_SCALES_NODE) {
			return new IndicatorScalesEditor(parent, chart);
		}
		if (node == ChartSettingsContentProvider.ADDITIONAL_INDICATOR_NODE) {
			return new AdditionalIndicatorEditor(parent, chart);
		}
		if (node == ChartSettingsContentProvider.AUTO_TIME_LINES_NODE) {
			return new AutoTimeLinesEditor(parent, chart);
		}
		if (node == ChartSettingsContentProvider.TIMES_OF_THE_DAY_NODE) {
			return new TimesOfTheDayEditor(parent, chart);
		}
		if (node == ChartSettingsContentProvider.TRADING_NODE) {
			return new TradingEditor(chart, parent);
		}
		if (node instanceof TimeLinesTool) {
			return new TimeLineToolEditor(chart, parent, context);
		}
		if (node instanceof PolylineTool) {
			return new PolylineToolEditor(chart, parent, context);
		}
		if (node instanceof HarmonicLinesTool) {
			return new HarmonicLinesToolEditor(parent, chart);
		}
		if (node instanceof LineTool) {
			return new LineToolEditor(chart, parent, context);
		}
		if (node instanceof TrendLinesTool) {
			return new TrendLinesEditor(chart, parent, context);
		}
		if (node instanceof ForecastingTool) {
			return new ForecastingToolEditor(parent, chart);
		}

		return new EmptyEditor(parent);
	}
}
