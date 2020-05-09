
package com.mfg.strategy.builder.views;

import java.util.List;

import com.mfg.persist.interfaces.IWorkspaceStorage;
import com.mfg.strategy.builder.StrategyBuilderPlugin;
import com.mfg.strategy.builder.persistence.StrategyBuilderStorage;
import com.mfg.ui.views.CommonNavigatorContentProvider;

public class StrategyContentProvider extends CommonNavigatorContentProvider {

	public static final Object ROOT = "com.mfg.strategy.builder.views.StrategyViewContentProvider.root";


	@Override
	public Object[] getChildren(Object parent) {
		StrategyBuilderStorage storage = getStorage();
		if (parent == ROOT) {
			return storage.getObjects().toArray();
		}
		return null;
	}


	@Override
	public boolean hasChildren(Object parent) {
		Object[] list = getChildren(parent);
		return list != null && list.length > 0;
	}


	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(ROOT);
	}


	@Override
	public Object getParent(Object element) {
		return null;
	}


	@Override
	protected void registerStorages(List<IWorkspaceStorage> storages) {
		storages.add(getStorage());
	}


	private static StrategyBuilderStorage getStorage() {
		return StrategyBuilderPlugin.getDefault().getStrategiesStorage();
	}
}
