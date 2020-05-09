package com.mfg.chart.ui.settings.global;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.mfg.chart.ui.ChartType;

public class ChartSettingsContentProvider implements ITreeContentProvider {
	public static final String ROOT_NODE = "ROOT";
	public static final String PRICES_NODE = "PRICES";
	public static final String MAIN_CHART_NODE = "MAIN_CHART_NODE";
	public static final String ARC_INDICATOR_NODE = "ARC_INIDICATOR_NODE";
	public static final String INDICATOR_SCALES_NODE = "INIDICATOR_SCALES";
	public static final String ADDITIONAL_INDICATOR_NODE = "ADDITIONAL_INDICATOR_NODE";
	public static final String TIMES_OF_THE_DAY_NODE = "TIMES_OF_THE_DAY_NODE";
	public static final String AUTO_TIME_LINES_NODE = "AUTO_TIME_LINES_NODE";
	public static final String TRADING_NODE = "TRADING_NODE";

	public static final String DRAWING_TOOLS_NODE = "SRAWING_TOOLS_NODE";
	private ChartSettingsInput _input;

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		_input = (ChartSettingsInput) newInput;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(ROOT_NODE);
	}

	@Override
	public Object[] getChildren(Object parent) {
		ChartType type = _input.getChart().getType();

		if (parent == ROOT_NODE) {
			List<Object> list = new ArrayList<>();
			list.add(MAIN_CHART_NODE);
			list.add(PRICES_NODE);
			if (type.hasChannels()) {
				list.add(ARC_INDICATOR_NODE);
				list.add(ADDITIONAL_INDICATOR_NODE);
			}
			if (type.hasExecutions()) {
				list.add(TRADING_NODE);
			}
			list.add(DRAWING_TOOLS_NODE);

			return list.toArray();
		}

		if (parent == ARC_INDICATOR_NODE) {
			return new Object[] { INDICATOR_SCALES_NODE };
		}
		if (parent == ADDITIONAL_INDICATOR_NODE) {
			return new Object[] { AUTO_TIME_LINES_NODE, TIMES_OF_THE_DAY_NODE };
		}
		if (parent == DRAWING_TOOLS_NODE) {
			return _input.getChart().getTools();
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		Object[] children = getChildren(element);
		return children != null && children.length > 0;
	}

}
