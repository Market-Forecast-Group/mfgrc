package com.mfg.symbols.inputs.ui.views;

import java.util.List;
import java.util.UUID;

import com.mfg.persist.interfaces.IWorkspaceStorage;
import com.mfg.symbols.SymbolsPlugin;
import com.mfg.symbols.configurations.ISymbolConfigurationAdaptable;
import com.mfg.symbols.inputs.persistence.InputsStorage;
import com.mfg.ui.views.CommonNavigatorContentProvider;

public class InputsContentProvider extends CommonNavigatorContentProvider {

	private InputsStorage storage;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.
	 * Object)
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof ISymbolConfigurationAdaptable) {
			UUID id = ((ISymbolConfigurationAdaptable) parentElement).getUUID();
			if (id != null) {
				return storage.findBySymbolId(id).toArray();
			}
		}
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.ui.views.CommonNavigatorContentProvider#registerStorages(java
	 * .util.List)
	 */
	@Override
	protected void registerStorages(List<IWorkspaceStorage> storages) {
		storage = SymbolsPlugin.getDefault().getInputsStorage();
		storages.add(storage);
	}
}
