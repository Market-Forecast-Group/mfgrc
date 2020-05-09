package com.mfg.chart.commands;

import static java.lang.System.out;

import java.util.Arrays;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.mfg.chart.ui.views.ChartContentAdapter;
import com.mfg.utils.ImageUtils;

public class DataLayerAction extends ChartAction {
	public static final String CMD_RANGE_ID = "com.mfg.chart.commands.switchLayer_Range";
	public static final String CMD_MINUTE_ID = "com.mfg.chart.commands.switchLayer_Minute";
	public static final String CMD_DAILY_ID = "com.mfg.chart.commands.switchLayer_Daily";
	public static final String[] CMD_IDS = { CMD_RANGE_ID, CMD_MINUTE_ID,
			CMD_DAILY_ID };

	public static DataLayerAction[] createActions(ChartContentAdapter adapter) {
		if (adapter.getChart().getModel().getDataLayerCount() > 1) {
			return new DataLayerAction[] {
					new DataLayerAction(adapter, DataLayerAction.CMD_RANGE_ID,
							"icons/R.png"),

					new DataLayerAction(adapter, DataLayerAction.CMD_MINUTE_ID,
							"icons/M.png"),

					new DataLayerAction(adapter, DataLayerAction.CMD_DAILY_ID,
							"icons/D.png") };
		}
		return null;
	}

	private int _layer;

	public DataLayerAction(ChartContentAdapter adapter, String id, String icon) {
		super(adapter, id, ImageUtils.getBundledImageDescriptor(
				"com.mfg.chart", icon), AS_CHECK_BOX);
		_layer = getLayer(id);
	}

	public static int getLayer(String id) {
		return Arrays.asList(CMD_IDS).indexOf(id);
	}

	@Override
	public void run() {
		if (isChecked()) {
			String error = _chart.setDataLayer(_layer);
			if (error == null) {
				_chart.setAutoDataLayer(false);
			} else {
				MessageDialog.openError(Display.getDefault().getActiveShell(),
						"Message", error);
			}
		} else {
			_chart.setAutoDataLayer(true);
		}

		Map<String, ChartAction> map = _adapter.getActionMap();
		boolean auto = _chart.isAutoDataLayer();
		for (String id : CMD_IDS) {
			ChartAction action = map.get(id);
			action.setChecked(!auto && getLayer(id) == _chart.getDataLayer());
		}
		_chart.update();
		out.println("auto " + auto);
	}

}