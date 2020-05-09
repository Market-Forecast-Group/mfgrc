package com.marketforecastgroup.dfsa.adapters;

import org.eclipse.core.runtime.IAdapterFactory;

import com.marketforecastgroup.dfsa.ui.editors.MaturityEditor;
import com.marketforecastgroup.dfsa.ui.editors.MaturityEditorInput;
import com.mfg.dfs.data.MaturityStats;
import com.mfg.ui.editors.Editable;
import com.mfg.ui.editors.IEditable;

public class MaturityStatsAdapter implements IAdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adaptableObject instanceof MaturityStats
				&& adapterType == IEditable.class) {
			return new Editable(MaturityEditor.EDITOR_ID,
					new MaturityEditorInput((MaturityStats) adaptableObject));
		}
		return null;
	}

	@Override
	public Class[] getAdapterList() {
		return null;
	}

}
