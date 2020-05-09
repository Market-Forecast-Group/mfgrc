package com.mfg.dm.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.mfg.dm.DMPlugin;

public class DataProvidersPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(DMPlugin.getDefault().getPreferenceStore());
	}

	@Override
	protected void createFieldEditors() {
		// addField(new BooleanFieldEditor(DMPlugin.ENABLED_CACHE_EXPANDER,
		// "Enabled streaming pipeline, not in memory? (experimental)",
		// getFieldEditorParent()));
		//
		// addField(new IntegerFieldEditor(DMPlugin.FILL_GAP_FILTER_ENHANCEMENT,
		// "Multiplier for streaming pipeline", getFieldEditorParent()));
	}

}
