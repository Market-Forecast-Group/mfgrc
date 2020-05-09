package com.mfg.chart.adapters;

import java.io.File;

import org.eclipse.core.runtime.IAdapterFactory;

import com.mfg.chart.ui.views.ChartBundleContentAdapter;
import com.mfg.chart.ui.views.IChartContentAdapter;

public class ChartBundleAdapterFactory implements IAdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adaptableObject instanceof File) {
			return new ChartBundleContentAdapter((File) adaptableObject);
		}
		return null;
	}

	@Override
	public Class[] getAdapterList() {
		return new Class[] { IChartContentAdapter.class };
	}

}
