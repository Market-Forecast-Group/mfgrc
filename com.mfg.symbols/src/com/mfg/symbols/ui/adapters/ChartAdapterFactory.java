package com.mfg.symbols.ui.adapters;

import org.eclipse.core.runtime.IAdapterFactory;

import com.mfg.chart.ui.views.IChartContentAdapter;
import com.mfg.symbols.configurations.SymbolConfiguration;

@SuppressWarnings("rawtypes")
public class ChartAdapterFactory implements IAdapterFactory {

	private static Class[] adapterList = { IChartContentAdapter.class };

	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adapterType == IChartContentAdapter.class) {
			if (adaptableObject instanceof SymbolConfiguration) {
				SymbolConfiguration<?, ?> configuration = (SymbolConfiguration<?, ?>) adaptableObject;
				return new SymbolChartAdapter(configuration);
			}
		}
		return null;
	}

	@Override
	public Class[] getAdapterList() {
		return adapterList;
	}

}
