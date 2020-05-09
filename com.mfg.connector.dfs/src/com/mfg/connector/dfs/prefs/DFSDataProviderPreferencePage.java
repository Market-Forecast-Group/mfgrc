package com.mfg.connector.dfs.prefs;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.mfg.connector.dfs.DFSPlugin;

//import com.mfg.connector.csv.CSVPlugin;

public class DFSDataProviderPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	private DirectoryFieldEditor _embeddedEditor;

	@Override
	protected void createFieldEditors() {
		RadioButtonFieldEditor editor1 = new RadioButtonFieldEditor(
				DFSPlugin.USE_PROXY, false, "Embedded", getFieldEditorParent()) {
			@Override
			protected void createControl(Composite parent) {
				Label label = new Label(parent, SWT.NONE);
				label.setText("Connection Mode");
				GridData gd = new GridData();
				gd.horizontalSpan = 1;
				label.setLayoutData(gd);
				super.createControl(parent);
			}
		};
		RadioButtonFieldEditor editor2 = new RadioButtonFieldEditor(
				DFSPlugin.USE_PROXY, true, "Remote", getFieldEditorParent()) {
			@Override
			protected void createControl(Composite parent) {
				super.createControl(parent);
				Label label = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
				GridData gd = new GridData();
				gd.horizontalSpan = 1;
				gd.grabExcessHorizontalSpace = true;
				gd.horizontalAlignment = GridData.FILL;
				label.setLayoutData(gd);
			}
		};
		editor1.setOtherEditors(editor2);
		editor2.setOtherEditors(editor1);

		addField(editor1);
		addField(editor2);

		_embeddedEditor = new DirectoryFieldEditor(DFSPlugin.DFS_LOCAL_ROOT,
				"Embedded DB Address", getFieldEditorParent());

		addField(_embeddedEditor);
		addField(new StringFieldEditor(DFSPlugin.DFS_REMOTE_ADDRESS,
				"Proxy Remote Address", getFieldEditorParent()));

		addField(new BooleanFieldEditor(DFSPlugin.ENABLE_MIXED_MODE,
				"DB mixed mode connection", getFieldEditorParent()));

		addField(new BooleanFieldEditor(DFSPlugin.CONNECT_TO_SIMULATOR,
				"Connect to simulator", getFieldEditorParent()));

		addField(new StringFieldEditor(DFSPlugin.DFS_DIR_PREFIX,
				"Dfs dir prefix", getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(DFSPlugin.getDefault().getPreferenceStore());

	}
}
