package com.marketforecastgroup.dfsa.adapters;

import org.eclipse.core.runtime.IAdapterFactory;

import com.marketforecastgroup.dfsa.ui.editors.SymbolEditorInput;
import com.marketforecastgroup.dfsa.ui.editors.SymbolEditor;
import com.mfg.common.DfsSymbol;
import com.mfg.ui.editors.Editable;
import com.mfg.ui.editors.IEditable;

public class SymbolAdapter implements IAdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adaptableObject instanceof DfsSymbol
				&& adapterType == IEditable.class) {
			return new Editable(SymbolEditor.EDITOR_ID,
					new SymbolEditorInput((DfsSymbol) adaptableObject));
		}
		return null;
	}

	@Override
	public Class[] getAdapterList() {
		return null;
	}

}
