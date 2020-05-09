package com.mfg.plstats.adapters;

import org.eclipse.core.runtime.IAdapterFactory;

import com.mfg.plstats.persist.PLStatsIndicatorConfiguration;
import com.mfg.plstats.ui.editors.IndicatorEditor;
import com.mfg.plstats.ui.editors.IndicatorEditorInput;
import com.mfg.ui.editors.Editable;
import com.mfg.ui.editors.IEditable;

public class PLStatsIndicatorConfigurationAdapterFactory implements
		IAdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adapterType == IEditable.class) {
			IndicatorEditorInput input = new IndicatorEditorInput(
					((PLStatsIndicatorConfiguration) adaptableObject)
							.getIndicator());
			return new Editable(IndicatorEditor.ID, input);
		}
		return null;
	}

	@Override
	public Class[] getAdapterList() {
		return new Class[] { IEditable.class };
	}

}
