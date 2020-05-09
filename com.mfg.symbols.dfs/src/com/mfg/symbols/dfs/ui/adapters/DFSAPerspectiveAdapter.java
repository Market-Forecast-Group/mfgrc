package com.mfg.symbols.dfs.ui.adapters;

import org.eclipse.core.runtime.IAdapterFactory;

import com.marketforecastgroup.dfsa.ui.Perspective;
import com.mfg.connector.dfs.DFSPlugin;
import com.mfg.ui.IMFGPerspectiveFactory;

public class DFSAPerspectiveAdapter implements IAdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adaptableObject instanceof Perspective) {
			return new IMFGPerspectiveFactory() {

				@Override
				public boolean isVisible() {
					boolean online = DFSPlugin.getDefault()
							.getPreferenceStore()
							.getBoolean(DFSPlugin.USE_PROXY);
					return !online;
				}
			};
		}
		return null;
	}

	@Override
	public Class[] getAdapterList() {
		return new Class[] { IMFGPerspectiveFactory.class };
	}
}
