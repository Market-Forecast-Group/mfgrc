package com.mfg.symbols.trading.ui.adapters;

import org.eclipse.core.runtime.IAdapterFactory;

import com.mfg.chart.ui.views.ChartView;
import com.mfg.symbols.trading.configurations.TradingConfiguration;
import com.mfg.symbols.trading.ui.views.ITradingView;

@SuppressWarnings("rawtypes")
public class ChartViewAdapterFactory implements IAdapterFactory {

	private final Class[] list = { ITradingView.class };

	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adaptableObject instanceof ChartView
				&& adapterType == ITradingView.class) {
			final ChartView view = (ChartView) adaptableObject;
			if (view.getContent() instanceof TradingConfiguration) {
				return new ChartViewToITradingViewAdapter(view);
			}
		}
		return null;
	}

	@Override
	public Class[] getAdapterList() {
		return list;
	}

}
