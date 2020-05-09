package com.mfg.symbols.csv.ui.views;

import java.util.List;

import com.mfg.connector.csv.CSVPlugin;
import com.mfg.connector.csv.CsvDataProvider;
import com.mfg.persist.interfaces.IWorkspaceStorage;
import com.mfg.persist.interfaces.SimpleStorage;
import com.mfg.symbols.csv.CSVSymbolPlugin;
import com.mfg.symbols.csv.configurations.CSVConfiguration;
import com.mfg.symbols.csv.configurations.CSVSymbolData2;
import com.mfg.symbols.ui.views.ISymbolNavigatorRoot;
import com.mfg.symbols.ui.widgets.SymbolContentProvider;

public class CSVSymbolContentProvider extends SymbolContentProvider {
	public static SimpleStorage<CSVConfiguration> getConfigurationsStorage() {
		return CSVSymbolPlugin.getDefault().getCSVStorage();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.symbols.ui.views.SymbolContentProvider#getChildren(java.lang.
	 * Object)
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof ISymbolNavigatorRoot) {
			return new Object[] { CSVPlugin.getDefault().getDataProvider() };
		}

		if (parentElement instanceof CsvDataProvider) {
			return getConfigurationsStorage().getObjects().toArray();
		}

		return super.getChildren(parentElement);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.ui.views.CommonNavigatorContentProvider#getParent(java.lang.Object
	 * )
	 */
	@Override
	public Object getParent(Object element) {
		if (element instanceof CSVSymbolData2) {
			return CSVPlugin.getDefault().getDataProvider();
		}
		return super.getParent(element);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.symbols.ui.views.SymbolContentProvider#addStorages(java.util.
	 * List)
	 */
	@Override
	protected void registerStorages(List<IWorkspaceStorage> storages) {
		super.registerStorages(storages);
		storages.add(getConfigurationsStorage());
	}
}
