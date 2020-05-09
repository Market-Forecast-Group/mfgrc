
package com.mfg.strategy.builder.adapters;

import org.eclipse.core.runtime.IAdapterFactory;

import com.mfg.strategy.builder.model.StrategyInfo;
import com.mfg.strategy.builder.ui.StrategyBuilderEditor;
import com.mfg.ui.editors.Editable;
import com.mfg.ui.editors.IEditable;
import com.mfg.ui.editors.StorageObjectEditorInput;

public class StrategyInfoAdapterFactory implements IAdapterFactory {

	public StrategyInfoAdapterFactory() {
	}


	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adapterType == IEditable.class) {
			StrategyInfo info = (StrategyInfo) adaptableObject;
			return new Editable(StrategyBuilderEditor.ID, new StorageObjectEditorInput<>(info));
		}
		return null;
	}


	@Override
	public Class[] getAdapterList() {
		return new Class[] { IEditable.class };
	}

}
