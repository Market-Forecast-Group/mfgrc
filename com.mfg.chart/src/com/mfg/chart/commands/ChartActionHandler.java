package com.mfg.chart.commands;

import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;

import com.mfg.chart.ui.views.AbstractChartView;
import com.mfg.chart.ui.views.ChartContentAdapter;
import com.mfg.chart.ui.views.ChartView;
import com.mfg.chart.ui.views.IChartContentAdapter;

public class ChartActionHandler extends AbstractChartViewHanlder {

	@Override
	protected Object execute(AbstractChartView view, ExecutionEvent event) {
		if (view instanceof ChartView) {
			String id = event.getCommand().getId();
			IChartContentAdapter adapter = view.getContentAdapter();
			if (adapter != null && adapter instanceof ChartContentAdapter) {
				Map<String, ChartAction> map = ((ChartContentAdapter) adapter)
						.getActionMap();
				ChartAction action = map.get(id);
				if (action == null) {
					executeWhenNoActionRegistered(view, event);
				} else {
					action.run();
				}
			}
		}
		return null;
	}

	/**
	 * Executed when there is not any action registered for this command id.
	 * 
	 * @param view
	 *            The view
	 * @param event
	 *            The event.
	 */
	protected void executeWhenNoActionRegistered(AbstractChartView view,
			ExecutionEvent event) {
		//
	}

}
