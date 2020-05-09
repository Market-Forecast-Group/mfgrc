package com.mfg.symbols.ui.widgets;

import java.util.List;

import com.mfg.persist.interfaces.IWorkspaceStorage;
import com.mfg.ui.views.CommonNavigatorContentProvider;

public class SymbolContentProvider extends CommonNavigatorContentProvider {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.ui.views.CommonNavigatorContentProvider#addStorages(java.util
	 * .List)
	 */
	@Override
	protected void registerStorages(List<IWorkspaceStorage> storages) {
		//Adding a comment to avoid empty block warning.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.
	 * Object)
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		return null;
	}

}
