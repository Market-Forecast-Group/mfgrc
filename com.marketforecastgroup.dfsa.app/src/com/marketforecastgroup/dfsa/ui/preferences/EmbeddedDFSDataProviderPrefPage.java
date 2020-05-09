package com.marketforecastgroup.dfsa.ui.preferences;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.mfg.dfs.DFSCorePlugin;

public class EmbeddedDFSDataProviderPrefPage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(DFSCorePlugin.getDefault().getPreferenceStore());
	}

	@Override
	protected void createFieldEditors() {
		addField(new DirectoryFieldEditor("OLDROOT", "Embedded DB Address",
				getFieldEditorParent()));
	}

}
